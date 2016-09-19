//  CEC2009_UF5.java
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
 * Class representing problem CEC2009_UF5
 */
public class UF5 extends AbstractDoubleProblem {
    private static final long serialVersionUID = 7586599361997218270L;
    int n;
    double epsilon;

    /**
     * Constructor.
     * Creates a default instance of problem CEC2009_UF5 (30 decision variables)
     */
    public UF5() throws ClassNotFoundException {
        this(30, 10, 0.1);
    }

    /**
     * Creates a new instance of problem CEC2009_UF5.
     *
     * @param numberOfVariables Number of variables.
     */
    public UF5(int numberOfVariables, int N, double epsilon) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(2);
        setNumberOfConstraints(0);
        setName("UF5");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        this.n = N;
        this.epsilon = epsilon;

        lowerLimit.add(0.0);
        upperLimit.add(1.0);
        for (int i = 1; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-1.0);
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
        double sum1, sum2, yj, hj;
        sum1 = sum2 = 0.0;
        count1 = count2 = 0;

        for (int j = 2; j <= getNumberOfVariables(); j++) {
            yj = x[j - 1] - Math.sin(6.0 * Math.PI * x[0] + j * Math.PI / getNumberOfVariables());
            hj = 2.0 * yj * yj - Math.cos(4.0 * Math.PI * yj) + 1.0;
            if (j % 2 == 0) {
                sum2 += hj;
                count2++;
            } else {
                sum1 += hj;
                count1++;
            }
        }
        hj = (0.5 / n + epsilon) * Math.abs(Math.sin(2.0 * n * Math.PI * x[0]));

        solution.setObjective(0, x[0] + hj + 2.0 * sum1 / (double) count1);
        solution.setObjective(1, 1.0 - x[0] + hj + 2.0 * sum2 / (double) count2);
    }
}
