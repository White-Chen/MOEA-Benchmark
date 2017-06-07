package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;


/**
 * Builder class
 */
public class NSGAIIIBuilder<S extends Solution<?>> implements AlgorithmBuilder<NSGAIII<S>> {

    // no access modifier means access from classes within the same package
    private Problem<S> problem;
    private int maxIterations;
    private int populationSize;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;

    private SolutionListEvaluator<S> evaluator;
    private String varInProcessPath;
    private String funInProcessPath;
    protected String inProcessDataPath;

    /**
     * Builder constructor
     */
    public NSGAIIIBuilder(Problem<S> problem) {
        this.problem = problem;
        maxIterations = 250;
        populationSize = 100;
        evaluator = new SequentialSolutionListEvaluator<S>();
        varInProcessPath = "";
        funInProcessPath = "";
    }

    public NSGAIIIBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        this.evaluator = evaluator;

        return this;
    }

    public SolutionListEvaluator<S> getEvaluator() {
        return evaluator;
    }

    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public NSGAIIIBuilder<S> setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;

        return this;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public NSGAIIIBuilder<S> setPopulationSize(int populationSize) {
        this.populationSize = populationSize;

        return this;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public NSGAIIIBuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;

        return this;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public NSGAIIIBuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;

        return this;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    public NSGAIIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        this.selectionOperator = selectionOperator;

        return this;
    }

    public String getVarInProcessPath() {
        return varInProcessPath;
    }

    public NSGAIIIBuilder setVarInProcessPath(String varInProcessPath) {
        this.varInProcessPath = varInProcessPath;
        return this;
    }

    public String getInProcessDataPath() {
        return inProcessDataPath;
    }

    public NSGAIIIBuilder setInProcessDataPath(String inProcessDataPath) {
        this.inProcessDataPath = inProcessDataPath;
        return this;
    }

    public String getFunInProcessPath() {
        return funInProcessPath;
    }

    public NSGAIIIBuilder setFunInProcessPath(String funInProcessPath) {
        this.funInProcessPath = funInProcessPath;
        return this;
    }

    public NSGAIII<S> build() {
        return new NSGAIII<>(this);
    }
}
