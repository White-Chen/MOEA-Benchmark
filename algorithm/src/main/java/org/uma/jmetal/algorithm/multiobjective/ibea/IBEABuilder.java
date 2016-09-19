//  IBEA.java
//
//  Author:
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Juan J. Durillo
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

// This implementation is based on the PISA code:
// http://www.tik.ee.ethz.ch/sop/pisa/selectors/ibea/?page=ibea.php

package org.uma.jmetal.algorithm.multiobjective.ibea;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;

import java.util.List;

/**
 * This class implements the IBEA algorithm
 */
public class IBEABuilder implements AlgorithmBuilder<IBEA<DoubleSolution>> {
    private Problem<DoubleSolution> problem;
    private int populationSize;
    private int archiveSize;
    private int maxEvaluations;

    private CrossoverOperator<DoubleSolution> crossover;
    private MutationOperator<DoubleSolution> mutation;
    private SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    /**
     * Constructor
     *
     * @param problem
     */
    public IBEABuilder(Problem<DoubleSolution> problem) {
        this.problem = problem;
        populationSize = 100;
        archiveSize = 100;
        maxEvaluations = 25000;

        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        selection = new BinaryTournamentSelection<DoubleSolution>();
    }

    /* Getters */
    public int getPopulationSize() {
        return populationSize;
    }

    /* Setters */
    public IBEABuilder setPopulationSize(int populationSize) {
        this.populationSize = populationSize;

        return this;
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public IBEABuilder setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;

        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public IBEABuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public CrossoverOperator<DoubleSolution> getCrossover() {
        return crossover;
    }

    public IBEABuilder setCrossover(CrossoverOperator<DoubleSolution> crossover) {
        this.crossover = crossover;

        return this;
    }

    public MutationOperator<DoubleSolution> getMutation() {
        return mutation;
    }

    public IBEABuilder setMutation(MutationOperator<DoubleSolution> mutation) {
        this.mutation = mutation;

        return this;
    }

    public SelectionOperator<List<DoubleSolution>, DoubleSolution> getSelection() {
        return selection;
    }

    public IBEABuilder setSelection(SelectionOperator<List<DoubleSolution>, DoubleSolution> selection) {
        this.selection = selection;

        return this;
    }

    public IBEA<DoubleSolution> build() {
        return new IBEA<DoubleSolution>(problem, populationSize, archiveSize, maxEvaluations, selection, crossover,
                mutation);
    }
}
