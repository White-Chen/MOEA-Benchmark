package org.uma.jmetal.experiment;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.admopso.AdMOPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.dmopso.DMOPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.mombi.MOMBI2;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.NonUniformMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.RandomizeMutation;
import org.uma.jmetal.operator.impl.mutation.UniformMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.cec2009Competition.UF1;
import org.uma.jmetal.problem.multiobjective.cec2009Competition.UF2;
import org.uma.jmetal.problem.multiobjective.cec2009Competition.UF3;
import org.uma.jmetal.problem.multiobjective.cec2009Competition.UF7;
import org.uma.jmetal.problem.multiobjective.dtlz.*;
import org.uma.jmetal.problem.multiobjective.wfg.WFG1;
import org.uma.jmetal.problem.multiobjective.wfg.WFG2;
import org.uma.jmetal.problem.multiobjective.wfg.WFG3;
import org.uma.jmetal.problem.multiobjective.wfg.WFG4;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ChenZhe on 4/26/2016.
 * <p>
 * This experiment assumes that the reference Pareto front ara known, so the names of file containing
 * them and the directory where they ara located must be specified.
 * <p>
 * Six quality indicators are used for performance assessment.
 * <p>
 * The steps to carry out the experiment are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Compute the quality indicators
 * 4. Generate Latex tables reporting means and medians
 * 5. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 * 6. Generate Latex tables with the ranking obtained by applying the Friedman test
 * 7. Generate R scripts to obtain boxplots
 */
public class DMOPSOStudy {

    public static final String experimentBaseDirectory = "F:/Experiment DATA/";
    private static final int INDEPENDENT_RUNS = 5;

    public static void main(String[] args) throws IOException {
        /*
        if (args.length != 1) {
            throw new JMetalException("Missing argument: experiment base directory");
        }
        String experimentBaseDirectory = args[0];
        */

        List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(
//                new ZDT1(), new ZDT2(), new ZDT3(), new ZDT4(), new ZDT6(),
//                new UF1(), new UF2(), new UF3(), new UF7(),
                new DTLZ1(), new DTLZ2()
//                new DTLZ3(), new DTLZ4(), new DTLZ5(), new DTLZ6(), new DTLZ7()
//                new WFG1(), new WFG2(), new WFG3(), new WFG4()
        );


        List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList, INDEPENDENT_RUNS);

        List<String> referenceFrontFileNames = Arrays.asList(
//                "ZDT1.pf", "ZDT2_100.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf",
//                "UF1.pf", "UF2_100.pf", "UF3.pf", "UF7.pf",
                "DTLZ1.3D.pf", "DTLZ2.3D.pf"
//                "DTLZ3.3D.pf", "DTLZ4.3D.pf", "DTLZ5.3D.pf", "DTLZ6.3D.pf", "DTLZ7.3D.pf"
//                "WFG1.2D.pf", "WFG2.2D.pf", "WFG3.2D.pf", "WFG4.2D.pf"
        );



        /*while(algorithmList.size() > 0)
        {
            String tempTag = algorithmList.get(0).getTag();
            List<TaggedAlgorithm<List<DoubleSolution>>> tempalgorithmList = algorithmList.stream()
                    .filter(al -> Objects.equals(al.getTag(), tempTag))
                    .collect(toList());
            algorithmList.removeIf(al -> Objects.equals(al.getTag(), tempTag));
            Experiment<DoubleSolution, List<DoubleSolution>> experiment =
                    new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("DMOPSO")
                            .setAlgorithmList(tempalgorithmList)
                            .setProblemList(problemList)
                            .setExperimentBaseDirectory(experimentBaseDirectory)
                            .setOutputParetoFrontFileName("FUN")
                            .setOutputParetoSetFileName("VAR")
                            .setReferenceFrontDirectory("/pareto_fronts")
                            .setReferenceFrontFileNames(referenceFrontFileNames)
                            .setIndicatorList(Arrays.asList(
                                    new InvertedGenerationalDistance<DoubleSolution>()
                                    , new InvertedGenerationalDistancePlus<DoubleSolution>()
                                    , new Epsilon<DoubleSolution>()
                                    , new Spread<DoubleSolution>()
                                    , new GenerationalDistance<DoubleSolution>()
                                    , new PISAHypervolume<DoubleSolution>()
                                    , new ErrorRatioBack<DoubleSolution>()
                            ))
                            .setIndependentRuns(INDEPENDENT_RUNS)
                            .build();

            new ExecuteAlgorithms<>(experiment).run();
            new ComputeQualityIndicators<>(experiment).run();
            new GenerateLatexTablesWithStatistics(experiment).run();
            new GenerateWilcoxonTestTablesWithR<>(experiment).run();
            new GenerateFriedmanTestTables<>(experiment).run();
            new GenerateBoxplotsWithR<>(experiment).setRows(4).setColumns(3).run();
            System.gc();
        }*/

        Experiment<DoubleSolution, List<DoubleSolution>> experiment =
                new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("MOEAD_PCA")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setExperimentBaseDirectory(experimentBaseDirectory)
                        .setOutputParetoFrontFileName("FUN")
                        .setOutputParetoSetFileName("VAR")
                        .setReferenceFrontDirectory("/pareto_fronts")
                        .setReferenceFrontFileNames(referenceFrontFileNames)
                        .setIndicatorList(Arrays.asList(
                                new InvertedGenerationalDistance<DoubleSolution>()
                                , new InvertedGenerationalDistancePlus<DoubleSolution>()
//                                , new Epsilon<DoubleSolution>()
                                , new Spread<DoubleSolution>()
                                , new GenerationalDistance<DoubleSolution>()
                                , new PISAHypervolume<DoubleSolution>()
                                ,
                                new ErrorRatioBack<DoubleSolution>()
                        ))
                        .setNumberOfCores(1)
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .build();

        new ExecuteAlgorithms<>(experiment).run();
        new ComputeQualityIndicators<>(experiment).run();
//        new GenerateLatexTablesWithStatistics(experiment).run();
//        new GenerateWilcoxonTestTablesWithR<>(experiment).run();
//        new GenerateFriedmanTestTables<>(experiment).run();
//        new GenerateBoxplotsWithR<>(experiment).setRows(4).setColumns(3).run();
        System.gc();


    }

    /**
     * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
     * {@link TaggedAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link TaggedAlgorithm}
     * has an optional tag component, that can be set as it is shown in this example, where four variants of a
     * same algorithm are defined.
     *
     * @param problemList
     * @return
     */
    static List<TaggedAlgorithm<List<DoubleSolution>>> configureAlgorithmList(
            List<Problem<DoubleSolution>> problemList, int independentRuns) {
        List<TaggedAlgorithm<List<DoubleSolution>>> algorithms = new ArrayList<>();

        for (int run = 0; run < INDEPENDENT_RUNS; run++) {


            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i), new SBXCrossover(1.0, 20.0),
                        new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 50000 : 30000)
                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 150 : 100)
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEAD_PCA/data/NSGAII/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "NSGAII", problemList.get(i), run));
            }


            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new AdMOPSOBuilder(
                        (DoubleProblem) problemList.get(i),
                        new AdaptiveGridArchiveII<>(
                                problemList.get(i).getNumberOfObjectives() > 2 ? 150 : 100, 30, problemList.get(i).getNumberOfObjectives()
                        ))
                        .setEvaluator(new SequentialSolutionListEvaluator<>())
                        .setDeltaDivision(2)
                        .setSelectionPressure(4)
                        .setEliminatePressure(2)
                        .setWeightMax(0.9)
                        .setWeightMin(0.4)
                        .setSwarmSize(problemList.get(i).getNumberOfObjectives() > 2 ? 150 : 100)
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 50000 : 30000)
                        .setMutation(new RandomizeMutation(0.5))
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEAD_PCA/data/agMOPSO/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();

                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "agMOPSO", problemList.get(i), run));
            }

            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEAD)
                        .setCrossover(new DifferentialEvolutionCrossover(0.9, 0.5, "rand/1/bin"))
                        .setMutation(new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 50000 : 30000)
                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 153 : 100)
                        .setResultPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 153 : 100)
                        .setNeighborhoodSelectionProbability(0.9)
                        .setMaximumNumberOfReplacedSolutions(2)
                        .setNeighborSize(20)
                        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("MOEAD_Weights")
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEAD_PCA/data/MOEAD/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEAD", problemList.get(i), run));
            }


            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEAD_PCA)
                        .setCrossover(new DifferentialEvolutionCrossover(0.9, 0.5, "best/1/diff"))
                        .setMutation(new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 50000 : 30000)
                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 153 : 100)
                        .setResultPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 153 : 100)
                        .setNeighborhoodSelectionProbability(0.9)
                        .setMaximumNumberOfReplacedSolutions(2)
                        .setNeighborSize(20)
                        .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
                        .setDataDirectory("MOEAD_Weights")
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEAD_PCA/data/MOEAD_PCA/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEAD_PCA", problemList.get(i), run));
            }
        }
        return algorithms;
    }
}
