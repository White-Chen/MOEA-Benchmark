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

package org.uma.jmetal.problem.multiobjective.cdtlz;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem C1-DTLZ3, defined in:
 * Jain, H. and K. Deb.  "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 * Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 * EEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class C1_DTLZ3 extends DTLZ3 implements ConstrainedProblem<DoubleSolution> {
    private static Map<Integer, Double> rValue;

    static {
        rValue = new HashMap<Integer, Double>();
        rValue.put(3, 9.0);
        rValue.put(5, 12.5);
        rValue.put(8, 12.5);
        rValue.put(10, 15.0);
        rValue.put(15, 15.0);
    }

    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;

    /**
     * Constructor
     *
     * @param numberOfVariables
     * @param numberOfObjectives
     */
    public C1_DTLZ3(int numberOfVariables, int numberOfObjectives) {
        super(numberOfVariables, numberOfObjectives);

        setNumberOfConstraints(1);

        overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>();
        numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>();
    }

    @Override
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.getNumberOfConstraints()];

        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < getNumberOfObjectives(); i++) {
            double v = Math.pow(solution.getObjective(i), 2);
            sum1 += v - 16.0;
            sum2 += v - Math.pow(rValue.get(getNumberOfObjectives()), 2.0);
        }

        constraint[0] = sum1 * sum2;

        double overallConstraintViolation = 0.0;
        int violatedConstraints = 0;
        for (int i = 0; i < getNumberOfConstraints(); i++) {
            if (constraint[i] < 0.0) {
                overallConstraintViolation += constraint[i];
                violatedConstraints++;
            }
        }

        overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
        numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }
}
