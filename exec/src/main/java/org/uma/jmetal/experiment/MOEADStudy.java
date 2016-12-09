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

package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT problems with four versions of NSGA-II, each
 * of them applying a different crossover probability (from 0.7 to 1.0).
 * <p>
 * This experiment assumes that the reference Pareto front are known, so the names of files containing
 * them and the directory where they are located must be specified.
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
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADStudy {
    private static final int INDEPENDENT_RUNS = 30;   //每个算法跑30次实验
    public static final String experimentBaseDirectory = "F:/Experiment Data/";

    public static void main(String[] args) throws IOException {
        /*
        if (args.length != 1) {
            throw new JMetalException("Missing argument: experiment base directory");
        }
        String experimentBaseDirectory = args[0];
        */

        List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(new ZDT1(), new ZDT2(),
                new ZDT3(), new ZDT4(), new ZDT6());

        List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList, INDEPENDENT_RUNS);

        List<String> referenceFrontFileNames = Arrays.asList("ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf");

        Experiment<DoubleSolution, List<DoubleSolution>> experiment =
                new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("MOEADStudy")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setExperimentBaseDirectory(experimentBaseDirectory)
                        .setOutputParetoFrontFileName("FUN")
                        .setOutputParetoSetFileName("VAR")
                        .setReferenceFrontDirectory("/pareto_fronts")
                        .setReferenceFrontFileNames(referenceFrontFileNames)
                        .setIndicatorList(Arrays.asList(
                                new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
                                new PISAHypervolume<DoubleSolution>(),
                                new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()))
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(8)
                        .build();

        new ExecuteAlgorithms<>(experiment).run();
        new ComputeQualityIndicators<>(experiment).run();
        new GenerateLatexTablesWithStatistics(experiment).run();
        new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        new GenerateFriedmanTestTables<>(experiment).run();
        new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();

        System.out.println("------------All Experiments Completed!!!!!!!!----------");
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

        for (int run = 0; run < independentRuns; run++) {

            //MOEA/D-Original 初始化
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
                                + "/MOEAD/data/MOEAD-original/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEAD", problemList.get(i), run));
            }

            //MOEA/D-STM初始化
            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEADSTM)
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
                                + "/MOEAD/data/MOEAD-STM/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEADSTM", problemList.get(i), run));
            }


            //MOEA/D-KM初始化
            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEADKM)
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
                                + "/MOEAD/data/MOEAD-KM/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEADKM", problemList.get(i), run));
            }



        }
        return algorithms;
    }
}