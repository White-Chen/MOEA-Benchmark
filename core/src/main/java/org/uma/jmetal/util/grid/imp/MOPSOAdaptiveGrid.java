package org.uma.jmetal.util.grid.imp;

import org.uma.jmetal.solution.Solution;

/**
 * \* Created with Chen Zhe on 2/12/2017.
 * \* Description:
 * \* @author ChenZhe
 * \* @author q953387601@163.com
 * \* @version 1.0.0
 * \
 */
public class MOPSOAdaptiveGrid <S extends Solution<?>>
        extends VariationAdaptiveGrid<S> {

    public MOPSOAdaptiveGrid(){
        super();
    }

    public MOPSOAdaptiveGrid(int bisections, int objectives, int divisionNumber) {
        super(bisections, objectives, divisionNumber);
    }

    /**
     * Returns the density of a specific hypercube.
     *
     * @param location Number of the hypercube.
     * @return The number of solutions into a specific hypercube.
     */
    @Override
    public int getLocationDensity(int location) {
        return densityMemory[location];
    }

    public int computeLocationDensity(int location){

        return location;
    }
}
