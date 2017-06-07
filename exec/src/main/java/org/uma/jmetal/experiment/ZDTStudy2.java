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
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT problems with five algorithms: NSGAII, SPEA2, MOCell,
 * SMPSO and AbYSS
 * <p>
 * This experiment assumes that the reference Pareto front are not known, so the names of files containing
 * them and the directory where they are located must be specified.
 * <p>
 * Six quality indicators are used for performance assessment.
 * <p>
 * The steps to carry out the experiment are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts
 * 4. Compute que quality indicators
 * 5. Generate Latex tables reporting means and medians
 * 6. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 * 7. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ZDTStudy2 {
    private static final int INDEPENDENT_RUNS = 25;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new JMetalException("Needed arguments: experimentBaseDirectory referenceFrontDirectory");
        }
        String experimentBaseDirectory = args[0];
        String referenceFrontDirectory = args[1];

        List<Problem<DoubleSolution>> problemList = Arrays.asList(new ZDT1(), new ZDT2(),
                new ZDT3(), new ZDT4(), new ZDT6());

        List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList, INDEPENDENT_RUNS);

        Experiment<DoubleSolution, List<DoubleSolution>> experiment =
                new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZDT2Study")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setExperimentBaseDirectory(experimentBaseDirectory)
                        .setOutputParetoFrontFileName("FUN")
                        .setOutputParetoSetFileName("VAR")
                        .setReferenceFrontDirectory(referenceFrontDirectory)
                        .setIndicatorList(Arrays.asList(
                                new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
                                new PISAHypervolume<DoubleSolution>(),
                                new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()))
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(8)
                        .build();

        new ExecuteAlgorithms<>(experiment).run();
        new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
        new ComputeQualityIndicators<>(experiment).run();
        new GenerateLatexTablesWithStatistics(experiment).run();
        new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        new GenerateFriedmanTestTables<>(experiment).run();
        new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).setDisplayNotch().run();
    }

    /**
     * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
     * {@link TaggedAlgorithm}, which is a decorator for class {@link Algorithm}.
     *
     * @param problemList
     * @return
     */
    static List<TaggedAlgorithm<List<DoubleSolution>>> configureAlgorithmList(
            List<Problem<DoubleSolution>> problemList,
            int independentRuns) {
        List<TaggedAlgorithm<List<DoubleSolution>>> algorithms = new ArrayList<>();

        for (int run = 0; run < independentRuns; run++) {
            for (Problem<DoubleSolution> problem : problemList) {
                Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(1.0, 20.0),
                        new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problem, run));
            }

            for (Problem<DoubleSolution> problem : problemList) {
                Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(problem, new SBXCrossover(1.0, 10.0),
                        new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problem, run));
            }

            for (Problem<DoubleSolution> problem : problemList) {
                Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder((DoubleProblem) problem,
                        new CrowdingDistanceArchive<DoubleSolution>(100))
                        .build();
                algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problem, run));
            }
        }

        return algorithms;
    }
}
