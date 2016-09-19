//  ZDT6.java
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

package org.uma.jmetal.problem.multiobjective.zdt;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem ZDT6
 */
public class ZDT6 extends AbstractDoubleProblem {

    private static final long serialVersionUID = 6168424891209557987L;

    /**
     * Constructor. Creates a default instance of problem ZDT6 (10 decision variables)
     */
    public ZDT6() {
        this(10);
    }

    /**
     * Creates a instance of problem ZDT6
     *
     * @param numberOfVariables Number of variables
     */
    public ZDT6(Integer numberOfVariables) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(2);
        setName("ZDT6");

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
    public void evaluate(DoubleSolution solution) {
        int numberOfVariables = getNumberOfVariables();

        double[] f = new double[getNumberOfObjectives()];
        double[] x = new double[numberOfVariables];

        double x1 = solution.getVariableValue(0);
        f[0] = 1.0 - Math.exp((-4.0) * x1) * Math.pow(Math.sin(6.0 * Math.PI * x1), 6.0);
        double g = this.evalG(solution);
        double h = this.evalH(f[0], g);
        f[1] = h * g;

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }

    /**
     * Returns the value of the ZDT6 function G.
     *
     * @param solution Solution
     */
    public double evalG(DoubleSolution solution) {
        double g = 0.0;
        for (int var = 1; var < solution.getNumberOfVariables(); var++) {
            g += solution.getVariableValue(var);
        }
        g = g / (solution.getNumberOfVariables() - 1);
        g = Math.pow(g, 0.25);
        g = 9.0 * g;
        g = 1.0 + g;
        return g;
    }

    /**
     * Returns the value of the ZDT6 function H.
     *
     * @param f First argument of the function H.
     * @param g Second argument of the function H.
     */
    public double evalH(double f, double g) {
        return 1.0 - Math.pow((f / g), 2.0);
    }
}
