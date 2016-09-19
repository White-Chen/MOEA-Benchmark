//  SMPSORunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
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
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.WFGHypervolume;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.util.List;

/**
 * Class for configuring and running the SMPSO algorithm using an HypervolumeArchive, i.e, the
 * SMPSOhv algorithm described in:
 * A.J Nebro, J.J. Durillo, C.A. Coello Coello. Analysis of Leader Selection Strategies in a
 * Multi-Objective Particle Swarm Optimizer. 2013 IEEE Congress on Evolutionary Computation. June 2013
 * DOI: 10.1109/CEC.2013.6557955
 * <p>
 * This is a variant using the WFG Hypervolume implementation
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMPSOHv2Runner extends AbstractAlgorithmRunner {
    /**
     * @param args Command line arguments. The first (optional) argument specifies
     *             the problem to solve.
     * @throws org.uma.jmetal.util.JMetalException
     * @throws java.io.IOException
     * @throws SecurityException                   Invoking command:
     *                                             java org.uma.jmetal.runner.multiobjective.SMPSOHvRunner problemName [referenceFront]
     */
    public static void main(String[] args) throws Exception {
        DoubleProblem problem;
        Algorithm<List<DoubleSolution>> algorithm;
        MutationOperator<DoubleSolution> mutation;

        String referenceParetoFront = "";

        String problemName;
        if (args.length == 1) {
            problemName = args[0];
        } else if (args.length == 2) {
            problemName = args[0];
            referenceParetoFront = args[1];
        } else {
            //problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1";
            //referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/DTLZ1.3D.pf" ;
            problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";
            referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT4.pf";
        }

        problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

        BoundedArchive<DoubleSolution> archive =
                new HypervolumeArchive<DoubleSolution>(100, new WFGHypervolume<DoubleSolution>());

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        algorithm = new SMPSOBuilder(problem, archive)
                .setMutation(mutation)
                .setMaxIterations(250)
                .setSwarmSize(100)
                .setRandomGenerator(new MersenneTwisterGenerator())
                .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
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
