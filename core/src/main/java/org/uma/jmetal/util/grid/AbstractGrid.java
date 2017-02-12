package org.uma.jmetal.util.grid;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lenovo on 2015/11/3.
 */
public interface AbstractGrid<S extends Solution<?>> extends Serializable {

    /**
     * Updates the grid limits considering the solutions contained in a
     * <code>solutionList</code>.
     *
     * @param solutionList The <code>solutionList</code> considered.
     */
    void updateLimits(List<S> solutionList);

    /**
     * Updates the grid adding solutions contained in a specific
     * <code>solutionList</code>.
     * <b>REQUIRE</b> The grid limits must have been previously calculated.
     *
     * @param solutionList The <code>solutionList</code> considered.
     */
    void addSolutionSet(List<S> solutionList);

    /**
     * Updates the grid limits and the grid content adding the solutions contained
     * in a specific <code>solutionList</code>.
     *
     * @param solutionList The <code>solutionList</code>.
     */
    void updateGrid(List<S> solutionList);

    /**
     * Updates the grid limits and the grid content adding a new
     * <code>Solution</code>.
     * If the solution falls out of the grid bounds, the limits and content of the
     * grid must be re-calculated.
     *
     * @param solution    <code>Solution</code> considered to update the grid.
     * @param solutionSet <code>SolutionSet</code> used to update the grid.
     */
    void updateGrid(S solution, List<S> solutionSet);

    /**
     * Calculates the hypercube of a solution
     *
     * @param solution The <code>Solution</code>.
     */
    int location(S solution);

    /**
     * Returns the value of the most populated hypercube.
     *
     * @return The hypercube with the maximum number of solutions.
     */
    int getMostPopulatedHypercube();

    /**
     * Returns the number of solutions into a specific hypercube.
     *
     * @param location Number of the hypercube.
     * @return The number of solutions into a specific hypercube.
     */
    double getLocationDensity(int location);

    /**
     * Decreases the number of solutions into a specific hypercube.
     *
     * @param location Number of hypercube.
     */
    void removeSolution(int location);

    /**
     * Increases the number of solutions into a specific hypercube.
     *
     * @param location Number of hypercube.
     */
    void addSolution(int location);


    /**
     * Returns a String representing the grid.
     *
     * @return The String.
     */
    String toString();

    /**
     * Returns a random hypercube using a rouleteWheel method.
     *
     * @return the number of the selected hypercube.
     */
    int rouletteWheel4Selection();

    /**
     * Returns a random hypercube using a rouleteWheel method
     *
     * @return the number of the selected hypercube
     */
    int rouletteWheel4Prune();

    /**
     * Calculates the number of hypercubes having one or more solutions.
     * return the number of hypercubes with more than zero solutions.
     */
    int calculateOccupied();

    /**
     * Returns the number of hypercubes with more than zero solutions.
     *
     * @return the number of hypercubes with more than zero solutions.
     */
    int occupiedHypercubes();

    /**
     * Returns a random hypercube that has more than zero solutions.
     *
     * @return The hypercube.
     */
    int randomOccupiedHypercube();

}
