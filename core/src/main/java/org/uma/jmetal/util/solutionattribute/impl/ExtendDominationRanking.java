package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.*;

/**
 * Created by zhoulifa on 17-2-23.
 */
public class ExtendDominationRanking extends DominanceRanking<DoubleSolution> {

    private List<List<DoubleSolution>> rankedSubPopulations;
    private int[] hierarchyIndex;

    public ExtendDominationRanking(){
        super();
    }

    public int[] getHierarchyIndex(){
        return hierarchyIndex;
    }

    public static void main(String[] args){

    }

    public int[] computeExtendRanking(List<DoubleSolution> solutionSet){
        List<DoubleSolution> population = solutionSet;
        hierarchyIndex = new int[population.size()];

        DominanceRanking<DoubleSolution> dominanceRanking1 = new DominanceRanking();
        Ranking<DoubleSolution> dominanceRanking = dominanceRanking1.computeRanking(population);
        hierarchyIndex = dominanceRanking1.getHierarchyIndex();

        List<DoubleSolution> firstFront = dominanceRanking.getSubfront(0);

        double s = 0.34 * Math.PI;
        for (int i = 0; i < firstFront.size(); i++){
            double r = 0.0;
            for (int j = 0; j < firstFront.get(i).getNumberOfObjectives(); j++){
                r = r + Math.pow(firstFront.get(i).getObjective(j), 2);
            }
            r = Math.sqrt(r);

            double w;
            for (int j = 0; j < firstFront.get(i).getNumberOfObjectives(); j++){
                w = Math.acos(firstFront.get(i).getObjective(j) / r);
                firstFront.get(i).setObjective(j, r * Math.sin(w + s) / Math.sin(s));
            }
        }
        DominanceRanking<DoubleSolution> dominanceRanking2 = new DominanceRanking();
        Ranking<DoubleSolution> firstFrontDominaceRanking = dominanceRanking2.computeRanking(firstFront);

        int[] hierarchyFirstIndex = dominanceRanking2.getHierarchyIndex();
        int temp = firstFrontDominaceRanking.getNumberOfSubfronts() - 1;
        int j = 0;
        for(int i = 0; i < population.size(); i++){
            if(hierarchyIndex[i] == 0){
                hierarchyIndex[i] = hierarchyFirstIndex[j];
                j++;
            }else{
                hierarchyIndex[i] += temp;
            }
        }

        rankedSubPopulations = new ArrayList<>();
        //0,1,2,....,i-1 are fronts, then i fronts
        for (int i = 0; i < firstFrontDominaceRanking.getNumberOfSubfronts(); i++) {
            rankedSubPopulations.add(i, firstFrontDominaceRanking.getSubfront(i));
        }
        for (int i = 1; i < dominanceRanking.getNumberOfSubfronts(); i++) {
            rankedSubPopulations.add(i + temp, dominanceRanking.getSubfront(i));
        }
        return hierarchyIndex;
    }
}
