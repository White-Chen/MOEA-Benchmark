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

package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionStrategy;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class implementing a (mu + lambda) Evolution Strategy (lambda must be divisible by mu)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NonElitistEvolutionStrategy<S extends Solution<?>> extends AbstractEvolutionStrategy<S, S> {
    private static final long serialVersionUID = 8440322669307204589L;
    private int mu;
    private int lambda;
    private int maxEvaluations;
    private int evaluations;
    private MutationOperator<S> mutation;

    private Comparator<S> comparator;

    /**
     * Constructor
     */
    public NonElitistEvolutionStrategy(Problem<S> problem, int mu, int lambda, int maxEvaluations,
                                       MutationOperator<S> mutation) {
        super(problem);
        this.mu = mu;
        this.lambda = lambda;
        this.maxEvaluations = maxEvaluations;
        this.mutation = mutation;

        comparator = new ObjectiveComparator<S>(0);
    }

    @Override
    protected void saveDataInProcess() {

    }

    @Override
    protected void initProgress() {
        evaluations = 1;
    }

    @Override
    protected void updateProgress() {
        evaluations += lambda;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<S> createInitialPopulation() {
        List<S> population = new ArrayList<>(mu);
        for (int i = 0; i < mu; i++) {
            S newIndividual = getProblem().createSolution();
            population.add(newIndividual);
        }

        return population;
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
        return population;
        //    List<Solution> matingPopulation = new ArrayList<>(mu) ;
        //    for (Solution solution: population) {
        //      matingPopulation.add(solution.copy()) ;
        //    }
        //    return matingPopulation ;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> offspringPopulation = new ArrayList<>(lambda);
        for (int i = 0; i < mu; i++) {
            for (int j = 0; j < lambda / mu; j++) {
                S offspring = (S) population.get(i).copy();
                mutation.execute(offspring);
                offspringPopulation.add(offspring);
            }
        }

        return offspringPopulation;
    }

    @Override
    protected List<S> replacement(List<S> population,
                                  List<S> offspringPopulation) {
        Collections.sort(offspringPopulation, comparator);

        List<S> newPopulation = new ArrayList<>(mu);
        for (int i = 0; i < mu; i++) {
            newPopulation.add(offspringPopulation.get(i));
        }
        return newPopulation;
    }

    @Override
    public S getResult() {
        return getPopulation().get(0);
    }

    @Override
    public String getName() {
        return "NonElitistEA";
    }

    @Override
    public String getDescription() {
        return "Non Elitist Evolution Strategy Algorithm, i.e, (mu , lambda) EA";
    }
}
