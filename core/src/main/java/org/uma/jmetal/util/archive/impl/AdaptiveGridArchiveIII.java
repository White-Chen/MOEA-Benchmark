package org.uma.jmetal.util.archive.impl;


import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.grid.imp.VariationAdaptiveGrid;

import java.util.*;

/**
 * Created by ChenZhe on 2015/11/5.
 * implementation of archive in MOPSOpd
 *
 * @author chenzhe <q953387601@163.com>
 */
public class AdaptiveGridArchiveIII<S extends Solution<?>> extends AbstractBoundedArchive<S> {

    private static final long serialVersionUID = -3423726064916214261L;
    private VariationAdaptiveGrid<S> grid;

    private Comparator<S> dominanceComparator;
    private int condition;
    private Random random = new Random();

    /**
     * Constructor.
     *
     * @param maxSize    The maximum size of the setArchive
     * @param divisionNumber The maximum number of sub-divisions for the adaptive
     *                   grid.
     * @param objectives The number of objectives.
     */
    public AdaptiveGridArchiveIII(int maxSize, int divisionNumber, int objectives) {
        super(maxSize);
        dominanceComparator = new DominanceComparator<S>();
        grid = new VariationAdaptiveGrid<S>(1, objectives,divisionNumber);
    }

    /**
     * Adds a <code>Solution</code> to the setArchive. If the <code>Solution</code>
     * is dominated by any member of the setArchive then it is discarded. If the
     * <code>Solution</code> dominates some members of the setArchive, these are
     * removed. If the setArchive is full and the <code>Solution</code> has to be
     * inserted, one <code>Solution</code> of the most populated hypercube of the
     * adaptive grid is removed.
     *
     * @param solution The <code>Solution</code>
     * @return true if the <code>Solution</code> has been inserted, false
     * otherwise.
     */
    public boolean add(S solution) {
        //Iterator of individuals over the list
        Iterator<S> iterator = getSolutionList().iterator();

        while (iterator.hasNext()) {
            S element = iterator.next();
            int flag = dominanceComparator.compare(solution, element);
            if (flag == -1) { // The Individual to insert dominates other
                // individuals in  the setArchive
                iterator.remove(); //Delete it from the setArchive
                int location = grid.location(element);
                if (grid.getLocationDensity(location) > 1) {//The hypercube contains
                    grid.removeSolution(location);            //more than one individual
                } else {
                    grid.updateGrid(getSolutionList());
                }
            } else if (flag == 1) { // An Individual into the file dominates the
                // solution to insert
                return false; // The solution will not be inserted
            }
        }

        // At this point, the solution may be inserted
        if (this.size() == 0) { //The setArchive is empty
            this.getSolutionList().add(solution);
            grid.updateGrid(getSolutionList());
            return true;
        }

        if (this.getSolutionList().size() < this.getMaxSize()) { //The setArchive is not full
            grid.updateGrid(solution, getSolutionList()); // Update the grid if applicable
            int location;
            location = grid.location(solution); // Get the location of the solution
            grid.addSolution(location); // Increment the density of the hypercube
            getSolutionList().add(solution); // Add the solution to the list
            return true;
        }

        // At this point, the solution has to be inserted and the setArchive is full
        grid.updateGrid(solution, getSolutionList());
        grid.addSolution(grid.location(solution));
        getSolutionList().add(solution);
        int location = grid.location(solution);
        double[] min = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        double[][] indicators = new double[getSolutionList().size()][3];
        for (int i = 0; i < getSolutionList().size(); i++) {
            indicators[i] = computeDensity(getSolutionList().get(i));
            for (int k = 0; k < 3; k++) {
                if(min[k] > indicators[i][k]){
                    min[k] = indicators[i][k];
                }
            }
        }

        int locationIndex = 0;
        List<Integer> cIndexs = new ArrayList<>();
        List<Integer> dIndexs = new ArrayList<>();
        switch (condition){
            case 0:
            case 1:
                for (int i = 0; i < indicators.length; i++) {
                    if(indicators[i][1] == min[1])
                        cIndexs.add(i);
                }
                double dmin = Double.MAX_VALUE;
                for (int i = 0; i < cIndexs.size(); i++) {
                    if(dmin == indicators[i][0])
                        dIndexs.add(i);
                    else if(dmin > indicators[i][0]){
                        dIndexs.clear();
                        dIndexs.add(i);
                        dmin = indicators[i][0];
                    }
                }
                if (dIndexs.size() == 1)
                    locationIndex = dIndexs.get(0);
                else {
                    double c2min = Double.MAX_VALUE;
                    for (int i = 0; i < dIndexs.size(); i++) {
                        if(c2min > indicators[i][2]){
                            c2min = indicators[i][2];
                            locationIndex = dIndexs.get(i);
                        }
                    }
                }
                break;
            case 2:
                for (int i = 0; i < indicators.length; i++) {
                    if(indicators[i][0] == min[0])
                        dIndexs.add(i);
                }
                double cmin = Double.MAX_VALUE;
                for (int i = 0; i < dIndexs.size(); i++) {
                    if(cmin == indicators[i][1])
                        cIndexs.add(i);
                    else if(cmin > indicators[i][1]){
                        cIndexs.clear();
                        cIndexs.add(i);
                        cmin = indicators[i][1];
                    }
                }
                if (cIndexs.size() == 1)
                    locationIndex = cIndexs.get(0);
                else {
                    double c2min = Double.MAX_VALUE;
                    for (int i = 0; i < cIndexs.size(); i++) {
                        if(c2min > indicators[i][2]){
                            c2min = indicators[i][2];
                            locationIndex = cIndexs.get(i);
                        }
                    }
                }
                break;
        }
        solution = getSolutionList().get(locationIndex);
        location = grid.location(solution);
        grid.removeSolution(location);
        getSolutionList().remove(locationIndex);
        return true;
    }

    public VariationAdaptiveGrid<S> getGrid() {
        return grid;
    }

    public void prune() {
        Iterator<S> iterator = getSolutionList().iterator();
        int index = grid.rouletteWheel4Prune();
        while (iterator.hasNext()) {
            S element = iterator.next();
            int location2 = grid.location(element);
            if (location2 == index) {
                iterator.remove();
                grid.removeSolution(location2);
            }
        }
    }

    public S select(){

        double[] min = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        double[][] indicators = new double[getSolutionList().size()][3];
        for (int i = 0; i < getSolutionList().size(); i++) {
            indicators[i] = computeDensity(getSolutionList().get(i));
            for (int k = 0; k < 3; k++) {
                if(min[k] > indicators[i][k]){
                    min[k] = indicators[i][k];
                }
            }
        }

        int locationIndex = 0;
        List<Integer> cIndexs = new ArrayList<>();
        List<Integer> dIndexs = new ArrayList<>();
        switch (condition){
            case 0:
            case 1:
                for (int i = 0; i < indicators.length; i++) {
                    if(indicators[i][1] == min[1])
                        cIndexs.add(i);
                }
                double dmin = Double.MAX_VALUE;
                for (int i = 0; i < cIndexs.size(); i++) {
                    if(dmin == indicators[i][0])
                        dIndexs.add(i);
                    else if(dmin > indicators[i][0]){
                        dIndexs.clear();
                        dIndexs.add(i);
                        dmin = indicators[i][0];
                    }
                }
                locationIndex = random.nextInt(dIndexs.size());
                break;
            case 2:
                for (int i = 0; i < indicators.length; i++) {
                    if(indicators[i][0] == min[0])
                        dIndexs.add(i);
                }
                double cmin = Double.MAX_VALUE;
                for (int i = 0; i < dIndexs.size(); i++) {
                    if(cmin == indicators[i][1])
                        cIndexs.add(i);
                    else if(cmin > indicators[i][1]){
                        cIndexs.clear();
                        cIndexs.add(i);
                        cmin = indicators[i][1];
                    }
                }
                locationIndex = random.nextInt(cIndexs.size());
                break;
        }
        return getSolutionList().get(locationIndex);
    }

    public double[] computeDensity(S solution){
        //1. find near solutions
        //Iterator of individuals over the list
        boolean[] isNear = new boolean[this.size()];
        double[] diff = new double[this.size()];
        int[] position_A = grid.positions(solution);
        for (int i = 0; i < getSolutionList().size(); i++) {
            diff[i] = 0;
            int[] position_B = grid.positions(getSolutionList().get(i));
            for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
                diff[i] = Math.abs(position_A[j] - position_B[j]);
            }
            isNear[i] = diff[i] < solution.getNumberOfObjectives();
        }

        //2. compute density itself
        double density = 0;

        for (int i = 0; i < isNear.length; i++) {
            if(!isNear[i]) continue;
            density += solution.getNumberOfObjectives() - diff[i];
        }

        //3. compute convergence itself
        double convergence_1 = 0;
        for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
            convergence_1 += position_A[i];
        }

        //4. compute convergence itself
        double convergence_2 = 0;
        for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
            convergence_2 += Math.pow((solution.getObjective(i) - ((grid.getGridLowerLimits())[i] + position_A[i])*(grid.getDivisionSize())[i])/(grid.getDivisionSize())[i], 2);
        }
        convergence_2 = Math.pow(convergence_2, 0.5);
        double[] indicator = {density, convergence_1, convergence_2};
        return indicator;
    }


    @Override
    public Comparator<S> getComparator() {
        return null;
    }

    @Override
    public void computeDensityEstimator() {
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }
}
