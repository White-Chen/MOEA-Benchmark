package org.uma.jmetal.algorithm.multiobjective.dmopso;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * Created by ChenZhe on 4/28/2016.
 */
public class DMOPSOBuilder implements AlgorithmBuilder<DMOPSO> {

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

    public DMOPSOBuilder(DoubleProblem problem) {
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

        this.dataDirectory = "MOEAD_Weights";
    }

    @Override
    public DMOPSO build() {
        DMOPSO algorithm = new DMOPSO(this.problem, this.swarmSize,
                this.maxIterations, this.r1Min, this.r1Max,
                this.r2Min, this.r2Max, this.c1Min, this.c1Max, this.c2Min, this.c2Max,
                this.weightMin, this.weightMax, this.changeVelocity1, this.changeVelocity2,
                this.functionType, this.maxAge,
                this.dataDirectory, this.inProcessDataPath);
        return algorithm;
    }

    public DoubleProblem getProblem() {
        return problem;
    }

    public DMOPSOBuilder setProblem(DoubleProblem problem) {
        this.problem = problem;
        return this;
    }

    public int getSwarmSize() {
        return swarmSize;
    }

    public DMOPSOBuilder setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;
        return this;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public DMOPSOBuilder setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public double getR1Min() {
        return r1Min;
    }

    public DMOPSOBuilder setR1Min(double r1Min) {
        this.r1Min = r1Min;
        return this;
    }

    public double getR1Max() {
        return r1Max;
    }

    public DMOPSOBuilder setR1Max(double r1Max) {
        this.r1Max = r1Max;
        return this;
    }

    public double getR2Min() {
        return r2Min;
    }

    public DMOPSOBuilder setR2Min(double r2Min) {
        this.r2Min = r2Min;
        return this;
    }

    public double getR2Max() {
        return r2Max;
    }

    public DMOPSOBuilder setR2Max(double r2Max) {
        this.r2Max = r2Max;
        return this;
    }

    public double getC1Min() {
        return c1Min;
    }

    public DMOPSOBuilder setC1Min(double c1Min) {
        this.c1Min = c1Min;
        return this;
    }

    public double getC1Max() {
        return c1Max;
    }

    public DMOPSOBuilder setC1Max(double c1Max) {
        this.c1Max = c1Max;
        return this;
    }

    public double getC2Min() {
        return c2Min;
    }

    public DMOPSOBuilder setC2Min(double c2Min) {
        this.c2Min = c2Min;
        return this;
    }

    public double getC2Max() {
        return c2Max;
    }

    public DMOPSOBuilder setC2Max(double c2Max) {
        this.c2Max = c2Max;
        return this;
    }

    public double getWeightMin() {
        return weightMin;
    }

    public DMOPSOBuilder setWeightMin(double weightMin) {
        this.weightMin = weightMin;
        return this;
    }

    public double getWeightMax() {
        return weightMax;
    }

    public DMOPSOBuilder setWeightMax(double weightMax) {
        this.weightMax = weightMax;
        return this;
    }

    public double getChangeVelocity1() {
        return changeVelocity1;
    }

    public DMOPSOBuilder setChangeVelocity1(double changeVelocity1) {
        this.changeVelocity1 = changeVelocity1;
        return this;
    }

    public double getChangeVelocity2() {
        return changeVelocity2;
    }

    public DMOPSOBuilder setChangeVelocity2(double changeVelocity2) {
        this.changeVelocity2 = changeVelocity2;
        return this;
    }

    public String getFunctionType() {
        return functionType;
    }

    public DMOPSOBuilder setFunctionType(String functionType) {
        this.functionType = functionType;
        return this;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public DMOPSOBuilder setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        return this;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public DMOPSOBuilder setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public String getInProcessDataPath() {
        return inProcessDataPath;
    }

    public DMOPSOBuilder setInProcessDataPath(String inProcessDataPath) {
        this.inProcessDataPath = inProcessDataPath;
        return this;
    }
}
