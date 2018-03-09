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

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class for configuring and running the MOEA/DD algorithm
 *
 * @author dy
 */
public class MOEADDRunner extends AbstractAlgorithmRunner {
    /**
     * @param args Command line arguments.
     * @throws SecurityException Invoking command:
     *                           java org.uma.jmetal.runner.multiobjective.MOEADDRunner problemName [referenceFront]
     */
    public static void main(String[] args) throws FileNotFoundException {
        DoubleProblem problem;
        Algorithm<List<DoubleSolution>> algorithm;
        MutationOperator<DoubleSolution> mutation;
        SBXCrossover crossover;

        String problemName;
        String referenceParetoFront = "";
        if (args.length == 1) {
            problemName = args[0];
        } else if (args.length == 2) {
            problemName = args[0];
            referenceParetoFront = args[1];
        } else {
            problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4";
            referenceParetoFront = "problem/src/test/resources/pareto_fronts/DTLZ4.3D.pf";
        }

        problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 30.0;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADD)
                .setCrossover(crossover)
                .setMutation(mutation)
                .setMaxEvaluations(100000)
                .setPopulationSize(100)
                .setResultPopulationSize(100)
                .setNeighborhoodSelectionProbability(0.9)
                .setMaximumNumberOfReplacedSolutions(20)
                .setNeighborSize(20)
                .setFunctionType(AbstractMOEAD.FunctionType.PBI)
                .setDataDirectory("MOEAD_Weights")
                .setInProcessDataPath("F:\\Experiment Data-Modify"+"\\MOEADD\\"+problem.getName())
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        List<DoubleSolution> population = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }
    }
}
