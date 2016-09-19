//  DifferentialEvolution.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import org.uma.jmetal.algorithm.impl.AbstractDifferentialEvolution;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.*;

/**
 * This class implements a differential evolution algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DifferentialEvolution extends AbstractDifferentialEvolution<DoubleSolution> {
    private static final long serialVersionUID = -1539643504570176803L;
    protected String funInProcessPath;
    protected String varInProcessPath;
    private int populationSize;
    private int maxEvaluations;
    private SolutionListEvaluator<DoubleSolution> evaluator;
    private Comparator<DoubleSolution> comparator;
    private int evaluations;

    /**
     * Constructor
     *
     * @param problem           Problem to solve
     * @param maxEvaluations    Maximum number of evaluations to perform
     * @param populationSize
     * @param crossoverOperator
     * @param selectionOperator
     * @param evaluator
     */
    public DifferentialEvolution(DoubleProblem problem, int maxEvaluations, int populationSize,
                                 DifferentialEvolutionCrossover crossoverOperator,
                                 DifferentialEvolutionSelection selectionOperator, SolutionListEvaluator<DoubleSolution> evaluator,
                                 String varInProcessPath, String funInProcessPath) {
        setProblem(problem);
        this.maxEvaluations = maxEvaluations;
        this.populationSize = populationSize;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = selectionOperator;
        this.evaluator = evaluator;
        this.varInProcessPath = varInProcessPath;
        this.funInProcessPath = funInProcessPath;

        comparator = new ObjectiveComparator<DoubleSolution>(0);
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    @Override
    protected void saveDataInProcess() {
        if (!funInProcessPath.isEmpty() && !varInProcessPath.isEmpty() && (evaluations % (10 * populationSize) == 0)) {
            new SolutionListOutput(Arrays.asList(getResult()))
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(varInProcessPath))
                    .setFunFileOutputContext(new DefaultFileOutputContext(funInProcessPath))
                    .print();
        }
    }

    @Override
    protected void initProgress() {
        evaluations = populationSize;
    }

    @Override
    protected void updateProgress() {
        evaluations += populationSize;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newIndividual = getProblem().createSolution();
            population.add(newIndividual);
        }
        return population;
    }

    @Override
    protected List<DoubleSolution> evaluatePopulation(List<DoubleSolution> population) {
        return evaluator.evaluate(population, getProblem());
    }

    @Override
    protected List<DoubleSolution> selection(List<DoubleSolution> population) {
        return population;
    }

    @Override
    protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
        List<DoubleSolution> offspringPopulation = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            selectionOperator.setIndex(i);
            List<DoubleSolution> parents = selectionOperator.execute(matingPopulation);

            crossoverOperator.setCurrentSolution(matingPopulation.get(i));
            List<DoubleSolution> children = crossoverOperator.execute(parents);

            offspringPopulation.add(children.get(0));
        }

        return offspringPopulation;
    }

    @Override
    protected List<DoubleSolution> replacement(List<DoubleSolution> population,
                                               List<DoubleSolution> offspringPopulation) {
        List<DoubleSolution> pop = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            if (comparator.compare(population.get(i), offspringPopulation.get(i)) < 0) {
                pop.add(population.get(i));
            } else {
                pop.add(offspringPopulation.get(i));
            }
        }

        Collections.sort(pop, comparator);
        return pop;
    }

    /**
     * Returns the best individual
     */
    @Override
    public DoubleSolution getResult() {
        Collections.sort(getPopulation(), comparator);

        return getPopulation().get(0);
    }

    @Override
    public String getName() {
        return "DE";
    }

    @Override
    public String getDescription() {
        return "Differential Evolution Algorithm";
    }
}
