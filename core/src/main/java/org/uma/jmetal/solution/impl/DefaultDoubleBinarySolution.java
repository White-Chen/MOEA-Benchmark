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

package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleBinaryProblem;
import org.uma.jmetal.solution.DoubleBinarySolution;

import java.util.BitSet;
import java.util.HashMap;

/**
 * Description:
 * - this solution contains an array of double value + a binary string
 * - getNumberOfVariables() returns the number of double values + 1 (the string)
 * - getNumberOfDoubleVariables() returns the number of double values
 * - getNumberOfVariables() = getNumberOfDoubleVariables() + 1
 * - the bitset is the last variable
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDoubleBinarySolution
        extends AbstractGenericSolution<Object, DoubleBinaryProblem<?>>
        implements DoubleBinarySolution {
    private int numberOfDoubleVariables;

    /**
     * Constructor
     */
    public DefaultDoubleBinarySolution(DoubleBinaryProblem<?> problem) {
        super(problem);

        numberOfDoubleVariables = problem.getNumberOfDoubleVariables();

        initializeDoubleVariables();
        initializeBitSet();
        initializeObjectiveValues();
    }

    /**
     * Copy constructor
     */
    public DefaultDoubleBinarySolution(DefaultDoubleBinarySolution solution) {
        super(solution.problem);
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            setObjective(i, solution.getObjective(i));
        }

        copyDoubleVariables(solution);
        copyBitSet(solution);

        // overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
        // numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;

        attributes = new HashMap<Object, Object>(solution.attributes);
    }

    private void initializeDoubleVariables() {
        for (int i = 0; i < numberOfDoubleVariables; i++) {
            Double value = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i));
            //variables.add(value) ;
            setVariableValue(i, value);
        }
    }

    private void initializeBitSet() {
        BitSet bitset = createNewBitSet(problem.getNumberOfBits());
        //variables.add(bitset) ;
        setVariableValue(numberOfDoubleVariables, bitset);
    }

    private void copyDoubleVariables(DefaultDoubleBinarySolution solution) {
//    variables = new ArrayList<>() ;
//    for (int i = 0 ; i < numberOfDoubleVariables; i++) {
//      variables.add(new Double((Double) solution.getVariableValue(i))) ;
        for (int i = 0; i < numberOfDoubleVariables; i++) {
            setVariableValue(i, solution.getVariableValue(i));
        }
    }

    private void copyBitSet(DefaultDoubleBinarySolution solution) {
        BitSet bitset = (BitSet) solution.getVariableValue(solution.getNumberOfVariables() - 1);
        //variables.add(bitset.clone()) ;
        setVariableValue(numberOfDoubleVariables, bitset);
    }

    @Override
    public int getNumberOfDoubleVariables() {
        return numberOfDoubleVariables;
    }

    @Override
    public Double getUpperBound(int index) {
        return (Double) problem.getUpperBound(index);
    }

    @Override
    public int getNumberOfBits() {
        return problem.getNumberOfBits();
    }

    @Override
    public Double getLowerBound(int index) {
        return (Double) problem.getLowerBound(index);
    }

    @Override
    public DefaultDoubleBinarySolution copy() {
        return new DefaultDoubleBinarySolution(this);
    }

    @Override
    public String getVariableValueString(int index) {
        return getVariableValue(index).toString();
    }

    private BitSet createNewBitSet(int numberOfBits) {
        BitSet bitSet = new BitSet(numberOfBits);

        for (int i = 0; i < numberOfBits; i++) {
            if (randomGenerator.nextDouble() < 0.5) {
                bitSet.set(i, true);
            } else {
                bitSet.set(i, false);
            }
        }
        return bitSet;
    }
}
