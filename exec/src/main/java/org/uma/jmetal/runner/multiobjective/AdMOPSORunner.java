package org.uma.jmetal.runner.multiobjective;


import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.admopso.AdMOPSOBuilder;
import org.uma.jmetal.operator.impl.mutation.RandomizeMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * Created by Lenovo on 2015/11/5.
 *
 * @author chenzhe
 */
public class AdMOPSORunner extends AbstractAlgorithmRunner {

    public static void main(String[] args) throws Exception {
        DoubleProblem problem;
        Algorithm<List<DoubleSolution>> algorithm;

        String referenceParetoFront = "";

        String problemName;
        if (args.length == 1) {
            problemName = args[0];
        } else if (args.length == 2) {
            problemName = args[0];
            referenceParetoFront = args[1];
        } else {
            problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
            referenceParetoFront = "/pareto_fronts/ZDT1.pf";
        }


        problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

        int iterations, maxEvaluations, populationSize, archiveSize;
        if (problem.getNumberOfObjectives() == 2) {
            maxEvaluations = 30000;
            populationSize = 100;
            archiveSize = 100;
        } else {
            maxEvaluations = 50000;
            populationSize = 150;
            archiveSize = 150;
        }

        AdaptiveGridArchiveII<DoubleSolution> archive = new AdaptiveGridArchiveII<>(archiveSize, 30, problem.getNumberOfObjectives());

        double mutationProbability = 0.5;

        algorithm = new AdMOPSOBuilder(problem, archive)
                .setEvaluator(new SequentialSolutionListEvaluator<>())
                .setDeltaDivision(2)
                .setSelectionPressure(4)
                .setEliminatePressure(2)
                .setWeightMax(0.9)
                .setWeightMin(0.4)
                .setSwarmSize(populationSize)
                .setMaxEvaluations(maxEvaluations)
                .setMutation(new RandomizeMutation(mutationProbability))
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
