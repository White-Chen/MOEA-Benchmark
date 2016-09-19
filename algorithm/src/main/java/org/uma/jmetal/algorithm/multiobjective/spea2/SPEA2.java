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

package org.uma.jmetal.algorithm.multiobjective.spea2;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.spea2.util.EnvironmentalSelection;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.solutionattribute.impl.StrengthRawFitness;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Juan J. Durillo
 **/
public class SPEA2<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
    private static final long serialVersionUID = 6985049169940950771L;
    protected final int maxIterations;
    protected final SolutionListEvaluator<S> evaluator;
    private final StrengthRawFitness<S> strenghtRawFitness = new StrengthRawFitness<S>();
    private final EnvironmentalSelection<S> environmentalSelection;
    protected int iterations;
    protected List<S> archive;
    private String inProcessDataPath;

    public SPEA2(Problem<S> problem, int maxIterations, int populationSize,
                 CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                 SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator,
                 String inProcessDataPath) {
        super(problem);
        this.maxIterations = maxIterations;
        this.setMaxPopulationSize(populationSize);

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;
        this.environmentalSelection = new EnvironmentalSelection<S>(populationSize);

        this.archive = new ArrayList<>(populationSize);

        this.evaluator = evaluator;

        this.inProcessDataPath = inProcessDataPath;
    }

    @Override
    protected void saveDataInProcess() {
        if (!inProcessDataPath.isEmpty() && ((iterations % 10 == 0) || iterations == 2)) {
            new SolutionListOutput(getResult())
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/VAR" + iterations + ".tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/FUN" + iterations + ".tsv"))
                    .print();
        }
    }

    @Override
    protected void initProgress() {
        iterations = 1;
    }

    @Override
    protected void updateProgress() {
        iterations++;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return iterations >= maxIterations;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        population = evaluator.evaluate(population, getProblem());
        return population;
    }

    @Override
    protected List<S> selection(List<S> population) {
        List<S> union = new ArrayList<>(2 * getMaxPopulationSize());
        union.addAll(archive);
        union.addAll(population);
        strenghtRawFitness.computeDensityEstimator(union);
        archive = environmentalSelection.execute(union);
        return archive;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> offSpringPopulation = new ArrayList<>(getMaxPopulationSize());

        while (offSpringPopulation.size() < getMaxPopulationSize()) {
            List<S> parents = new ArrayList<>(2);
            S candidateFirstParent = selectionOperator.execute(population);
            parents.add(candidateFirstParent);
            S candidateSecondParent;
            candidateSecondParent = selectionOperator.execute(population);
            parents.add(candidateSecondParent);

            List<S> offspring = crossoverOperator.execute(parents);
            mutationOperator.execute(offspring.get(0));
            offSpringPopulation.add(offspring.get(0));
        }
        return offSpringPopulation;
    }

    @Override
    protected List<S> replacement(List<S> population,
                                  List<S> offspringPopulation) {
        return offspringPopulation;
    }

    @Override
    public List<S> getResult() {
        return archive;
    }

    @Override
    public String getName() {
        return "SPEA2";
    }

    @Override
    public String getDescription() {
        return "Strength Pareto. Evolutionary Algorithm";
    }
}
