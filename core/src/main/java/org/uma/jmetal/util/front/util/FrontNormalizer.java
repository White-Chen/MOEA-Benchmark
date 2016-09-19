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

package org.uma.jmetal.util.front.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.util.List;

/**
 * Class for normalizing {@link Front} objects
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class FrontNormalizer {

    private double[] maximumValues;
    private double[] minimumValues;

    /**
     * Constructor.
     *
     * @param referenceFront
     */
    public FrontNormalizer(List<? extends Solution<?>> referenceFront) {
        if (referenceFront == null) {
            throw new JMetalException("The reference front is null");
        }
        maximumValues = FrontUtils.getMaximumValues(new ArrayFront(referenceFront));
        minimumValues = FrontUtils.getMinimumValues(new ArrayFront(referenceFront));
    }

    /**
     * Constructor.
     *
     * @param referenceFront
     */
    public FrontNormalizer(Front referenceFront) {
        if (referenceFront == null) {
            throw new JMetalException("The reference front is null");
        }
        maximumValues = FrontUtils.getMaximumValues(referenceFront);
        minimumValues = FrontUtils.getMinimumValues(referenceFront);
    }

    /**
     * Constructor
     *
     * @param minimumValues
     * @param maximumValues
     */
    public FrontNormalizer(double[] minimumValues, double[] maximumValues) {
        if (minimumValues == null) {
            throw new JMetalException("The array of minimum values is null");
        } else if (maximumValues == null) {
            throw new JMetalException("The array of maximum values is null");
        } else if (maximumValues.length != minimumValues.length) {
            throw new JMetalException("The length of the maximum array (" + maximumValues.length + ") " +
                    "is different from the length of the minimum array (" + minimumValues.length + ")");
        }
        this.maximumValues = maximumValues;
        this.minimumValues = minimumValues;
    }

    /**
     * Returns a normalized front
     *
     * @param solutionList
     * @return
     */
    public List<? extends Solution<?>> normalize(List<? extends Solution<?>> solutionList) {
        Front normalizedFront;
        if (solutionList == null) {
            throw new JMetalException("The front is null");
        }

        normalizedFront = getNormalizedFront(new ArrayFront(solutionList), maximumValues, minimumValues);

        return FrontUtils.convertFrontToSolutionList(normalizedFront);
    }

    /**
     * Returns a normalized front
     *
     * @param front
     * @return
     */
    public Front normalize(Front front) {
        if (front == null) {
            throw new JMetalException("The front is null");
        }

        return getNormalizedFront(front, maximumValues, minimumValues);
    }

    private Front getNormalizedFront(Front front, double[] maximumValues, double[] minimumValues) {
        if (front.getNumberOfPoints() == 0) {
            throw new JMetalException("The front is empty");
        } else if (front.getPoint(0).getNumberOfDimensions() != maximumValues.length) {
            throw new JMetalException("The length of the point dimensions ("
                    + front.getPoint(0).getNumberOfDimensions() + ") "
                    + "is different from the length of the maximum array (" + maximumValues.length + ")");
        }

        Front normalizedFront = new ArrayFront(front);
        int numberOfPointDimensions = front.getPoint(0).getNumberOfDimensions();

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            for (int j = 0; j < numberOfPointDimensions; j++) {
                if ((maximumValues[j] - minimumValues[j]) == 0) {
                    throw new JMetalException("Maximum and minimum values of index " + j + " "
                            + "are the same: " + maximumValues[j]);
                }

                normalizedFront.getPoint(i).setDimensionValue(j, (front.getPoint(i).getDimensionValue(j)
                        - minimumValues[j]) / (maximumValues[j] - minimumValues[j]));
            }
        }
        return normalizedFront;
    }
}
