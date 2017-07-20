package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pca.PrincipalComponentAnalysis;
import org.uma.jmetal.util.solutionattribute.impl.ExtendDominationRanking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Zhou-LF on 2017/3/20.
 */
public class MOEAD_PCA extends AbstractMOEAD<DoubleSolution> {

    private DifferentialEvolutionCrossover differentialEvolutionCrossover;
    private PrincipalComponentAnalysis pca;
    private Random random;

    private double[] weightOfPopulation;
    private int numplieMax;
    private int numplieMin;
    private int[] hierarchy;
    private int[] numberOfIndividualUpdate;
    private int numberOfVariables;
    private double[][] weightOfNeighbor;
    private double[][] variablesValue;

    public MOEAD_PCA(Problem<DoubleSolution> problem,
                 int populationSize,
                 int resultPopulationSize,
                 int maxEvaluations,
                 MutationOperator<DoubleSolution> mutation,
                 CrossoverOperator<DoubleSolution> crossover,
                 FunctionType functionType,
                 String dataDirectory,
                 double neighborhoodSelectionProbability,
                 int maximumNumberOfReplacedSolutions,
                 int neighborSize,
                 String inProcessDataPath) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
                neighborSize, inProcessDataPath);

        differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
        pca = new PrincipalComponentAnalysis();
    }

    @Override
    public void run() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();

        numberOfVariables = problem.getNumberOfVariables();
        weightOfNeighbor = new double[populationSize][numberOfVariables];
        variablesValue = new double[populationSize][numberOfVariables];
        random = new Random();
        numberOfIndividualUpdate = new int[maxEvaluations / populationSize];

        evaluations = populationSize;
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            ifPCA(evaluations/populationSize);
            hierarchy = hierarchySolution();
            setVariablesValue();
            setWeightOfPopulation();

            for (int i = 0; i < populationSize; i++) {
                int subProblemId = permutation[i];

                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

                differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
                setDEWeight(neighborType, evaluations / populationSize, maxEvaluations / populationSize, subProblemId);
                List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

                DoubleSolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNeighborhood(child, subProblemId, neighborType, evaluations / populationSize, maxEvaluations / populationSize);
            }

            numberOfIndividualUpdate[evaluations/populationSize - 2] = getNumberOfIndividualUpdate();
            saveDataInProcess();
        } while (evaluations < maxEvaluations);
    }

    /**
     * Update neighborhood method
     *
     * @param individual
     * @param subProblemId
     * @param neighborType
     * @throws JMetalException
     */
    protected void updateNeighborhood(DoubleSolution individual, int subProblemId, NeighborType neighborType, int itrCounter, int iteration) throws JMetalException {
        int size;
        int time;

        time = 0;

        if (neighborType == NeighborType.NEIGHBOR) {
            size = neighborhood[subProblemId].length;
        } else {
            size = population.size();
        }
        int[] perm = new int[size];

        MOEADUtils.randomPermutation(perm, size);

        for (int i = 0; i < size; i++) {
            int k;
            if (neighborType == NeighborType.NEIGHBOR) {
                k = neighborhood[subProblemId][perm[i]];
            } else {
                k = perm[i];
            }
            double f1, f2;

            if (itrCounter < 3 * iteration) {
                f1 = fitnessFunction(population.get(k), lambda[k]);
                f2 = fitnessFunction(individual, lambda[k]);
            } else {
                f1 = fitnessFunctionSta(population.get(k), lambda[k]);
                f2 = fitnessFunctionSta(individual, lambda[k]);
            }

            if (f2 < f1) {
                population.set(k, (DoubleSolution) individual.copy());
                time++;
            }

            if (time >= maximumNumberOfReplacedSolutions) {
                return;
            }
        }
    }

    //zlf 修改  对领域更新个体的标准化
    double fitnessFunctionSta(DoubleSolution individual, double[] lambda) throws JMetalException {
        double fitness;
        //对个体与理想点进行标准化
        double[] StaInd   = new double[problem.getNumberOfObjectives()];    //标准化个体
        double[] MaxPoint = new double[problem.getNumberOfObjectives()];    //极大点
        double[] MinPoint = idealPoint;                                     //极小点
        for (int i = 0; i < problem.getNumberOfObjectives(); i++){
            MaxPoint[i] = - 1.0e+30;
        }
        for (int i = 0; i < populationSize; i++){
            for (int j = 0; j < problem.getNumberOfObjectives(); j++){
                if (population.get(i).getObjective(j) > MaxPoint[j]){
                    MaxPoint[j] = population.get(i).getObjective(j);
                }
            }
        }
        for (int j = 0; j < problem.getNumberOfObjectives(); j++){
            if (individual.getObjective(j) > MaxPoint[j]){
                MaxPoint[j] = individual.getObjective(j);
            }
        }
        for (int i = 0; i < problem.getNumberOfObjectives(); i++){           //标准化
            StaInd[i] = (individual.getObjective(i) - MinPoint[i]) / (MaxPoint[i] - MinPoint[i]);
        }

        //聚合函数
        if (MOEAD.FunctionType.TCHE.equals(functionType)) {
            double maxFun = -1.0e+30;

            int n = 0;
            while (n < problem.getNumberOfObjectives()) {
                double diff = Math.abs(StaInd[n]);

                double feval;
                if (lambda[n] == 0) {
                    feval = 0.0001 * diff;
                } else {
                    feval = diff * lambda[n];
                }
                if (feval > maxFun) {
                    maxFun = feval;
                }
                n++;
            }

            fitness = maxFun;
        } else if (MOEAD.FunctionType.AGG.equals(functionType)) {
            double sum = 0.0;
            for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
                sum += (lambda[n]) * StaInd[n];
            }

            fitness = sum;

        } else if (MOEAD.FunctionType.PBI.equals(functionType)) {
            double d1, d2, nl;
            double theta = 5.0;

            d1 = d2 = nl = 0.0;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d1 += (StaInd[i]) * lambda[i];
                nl += Math.pow(lambda[i], 2.0);
            }
            nl = Math.sqrt(nl);
            d1 = Math.abs(d1) / nl;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d2 += Math.pow((StaInd[i]) - d1 * (lambda[i] / nl), 2.0);
            }
            d2 = Math.sqrt(d2);

            fitness = (d1 + theta * d2);
        } else {
            throw new JMetalException(" MOEAD.fitnessFunction: unknown type " + functionType);
        }
        return fitness;
    }

    public int getNumberOfIndividualUpdate(){
        int i,j,count = 0;
        for (i = 0; i < populationSize; i++){
            for (j = 0; j < problem.getNumberOfVariables(); j++){
                if (population.get(i).getVariableValue(j) != variablesValue[i][j]){
                    break;
                }
            }
            if (j != problem.getNumberOfVariables()){
                count++;
            }
        }
        return count;
    }

    /* setDifferentialEvolutionCrossoverWeight */
    public void setDEWeight(NeighborType neighborType, int itrCounter, int iteration, int subProblemId){
        if (itrCounter < 0.7 * maxEvaluations / populationSize) {
            differentialEvolutionCrossover.setWeightAndItrCounter(weightOfNeighbor[numplieMin], itrCounter, iteration);
        } else {
            differentialEvolutionCrossover.setWeightAndItrCounter(weightOfNeighbor[subProblemId], itrCounter, iteration);
        }
    }

    public int[] hierarchySolution(){
        ExtendDominationRanking<DoubleSolution> extendDominationRanking = new ExtendDominationRanking();
        extendDominationRanking.computeExtendRanking(population, extendFront);
        int[] hierarchyIndex = extendDominationRanking.getHierarchyIndex();
        return hierarchyIndex;
    }

    public void ifPCA(int itrCounter){
//        System.out.print(itrCounter+" ");
//        if (itrCounter!=1)
//        System.out.println(numberOfIndividualUpdate[itrCounter - 2]);

        int pcaParameter = 3;
        boolean flat = false;
        if (itrCounter < 5){
            for (int i = 0; i < populationSize; i++){
                double[] weight1 = setWeightOfNeighbor(i, numberOfVariables);
                System.arraycopy(weight1, 0, weightOfNeighbor[i], 0, numberOfVariables);
            }
        } else {
            int j;
            for (j  = 0; j < pcaParameter; j++){
                if (0 != numberOfIndividualUpdate[itrCounter - 2 - j]){
                    break;
                }
            }
            if (j == pcaParameter){
                for (int i = 0; i < populationSize; i++){
                    double[] weight1 = setWeightOfNeighbor(i, numberOfVariables);
                    System.arraycopy(weight1, 0, weightOfNeighbor[i], 0, numberOfVariables);
                }
                flat = true;
            }
        }

        //保证进行主成分分析
        if (1 == itrCounter % 8){
            if (!flat){
                for (int i = 0; i < populationSize; i++){
                    double[] weight1 = setWeightOfNeighbor(i, numberOfVariables);
                    System.arraycopy(weight1, 0, weightOfNeighbor[i], 0, numberOfVariables);
                }
            }
        }
    }

    //对邻域的个体进行主成分分析
    public double[] setWeightOfNeighbor(int subProblemId, int numberOfVariables){
        double[] weight;
        double[][] pcaData = new double[neighborSize][numberOfVariables];

        // System.out.println("*********************");
        for (int j = 0; j < neighborSize; j++){
            for (int k = 0; k < numberOfVariables; k++){
                pcaData[j][k] = population.get(neighborhood[subProblemId][j]).getVariableValue(k);
                //      System.out.print(pcaData[j][k]+" ");
            }
            //  System.out.println();
        }
        weight = pca.weightFromPCA(pcaData, neighborSize, numberOfVariables);
        return weight;
    }

    //对所有个体进行主成分分析
    public double[] setWeightOfPopulation(){
        double[][] populationData = new double[populationSize][numberOfVariables];
        for (int j = 0; j < populationSize; j++){
            for (int k = 0; k < numberOfVariables; k++){
                populationData[j][k] = population.get(j).getVariableValue(k);
            }
        }
        weightOfPopulation = pca.weightFromPCA(populationData, populationSize, numberOfVariables);
        return weightOfPopulation;
    }

    protected List<DoubleSolution> parentSelection(int subProblemId, NeighborType neighborType) {
        List<Integer> matingPool = matingSelection(subProblemId, 2, neighborType);

        List<DoubleSolution> parents = new ArrayList<>(3);
        parents.add(population.get(matingPool.get(0)));
        parents.add(population.get(matingPool.get(1)));
        parents.add(population.get(subProblemId));
        return parents;
    }

    protected List<Integer> matingSelection(int subproblemId, int numberOfSolutionsToSelect, NeighborType neighbourType) {
        int neighbourSize;
        int numplieOfMaxIndex;
        int numplieOfMinIndex;
        int selectedSolution;
        List<Integer> numplieOfMaxList = new ArrayList<>();
        List<Integer> numplieOfMinList = new ArrayList<>();

        List<Integer> listOfSolutions = new ArrayList<>(numberOfSolutionsToSelect);
        neighbourSize = neighborhood[subproblemId].length;

        if(random.nextDouble() > 0.0){
            numplieOfMaxIndex = neighborhood[subproblemId][0];
            numplieOfMinIndex = neighborhood[subproblemId][0];

            //寻找最大最小值
            for (int i = 1; i < neighbourSize; i++){
                if (hierarchy[numplieOfMaxIndex] < hierarchy[neighborhood[subproblemId][i]]){
                    numplieOfMaxIndex = neighborhood[subproblemId][i];
                }
                if (hierarchy[numplieOfMinIndex] > hierarchy[neighborhood[subproblemId][i]]){
                    numplieOfMinIndex = neighborhood[subproblemId][i];
                }
            }
            //找出相同的最大最小值
            for (int i = 0; i < neighbourSize; i++){
                if (hierarchy[numplieOfMaxIndex] == hierarchy[neighborhood[subproblemId][i]]){
                    numplieOfMaxList.add(neighborhood[subproblemId][i]);
                }
                if (hierarchy[numplieOfMinIndex] == hierarchy[neighborhood[subproblemId][i]]){
                    numplieOfMinList.add(neighborhood[subproblemId][i]);
                }
            }

            //随机选择最大最小值
            if (1 != numplieOfMaxList.size()){
                int randomMax = random.nextInt(numplieOfMaxList.size());
                numplieOfMaxIndex = numplieOfMaxList.get(randomMax);
            } else {
                numplieOfMaxIndex = numplieOfMaxList.get(0);
            }
            if (1 != numplieOfMinList.size()){
                int randomMin = random.nextInt(numplieOfMinList.size());
                numplieOfMinIndex = numplieOfMinList.get(randomMin);
            } else {
                numplieOfMinIndex = numplieOfMinList.get(0);
            }
            while (numplieOfMinIndex == numplieOfMaxIndex){
                numplieOfMinIndex = numplieOfMinList.get(random.nextInt(numplieOfMinList.size()));
            }

            listOfSolutions.add(numplieOfMaxIndex);
            listOfSolutions.add(numplieOfMinIndex);

            numplieMax = numplieOfMaxIndex;
            numplieMin = numplieOfMinIndex;
//        System.out.println(numplieMax+" "+numplieMin);
        }else {
            while (listOfSolutions.size() < numberOfSolutionsToSelect) {
                int random;
                if (neighbourType == NeighborType.NEIGHBOR) {
                    random = randomGenerator.nextInt(0, neighbourSize - 1);
                    selectedSolution = neighborhood[subproblemId][random];
                } else {
                    selectedSolution = randomGenerator.nextInt(0, populationSize - 1);
                }
                boolean flag = true;
                for (Integer individualId : listOfSolutions) {
                    if (individualId == selectedSolution) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    listOfSolutions.add(selectedSolution);
                }
            }
            numplieMax = listOfSolutions.get(0);
            numplieMin = listOfSolutions.get(1);
        }

        return listOfSolutions;
    }

    public void setVariablesValue(){
        for (int i = 0; i < populationSize; i++){
            for (int j = 0; j < problem.getNumberOfVariables(); j++){
                variablesValue[i][j] = population.get(i).getVariableValue(j);
            }
        }
    }

    protected void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newSolution1 = problem.createSolution();
            DoubleSolution newSolution2 = problem.createSolution();

            problem.evaluate(newSolution1);
            problem.evaluate(newSolution2);
            population.add(newSolution1);
            extendFront.add(newSolution2);
        }
    }

    @Override
    public String getName() {
        return "MOEAD_PCA";
    }

    @Override
    public String getDescription() {
        return "Multi-Objective Evolutionary Algorithm based on Decomposition and PCA";
    }
}
