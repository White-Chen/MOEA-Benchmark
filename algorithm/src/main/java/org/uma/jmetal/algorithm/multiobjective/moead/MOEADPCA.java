package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.pca.PrincipalComponentAnalysis;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.ExtendDominationRanking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by zhoulifa on 17-2-20.
 */
public class MOEADPCA extends AbstractMOEAD<DoubleSolution> {
    private DifferentialEvolutionCrossover differentialEvolutionCrossover;
    private PrincipalComponentAnalysis pca;

    private double[] weightOfPopulation;
    int numplieMax;
    int numplieMin;
    int[] hierarchy;
    int numberOfVariables;
    double[][] weightOfNeighbor;

    public MOEADPCA(Problem<DoubleSolution> problem,
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
                    String inProcessDataPath){
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
                neighborSize, inProcessDataPath);

        differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
        pca = new PrincipalComponentAnalysis();
    }

    @Override
    public void run(){
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();

        numberOfVariables = population.get(0).getNumberOfVariables();
        weightOfNeighbor = new double[populationSize][numberOfVariables];
        evaluations = populationSize;
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            ifPCA(evaluations/populationSize);
            hierarchy = hierarchySolution();
            setWeightOfPopulation();


            System.out.println("###########################################"+evaluations/populationSize);
//            for (int i = 0;i< populationSize;i++){
//                for (int j=0;j< numberOfVariables;j++){
//                    System.out.print(weightOfNeighbor[i][j]+" ");
//                }
//                System.out.println();
//            }
//            if (evaluations/populationSize ==15)
//                break;
            for (int i = 0; i < populationSize; i++){
                int subProblemId = permutation[i];

                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

                differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
                // setWeight
                if (neighborType == NeighborType.NEIGHBOR){
                    differentialEvolutionCrossover.setWeight(weightOfNeighbor[numplieMin]);
                } else{
                    differentialEvolutionCrossover.setWeight(weightOfPopulation);
                }
                List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

                DoubleSolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNeighborhood(child, subProblemId,neighborType);
            }
            saveDataInProcess();
        } while (evaluations < maxEvaluations);

    }

    public void ifPCA(int itrCounter){
        if ((itrCounter % 18) == 1){
            for (int i = 0; i < populationSize; i++){
                double[] weight1 = setWeightOfNeighbor(i, numberOfVariables);
                System.arraycopy(weight1, 0, weightOfNeighbor[i], 0, numberOfVariables);
            }
        }
    }

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

    public int[] hierarchySolution(){
        ExtendDominationRanking extendDominationRanking = new ExtendDominationRanking();
        extendDominationRanking.computeExtendRanking(population);
        int[] hierarchyIndex = extendDominationRanking.getHierarchyIndex();

        return hierarchyIndex;
    }

    protected void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newSolution = problem.createSolution();

            problem.evaluate(newSolution);
            population.add(newSolution);
        }
    }

    @Override
    public String getName() { return "MOEADPCA";}

    @Override
    public String getDescription() { return "Multi-Objective Evolutionary Algorithm based on Decomposition and PCA"; }

    protected List<DoubleSolution> parentSelection(int subProblemId, NeighborType neighborType) {
        List<Integer> matingPool = matingSelection(subProblemId, 2, neighborType);

        List<DoubleSolution> parents = new ArrayList<>(3);
        parents.add(population.get(matingPool.get(0)));
        parents.add(population.get(matingPool.get(1)));
        parents.add(population.get(subProblemId));
        return parents;
    }

    /**
     * @param subproblemId  the id of current subproblem
     * @param neighbourType neighbour type
     */
    protected List<Integer> matingSelection(int subproblemId, int numberOfSolutionsToSelect, NeighborType neighbourType) {
        Random random = new Random();
        int neighbourSize;
        int numplieOfMaxIndex;
        int numplieOfMinIndex;
        List<Integer> numplieOfMaxList = new ArrayList<>();
        List<Integer> numplieOfMinList = new ArrayList<>();

        List<Integer> listOfSolutions = new ArrayList<>(numberOfSolutionsToSelect);
        neighbourSize = neighborhood[subproblemId].length;

        if(true){
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
        }else {
            numplieOfMaxIndex = 0;
            numplieOfMinIndex = 0;

            //寻找最大最小值
            for (int i = 1; i < populationSize; i++){
                if (hierarchy[numplieOfMaxIndex] < hierarchy[i]){
                    numplieOfMaxIndex = i;
                }
                if (hierarchy[numplieOfMinIndex] > hierarchy[i]){
                    numplieOfMinIndex = i;
                }
            }
            //找出相同的最大最小值
            for (int i = 0; i < populationSize; i++){
                if (hierarchy[numplieOfMaxIndex] == hierarchy[i]){
                    numplieOfMaxList.add(i);
                }
                if (hierarchy[numplieOfMinIndex] == hierarchy[i]){
                    numplieOfMinList.add(i);
                }
            }
        }

        //随机选择最大最小值
        numplieOfMaxIndex = numplieOfMaxList.get(random.nextInt(numplieOfMaxList.size()));
        numplieOfMinIndex = numplieOfMinList.get(random.nextInt(numplieOfMinList.size()));
//        numplieOfMaxIndex = neighborhood[subproblemId][random.nextInt(20)];
//        numplieOfMinIndex = neighborhood[subproblemId][random.nextInt(20)];
        while (numplieOfMinIndex == numplieOfMaxIndex){
            numplieOfMinIndex = numplieOfMinList.get(random.nextInt(numplieOfMinList.size()));
//            numplieOfMinIndex = neighborhood[subproblemId][random.nextInt(20)];
        }

        listOfSolutions.add(numplieOfMaxIndex);
        listOfSolutions.add(numplieOfMinIndex);

        numplieMax = numplieOfMaxIndex;
        numplieMin = numplieOfMinIndex;
//        System.out.println("***********************************");
//        for (int i = 0;i<neighbourSize;i++){
//            System.out.print(hierarchy[neighborhood[subproblemId][i]]+"    ");
//            for (int j = 0;j<population.get(0).getNumberOfVariables();j++){
//                System.out.print(population.get(neighborhood[subproblemId][i]).getVariableValue(j)+" ");
//            }
//            System.out.println();
//        }
//        System.out.println("-----------------------------------");
//        for (int i = 0;i< 2;i++){
//            for (int j = 0;j<population.get(0).getNumberOfVariables();j++){
//                System.out.print(population.get(listOfSolutions.get(i)).getVariableValue(j)+"--------");
//            }
//            System.out.println("*****"+listOfSolutions.get(i));
//        }
//        System.out.println("************************************");

        return listOfSolutions;
    }
}
