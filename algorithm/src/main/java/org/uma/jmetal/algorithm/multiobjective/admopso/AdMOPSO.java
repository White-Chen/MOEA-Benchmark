package org.uma.jmetal.algorithm.multiobjective.admopso;

import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.algorithm.multiobjective.admopso.util.AdMOPSOSelection;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.grid.util.ShuffleListBuilder;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ChenZhe on 2015/11/3.
 * <p>
 * This class is the implementation of the
 * "Handling multiple objectives with particle swarm optimization."
 * Coello, Carlos A. Coello, Gregorio Toscano Pulido, and M. Salazar Lechuga.
 * Evolutionary Computation, IEEE Transactions on 8.3 (2004): 256-279.
 */
public class AdMOPSO extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>> {

    private static final double phi1 = 2.05;
    private static final double phi2 = 2.05;
    private static final double chi = 0.7298;

    private double r1Max;
    private double r1Min;
    private double r2Max;
    private double r2Min;
    private double weightMax;
    private double weightMin;


    private int swarmSize;
    private int archiveSize;
    private int maxEvaluations;
    private int evaluations;
    private double mutationRate;

    private SolutionListEvaluator<DoubleSolution> evaluator;
    private DoubleProblem problem;
    private SelectionOperator<AdaptiveGridArchiveII<DoubleSolution>, DoubleSolution> selector;

    private double[][] speed;
    private double deltaMax[];
    private double deltaMin[];
    private double deltaDivision;

    private List<DoubleSolution> swarm;
    private DoubleSolution[] localBest;
    private AdaptiveGridArchiveII<DoubleSolution> leadersArchive;
    private JMetalRandom randomGenerator;
    private MutationOperator<DoubleSolution> mutation;
    private Comparator<DoubleSolution> dominanceComparator;
    private String inProcessDataPath;

    public AdMOPSO(DoubleProblem problem,
                   AdaptiveGridArchiveII<DoubleSolution> leadersArchive,
                   SolutionListEvaluator<DoubleSolution> evaluator,
                   MutationOperator<DoubleSolution> mutation,
                   AdMOPSOSelection<DoubleSolution> selector,
                   Comparator<DoubleSolution> dominanceComparator,
                   int swarmSize, int archiveSize, int maxEvaluations,
                   double r1Max, double r1Min,
                   double r2Max, double r2Min,
                   double weightMax, double weightMin,
                   int eliminatePressure, int selectionPressure, int divisionNumber,
                   int deltaDivision, double mutationRate,
                   String inProcessDataPath
    ) {

        this.evaluator = evaluator;
        this.problem = problem;
        this.leadersArchive = leadersArchive;
        this.mutation = mutation;
        this.selector = selector;
        this.dominanceComparator = dominanceComparator;

        this.r1Max = r1Max;
        this.r1Min = r1Min;
        this.r2Max = r2Max;
        this.r2Min = r2Min;
        this.weightMax = weightMax;
        this.weightMin = weightMin;
        this.maxEvaluations = maxEvaluations;
        this.swarmSize = swarmSize;
        this.archiveSize = archiveSize;
        this.deltaDivision = deltaDivision;
        this.mutationRate = mutationRate;
        this.inProcessDataPath = inProcessDataPath;

        this.leadersArchive.getGrid()
                .setDivisionNumber(divisionNumber)
                .setEliminatePressure(eliminatePressure)
                .setSelectionPressure(selectionPressure);
        randomGenerator = JMetalRandom.getInstance();
        localBest = new DoubleSolution[swarmSize];
        speed = new double[swarmSize][problem.getNumberOfVariables()];
        deltaMax = new double[problem.getNumberOfVariables()];
        deltaMin = new double[problem.getNumberOfVariables()];
        evaluations = 0;

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            deltaMax[i] = (problem.getUpperBound(i) - problem.getLowerBound(i)) / this.deltaDivision;
            deltaMin[i] = -deltaMax[i];
        }

    }

    protected static double c1constriction() {
        return chi * phi1;
    }

    protected static double c2constriction() {
        return chi * phi2;
    }

    @Override
    protected void initProgress() {
        evaluations = swarmSize;
    }

    @Override
    protected void updateProgress() {
        evaluations += swarmSize;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<DoubleSolution> createInitialSwarm() {
        swarm = new ArrayList<>(swarmSize);

        DoubleSolution newSolution;
        for (int i = 0; i < swarmSize; i++) {
            newSolution = problem.createSolution();
            swarm.add(newSolution);
        }

        return swarm;
    }

    @Override
    protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
        swarm = evaluator.evaluate(swarm, problem);
        return swarm;
    }

    @Override
    protected void initializeLeader(List<DoubleSolution> swarm) {
        for (DoubleSolution particle : swarm) {
            leadersArchive.add((DoubleSolution) particle.copy());
        }
    }

    @Override
    protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
            localBest[i] = particle;
        }
    }

    @Override
    protected void initializeVelocity(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarmSize; i++) {
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                speed[i][j] = 0.0;
            }
        }
    }

    @Override
    protected void updateVelocity(List<DoubleSolution> swarm) {
        double c1, c2, r1, r2, wmax, wmin;
        DoubleSolution bestGlobal;

        for (int i = 0; i < swarm.size(); i++) {
            DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
            DoubleSolution bestPaticle = localBest[i];

            bestGlobal = selectGlobalBest();

            r1 = randomGenerator.nextDouble(r1Min, r1Max);
            r2 = randomGenerator.nextDouble(r2Min, r2Max);
            c1 = c1constriction();
            c2 = c2constriction();
            wmax = weightMax;
            wmin = weightMin;

            for (int j = 0; j < particle.getNumberOfVariables(); j++) {
                speed[i][j] = velocityConstriction
                        (
                                inertiaWeight(evaluations / maxEvaluations, wmax, wmin) * speed[i][j]
                                        + c1 * r1 * (bestPaticle.getVariableValue(j) - particle.getVariableValue(j))
                                        + c2 * r2 * (bestGlobal.getVariableValue(j) - particle.getVariableValue(j)),
                                deltaMax,
                                deltaMin,
                                j
                        );
            }

        }
    }

    @Override
    protected void updatePosition(List<DoubleSolution> swarm) {
        DoubleSolution particle;
        for (int i = 0; i < swarm.size(); i++) {
            particle = swarm.get(i);

            for (int j = 0; j < particle.getNumberOfVariables(); j++) {
                particle.setVariableValue(j, particle.getVariableValue(j) + speed[i][j]);

                if (particle.getVariableValue(j) < problem.getLowerBound(j)) {
                    particle.setVariableValue(j, problem.getLowerBound(j));
                    speed[i][j] = speed[i][j] * -1.0;
                }

                if (particle.getVariableValue(j) > problem.getUpperBound(j)) {
                    particle.setVariableValue(j, problem.getUpperBound(j));
                    speed[i][j] = speed[i][j] * -1.0;
                }
            }
        }
    }

    @Override
    protected void perturbation(List<DoubleSolution> swarm) {

        int flag;
        double pm = Math.pow(1 - ((double) (evaluations / maxEvaluations - 1) / (double) (maxEvaluations / swarmSize - 1)), 5 / mutationRate);
        for (int i = 0; i < swarm.size(); i++) {
            if (randomGenerator.nextDouble() < pm) {
                DoubleSolution tempParticle = (DoubleSolution) swarm.get(i).copy();

                mutation.execute(tempParticle);
                problem.evaluate(tempParticle);

                flag = dominanceComparator.compare(swarm.get(i), tempParticle);
                if (flag == 1) {
                    swarm.set(i, tempParticle);
                }
                if (flag == 0) {
                    if (randomGenerator.nextDouble() < 0.5) swarm.set(i, tempParticle);
                }
            }

        }
    }

    @Override
    protected void updateLeaders(List<DoubleSolution> swarm) {
        // improve random ability
        List<Integer> tempList = ShuffleListBuilder.getShuffleList(swarm.size());
        for (int i = 0; i < swarm.size(); i++) {
            int tempIndex = tempList.get(i);
            leadersArchive.add((DoubleSolution) swarm.get(tempIndex).copy());
        }
    }

    @Override
    protected void updateParticlesMemory(List<DoubleSolution> swarm) {
        int flag;
        for (int i = 0; i < swarm.size(); i++) {
            flag = dominanceComparator.compare(swarm.get(i), localBest[i]);
            if (flag == -1) {
                DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
                localBest[i] = particle;
            } else if (flag == 0 && randomGenerator.nextDouble(0, 1) <= 0.5) {
                DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
                localBest[i] = particle;
            }
        }
    }

    @Override
    public List<DoubleSolution> getResult() {
        return leadersArchive.getSolutionList();
    }

    protected DoubleSolution selectGlobalBest() {

        return selector.execute(leadersArchive);

    }

    protected double velocityConstriction(double v, double[] deltaMax, double[] deltaMin,
                                          int variableIndex) {
        double result;

        double dmax = deltaMax[variableIndex];
        double dmin = deltaMin[variableIndex];

        result = v;

        if (v > dmax) result = dmax;
        if (v < dmin) result = dmin;

        return result;
    }

    protected double constrictionCoefficient(double c1, double c2) {
        double rho = c1 + c2;
        if (rho <= 4) {
            return 1.0;
        } else {
            return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
        }
    }

    protected double inertiaWeight(int miter, double wma, double wmin) {
        return wma;
    }

    /**
     */
    @Override
    public String getName() {
        return "agMOPSO";
    }

    /**
     */
    @Override
    public String getDescription() {
        return "Adptive MOPSO";
    }

    @Override
    protected void saveDataInProcess() {
        if (!inProcessDataPath.isEmpty() && ((evaluations % swarmSize == 0) || evaluations == 2 * swarmSize)) {
            new SolutionListOutput(getResult())
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/VAR" + evaluations / (10 * swarmSize) + ".tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/FUN" + evaluations / (10 * swarmSize) + ".tsv"))
                    .print();
        }
    }
}
