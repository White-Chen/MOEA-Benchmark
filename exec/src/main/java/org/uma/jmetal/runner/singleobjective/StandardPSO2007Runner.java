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

package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.StandardPSO2007;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to configure and run a StandardPSO2007. The algorithm can be configured
 * to use threads. The number of cores is specified as an optional parameter. The target problem is Sphere.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StandardPSO2007Runner {
    private static final int DEFAULT_NUMBER_OF_CORES = 1;

    /**
     * Usage: java org.uma.jmetal.runner.singleobjective.StandardPSO2007Runner [cores]
     */
    public static void main(String[] args) throws Exception {

        DoubleProblem problem;
        Algorithm<DoubleSolution> algorithm;
        SolutionListEvaluator<DoubleSolution> evaluator;

        String problemName = "org.uma.jmetal.problem.singleobjective.Sphere";

        problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

        int numberOfCores;
        if (args.length == 1) {
            numberOfCores = Integer.valueOf(args[0]);
        } else {
            numberOfCores = DEFAULT_NUMBER_OF_CORES;
        }

        if (numberOfCores == 1) {
            evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();
        } else {
            evaluator = new MultithreadedSolutionListEvaluator<DoubleSolution>(numberOfCores, problem);
        }

        algorithm = new StandardPSO2007(problem,
                10 + (int) (2 * Math.sqrt(problem.getNumberOfVariables())),
                80000, 3, evaluator);

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        DoubleSolution solution = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        List<DoubleSolution> population = new ArrayList<>(1);
        population.add(solution);
        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        evaluator.shutdown();
    }
}
