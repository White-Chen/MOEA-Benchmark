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

package org.uma.jmetal.algorithm.multiobjective.mopsopd;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.dmopso.DMOPSO;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.grid.util.ShuffleListBuilder;

import java.util.List;

public class MOPSOpd extends DMOPSO implements Algorithm<List<DoubleSolution>> {
    private static final long serialVersionUID = 7100961861651318429L;
    private AdaptiveGridArchiveII<DoubleSolution> leadersArchive;

    public MOPSOpd(DoubleProblem problem, int swarmSize,
                   int maxIterations, double r1Min, double r1Max,
                   double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                   double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                   String functionType, int maxAge,
                   String dataDirectory, String inProcessDataPath,
                   AdaptiveGridArchiveII<DoubleSolution> leadersArchive,
                   int eliminatePressure, int selectionPressure, int divisionNumber) {
        super(problem,swarmSize,maxIterations,r1Min,r1Max,r2Min,r2Max,c1Min,c1Max,c2Min,c2Max,
                weightMin,weightMax,changeVelocity1,changeVelocity2,functionType,maxAge,
                dataDirectory,inProcessDataPath);
        this.leadersArchive = leadersArchive;
    }

    @Override
    protected void initProgress() {
        iterations = 1;
    }

    @Override
    protected void updateProgress() {
        iterations++;
    }

    @Override
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

    @Override
    protected void updateGlobalBest() {

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

    @Override
    protected double fitnessFunction(DoubleSolution sol, double[] lambda) {
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

    @Override
    protected void saveDataInProcess() {
        if (!inProcessDataPath.isEmpty() && ((iterations % 20 == 0) || iterations == 2)) {
            new SolutionListOutput(getResult())
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/VAR" + iterations + ".tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext(inProcessDataPath + "/FUN" + iterations + ".tsv"))
                    .print();
        }

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