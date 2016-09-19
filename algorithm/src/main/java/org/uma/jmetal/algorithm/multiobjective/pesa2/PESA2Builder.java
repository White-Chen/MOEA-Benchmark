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

package org.uma.jmetal.algorithm.multiobjective.pesa2;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Created by Antonio J. Nebro
 */
public class PESA2Builder<S extends Solution<?>> implements AlgorithmBuilder<PESA2<S>> {
    private final Problem<S> problem;
    private int maxEvaluations;
    private int archiveSize;
    private int populationSize;
    private int biSections;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SolutionListEvaluator<S> evaluator;
    private String varInProcessPath;
    private String funInProcessPath;

    /**
     * Constructor
     */
    public PESA2Builder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
                        MutationOperator<S> mutationOperator) {
        this.problem = problem;
        maxEvaluations = 250;
        populationSize = 100;
        archiveSize = 100;
        biSections = 5;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;

        evaluator = new SequentialSolutionListEvaluator<S>();
    }

    public PESA2Builder<S> setBisections(int biSections) {
        if (biSections < 0) {
            throw new JMetalException("biSections is negative: " + maxEvaluations);
        }
        this.biSections = biSections;

        return this;
    }

    public PESA2<S> build() {
        PESA2<S> algorithm;
        algorithm = new PESA2<S>(problem, maxEvaluations, populationSize, archiveSize, biSections,
                crossoverOperator, mutationOperator, evaluator, varInProcessPath, funInProcessPath);

        return algorithm;
    }

    /* Getters */
    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public PESA2Builder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
        }
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public PESA2Builder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        }

        this.populationSize = populationSize;

        return this;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public SolutionListEvaluator<S> getSolutionListEvaluator() {
        return evaluator;
    }

    public PESA2Builder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("evaluator is null");
        }
        this.evaluator = evaluator;

        return this;
    }

    public int getBiSections() {
        return biSections;
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public PESA2Builder<S> setArchiveSize(int archiveSize) {
        if (archiveSize < 0) {
            throw new JMetalException("archiveSize is negative: " + maxEvaluations);
        }
        this.archiveSize = archiveSize;

        return this;
    }

    public String getVarInProcessPath() {
        return varInProcessPath;
    }

    public PESA2Builder setVarInProcessPath(String varInProcessPath) {
        this.varInProcessPath = varInProcessPath;
        return this;
    }

    public String getFunInProcessPath() {
        return funInProcessPath;
    }

    public PESA2Builder setFunInProcessPath(String funInProcessPath) {
        this.funInProcessPath = funInProcessPath;
        return this;
    }
}
