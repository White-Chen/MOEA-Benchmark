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

package org.uma.jmetal.algorithm.multiobjective.paes;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PAESBuilder<S extends Solution<?>> implements AlgorithmBuilder<PAES<S>> {
    private Problem<S> problem;

    private int archiveSize;
    private int maxEvaluations;
    private int biSections;

    private MutationOperator<S> mutationOperator;
    private String varInProcessPath;
    private String funInProcessPath;

    public PAESBuilder(Problem<S> problem) {
        this.problem = problem;
    }

    public PAES<S> build() {
        return new PAES<S>(problem, archiveSize, maxEvaluations, biSections, mutationOperator,
                varInProcessPath, funInProcessPath);
    }

    /*
     * Getters
     */
    public Problem<S> getProblem() {
        return problem;
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public PAESBuilder<S> setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;

        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public PAESBuilder<S> setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public int getBiSections() {
        return biSections;
    }

    public PAESBuilder<S> setBiSections(int biSections) {
        this.biSections = biSections;

        return this;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public PAESBuilder<S> setMutationOperator(MutationOperator<S> mutation) {
        mutationOperator = mutation;

        return this;
    }

    public String getVarInProcessPath() {
        return varInProcessPath;
    }

    public PAESBuilder setVarInProcessPath(String varInProcessPath) {
        this.varInProcessPath = varInProcessPath;
        return this;
    }

    public String getFunInProcessPath() {
        return funInProcessPath;
    }

    public PAESBuilder setFunInProcessPath(String funInProcessPath) {
        this.funInProcessPath = funInProcessPath;
        return this;
    }
}
