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
import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.UF.UF1;
import org.uma.jmetal.problem.multiobjective.UF.UF2;
import org.uma.jmetal.problem.multiobjective.UF.UF3;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
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
public class MOEADASStudy {
    private static final int INDEPENDENT_RUNS =3;   //每个算法跑20次实验m
    public static final String experimentBaseDirectory = "F:\\Experiment Data-Modify";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /*
        if (args.length != 1) {
            throw new JMetalException("MissperimentData PBI/MOEADStudy/data/MOEADDRA/LZ09F5/IGDPlus does NOT exist. [org.uma.jmetal.util.experiment.component.ComputeQualityIndicators resetFileing argument: experiment base directory");
        }
        String experimentBaseDirectory = args[0];
        */

        List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(
               // new ZDT1(), new ZDT2(),new ZDT3(),new ZDT4(), new ZDT6()
               // new DTLZ1(),new DTLZ2(), new DTLZ3(),new DTLZ4(),new DTLZ5(),new DTLZ6() new DTLZ7()
               // new UF1(),new UF2(),new UF3(),new UF4("Real",30),new UF5(30, 10, 0.1),new UF6(30, 2, 0.1),new UF7(),new UF8(30),
                //new UF9(30, 0.1),
              //  new UF10(30)
               // new WFG1(2,4,2),new WFG2(2,4,2),new WFG3(2,4,2),new WFG4(2,4,2),new WFG5(2,4,2),new WFG6(2,4,2),new WFG7(2,4,2),new WFG8(2,4,2),new WFG9(2,4,2)
              //  new LZ09F1(),new LZ09F2(),new LZ09F3(),new LZ09F4(),new LZ09F5(), new LZ09F6(),new LZ09F7(),new LZ09F8(),new LZ09F9()
             //  new UF6(30, 2, 0.1),new UF7(),new UF1(),new UF3(),new LZ09F3(),new LZ09F9(),new WFG2(2,4,2)
                new UF1(),new UF2(),new UF3()
        );

        List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList, INDEPENDENT_RUNS);

        List<String> referenceFrontFileNames = Arrays.asList(
               // "ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf"
               // "DTLZ1.3D.pf","DTLZ2.3D.pf","DTLZ3.3D.pf","DTLZ4.3D.pf", "DTLZ6.3D.pf", "DTLZ7.3D.pf",
                // "UF1.pf","UF2.pf","UF3.pf","UF4.pf","UF5.pf","UF6.pf","UF7.pf","UF8.pf","UF9.pf", "UF10.pf"
               // "WFG1.2D.pf","WFG2.2D.pf","WFG3.2D.pf","WFG4.2D.pf","WFG5.2D.pf","WFG6.2D.pf","WFG7.2D.pf","WFG8.2D.pf","WFG9.2D.pf"
               // "LZ09_F1.pf","LZ09_F2.pf","LZ09_F3.pf","LZ09_F4.pf","LZ09_F5.pf",
                 //"LZ09_F6.pf","LZ09_F7.pf","LZ09_F8.pf","LZ09_F9.pf"
               // "UF6.pf","UF7.pf","UF1.pf","UF3.pf","LZ09_F3.pf","LZ09_F9.pf","WFG2.2D.pf"
               // "UF6.pf","UF3.pf","UF4.pf", "WFG1.2D.pf"
                "UF1.pf","UF2.pf","UF3.pf"

        );

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
                                new Spread<DoubleSolution>(),
                               // new InvertedGenerationalDistancePlus<DoubleSolution>(),
                                new InvertedGenerationalDistance<DoubleSolution>()
                        ))

                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(8)
                        .build();
        new ExecuteAlgorithms<>(experiment).run();

       new ComputeQualityIndicators<>(experiment).run();
       // new GenerateLatexTablesWithStatistics(experiment).run();
       // new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        //new GenerateFriedmanTestTables<>(experiment).run();
       // new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();

        System.out.println("------------All Experiments Copmleted!!!!!!!!----------");
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

        MOEAD.FunctionType functionType = AbstractMOEAD.FunctionType.TCHE;

        for (int run = 0; run < independentRuns; run++) {

            //MOEA/D-AKM初始化
            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEADAS)
                        .setCrossover(new DifferentialEvolutionCrossover(0.9, 0.5, "rand/1/bin"))
                        .setMutation(new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 300000 : 100000)
                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setResultPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setNeighborhoodSelectionProbability(0.9)
                        .setMaximumNumberOfReplacedSolutions(30)
                        .setNeighborSize(30)
                        .setRun(run)
                        .setFunctionType(functionType)
                        .setDataDirectory("MOEAD_Weights")
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEADStudy/data/MOEADAS/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEADAS", problemList.get(i), run));
            }

            //MOEA/D-Original 初始化
            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEAD)
                        .setCrossover(new DifferentialEvolutionCrossover(0.9, 0.5, "rand/1/bin"))
                        .setMutation(new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 300000 : 100000)
                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setResultPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setNeighborhoodSelectionProbability(0.9)
                        .setMaximumNumberOfReplacedSolutions(30)
                        .setNeighborSize(30)
                        .setFunctionType(functionType)
                        .setDataDirectory("MOEAD_Weights")
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEADStudy/data/MOEAD/"
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
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 300000 : 100000)
                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setResultPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setNeighborhoodSelectionProbability(0.9)
                        .setMaximumNumberOfReplacedSolutions(30)
                        .setNeighborSize(30)
                        .setFunctionType(functionType)
                        .setDataDirectory("MOEAD_Weights")
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEADStudy/data/MOEADSTM/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEADSTM", problemList.get(i), run));
            }

            //MOEA/D-DRA初始化
            for (int i = 0; i < problemList.size(); i++) {
                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEADDRA)
                        .setCrossover(new DifferentialEvolutionCrossover(0.9, 0.5, "rand/1/bin"))
                        .setMutation(new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 300000 : 100000)
                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setResultPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 300 : 100)
                        .setNeighborhoodSelectionProbability(0.9)
                        .setMaximumNumberOfReplacedSolutions(30)
                        .setNeighborSize(30)
                        .setFunctionType(functionType)
                        .setDataDirectory("MOEAD_Weights")
                        .setInProcessDataPath(experimentBaseDirectory
                                + "/MOEADStudy/data/MOEADDRA/"
                                + problemList.get(i).getName()
                                + "/INPROCESSDATA"
                                + run
                                + "/")
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEADDRA", problemList.get(i), run));
            }





//            //MOEA/D-KMC初始化
//            for (int i = 0; i < problemList.size(); i++) {
//                Algorithm<List<DoubleSolution>> algorithm = new MOEADBuilder(problemList.get(i), MOEADBuilder.Variant.MOEADKMC)
//                        .setCrossover(new DifferentialEvolutionCrossover(0.9, 0.5, "rand/1/bin"))
//                        .setMutation(new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
//                        .setMaxEvaluations(problemList.get(i).getNumberOfObjectives() > 2 ? 50000 : 30000)
//                        .setPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 150 : 100)
//                        .setResultPopulationSize(problemList.get(i).getNumberOfObjectives() > 2 ? 150 : 100)
//                        .setNeighborhoodSelectionProbability(0.9)
//                        .setMaximumNumberOfReplacedSolutions(2)
//                        .setNeighborSize(2)
//                        .setRun(run)
//                        .setFunctionType(functionType)
//                        .setDataDirectory("MOEAD_Weights")
//                        .setInProcessDataPath(experimentBaseDirectory
//                                + "/MOEADStudy/data/MOEADKMC/"
//                                + problemList.get(i).getName()
//                                + "/INPROCESSDATA"
//                                + run
//                                + "/")
//                        .build();
//                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEADKMC", problemList.get(i), run));
//            }




        }
        return algorithms;
    }
}