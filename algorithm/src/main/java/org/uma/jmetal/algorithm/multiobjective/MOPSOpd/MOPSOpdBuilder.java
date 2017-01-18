package org.uma.jmetal.algorithm.multiobjective.MOPSOpd;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;

/**
 * Created by ChenZhe on 4/28/2015.
 * @author Chenzhe <q953387601@163.com>
 */
public class MOPSOpdBuilder implements AlgorithmBuilder<MOPSOpd> {

    protected DoubleProblem problem;
    protected int swarmSize;
    protected int maxIterations;
    protected double r1Min;
    protected double r1Max;
    protected double r2Min;
    protected double r2Max;
    protected double c1Min;
    protected double c1Max;
    protected double c2Min;
    protected double c2Max;
    protected double weightMin;
    protected double weightMax;
    protected double changeVelocity1;
    protected double changeVelocity2;
    protected String functionType;
    protected String dataDirectory;
    protected int maxAge;
    protected String inProcessDataPath;
    protected AdaptiveGridArchiveII<DoubleSolution> leadersArchive;
    protected int eliminatePressure, selectionPressure, divisionNumber;

    public MOPSOpdBuilder(DoubleProblem problem) {
        this.problem = problem;
        this.swarmSize = 100;
        this.maxIterations = 300;

        this.r1Max = 0.0;
        this.r1Min = 0.1;
        this.r2Max = 0.0;
        this.r2Min = 1.0;
        this.c1Max = 1.5;
        this.c1Min = 2.5;
        this.c2Max = 1.5;
        this.c2Min = 2.5;
        this.weightMax = 0.5;
        this.weightMin = 0.7;
        this.changeVelocity1 = -1.0;
        this.changeVelocity2 = -1.0;
        this.functionType = "_TCHE";
        this.maxAge = 5;
        this.eliminatePressure = 2;
        this.selectionPressure = 4;
        this.divisionNumber = 30;
        this.dataDirectory = "MOEAD_Weights";
        this.leadersArchive.getGrid()
                .setDivisionNumber(divisionNumber)
                .setEliminatePressure(eliminatePressure)
                .setSelectionPressure(selectionPressure);
    }

    @Override
    public MOPSOpd build() {
        MOPSOpd algorithm = new MOPSOpd(this.problem, this.swarmSize,
                this.maxIterations, this.r1Min, this.r1Max,
                this.r2Min, this.r2Max, this.c1Min, this.c1Max, this.c2Min, this.c2Max,
                this.weightMin, this.weightMax, this.changeVelocity1, this.changeVelocity2,
                this.functionType, this.maxAge,
                this.dataDirectory, this.inProcessDataPath,
                this.leadersArchive,
                this.eliminatePressure, this.selectionPressure, this.divisionNumber);
        return algorithm;
    }

    public DoubleProblem getProblem() {
        return problem;
    }

    public MOPSOpdBuilder setProblem(DoubleProblem problem) {
        this.problem = problem;
        return this;
    }

    public int getSwarmSize() {
        return swarmSize;
    }

    public MOPSOpdBuilder setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;
        return this;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public MOPSOpdBuilder setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public double getR1Min() {
        return r1Min;
    }

    public MOPSOpdBuilder setR1Min(double r1Min) {
        this.r1Min = r1Min;
        return this;
    }

    public double getR1Max() {
        return r1Max;
    }

    public MOPSOpdBuilder setR1Max(double r1Max) {
        this.r1Max = r1Max;
        return this;
    }

    public double getR2Min() {
        return r2Min;
    }

    public MOPSOpdBuilder setR2Min(double r2Min) {
        this.r2Min = r2Min;
        return this;
    }

    public double getR2Max() {
        return r2Max;
    }

    public MOPSOpdBuilder setR2Max(double r2Max) {
        this.r2Max = r2Max;
        return this;
    }

    public double getC1Min() {
        return c1Min;
    }

    public MOPSOpdBuilder setC1Min(double c1Min) {
        this.c1Min = c1Min;
        return this;
    }

    public double getC1Max() {
        return c1Max;
    }

    public MOPSOpdBuilder setC1Max(double c1Max) {
        this.c1Max = c1Max;
        return this;
    }

    public double getC2Min() {
        return c2Min;
    }

    public MOPSOpdBuilder setC2Min(double c2Min) {
        this.c2Min = c2Min;
        return this;
    }

    public double getC2Max() {
        return c2Max;
    }

    public MOPSOpdBuilder setC2Max(double c2Max) {
        this.c2Max = c2Max;
        return this;
    }

    public double getWeightMin() {
        return weightMin;
    }

    public MOPSOpdBuilder setWeightMin(double weightMin) {
        this.weightMin = weightMin;
        return this;
    }

    public double getWeightMax() {
        return weightMax;
    }

    public AdaptiveGridArchiveII<DoubleSolution> getLeadersArchive() {
        return leadersArchive;
    }

    public MOPSOpdBuilder setLeadersArchive(AdaptiveGridArchiveII<DoubleSolution> leadersArchive) {
        this.leadersArchive = leadersArchive;
        return this;
    }

    public int getEliminatePressure() {
        return eliminatePressure;
    }

    public MOPSOpdBuilder setEliminatePressure(int eliminatePressure) {
        this.eliminatePressure = eliminatePressure;
        return this;
    }

    public int getSelectionPressure() {
        return selectionPressure;
    }

    public MOPSOpdBuilder setSelectionPressure(int selectionPressure) {
        this.selectionPressure = selectionPressure;
        return this;
    }

    public int getDivisionNumber() {
        return divisionNumber;
    }

    public MOPSOpdBuilder setDivisionNumber(int divisionNumber) {
        this.divisionNumber = divisionNumber;
        return this;
    }

    public MOPSOpdBuilder setWeightMax(double weightMax) {
        this.weightMax = weightMax;
        return this;
    }

    public double getChangeVelocity1() {
        return changeVelocity1;
    }

    public MOPSOpdBuilder setChangeVelocity1(double changeVelocity1) {
        this.changeVelocity1 = changeVelocity1;
        return this;
    }

    public double getChangeVelocity2() {
        return changeVelocity2;
    }

    public MOPSOpdBuilder setChangeVelocity2(double changeVelocity2) {
        this.changeVelocity2 = changeVelocity2;
        return this;
    }

    public String getFunctionType() {
        return functionType;
    }

    public MOPSOpdBuilder setFunctionType(String functionType) {
        this.functionType = functionType;
        return this;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public MOPSOpdBuilder setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        return this;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public MOPSOpdBuilder setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public String getInProcessDataPath() {
        return inProcessDataPath;
    }

    public MOPSOpdBuilder setInProcessDataPath(String inProcessDataPath) {
        this.inProcessDataPath = inProcessDataPath;
        return this;
    }
}
