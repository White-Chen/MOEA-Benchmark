//  ConstrEx.java
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

package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing problem ConstrEx
 */
@SuppressWarnings("serial")
public class ConstrEx extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution> {

    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;

    /**
     * Constructor
     * Creates a default instance of the ConstrEx problem
     */
    public ConstrEx() {
        setNumberOfVariables(2);
        setNumberOfObjectives(2);
        setNumberOfConstraints(2);
        setName("ConstrEx");

        List<Double> lowerLimit = Arrays.asList(0.1, 0.0);
        List<Double> upperLimit = Arrays.asList(1.0, 5.0);

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);

        overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>();
        numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>();
    }

    /**
     * Evaluate() method
     */
    @Override
    public void evaluate(DoubleSolution solution) {
        double[] f = new double[getNumberOfObjectives()];
        f[0] = solution.getVariableValue(0);
        f[1] = (1.0 + solution.getVariableValue(1)) / solution.getVariableValue(0);

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }

    /**
     * EvaluateConstraints() method
     */
    @Override
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.getNumberOfConstraints()];

        double x1 = solution.getVariableValue(0);
        double x2 = solution.getVariableValue(1);

        constraint[0] = (x2 + 9 * x1 - 6.0);
        constraint[1] = (-x2 + 9 * x1 - 1.0);

        double overallConstraintViolation = 0.0;
        int violatedConstraints = 0;
        for (int i = 0; i < this.getNumberOfConstraints(); i++)
            if (constraint[i] < 0.0) {
                overallConstraintViolation += constraint[i];
                violatedConstraints++;
            }

        overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
        numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }
}
