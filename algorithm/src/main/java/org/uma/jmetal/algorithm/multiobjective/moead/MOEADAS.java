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
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the MOEA/D-AS algorithm described in :
 * Create by dyy
 * "Optimal  Matching-Based Selection in Evolutionary Multiobjective Optimization",
 * 自适应KM选择策略 MOEAD-AS，在紊乱判断后自适应选择选择策略；
 *加入ParentPopulation存放差分进化前的种群，避免使用邻域更新后的population
 * @author dyy
 * @version 1.0     2017 3/10  14:25
 */
public class MOEADAS extends AbstractMOEAD<DoubleSolution> {
    private static final long serialVersionUID = 2515198940774839154L;
    JMetalRandom randomGenerator;
    private DifferentialEvolutionCrossover differentialEvolutionCrossover;
    private DoubleSolution[] savedValues;
    private double[] utility;
    private int[] frequency;
    int run;

    public MOEADAS(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations,
                   MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover,
                   FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability,
                   int maximumNumberOfReplacedSolutions, int neighborSize, String inProcessDataPath ,int run) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize
                , inProcessDataPath,run);

        differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;

        this.run = run;
        savedValues = new DoubleSolution[populationSize];
        utility = new double[populationSize];
        frequency = new int[populationSize];
        for (int i = 0; i < utility.length; i++) {
            utility[i] = 1.0;
            frequency[i] = 0;
        }

        randomGenerator = JMetalRandom.getInstance();
    }

    @Override
    public void run() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();
        initializeNadirPoint();

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
                frequency[subProblemId]++;

                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

                differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
                List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

                DoubleSolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNadirPoint(child);
                updateNeighborhood(child, subProblemId, neighborType);

                offspringPopulation.add(child);
            }
            String path="F:\\Experiment Data(last)\\MOEADAS\\"+problem.getName()+"\\updateAbility"+this.run+".txt";
            appendToFile(path,generation+"-----------"+problem.getName()+"------------");
            appendToFile(path,updateAbility+"\r\n");

            if (updateAbility>populationSize/neighborSize || generation%50==0){
            // Combine the parent and the current offspring populations
            jointPopulation.clear();
            jointPopulation.addAll(offspringPopulation);
            jointPopulation.addAll(parentPopulation);
            // selection process---KM匹配------
            KMSelection();
            }
            generation++;
            if (generation % 30 == 0) {
                utilityFunction();
            }
            saveDataInProcess();
            System.out.println("-------generation"+generation+"---------");

        } while (evaluations < maxEvaluations);

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
            savedValues[i] = (DoubleSolution) newSolution.copy();
        }
    }

    @Override
    public List<DoubleSolution> getResult() {
        return population;
    }

    public void utilityFunction() throws JMetalException {
        double f1, f2, uti, delta;
        for (int n = 0; n < populationSize; n++) {
            f1 = fitnessFunction(population.get(n), lambda[n]);
            f2 = fitnessFunction(savedValues[n], lambda[n]);
            delta = f2 - f1;
            if (delta > 0.001) {
                utility[n] = 1.0;
            } else {
                uti = (0.95 + (0.05 * delta / 0.001)) * utility[n];
                utility[n] = uti < 1.0 ? uti : 1.0;
            }
            savedValues[n] = (DoubleSolution) population.get(n).copy();
        }
    }

    public List<Integer> tourSelection(int depth) {
        List<Integer> selected = new ArrayList<Integer>();
        List<Integer> candidate = new ArrayList<Integer>();

        for (int k = 0; k < problem.getNumberOfObjectives(); k++) {
            // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU Et AL
            // (NOT SORTED!!!!)
            selected.add(k);
        }

        for (int n = problem.getNumberOfObjectives(); n < populationSize; n++) {
            // set of unselected weights
            candidate.add(n);
        }

        while (selected.size() < (int) (populationSize / 5.0)) {
            int best_idd = (int) (randomGenerator.nextDouble() * candidate.size());
            int i2;
            int best_sub = candidate.get(best_idd);
            int s2;
            for (int i = 1; i < depth; i++) {
                i2 = (int) (randomGenerator.nextDouble() * candidate.size());
                s2 = candidate.get(i2);
                if (utility[s2] > utility[best_sub]) {
                    best_idd = i2;
                    best_sub = s2;
                }
            }
            selected.add(best_sub);
            candidate.remove(best_idd);
        }
        return selected;
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

    @Override
    public String getName() {
        return "MOEADAS";
    }

    @Override
    public String getDescription() {
        return "Multi-Objective Evolutionary Algorithm based on Decomposition. Version with KM Matching Model";
    }
}
