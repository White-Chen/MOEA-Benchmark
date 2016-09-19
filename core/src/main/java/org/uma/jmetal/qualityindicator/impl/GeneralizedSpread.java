//  GeneralizedSpread.java
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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.comparator.LexicographicalPointComparator;
import org.uma.jmetal.util.point.util.comparator.PointDimensionComparator;
import org.uma.jmetal.util.point.util.distance.EuclideanDistance;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the generalized spread metric for two or more dimensions.
 * Reference: A. Zhou, Y. Jin, Q. Zhang, B. Sendhoff, and E. Tsang
 * Combining model-based and genetics-based offspring generation for
 * multi-objective optimization using a convergence criterion,
 * 2006 IEEE Congress on Evolutionary Computation, 2006, pp. 3234-3241.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GeneralizedSpread<S extends Solution<?>> extends GenericIndicator<S> {

    /**
     * Default constructor
     */
    public GeneralizedSpread() {
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException
     */
    public GeneralizedSpread(String referenceParetoFrontFile) throws FileNotFoundException {
        super(referenceParetoFrontFile);
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront
     * @throws FileNotFoundException
     */
    public GeneralizedSpread(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    /**
     * Evaluate() method
     *
     * @param solutionList
     * @return
     */
    @Override
    public Double evaluate(List<S> solutionList) {
        return generalizedSpread(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Calculates the generalized spread metric. Given the
     * pareto front, the true pareto front as <code>double []</code>
     * and the number of objectives, the method return the value for the
     * metric.
     *
     * @param front          The front.
     * @param referenceFront The reference pareto front.
     * @return the value of the generalized spread metric
     **/
    public double generalizedSpread(Front front, Front referenceFront) {
        int numberOfObjectives = front.getPoint(0).getNumberOfDimensions();

        Point[] extremeValues = new Point[numberOfObjectives];
        for (int i = 0; i < numberOfObjectives; i++) {
            referenceFront.sort(new PointDimensionComparator(i));
            Point newPoint = new ArrayPoint(numberOfObjectives);
            for (int j = 0; j < numberOfObjectives; j++) {
                newPoint.setDimensionValue(j,
                        referenceFront.getPoint(referenceFront.getNumberOfPoints() - 1).getDimensionValue(j));
            }
            extremeValues[i] = newPoint;
        }

        int numberOfPoints = front.getNumberOfPoints();

        front.sort(new LexicographicalPointComparator());

        if (new EuclideanDistance().compute(front.getPoint(0),
                front.getPoint(front.getNumberOfPoints() - 1)) == 0.0) {
            return 1.0;
        } else {
            double dmean = 0.0;

            for (int i = 0; i < front.getNumberOfPoints(); i++) {
                dmean += FrontUtils.distanceToNearestPoint(front.getPoint(i), front);
            }

            dmean = dmean / (numberOfPoints);

            double dExtrems = 0.0;
            for (int i = 0; i < extremeValues.length; i++) {
                dExtrems += FrontUtils.distanceToClosestPoint(extremeValues[i], front);
            }

            double mean = 0.0;
            for (int i = 0; i < front.getNumberOfPoints(); i++) {
                mean += Math.abs(FrontUtils.distanceToNearestPoint(front.getPoint(i), front) -
                        dmean);
            }

            return (dExtrems + mean) / (dExtrems + (numberOfPoints * dmean));
        }
    }

    @Override
    public String getName() {
        return "GSPREAD";
    }

    @Override
    public String getDescription() {
        return "Generalized Spread quality indicator";
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }
}

