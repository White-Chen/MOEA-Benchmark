//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.KMmatching;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 进行子问题和个体在不同选择策略下的对比
 *分别输出三种选择策略的解
 * @author dyy
 * @version 1.0
 */
public class MOEADASTest extends AbstractMOEAD<DoubleSolution> {

    private static final long serialVersionUID = 1556879137354418923L;
    private DifferentialEvolutionCrossover differentialEvolutionCrossover;

    public MOEADASTest(Problem<DoubleSolution> problem,
                       int populationSize,
                       int resultPopulationSize,
                       int maxEvaluations,
                       MutationOperator<DoubleSolution> mutation,
                       CrossoverOperator<DoubleSolution> crossover,
                       FunctionType functionType,
                       String dataDirectory,
                       double neighborhoodSelectionProbability,
                       int maximumNumberOfReplacedSolutions,
                       int neighborSize,
                       String inProcessDataPath,int run) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
                neighborSize, inProcessDataPath,run);

        differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
    }

    @Override
    public void run() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();
        int generation = 0;


        evaluations = populationSize;
        do {
            updateAbility = 0;
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            offspringPopulation.clear();
            parentPopulation.clear();
            parentPopulation.addAll(population);

            for (int i = 0; i < populationSize; i++) {
                int subProblemId = permutation[i];

                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

                differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
                List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

                DoubleSolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNeighborhood(child, subProblemId, neighborType);
                offspringPopulation.add(child);
            }
            _saveDataInProcess(population,inProcessDataPath+"/MOEAD");
            String path="F:\\Experiment Data\\MOEADKM\\"+problem.getName()+"\\updateAbility"+run+".txt";
            appendToFile(path,generation+"-----------"+problem.getName()+"------------");
            appendToFile(path,updateAbility+"\r\n");

            jointPopulation.clear();
            jointPopulation.addAll(offspringPopulation);
            jointPopulation.addAll(parentPopulation);      //保证KM之前未进行邻域更新
            offspringPopulation.clear();
            offspringPopulation.addAll(population);        //offspring作为临时变量，存放邻域更新后选择的解
            stmSelection();
            _saveDataInProcess(population,inProcessDataPath+"/MOEAD-STM");

            KMSelection();
            _saveDataInProcess(population,inProcessDataPath+"/MOEAD-KM");


//            population.clear();
//            population.addAll(offspringPopulation);
            generation++;
            System.out.println("-------generation"+generation+"---------");
        } while (evaluations < maxEvaluations);

    }

    protected void _saveDataInProcess( List<DoubleSolution> population,String filepath) {
        File file = new File(filepath);
        file.mkdirs();
        if (!inProcessDataPath.isEmpty()) {
//         if (!inProcessDataPath.isEmpty()) {
            new SolutionListOutput(population)
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(filepath + "/VAR" + evaluations / (populationSize) + ".tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext(filepath + "/FUN" + evaluations / (populationSize) + ".tsv"))
                    .print();
        }
    }

    protected void initializePopulation() {
        population = new ArrayList<>(populationSize);
        offspringPopulation = new ArrayList<>(populationSize);
        jointPopulation = new ArrayList<>(populationSize);
        parentPopulation = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newSolution = problem.createSolution();

            problem.evaluate(newSolution);
            population.add(newSolution);
        }
    }

    /**
     * 初始化KM算法的权重------个体集和参考向量的APD值
     * @return
     */
    public double[][] initKMWeights(){
        double[][] weights_init = new double[jointPopulation.size()][lambda.length];

        for (int i=0;i< jointPopulation.size();i++)
        {
            for (int j=0;j< lambda.length;j++)
            {
                weights_init[i][j] = 0;
                double ws = -1*fitnessFunction(jointPopulation.get(i),lambda[j]);
                //double ws = -1*Kmfitness("APBI",jointPopulation.get(i),lambda[j]);
                //weights_init[i][j] = Double.parseDouble(String.format("%.4f", ws));
                weights_init[i][j] = ws;
            }
        }
        return weights_init;
    }

    /**
     * KM匹配算法
     */
    public void KMSelection(){
        KMmatching kMmatch = new KMmatching(populationSize*2);
        double[][] kmWeights = initKMWeights();
        kMmatch.getBestBipartie(kmWeights);
        int[] matchResults = kMmatch.match;
        population.clear();
        for (int i = 0; i < matchResults.length/2; i++) {
            population.add(i, jointPopulation.get(matchResults[i]));
        }
    }

    /**
     * 向文件写字符串
     */
    public static void appendToFile(String path,String word){
        try
        {
            File file=new File(path);
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if(!file.exists())
                file.createNewFile();
            FileOutputStream out=new FileOutputStream(file,true); //如果追加方式用true
            StringBuffer sb=new StringBuffer();
            sb.append(word);
            out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
            out.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getStackTrace());
        }
    }

    /**
     * Select the next parent population, based on the stable matching criteria
     */
    public void stmSelection() {

        int[] idx = new int[populationSize];
        double[] nicheCount = new double[populationSize];

        int[][] solPref = new int[jointPopulation.size()][];
        double[][] solMatrix = new double[jointPopulation.size()][];
        double[][] distMatrix = new double[jointPopulation.size()][];
        double[][] fitnessMatrix = new double[jointPopulation.size()][];

        for (int i = 0; i < jointPopulation.size(); i++) {
            solPref[i] = new int[populationSize];
            solMatrix[i] = new double[populationSize];
            distMatrix[i] = new double[populationSize];
            fitnessMatrix[i] = new double[populationSize];
        }
        int[][] subpPref = new int[populationSize][];
        double[][] subpMatrix = new double[populationSize][];
        for (int i = 0; i < populationSize; i++) {
            subpPref[i] = new int[jointPopulation.size()];
            subpMatrix[i] = new double[jointPopulation.size()];
        }

        // Calculate the preference values of solution matrix
        for (int i = 0; i < jointPopulation.size(); i++) {
            int minIndex = 0;
            for (int j = 0; j < populationSize; j++) {
                fitnessMatrix[i][j] = fitnessFunction(jointPopulation.get(i), lambda[j]);
                distMatrix[i][j] = calculateDistance2(jointPopulation.get(i), lambda[j]);
                if (distMatrix[i][j] < distMatrix[i][minIndex])
                    minIndex = j;
            }
            nicheCount[minIndex] = nicheCount[minIndex] + 1;
        }

        // calculate the preference values of subproblem matrix and solution matrix
        for (int i = 0; i < jointPopulation.size(); i++) {
            for (int j = 0; j < populationSize; j++) {
                subpMatrix[j][i] = fitnessFunction(jointPopulation.get(i), lambda[j]);
                solMatrix[i][j] = distMatrix[i][j] + nicheCount[j];
            }
        }

        // sort the preference value matrix to get the preference rank matrix
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < jointPopulation.size(); j++)
                subpPref[i][j] = j;
            MOEADUtils.quickSort(subpMatrix[i], subpPref[i], 0, jointPopulation.size() - 1);
        }
        for (int i = 0; i < jointPopulation.size(); i++) {
            for (int j = 0; j < populationSize; j++)
                solPref[i][j] = j;
            MOEADUtils.quickSort(solMatrix[i], solPref[i], 0, populationSize - 1);
        }

        idx = stableMatching(subpPref, solPref, populationSize, jointPopulation.size());

        population.clear();
        for (int i = 0; i < populationSize; i++)
            population.add(i, jointPopulation.get(idx[i]));
    }

    /**
     * Return the stable matching between 'subproblems' and 'solutions'
     * ('subproblems' propose first). It is worth noting that the number of
     * solutions is larger than that of the subproblems.
     *
     * @param manPref
     * @param womanPref
     * @param menSize
     * @param womenSize
     * @return
     */
    public int[] stableMatching(int[][] manPref, int[][] womanPref, int menSize, int womenSize) {

        // Indicates the mating status
        int[] statusMan = new int[menSize];
        int[] statusWoman = new int[womenSize];

        final int NOT_ENGAGED = -1;
        for (int i = 0; i < womenSize; i++)
            statusWoman[i] = NOT_ENGAGED;

        // List of men that are not currently engaged.
        LinkedList<Integer> freeMen = new LinkedList<Integer>();
        for (int i = 0; i < menSize; i++)
            freeMen.add(i);

        // next[i] is the next woman to whom i has not yet proposed.
        int[] next = new int[womenSize];

        while (!freeMen.isEmpty()) {
            int m = freeMen.remove();
            int w = manPref[m][next[m]];
            next[m]++;
            if (statusWoman[w] == NOT_ENGAGED) {
                statusMan[m] = w;
                statusWoman[w] = m;
            } else {
                int m1 = statusWoman[w];
                if (prefers(m, m1, womanPref[w], menSize)) {
                    statusMan[m] = w;
                    statusWoman[w] = m;
                    freeMen.add(m1);
                } else {
                    freeMen.add(m);
                }
            }
        }

        return statusMan;
    }

    /**
     * Returns true in case that a given woman prefers x to y.
     *
     * @param x
     * @param y
     * @param womanPref
     * @return
     */
    public boolean prefers(int x, int y, int[] womanPref, int size) {

        for (int i = 0; i < size; i++) {
            int pref = womanPref[i];
            if (pref == x)
                return true;
            if (pref == y)
                return false;
        }
        // this should never happen.
        System.out.println("Error in womanPref list!");
        return false;
    }

    /**
     * Calculate the perpendicular distance between the solution and reference
     * line
     *
     * @param individual
     * @param lambda
     * @return
     */
    public double calculateDistance(DoubleSolution individual, double[] lambda) {
        double scale;
        double distance;

        double[] vecInd = new double[problem.getNumberOfObjectives()];
        double[] vecProj = new double[problem.getNumberOfObjectives()];

        // vecInd has been normalized to the range [0,1]
        for (int i = 0; i < problem.getNumberOfObjectives(); i++)
            vecInd[i] = (individual.getObjective(i) - idealPoint[i]) / (nadirPoint[i] - idealPoint[i]);

        scale = innerproduct(vecInd, lambda) / innerproduct(lambda, lambda);
        for (int i = 0; i < problem.getNumberOfObjectives(); i++)
            vecProj[i] = vecInd[i] - scale * lambda[i];

        distance = norm_vector(vecProj);

        return distance;
    }

    /**
     * Calculate the perpendicular distance between the solution and reference line
     *
     * @param individual
     * @param lambda
     * @return
     */
    public double calculateDistance2(DoubleSolution individual, double[] lambda) {

        double distance;
        double distanceSum = 0.0;

        double[] vecInd = new double[problem.getNumberOfObjectives()];
        double[] normalizedObj = new double[problem.getNumberOfObjectives()];

        for (int i = 0; i < problem.getNumberOfObjectives(); i++)
            distanceSum += individual.getObjective(i);
        for (int i = 0; i < problem.getNumberOfObjectives(); i++)
            normalizedObj[i] = individual.getObjective(i) / distanceSum;
        for (int i = 0; i < problem.getNumberOfObjectives(); i++)
            vecInd[i] = normalizedObj[i] - lambda[i];

        distance = norm_vector(vecInd);

        return distance;
    }

    /**
     * Calculate the norm of the vector
     *
     * @param z
     * @return
     */
    public double norm_vector(double[] z) {
        double sum = 0;

        for (int i = 0; i < problem.getNumberOfObjectives(); i++)
            sum += z[i] * z[i];

        return Math.sqrt(sum);
    }

    /**
     * Calculate the dot product of two vectors
     *
     * @param vec1
     * @param vec2
     * @return
     */
    public double innerproduct(double[] vec1, double[] vec2) {
        double sum = 0;

        for (int i = 0; i < vec1.length; i++)
            sum += vec1[i] * vec2[i];

        return sum;
    }

    @Override
    public String getName() {
        return "MOEADTest";
    }

    @Override
    public String getDescription() {
        return "Multi-Objective Evolutionary Algorithm based on Decomposition";
    }
}
