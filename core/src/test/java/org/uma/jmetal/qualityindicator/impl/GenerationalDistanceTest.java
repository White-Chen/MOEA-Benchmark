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

package org.uma.jmetal.qualityindicator.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import static org.hamcrest.core.StringContains.containsString;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class GenerationalDistanceTest {
    private static final double EPSILON = 0.0000000000001;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The pareto front approximation is null"));

        Front front = new ArrayFront(0, 0);

        GenerationalDistance<DoubleSolution> gd = new GenerationalDistance<DoubleSolution>(front);
        gd.evaluate(null);
    }

    @Test
    public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The reference pareto front is null"));

        Front front = null;

        new GenerationalDistance<>(front);
    }
/*
  @Test
  public void shouldExecuteRaiseAndExceptionIfTheFrontsContainOnePointWhichIsTheSame() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Maximum and minimum values of index 0 are the same: 10"));

    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front frontApproximation = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front paretoFront = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 10.0);
    point1.setDimensionValue(1, 12.0);

    frontApproximation.setPoint(0, point1);
    paretoFront.setPoint(0, point1);

    GenerationalDistance<List<DoubleSolution>> gd =
        new GenerationalDistance<List<DoubleSolution>>(paretoFront) ;

    assertEquals(0.0, gd.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
  }
  */

    /**
     * @Test public void shouldExecuteReturnTheCorrectValue() {
     * int numberOfDimensions = 2 ;
     * Front frontApproximation = new ArrayFront(3, numberOfDimensions);
     * Front paretoFront = new ArrayFront(4, numberOfDimensions);
     * <p>
     * Point point1 = new ArrayPoint(numberOfDimensions) ;
     * point1.setDimensionValue(0, 2.5);
     * point1.setDimensionValue(1, 9.0);
     * <p>
     * Point point2 = new ArrayPoint(numberOfDimensions) ;
     * point2.setDimensionValue(0, 3.0);
     * point2.setDimensionValue(1, 6.0);
     * <p>
     * Point point3 = new ArrayPoint(numberOfDimensions) ;
     * point3.setDimensionValue(0, 5.0);
     * point3.setDimensionValue(1, 4.0);
     * <p>
     * frontApproximation.setPoint(0, point1);
     * frontApproximation.setPoint(1, point2);
     * frontApproximation.setPoint(2, point3);
     * <p>
     * Point point4 = new ArrayPoint(numberOfDimensions) ;
     * point4.setDimensionValue(0, 1.5);
     * point4.setDimensionValue(1, 10.0);
     * <p>
     * Point point5 = new ArrayPoint(numberOfDimensions) ;
     * point5.setDimensionValue(0, 2.0);
     * point5.setDimensionValue(1, 8.0);
     * <p>
     * Point point6 = new ArrayPoint(numberOfDimensions) ;
     * point6.setDimensionValue(0, 3.0);
     * point6.setDimensionValue(1, 6.0);
     * <p>
     * Point point7 = new ArrayPoint(numberOfDimensions) ;
     * point7.setDimensionValue(0, 4.0);
     * point7.setDimensionValue(1, 4.0);
     * <p>
     * paretoFront.setPoint(0, point4);
     * paretoFront.setPoint(1, point5);
     * paretoFront.setPoint(2, point6);
     * paretoFront.setPoint(3, point7);
     * <p>
     * QualityIndicator gd = new GenerationalDistance(paretoFront) ;
     * <p>
     * assertEquals(0.5, (Double)gd.evaluate(FrontUtils.convertFrontToSolutionList(frontApproximation)), EPSILON);
     * }
     */

    @Test
    public void shouldGetNameReturnTheCorrectValue() {
        //assertEquals("GD", generationalDistance.getName());
    }

}
