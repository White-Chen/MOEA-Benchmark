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

package org.uma.jmetal.algorithm.multiobjective.MOPSOpd;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.grid.util.ShuffleListBuilder;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by ChenZhe on 4/28/2015.
 * @author Chenzhe <q953387601@163.com>
 */

public class MOPSOpd implements Algorithm<List<DoubleSolution>> {
    private static final long serialVersionUID = 7100961861651318429L;
    /**
     *
     */
    double[] z;
    double[][] lambda;
    DoubleSolution[] indArray;
    String dataDirectory;
    String functionType = "_PBI";//"_PBI";//"_TCHE";//"_AGG";
    private DoubleProblem problem;
    private List<DoubleSolution> swarm;
    private double c1Max;
    private double c1Min;
    private double c2Max;
    private double c2Min;
    private double r1Max;
    private double r1Min;
    private double r2Max;
    private double r2Min;
    private double weightMax;
    private double weightMin;
    private double changeVelocity1;
    private double changeVelocity2;
    private int swarmSize;
    private int maxIterations;
    private int iterations;
    private int maxAge;
    private DoubleSolution[] localBest;
    private DoubleSolution[] globalBest;
    private AdaptiveGridArchiveII<DoubleSolution> leadersArchive;
    private int[] shfGBest;
    private double[][] speed;
    private int[] age;
    private double deltaMax[];
    private double deltaMin[];
    private JMetalRandom randomGenerator;
    private SolutionListEvaluator<DoubleSolution> evaluator;
    private String inProcessDataPath;
    private List<GenericIndicator> indicatorList;

    public MOPSOpd(DoubleProblem problem, int swarmSize,
                   int maxIterations, double r1Min, double r1Max,
                   double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                   double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                   String functionType, int maxAge,
                   String dataDirectory, String inProcessDataPath,
                   AdaptiveGridArchiveII<DoubleSolution> leadersArchive,
                   int eliminatePressure, int selectionPressure, int divisionNumber) {
        this.problem = problem;
        this.swarmSize = swarmSize;
        this.maxIterations = maxIterations;
        this.leadersArchive = leadersArchive;

        this.r1Max = r1Max;
        this.r1Min = r1Min;
        this.r2Max = r2Max;
        this.r2Min = r2Min;
        this.c1Max = c1Max;
        this.c1Min = c1Min;
        this.c2Max = c2Max;
        this.c2Min = c2Min;
        this.weightMax = weightMax;
        this.weightMin = weightMin;
        this.changeVelocity1 = changeVelocity1;
        this.changeVelocity2 = changeVelocity2;
        this.functionType = functionType;
        this.maxAge = maxAge;

        this.dataDirectory = dataDirectory;
        this.inProcessDataPath = inProcessDataPath;

        evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();

        randomGenerator = JMetalRandom.getInstance();

        localBest = new DoubleSolution[swarmSize];
        globalBest = new DoubleSolution[swarmSize];
        shfGBest = new int[swarmSize];
        speed = new double[swarmSize][problem.getNumberOfVariables()];
        age = new int[swarmSize];

        indArray = new DoubleSolution[problem.getNumberOfObjectives()];
        z = new double[problem.getNumberOfObjectives()];
        lambda = new double[swarmSize][problem.getNumberOfObjectives()];

        deltaMax = new double[problem.getNumberOfVariables()];
        deltaMin = new double[problem.getNumberOfVariables()];
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            deltaMax[i] = (problem.getUpperBound(i) -
                    problem.getLowerBound(i)) / 2.0;
            deltaMin[i] = -deltaMax[i];
        }
        this.leadersArchive.getGrid()
                .setDivisionNumber(divisionNumber)
                .setEliminatePressure(eliminatePressure)
                .setSelectionPressure(selectionPressure);
    }

    public List<DoubleSolution> getSwarm() {
        return swarm;
    }

    protected void initProgress() {
        iterations = 1;
    }

    protected void updateProgress() {
        iterations++;
    }

    protected boolean isStoppingConditionReached() {
        return iterations >= maxIterations;
    }

    protected List<DoubleSolution> createInitialSwarm() {
        List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

        DoubleSolution newSolution;
        for (int i = 0; i < swarmSize; i++) {
            newSolution = problem.createSolution();
            swarm.add(newSolution);
        }

        return swarm;
    }

    protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
        swarm = evaluator.evaluate(swarm, problem);

        return swarm;
    }

    protected void initializeLeaders(List<DoubleSolution> swarm) {
        for (int i = 0; i < getSwarm().size(); i++) {
            DoubleSolution particle = (DoubleSolution) getSwarm().get(i).copy();
            globalBest[i] = particle;
        }

        updateGlobalBest();
    }

    protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
        for (int i = 0; i < getSwarm().size(); i++) {
            DoubleSolution particle = (DoubleSolution) getSwarm().get(i).copy();
            localBest[i] = particle;
        }
    }

    protected void initializeVelocity(List<DoubleSolution> swarm) {
        // Initialize the speed and age of each particle to 0
        for (int i = 0; i < swarmSize; i++) {
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                speed[i][j] = 0.0;
            }
            age[i] = 0;
        }
    }

    protected void updateVelocity(int i) {

        DoubleSolution particle = getSwarm().get(i);
        DoubleSolution bestParticle = localBest[i];
        DoubleSolution bestGlobal = globalBest[shfGBest[i]];

        double r1 = randomGenerator.nextDouble(r1Min, r1Max);
        double r2 = randomGenerator.nextDouble(r2Min, r2Max);
        double C1 = randomGenerator.nextDouble(c1Min, c1Max);
        double C2 = randomGenerator.nextDouble(c2Min, c2Max);

        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            //Computing the velocity of this particle
            speed[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
                    (inertiaWeight(iterations, maxIterations, this.weightMax, this.weightMin) * speed[i][var] +
                            C1 * r1 * (bestParticle.getVariableValue(var) -
                                    particle.getVariableValue(var)) +
                            C2 * r2 * (bestGlobal.getVariableValue(var) -
                                    particle.getVariableValue(var))), deltaMax, deltaMin, var, i);

        }
    }

    private void computeNewPositions(int i) {
        DoubleSolution particle = getSwarm().get(i);
        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            particle.setVariableValue(var, particle.getVariableValue(var) + speed[i][var]);
        }
    }

    /**
     * initUniformWeight
     */
    private void initUniformWeight() {
        if ((problem.getNumberOfObjectives() == 2) && (swarmSize <= 300)) {
            for (int n = 0; n < swarmSize; n++) {
                double a = 1.0 * n / (swarmSize - 1);
                lambda[n][0] = a;
                lambda[n][1] = 1 - a;
            }
        } else {
            String dataFileName;
            dataFileName = "W" + problem.getNumberOfObjectives() + "D_" +
                    swarmSize + ".dat";

            try {
                InputStream in = getClass().getResourceAsStream("/" + dataDirectory + "/" + dataFileName);
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);

                int i = 0;
                int j = 0;
                String aux = br.readLine();
                while (aux != null) {
                    StringTokenizer st = new StringTokenizer(aux);
                    j = 0;
                    while (st.hasMoreTokens()) {
                        double value = new Double(st.nextToken());
                        lambda[i][j] = value;
                        j++;
                    }
                    aux = br.readLine();
                    i++;
                }
                br.close();
            } catch (Exception e) {
                throw new JMetalException("initializeUniformWeight: failed when reading for file: "
                        + dataDirectory + "/" + dataFileName, e);
            }
        }
    }


    private void initIdealPoint() {
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            z[i] = 1.0e+30;
            indArray[i] = problem.createSolution();
            problem.evaluate(indArray[i]);
        }

        for (int i = 0; i < swarmSize; i++) {
            updateReference(getSwarm().get(i));
        }
    }

    private void updateReference(DoubleSolution individual) {
        for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
            if (individual.getObjective(n) < z[n]) {
                z[n] = individual.getObjective(n);

                indArray[n] = (DoubleSolution) individual.copy();
            }
        }
    }

    private void updateGlobalBest() {

        double gBestFitness;

        for (int j = 0; j < lambda.length; j++) {
            gBestFitness = fitnessFunction(globalBest[j], lambda[j]);

            for (int i = 0; i < getSwarm().size(); i++) {
                double v1 = fitnessFunction(getSwarm().get(i), lambda[j]);
                double v2 = gBestFitness;
                if (v1 < v2) {
                    globalBest[j] = (DoubleSolution) getSwarm().get(i).copy();
                    gBestFitness = v1;
                }
            }
        }

        List<Integer> tempList = ShuffleListBuilder.getShuffleList(swarm.size());
        for (int i = 0; i < swarm.size(); i++) {
            int tempIndex = tempList.get(i);
            leadersArchive.add((DoubleSolution) swarm.get(tempIndex).copy());
        }
    }

    private void updateLocalBest(int part) {

        double f1, f2;
        DoubleSolution indiv = (DoubleSolution) getSwarm().get(part).copy();

        f1 = fitnessFunction(localBest[part], lambda[part]);
        f2 = fitnessFunction(indiv, lambda[part]);

        if (age[part] >= maxAge || f2 <= f1) {
            localBest[part] = indiv;
            age[part] = 0;
        } else {
            age[part]++;
        }
    }

    private double fitnessFunction(DoubleSolution sol, double[] lambda) {
        double fitness = 0.0;

        if (functionType.equals("_TCHE")) {
            double maxFun = -1.0e+30;

            for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
                double diff = Math.abs(sol.getObjective(n) - z[n]);

                double feval;
                if (lambda[n] == 0) {
                    feval = 0.0001 * diff;
                } else {
                    feval = diff * lambda[n];
                }
                if (feval > maxFun) {
                    maxFun = feval;
                }
            }

            fitness = maxFun;

        } else if (functionType.equals("_AGG")) {
            double sum = 0.0;
            for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
                sum += (lambda[n]) * sol.getObjective(n);
            }

            fitness = sum;

        } else if (functionType.equals("_PBI")) {
            double d1, d2, nl;
            double theta = 5.0;

            d1 = d2 = nl = 0.0;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d1 += (sol.getObjective(i) - z[i]) * lambda[i];
                nl += Math.pow(lambda[i], 2.0);
            }
            nl = Math.sqrt(nl);
            d1 = Math.abs(d1) / nl;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d2 += Math.pow((sol.getObjective(i) - z[i]) - d1 * (lambda[i] / nl), 2.0);
            }
            d2 = Math.sqrt(d2);

            fitness = (d1 + theta * d2);

        } else {
            System.out.println("dMOPSO.fitnessFunction: unknown type " + functionType);
            System.exit(-1);
        }
        return fitness;
    }

    private void shuffleGlobalBest() {
        int[] aux = new int[swarmSize];
        int rnd;
        int tmp;

        for (int i = 0; i < swarmSize; i++) {
            aux[i] = i;
        }

        for (int i = 0; i < swarmSize; i++) {
            rnd = randomGenerator.nextInt(i, swarmSize - 1);
            tmp = aux[rnd];
            aux[rnd] = aux[i];
            shfGBest[i] = tmp;
        }
    }

    private void repairBounds(int part) {

        DoubleSolution particle = getSwarm().get(part);

        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            if (particle.getVariableValue(var) < problem.getLowerBound(var)) {
                particle.setVariableValue(var, problem.getLowerBound(var));
                speed[part][var] = speed[part][var] * changeVelocity1;
            }
            if (particle.getVariableValue(var) > problem.getUpperBound(var)) {
                particle.setVariableValue(var, problem.getUpperBound(var));
                speed[part][var] = speed[part][var] * changeVelocity2;
            }
        }
    }

    private void resetParticle(int i) {
        DoubleSolution particle = getSwarm().get(i);
        double mean, sigma, N;

        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            DoubleSolution gB, pB;
            gB = globalBest[shfGBest[i]];
            pB = localBest[i];

            mean = (gB.getVariableValue(var) - pB.getVariableValue(var)) / 2;

            sigma = Math.abs(gB.getVariableValue(var) - pB.getVariableValue(var));

            java.util.Random rnd = new java.util.Random();

            N = rnd.nextGaussian() * sigma + mean;

            particle.setVariableValue(var, N);
            speed[i][var] = 0.0;
        }
    }

    private double velocityConstriction(double v, double[] deltaMax, double[] deltaMin,
                                        int variableIndex, int particleIndex) {

        double result;

        double dmax = deltaMax[variableIndex];
        double dmin = deltaMin[variableIndex];

        result = v;

        if (v > dmax) {
            result = dmax;
        }

        if (v < dmin) {
            result = dmin;
        }

        return result;
    }

    private double constrictionCoefficient(double c1, double c2) {
        double rho = c1 + c2;
        if (rho <= 4) {
            return 1.0;
        } else {
            return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
        }
    }

    private double inertiaWeight(int iter, int miter, double wma, double wmin) {
        return wma;
    }

    @Override
    public void run() {
        swarm = createInitialSwarm();
        evaluateSwarm(swarm);
        initializeVelocity(getSwarm());

        initUniformWeight();
        initIdealPoint();

        initializeLeaders(getSwarm());
        initializeParticlesMemory(getSwarm());

        updateGlobalBest();

        initProgress();
        while (!isStoppingConditionReached()) {
            shuffleGlobalBest();

            for (int i = 0; i < getSwarm().size(); i++) {
                if (age[i] < maxAge) {
                    updateVelocity(i);
                    computeNewPositions(i);
                } else {
                    resetParticle(i);
                }

                repairBounds(i);

                problem.evaluate(swarm.get(i));
                updateReference(swarm.get(i));
                updateLocalBest(i);
            }
            updateGlobalBest();
            updateProgress();
            saveDataInProcess();
        }
    }

    private void saveDataInProcess() {
        if (!inProcessDataPath.isEmpty() && ((iterations % 20 == 0) || iterations == 2)) {
            new SolutionListOutput(getResult())
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/VAR" + iterations + ".tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/FUN" + iterations + ".tsv"))
                    .print();
        }

    }


    @Override
    public List<DoubleSolution> getResult() {
        return Arrays.asList(globalBest);
    }

    @Override
    public String getName() {
        return "MOPSOpd";
    }

    @Override
    public String getDescription() {
        return "MOPSOpd";
    }


}