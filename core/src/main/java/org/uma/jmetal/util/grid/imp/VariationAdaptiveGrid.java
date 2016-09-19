package org.uma.jmetal.util.grid.imp;

import org.uma.jmetal.solution.Solution;

/**
 * This class is implementation of the adaptive grid in adMOPSO class
 * Created by Lenovo on 2015/11/3.
 */
public class VariationAdaptiveGrid<S extends Solution<?>>
        extends AdaptiveGrid<S>{

    private int divisionNumber;

    public VariationAdaptiveGrid() {
        this.setBisections(1)
            .setDivisionNumber(30);
    }

    public VariationAdaptiveGrid(int bisections, int objetives, int divisionNumber) {
        this.setDivisionNumber(divisionNumber)
                .setNumberOfObjectives(objetives)
                .setGridUpperLimits(new double[numberOfObjectives])
                .setGridLowerLimits(new double[numberOfObjectives])
                .setDivisionSize(new double[numberOfObjectives])
                .setSelectionPressure(4)
                .setEliminatePressure(2)
                .setBisections(1)
                .setHypercubes(new int[(int) Math.pow(divisionNumber, bisections * numberOfObjectives)]);

        for (int i = 0; i < hypercubes.length; i++) {
            hypercubes[i] = 0;
        }
    }

    /**
     * Calculates the hypercube of a solution
     *
     * @param solution The <code>Solution</code>.
     */
    @Override
    public int location(S solution) {

        //Create a int [] to store the range of each objective
        int[] position = new int[numberOfObjectives];

        //Calculate the position for each objective
        for (int obj = 0; obj < numberOfObjectives; obj++) {
            if ((solution.getObjective(obj) > gridUpperLimits[obj])
                    || (solution.getObjective(obj) < gridLowerLimits[obj])) {
                return -1;
            } else if (solution.getObjective(obj) == gridLowerLimits[obj]) {
                position[obj] = 0;
            } else if (solution.getObjective(obj) == gridUpperLimits[obj]) {
                position[obj] = ((int) Math.pow(divisionNumber, bisections)) - 1;
            } else {
                double tmpSize = divisionSize[obj];
                double value = solution.getObjective(obj);
                double account = gridLowerLimits[obj];
                int ranges = (int) Math.pow(divisionNumber, bisections);

                tmpSize /= ranges;
                position[obj] =(int) ((value - account) / tmpSize);
            }
        }

        //Calculate the location into the hypercubes
        int location = 0;
        for (int obj = 0; obj < numberOfObjectives; obj++) {
            location += position[obj] * Math.pow(divisionNumber, obj * bisections);
        }
        return location;
    }

    /**
     * Returns the number of solutions into a specific hypercube.
     *
     * @param location Number of the hypercube.
     * @return The number of solutions into a specific hypercube.
     */
    @Override
    public int getLocationDensity(int location) {
        return hypercubes[location];
    }



    /**
     * -------------------------------------------------------------
     * overwrite superclass setter() and getter() method
     * -------------------------------------------------------------
     */
    @Override
    public VariationAdaptiveGrid<S> setEliminatePressure(int eliminatePressure) {
        this.eliminatePressure = eliminatePressure;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setSelectionPressure(int selectionPressure) {
        this.selectionPressure = selectionPressure;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setMostPopulatedHypercube(int mostPopulatedHypercube) {
        this.mostPopulatedHypercube = mostPopulatedHypercube;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setDivisionSize(double[] divisionSize) {
        this.divisionSize = divisionSize;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setGridUpperLimits(double[] gridUpperLimits) {
        this.gridUpperLimits = gridUpperLimits;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setGridLowerLimits(double[] gridLowerLimits) {
        this.gridLowerLimits = gridLowerLimits;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setHypercubes(int[] hypercubes) {
        this.hypercubes = hypercubes;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setNumberOfObjectives(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
        return this;
    }

    @Override
    public VariationAdaptiveGrid<S> setBisections(int bisections) {
        this.bisections = bisections;
        return this;
    }

    /**
     * -------------------------------------------------------------
     * implementation subclass setter() and getter() method
     * -------------------------------------------------------------
     */
    public int getDivisionNumber() {
        return divisionNumber;
    }

    public VariationAdaptiveGrid<S> setDivisionNumber(int divisionNumber) {
        this.divisionNumber = divisionNumber;
        return this;
    }
}

