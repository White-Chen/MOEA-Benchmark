//  StandardPSO2007.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
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

package org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization;

import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.impl.selection.BestSolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.impl.AdaptiveRandomNeighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class implementing a Standard PSO 2007 algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StandardPSO2007 extends AbstractParticleSwarmOptimization<DoubleSolution, DoubleSolution> {
    private static final long serialVersionUID = 6521108649639964396L;
    private DoubleProblem problem;
    private SolutionListEvaluator<DoubleSolution> evaluator;

    private Operator<List<DoubleSolution>, DoubleSolution> findBestSolution;
    private Comparator<DoubleSolution> fitnessComparator;
    private int swarmSize;
    private int maxIterations;
    private int iterations;
    private int numberOfParticlesToInform;
    private DoubleSolution[] localBest;
    private DoubleSolution[] neighborhoodBest;
    private double[][] speed;
    private AdaptiveRandomNeighborhood<DoubleSolution> neighborhood;
    private GenericSolutionAttribute<DoubleSolution, Integer> positionInSwarm;
    private double weight;
    private double c;
    private JMetalRandom randomGenerator = JMetalRandom.getInstance();
    private DoubleSolution bestFoundParticle;

    private int objectiveId;

    /**
     * Constructor
     *
     * @param problem
     * @param objectiveId               This field indicates which objective, in the case of a multi-objective problem,
     *                                  is selected to be optimized.
     * @param swarmSize
     * @param maxIterations
     * @param numberOfParticlesToInform
     * @param evaluator
     */
    public StandardPSO2007(DoubleProblem problem, int objectiveId, int swarmSize, int maxIterations,
                           int numberOfParticlesToInform, SolutionListEvaluator<DoubleSolution> evaluator) {
        this.problem = problem;
        this.swarmSize = swarmSize;
        this.maxIterations = maxIterations;
        this.numberOfParticlesToInform = numberOfParticlesToInform;
        this.evaluator = evaluator;
        this.objectiveId = objectiveId;

        weight = 1.0 / (2.0 * Math.log(2));
        c = 1.0 / 2.0 + Math.log(2);

        fitnessComparator = new ObjectiveComparator<DoubleSolution>(objectiveId);
        findBestSolution = new BestSolutionSelection<DoubleSolution>(fitnessComparator);

        localBest = new DoubleSolution[swarmSize];
        neighborhoodBest = new DoubleSolution[swarmSize];
        speed = new double[swarmSize][problem.getNumberOfVariables()];

        positionInSwarm = new GenericSolutionAttribute<DoubleSolution, Integer>();

        bestFoundParticle = null;
        neighborhood = new AdaptiveRandomNeighborhood<DoubleSolution>(swarmSize, this.numberOfParticlesToInform);
    }

    /**
     * Constructor
     *
     * @param problem
     * @param swarmSize
     * @param maxIterations
     * @param numberOfParticlesToInform
     * @param evaluator
     */
    public StandardPSO2007(DoubleProblem problem, int swarmSize, int maxIterations,
                           int numberOfParticlesToInform, SolutionListEvaluator<DoubleSolution> evaluator) {
        this(problem, 0, swarmSize, maxIterations, numberOfParticlesToInform, evaluator);
    }

    @Override
    public void initProgress() {
        iterations = 1;
    }

    @Override
    public void updateProgress() {
        iterations += 1;
    }

    @Override
    public boolean isStoppingConditionReached() {
        return iterations >= maxIterations;
    }

    @Override
    public List<DoubleSolution> createInitialSwarm() {
        List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

        DoubleSolution newSolution;
        for (int i = 0; i < swarmSize; i++) {
            newSolution = problem.createSolution();
            positionInSwarm.setAttribute(newSolution, i);
            swarm.add(newSolution);
        }

        return swarm;
    }

    @Override
    public List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
        swarm = evaluator.evaluate(swarm, problem);

        return swarm;
    }

    @Override
    public void initializeLeader(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            neighborhoodBest[i] = getNeighborBest(i);
        }
    }

    @Override
    public void initializeParticlesMemory(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            localBest[i] = (DoubleSolution) swarm.get(i).copy();
        }
    }

    @Override
    public void initializeVelocity(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            DoubleSolution particle = swarm.get(i);
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                speed[i][j] =
                        (randomGenerator.nextDouble(particle.getLowerBound(j), particle.getUpperBound(j))
                                - particle.getVariableValue(j)) / 2.0;
            }
        }
    }

    @Override
    public void updateVelocity(List<DoubleSolution> swarm) {
        double r1, r2;

        for (int i = 0; i < swarmSize; i++) {
            DoubleSolution particle = swarm.get(i);

            r1 = randomGenerator.nextDouble(0, c);
            r2 = randomGenerator.nextDouble(0, c);

            if (localBest[i] != neighborhoodBest[i]) {
                for (int var = 0; var < particle.getNumberOfVariables(); var++) {
                    speed[i][var] = weight * speed[i][var] +
                            r1 * (localBest[i].getVariableValue(var) - particle.getVariableValue(var)) +
                            r2 * (neighborhoodBest[i].getVariableValue(var) - particle.getVariableValue
                                    (var));
                }
            } else {
                for (int var = 0; var < particle.getNumberOfVariables(); var++) {
                    speed[i][var] = weight * speed[i][var] +
                            r1 * (localBest[i].getVariableValue(var) -
                                    particle.getVariableValue(var));
                }
            }
        }
    }

    @Override
    public void updatePosition(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarmSize; i++) {
            DoubleSolution particle = swarm.get(i);
            for (int var = 0; var < particle.getNumberOfVariables(); var++) {
                particle.setVariableValue(var, particle.getVariableValue(var) + speed[i][var]);

                if (particle.getVariableValue(var) < problem.getLowerBound(var)) {
                    particle.setVariableValue(var, problem.getLowerBound(var));
                    speed[i][var] = 0;
                }
                if (particle.getVariableValue(var) > problem.getUpperBound(var)) {
                    particle.setVariableValue(var, problem.getUpperBound(var));
                    speed[i][var] = 0;
                }
            }
        }
    }

    @Override
    public void perturbation(List<DoubleSolution> swarm) {
    /*
    MutationOperator<DoubleSolution> mutation =
            new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0) ;
    for (DoubleSolution particle : swarm) {
      mutation.execute(particle) ;
    }
    */
    }

    @Override
    public void updateLeaders(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            neighborhoodBest[i] = getNeighborBest(i);
        }

        DoubleSolution bestSolution = findBestSolution.execute(swarm);

        if (bestFoundParticle == null) {
            bestFoundParticle = bestSolution;
        } else {
            if (bestSolution.getObjective(objectiveId) == bestFoundParticle.getObjective(0)) {
                neighborhood.recompute();
            }
            if (bestSolution.getObjective(objectiveId) < bestFoundParticle.getObjective(0)) {
                bestFoundParticle = bestSolution;
            }
        }
    }

    @Override
    public void updateParticlesMemory(List<DoubleSolution> swarm) {
        for (int i = 0; i < swarm.size(); i++) {
            if ((swarm.get(i).getObjective(objectiveId) < localBest[i].getObjective(0))) {
                localBest[i] = (DoubleSolution) swarm.get(i).copy();
            }
        }
    }

    @Override
    public DoubleSolution getResult() {
        return bestFoundParticle;
    }

    private DoubleSolution getNeighborBest(int i) {
        DoubleSolution bestLocalBestSolution = null;

        for (DoubleSolution solution : neighborhood.getNeighbors(getSwarm(), i)) {
            int solutionPositionInSwarm = positionInSwarm.getAttribute(solution);
            if ((bestLocalBestSolution == null) || (bestLocalBestSolution.getObjective(0)
                    > localBest[solutionPositionInSwarm].getObjective(0))) {
                bestLocalBestSolution = localBest[solutionPositionInSwarm];
            }
        }

        return bestLocalBestSolution;
    }

    /* Getters */
    public double[][] getSwarmSpeedMatrix() {
        return speed;
    }

    public DoubleSolution[] getLocalBest() {
        return localBest;
    }

    @Override
    public String getName() {
        return "SPSO07";
    }

    @Override
    public String getDescription() {
        return "Standard PSO 2007";
    }
}