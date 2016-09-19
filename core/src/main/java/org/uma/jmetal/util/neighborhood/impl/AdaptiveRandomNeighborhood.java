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

package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the adaptive random neighborhood (topology) defined by M. Clerc.
 * Each {@link Solution} in a solution list must have a neighborhood composed by it itself and
 * K random selected neighbors (the same solution can be chosen several times).
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class AdaptiveRandomNeighborhood<S extends Solution<?>> implements Neighborhood<S> {
    private int solutionListSize;
    private int numberOfRandomNeighbours;
    private List<List<Integer>> neighbours;
    private JMetalRandom randomGenerator;

    /**
     * Constructor
     *
     * @param solutionListSize         The expected size of the list of solutions
     * @param numberOfRandomNeighbours The number of neighbors per solution
     */
    public AdaptiveRandomNeighborhood(int solutionListSize, int numberOfRandomNeighbours) {
        if (numberOfRandomNeighbours < 0) {
            throw new JMetalException("The number of neighbors is negative: " + numberOfRandomNeighbours);
        } else if (solutionListSize <= numberOfRandomNeighbours) {
            throw new JMetalException("The population size: " + solutionListSize + " is " +
                    "less or equal to the number of requested neighbors: " + numberOfRandomNeighbours);
        }

        this.solutionListSize = solutionListSize;
        this.numberOfRandomNeighbours = numberOfRandomNeighbours;
        this.randomGenerator = JMetalRandom.getInstance();

        createNeighborhoods();
        addRandomNeighbors();
    }

    /**
     * Initialize all the neighborhoods, adding the current solution to them.
     */
    private void createNeighborhoods() {
        neighbours = new ArrayList<List<Integer>>(solutionListSize);

        for (int i = 0; i < solutionListSize; i++) {
            neighbours.add(new ArrayList<Integer>());
            neighbours.get(i).add(i);
        }
    }

    /**
     * Add random neighbors to all the neighborhoods
     */
    private void addRandomNeighbors() {
        for (int i = 0; i < solutionListSize; i++) {
            while (neighbours.get(i).size() <= numberOfRandomNeighbours) {
                int random = randomGenerator.nextInt(0, solutionListSize - 1);
                neighbours.get(i).add(random);
            }
        }
    }

    private List<S> getIthNeighborhood(List<S> solutionList, int index) {
        List<S> neighborhood = new ArrayList<>();
        for (int i = 0; i < (numberOfRandomNeighbours + 1); i++) {
            int neighboursIndex = neighbours.get(index).get(i);
            neighborhood.add(solutionList.get(neighboursIndex));
        }

        return neighborhood;
    }

    /**
     * Recomputes the neighbors
     */
    public void recompute() {
        createNeighborhoods();
        addRandomNeighbors();
    }

    @Override
    public List<S> getNeighbors(List<S> solutionList, int solutionIndex) {
        if (solutionList == null) {
            throw new JMetalException("The solution list is null");
        } else if (solutionList.size() != solutionListSize) {
            throw new JMetalException("The solution list size: " + solutionList.size() + " is" +
                    " different to the value: " + solutionListSize);
        } else if (solutionIndex < 0) {
            throw new JMetalException("The solution position value is negative: " + solutionIndex);
        } else if (solutionIndex >= solutionList.size()) {
            throw new JMetalException("The solution position value: " + solutionIndex +
                    " is equal or greater than the solution list size: "
                    + solutionList.size());
        }

        return getIthNeighborhood(solutionList, solutionIndex);
    }
}
