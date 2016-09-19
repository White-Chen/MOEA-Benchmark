//  CEC2009_UF3.java
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
 * Class representing problem CEC2009_UF3
 */
public class UF3 extends AbstractDoubleProblem {

    private static final long serialVersionUID = 6210407788918006160L;

    /**
     * Constructor.
     * Creates a default instance of problem CEC2009_UF3 (30 decision variables)
     */
    public UF3() {
        this(30);
    }

    /**
     * Creates a new instance of problem CEC2009_UF3.
     *
     * @param numberOfVariables Number of variables.
     */
    public UF3(int numberOfVariables) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(2);
        setNumberOfConstraints(0);
        setName("UF3");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0.0);
            upperLimit.add(1.0);
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


        int count1, count2;
        double sum1, sum2, prod1, prod2, yj, pj;
        sum1 = sum2 = 0.0;
        count1 = count2 = 0;
        prod1 = prod2 = 1.0;


        for (int j = 2; j <= getNumberOfVariables(); j++) {
            yj = x[j - 1] - Math.pow(x[0], 0.5 * (1.0 + 3.0 * (j - 2.0) / (getNumberOfVariables() - 2.0)));
            pj = Math.cos(20.0 * yj * Math.PI / Math.sqrt(j));
            if (j % 2 == 0) {
                sum2 += yj * yj;
                prod2 *= pj;
                count2++;
            } else {
                sum1 += yj * yj;
                prod1 *= pj;
                count1++;
            }
        }

        solution.setObjective(0, x[0] + 2.0 * (4.0 * sum1 - 2.0 * prod1 + 2.0) / (double) count1);
        solution.setObjective(1, 1.0 - Math.sqrt(x[0]) + 2.0 * (4.0 * sum2 - 2.0 * prod2 + 2.0) / (double) count2);
    }
}
