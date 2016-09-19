//  CEC2009_UF9
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

package org.uma.jmetal.problem.multiobjective.cec2009Competition;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem CEC2009_UF9
 */
public class UF9 extends AbstractDoubleProblem {
    private static final long serialVersionUID = -1088295875508130204L;
    double epsilon;

    /**
     * Constructor.
     * Creates a default instance of problem CEC2009_UF9 (30 decision variables, epsilon = 0.1)
     */
    public UF9() {
        this(30, 0.1);
    }

    /**
     * Creates a new instance of problem CEC2009_UF9.
     *
     * @param numberOfVariables Number of variables.
     */
    public UF9(int numberOfVariables, double epsilon) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(2);
        setNumberOfConstraints(0);
        setName("UF9");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        this.epsilon = epsilon;

        lowerLimit.add(0.0);
        upperLimit.add(1.0);
        lowerLimit.add(0.0);
        upperLimit.add(1.0);
        for (int i = 2; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-2.0);
            upperLimit.add(2.0);
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    /**
     * Evaluate() method
     */
    @Override
    public void evaluate(DoubleSolution solution) {
        double[] x = new double[getNumberOfVariables()];
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            x[i] = solution.getVariableValue(i);
        }

        int count1, count2, count3;
        double sum1, sum2, sum3, yj;
        sum1 = sum2 = sum3 = 0.0;
        count1 = count2 = count3 = 0;

        for (int j = 3; j <= getNumberOfVariables(); j++) {
            yj = x[j - 1] - 2.0 * x[1] * Math.sin(2.0 * Math.PI * x[0] + j * Math.PI / getNumberOfVariables());
            if (j % 3 == 1) {
                sum1 += yj * yj;
                count1++;
            } else if (j % 3 == 2) {
                sum2 += yj * yj;
                count2++;
            } else {
                sum3 += yj * yj;
                count3++;
            }
        }

        yj = (1.0 + epsilon) * (1.0 - 4.0 * (2.0 * x[0] - 1.0) * (2.0 * x[0] - 1.0));
        if (yj < 0.0)
            yj = 0.0;

        solution.setObjective(0, 0.5 * (yj + 2 * x[0]) * x[1] + 2.0 * sum1 / (double) count1);
        solution.setObjective(1, 0.5 * (yj - 2 * x[0] + 2.0) * x[1] + 2.0 * sum2 / (double) count2);
        solution.setObjective(2, 1.0 - x[1] + 2.0 * sum3 / (double) count3);
    }
}
