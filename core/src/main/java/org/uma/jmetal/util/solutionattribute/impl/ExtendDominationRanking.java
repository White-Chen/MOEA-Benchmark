package org.uma.jmetal.util.solutionattribute.impl;

import com.sun.deploy.resources.Deployment_sv;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zhou-LF on 2017/3/20.
 */
public class ExtendDominationRanking<S extends Solution<?>> extends DominanceRanking<S> {

    private int[] hierarchyIndex;

    public ExtendDominationRanking(){
        super();
    }

    public int[] getHierarchyIndex(){
        return hierarchyIndex;
    }

    public int[] computeExtendRanking(List<S> solutionSet, List<S> extendFront){
        List<S> population = solutionSet;
        hierarchyIndex = new int[population.size()];

        DominanceRanking<S> dominanceRanking1 = new DominanceRanking();
        Ranking<S> dominanceRanking = dominanceRanking1.computeRanking(population);
        hierarchyIndex = dominanceRanking1.getHierarchyIndex();

        List<S> firstFront = dominanceRanking.getSubfront(0);
        List<S> firstFrontExtend = new ArrayList<S>(firstFront.size());
        for (int i = 0; i < firstFront.size(); i++){
            firstFrontExtend.add(extendFront.get(i));
        }

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
                firstFrontExtend.get(i).setObjective(j, r * Math.sin(w + s) / Math.sin(s));
            }
        }
        System.out.println(firstFrontExtend.size());
        DominanceRanking<S> dominanceRanking2 = new DominanceRanking();
        Ranking<S> firstFrontDominaceRanking = dominanceRanking2.computeRanking(firstFrontExtend);

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
        return hierarchyIndex;
    }
}
