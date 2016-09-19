package org.uma.jmetal.solution;

/**
 * Interface representing a solution having an array of real values and a bitset
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleBinarySolution extends Solution<Object> {
    int getNumberOfDoubleVariables();

    Double getLowerBound(int index);

    Double getUpperBound(int index);

    int getNumberOfBits();
}
