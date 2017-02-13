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
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveIII;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.grid.util.ShuffleListBuilder;

import java.util.Arrays;
import java.util.List;

public class MOPSOpd extends DMOPSO implements Algorithm<List<DoubleSolution>> {
    private static final long serialVersionUID = 7100961861651318429L;
    private AdaptiveGridArchiveIII<DoubleSolution> leadersArchive;
    private double[] iqrs;
    private double[] mids;
    private double[] entropies;
    private double[] entropyDiff;
    private double threshold;
    private int condition;

    @Override
    protected void resetParticle(int i) {
        super.resetParticle(i);
    }

    public MOPSOpd(DoubleProblem problem, int swarmSize,
                   int maxIterations, double r1Min, double r1Max,
                   double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                   double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                   String functionType, int maxAge,
                   String dataDirectory, String inProcessDataPath,
                   AdaptiveGridArchiveIII<DoubleSolution> leadersArchive,
                   int eliminatePressure, int selectionPressure, int divisionNumber, double threshold) {
        super(problem,swarmSize,maxIterations,r1Min,r1Max,r2Min,r2Max,c1Min,c1Max,c2Min,c2Max,
                weightMin,weightMax,changeVelocity1,changeVelocity2,functionType,maxAge,
                dataDirectory,inProcessDataPath);
        this.leadersArchive = leadersArchive;
        iqrs = new double[problem.getNumberOfVariables()];
        mids = new double[problem.getNumberOfVariables()];
        entropies = new double[maxIterations];
        entropyDiff = new double[maxIterations];
        this.threshold = threshold;

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

    private void computeEntropy(){

        int archiveSize = leadersArchive.size();
        double[] position = new double[archiveSize];
        double midDiff;
        double iqrValue;
        double q1, q3;
        double midValue = 0;

        List<DoubleSolution> solutions = leadersArchive.getSolutionList();
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            //1. sort
            for (int j = 0; j < archiveSize; j++) {
                position[j] = solutions.get(j).getVariableValue(i);
            }
            Arrays.sort(position);

            //2. iqr compute
            q1 = position[(archiveSize/4 - 1) < 0 ? 0 : (archiveSize/4 - 1)] ;
            q3 = position[(archiveSize*3/4 - 1) < 0 ? 0 : (archiveSize*3/4 -1)];
            iqrValue = Math.abs(q1-q3);
            iqrValue = iqrValue/(position[archiveSize-1]-position[0]);
            // init iterations == 1
            iqrs[i] = iqrValue;

            //3. mid compute
            midDiff = iterations > 1 ? Math.abs(position[(archiveSize/2 - 1) < 0 ? 0 : (archiveSize/2 -1)] - mids[i]): 0;
            mids[i] = position[archiveSize/2 -1];

            //4. compute entropy
            entropies[iterations - 1] = entropies[iterations - 1]
                    - iqrs[i] * Math.log(iqrs[i])
                    - midDiff * Math.log(midDiff);
        }

        //5. compute entropy diff
        entropyDiff[iterations - 1] = iterations > 1 ? Math.abs(entropyDiff[iterations - 1] - entropies[iterations - 2]) : 0;
    }

    // 0:explore
    // 1:localSearch
    // 2:convergence
    private void classifyEvolutionaryCondition(){
        if (iterations <= 10){
            condition = 0;
        }
        else {
            double entropyAccumulate = 0;
            for (int i = iterations - 11; i < (iterations - 1); i++) {
                entropyAccumulate += entropyDiff[i];
            }
            if (entropyAccumulate >= threshold)
                condition = 0;
            else if(leadersArchive.size() < leadersArchive.getMaxSize())
                condition = 1;
            else if(leadersArchive.size() == leadersArchive.getMaxSize())
                condition = 2;
        }

    }

    private void setCondition(){
        leadersArchive.setCondition(condition);
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
            computeEntropy();
            classifyEvolutionaryCondition();
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


    public AdaptiveGridArchiveIII<DoubleSolution> getLeadersArchive() {
        return leadersArchive;
    }

    public void setLeadersArchive(AdaptiveGridArchiveIII<DoubleSolution> leadersArchive) {
        this.leadersArchive = leadersArchive;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}