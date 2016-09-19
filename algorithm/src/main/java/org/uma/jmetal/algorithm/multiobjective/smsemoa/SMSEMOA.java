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

package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMSEMOA<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
    private static final long serialVersionUID = 2638444014883892878L;
    protected final int maxEvaluations;
    protected final double offset;

    protected int evaluations;

    private Hypervolume<S> hypervolume;
    private String varInProcessPath;
    private String funInProcessPath;

    /**
     * Constructor
     */
    public SMSEMOA(Problem<S> problem, int maxEvaluations, int populationSize, double offset,
                   CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                   SelectionOperator<List<S>, S> selectionOperator, Hypervolume<S> hypervolumeImplementation,
                   String varInProcessPath, String funInProcessPath) {
        super(problem);
        this.maxEvaluations = maxEvaluations;
        setMaxPopulationSize(populationSize);

        this.offset = offset;

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;

        this.hypervolume = hypervolumeImplementation;

        this.varInProcessPath = varInProcessPath;
        this.funInProcessPath = funInProcessPath;
    }

    @Override
    protected void saveDataInProcess() {
        if (!funInProcessPath.isEmpty() && !varInProcessPath.isEmpty() && ((evaluations % 10 == 0) || evaluations == 2)) {
            new SolutionListOutput(getResult())
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(varInProcessPath))
                    .setFunFileOutputContext(new DefaultFileOutputContext(funInProcessPath))
                    .print();
        }
    }

    @Override
    protected void initProgress() {
        evaluations = 1;
    }

    @Override
    protected void updateProgress() {
        evaluations++;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        for (S solution : population) {
            getProblem().evaluate(solution);
        }
        return population;
    }

    @Override
    protected List<S> selection(List<S> population) {
        List<S> matingPopulation = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            S solution = selectionOperator.execute(population);
            matingPopulation.add(solution);
        }

        return matingPopulation;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> offspringPopulation = new ArrayList<>(1);

        List<S> parents = new ArrayList<>(2);
        parents.add(population.get(0));
        parents.add(population.get(1));

        List<S> offspring = crossoverOperator.execute(parents);

        mutationOperator.execute(offspring.get(0));

        offspringPopulation.add(offspring.get(0));
        return offspringPopulation;
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);

        Ranking<S> ranking = computeRanking(jointPopulation);
        List<S> lastSubfront = ranking.getSubfront(ranking.getNumberOfSubfronts() - 1);

        lastSubfront = hypervolume.computeHypervolumeContribution(lastSubfront, jointPopulation);

        List<S> resultPopulation = new ArrayList<>();
        for (int i = 0; i < ranking.getNumberOfSubfronts() - 1; i++) {
            for (S solution : ranking.getSubfront(i)) {
                resultPopulation.add(solution);
            }
        }

        for (int i = 0; i < lastSubfront.size() - 1; i++) {
            resultPopulation.add(lastSubfront.get(i));
        }

        return resultPopulation;
    }

    @Override
    public List<S> getResult() {
        return getPopulation();
    }

    protected Ranking<S> computeRanking(List<S> solutionList) {
        Ranking<S> ranking = new DominanceRanking<S>();
        ranking.computeRanking(solutionList);

        return ranking;
    }

    @Override
    public String getName() {
        return "SMSEMOA";
    }

    @Override
    public String getDescription() {
        return "S metric selection EMOA";
    }
}
