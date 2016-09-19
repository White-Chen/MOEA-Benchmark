package org.uma.jmetal.algorithm.multiobjective.admopso;


import org.uma.jmetal.algorithm.multiobjective.admopso.util.AdMOPSOSelection;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.mutation.RandomizeMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.Comparator;

/**
 * Created by ChenZhe on 2015/11/3.
 * <p>
 * Class for building the AdMOPSO algorithm
 */
public class AdMOPSOBuilder implements AlgorithmBuilder<AdMOPSO> {


    private double r1Max;
    private double r1Min;
    private double r2Max;
    private double r2Min;
    private double weightMax;
    private double weightMin;
    private int swarmSize;
    private int archiveSize;
    private int maxEvaluations;
    private int divisionNumber;
    private int selectionPressure;
    private int eliminatePressure;
    private int deltaDivision;
    private double mutationRate;

    private DoubleProblem problem;
    private AdaptiveGridArchiveII<DoubleSolution> leadersArchive;
    private MutationOperator<DoubleSolution> mutation;
    private SolutionListEvaluator<DoubleSolution> evaluator;
    private AdMOPSOSelection<DoubleSolution> selector;
    private Comparator<DoubleSolution> dominanceComparator;
    private String inProcessDataPath;

    public AdMOPSOBuilder(DoubleProblem problem, AdaptiveGridArchiveII<DoubleSolution> leadersArchive) {
        this.problem = problem;
        this.leadersArchive = leadersArchive;

        swarmSize = 100;
        maxEvaluations = 30000;
        r1Max = 1.0;
        r2Max = 1.0;
        r1Min = 0.0;
        r2Min = 0.0;
        weightMax = 0.9;
        weightMin = 0.4;
        selectionPressure = 4;
        eliminatePressure = 2;
        divisionNumber = 30;
        deltaDivision = 10;
        mutationRate = 0.5;
        mutation = new RandomizeMutation(0.5);
        evaluator = new SequentialSolutionListEvaluator<>();
        selector = new AdMOPSOSelection<>();
        dominanceComparator = new DominanceComparator<>();
        inProcessDataPath = "";

    }

    @Override
    public AdMOPSO build() {
        return new AdMOPSO(problem, leadersArchive, evaluator, mutation, selector, dominanceComparator,
                swarmSize, archiveSize, maxEvaluations,
                r1Max, r1Min, r2Max, r2Min, weightMax, weightMin,
                eliminatePressure, selectionPressure, divisionNumber, deltaDivision, mutationRate,
                inProcessDataPath);
    }

    /*setters*/

    /*getters*/
    public double getR1Max() {
        return r1Max;
    }

    public AdMOPSOBuilder setR1Max(double r1Max) {
        this.r1Max = r1Max;
        return this;
    }

    public double getR1Min() {
        return r1Min;
    }

    public AdMOPSOBuilder setR1Min(double r1Min) {
        this.r1Min = r1Min;
        return this;
    }

    public double getWeightMin() {
        return weightMin;
    }

    public AdMOPSOBuilder setWeightMin(double weightMin) {
        this.weightMin = weightMin;
        return this;
    }

    public double getR2Max() {
        return r2Max;
    }

    public AdMOPSOBuilder setR2Max(double r2Max) {
        this.r2Max = r2Max;
        return this;
    }

    public double getWeightMax() {
        return weightMax;
    }

    public AdMOPSOBuilder setWeightMax(double weightMax) {
        this.weightMax = weightMax;
        return this;
    }

    public double getR2Min() {
        return r2Min;
    }

    public AdMOPSOBuilder setR2Min(double r2Min) {
        this.r2Min = r2Min;
        return this;
    }

    public int getSwarmSize() {
        return swarmSize;
    }

    public AdMOPSOBuilder setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;
        return this;
    }

    public int getArchiveSize() {
        return archiveSize;
    }

    public AdMOPSOBuilder setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;
        return this;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public AdMOPSOBuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public DoubleProblem getProblem() {
        return problem;
    }

    public AdMOPSOBuilder setProblem(DoubleProblem problem) {
        this.problem = problem;
        return this;
    }

    public MutationOperator<DoubleSolution> getMutation() {
        return mutation;
    }

    public AdMOPSOBuilder setMutation(MutationOperator<DoubleSolution> mutation) {
        this.mutation = mutation;
        return this;
    }

    public Archive<DoubleSolution> getLeadersArchive() {
        return leadersArchive;
    }

    public AdMOPSOBuilder setLeadersArchive(AdaptiveGridArchiveII<DoubleSolution> leadersArchive) {
        this.leadersArchive = leadersArchive;
        return this;
    }

    public SolutionListEvaluator<DoubleSolution> getEvaluator() {
        return evaluator;
    }

    public AdMOPSOBuilder setEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    public SelectionOperator<AdaptiveGridArchiveII<DoubleSolution>, DoubleSolution> getSelector() {
        return selector;
    }

    public AdMOPSOBuilder setSelector(AdMOPSOSelection<DoubleSolution> selector) {
        this.selector = selector;
        return this;
    }

    public int getDivisionNumber() {
        return divisionNumber;
    }

    public AdMOPSOBuilder setDivisionNumber(int divisionNumber) {
        this.divisionNumber = divisionNumber;
        return this;
    }

    public int getSelectionPressure() {
        return selectionPressure;
    }

    public AdMOPSOBuilder setSelectionPressure(int selectionPressure) {
        this.selectionPressure = selectionPressure;
        return this;
    }

    public int getEliminatePressure() {
        return eliminatePressure;
    }

    public AdMOPSOBuilder setEliminatePressure(int eliminatePressure) {
        this.eliminatePressure = eliminatePressure;
        return this;
    }

    public Comparator<DoubleSolution> getDominanceComparator() {
        return dominanceComparator;
    }

    public AdMOPSOBuilder setDominanceComparator(Comparator<DoubleSolution> dominanceComparator) {
        this.dominanceComparator = dominanceComparator;
        return this;
    }

    public int getDeltaDivision() {
        return deltaDivision;
    }

    public AdMOPSOBuilder setDeltaDivision(int deltaDivision) {
        this.deltaDivision = deltaDivision;
        return this;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public AdMOPSOBuilder setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
        return this;
    }

    public String getInProcessDataPath() {
        return inProcessDataPath;
    }

    public AdMOPSOBuilder setInProcessDataPath(String inProcessDataPath) {
        this.inProcessDataPath = inProcessDataPath;
        return this;
    }
}
