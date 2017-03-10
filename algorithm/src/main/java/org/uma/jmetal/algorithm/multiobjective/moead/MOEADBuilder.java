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

package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Builder class for algorithm MOEA/D and variants
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class MOEADBuilder implements AlgorithmBuilder<AbstractMOEAD<DoubleSolution>> {
    protected Problem<DoubleSolution> problem;

    /**
     * T in Zhang & Li paper
     */
    protected int neighborSize;
    /**
     * Delta in Zhang & Li paper
     */
    protected double neighborhoodSelectionProbability;
    /**
     * nr in Zhang & Li paper
     */
    protected int maximumNumberOfReplacedSolutions;
    protected MOEAD.FunctionType functionType;
    protected CrossoverOperator crossover;
    protected MutationOperator mutation;
    protected String dataDirectory;
    protected int populationSize;
    protected int resultPopulationSize;
    protected int maxEvaluations;
    protected int numberOfThreads;
    protected Variant moeadVariant;
    protected String inProcessDataPath;

    /**
     * KM in dyy
     */
    protected int run;

    /**
     * Constructor
     */
    public MOEADBuilder(Problem<DoubleSolution> problem, Variant variant) {
        this.problem = problem;
        populationSize = 300;
        resultPopulationSize = 300;
        maxEvaluations = 150000;
        crossover = new DifferentialEvolutionCrossover();
        mutation = new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
        functionType = MOEAD.FunctionType.TCHE;
        neighborhoodSelectionProbability = 0.1;
        maximumNumberOfReplacedSolutions = 2;
        dataDirectory = "";
        neighborSize = 20;
        numberOfThreads = 1;
        moeadVariant = variant;
    }

    public MOEADBuilder setRun(int run) {
        this.run = run;

        return this;
    }

    /* Getters/Setters */
    public int getNeighborSize() {
        return neighborSize;
    }

    public MOEADBuilder setNeighborSize(int neighborSize) {
        this.neighborSize = neighborSize;

        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public MOEADBuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public MOEADBuilder setPopulationSize(int populationSize) {
        this.populationSize = populationSize;

        return this;
    }

    public int getResultPopulationSize() {
        return resultPopulationSize;
    }

    public MOEADBuilder setResultPopulationSize(int resultPopulationSize) {
        this.resultPopulationSize = resultPopulationSize;

        return this;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public MOEADBuilder setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        return this;
    }

    public MutationOperator<DoubleSolution> getMutation() {
        return mutation;
    }

    public MOEADBuilder setMutation(MutationOperator<DoubleSolution> mutation) {
        this.mutation = mutation;

        return this;
    }

    public CrossoverOperator<DoubleSolution> getCrossover() {
        return crossover;
    }

    public MOEADBuilder setCrossover(CrossoverOperator<DoubleSolution> crossover) {
        this.crossover = crossover;

        return this;
    }

    public MOEAD.FunctionType getFunctionType() {
        return functionType;
    }

    public MOEADBuilder setFunctionType(MOEAD.FunctionType functionType) {
        this.functionType = functionType;

        return this;
    }

    public int getMaximumNumberOfReplacedSolutions() {
        return maximumNumberOfReplacedSolutions;
    }

    public MOEADBuilder setMaximumNumberOfReplacedSolutions(int maximumNumberOfReplacedSolutions) {
        this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;

        return this;
    }

    public double getNeighborhoodSelectionProbability() {
        return neighborhoodSelectionProbability;
    }

    public MOEADBuilder setNeighborhoodSelectionProbability(double neighborhoodSelectionProbability) {
        this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;

        return this;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public MOEADBuilder setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;

        return this;
    }

    public String getInProcessDataPath() {
        return inProcessDataPath;
    }

    public MOEADBuilder setInProcessDataPath(String inProcessDataPath) {
        this.inProcessDataPath = inProcessDataPath;
        return this;
    }

    public AbstractMOEAD<DoubleSolution> build() {
        AbstractMOEAD<DoubleSolution> algorithm = null;
        if (moeadVariant.equals(Variant.MOEAD)) {
            algorithm = new MOEAD(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                    crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions, neighborSize, inProcessDataPath);
        } else if (moeadVariant.equals(Variant.ConstraintMOEAD)) {
            algorithm = new ConstraintMOEAD(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                    crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions, neighborSize, inProcessDataPath);
        } else if (moeadVariant.equals(Variant.MOEADDRA)) {
            algorithm = new MOEADDRA(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                    crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions, neighborSize, inProcessDataPath);
        } else if (moeadVariant.equals(Variant.MOEADSTM)) {
            algorithm = new MOEADSTM(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                    crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions, neighborSize, inProcessDataPath);
        }else if (moeadVariant.equals(Variant.MOEADAKM)) {
            algorithm = new MOEADAKM(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                    crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions, neighborSize, inProcessDataPath ,run);
        }else if (moeadVariant.equals(Variant.MOEADKMC)) {
            algorithm = new MOEADKMC(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                    crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions, neighborSize, inProcessDataPath, run);
        }else if (moeadVariant.equals(Variant.MOEADTest)) {
            algorithm = new MOEADTest(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
                    crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
                    maximumNumberOfReplacedSolutions, neighborSize, inProcessDataPath );
        }

        return algorithm;
    }

    public enum Variant {MOEAD, ConstraintMOEAD, MOEADDRA, MOEADSTM,MOEADAKM,MOEADKMC ,MOEADTest}
}
