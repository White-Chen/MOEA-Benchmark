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
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Class implementing the MOEA/D-DE algorithm described in :
 * Hui Li; Qingfu Zhang, "Multiobjective Optimization Problems With Complicated Pareto Sets,
 * MOEA/D and NSGA-II," Evolutionary Computation, IEEE Transactions on , vol.13, no.2, pp.284,302,
 * April 2009. doi: 10.1109/TEVC.2008.925798
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class MOEAD extends AbstractMOEAD<DoubleSolution> {

    private static final long serialVersionUID = 1556879137354418923L;
    private DifferentialEvolutionCrossover differentialEvolutionCrossover;

    public MOEAD(Problem<DoubleSolution> problem,
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
                 String inProcessDataPath) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
                neighborSize, inProcessDataPath);

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
            }
            String path="\\\\Dy-pc\\f\\Experiment Data(lw)\\"+getName()+"\\"+problem.getName()+"\\updateAbility"+run+".txt";
            appendToFile(path,generation+"-----------"+problem.getName()+"------------");
            appendToFile(path,updateAbility+"\r\n");
            saveDataInProcess();
            generation++;
        } while (evaluations < maxEvaluations);

    }

    protected void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newSolution = problem.createSolution();

            problem.evaluate(newSolution);
            population.add(newSolution);
        }
    }

    @Override
    public String getName() {
        return "MOEAD";
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
    public String getDescription() {
        return "Multi-Objective Evolutionary Algorithm based on Decomposition";
    }
}
