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

package org.uma.jmetal.operator.impl.crossover;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * Note: this class does check that the SBX crossover operator does not return invalid
 * values, but not that it works properly (@see SBXCrossoverWorkingTest)
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SBXCrossoverTest {
    private static final double EPSILON = 0.00000000000001;

    @Test
    public void shouldConstructorAssignTheCorrectProbabilityValue() {
        double crossoverProbability = 0.1;
        SBXCrossover crossover = new SBXCrossover(crossoverProbability, 2.0);
        assertEquals(crossoverProbability, (Double) ReflectionTestUtils
                .getField(crossover, "crossoverProbability"), EPSILON);
    }

    @Test
    public void shouldConstructorAssignTheCorrectDistributionIndex() {
        double distributionIndex = 15.0;
        SBXCrossover crossover = new SBXCrossover(0.1, distributionIndex);
        assertEquals(distributionIndex, (Double) ReflectionTestUtils
                .getField(crossover, "distributionIndex"), EPSILON);
    }

    @Test(expected = JMetalException.class)
    public void shouldConstructorFailWhenPassedANegativeProbabilityValue() {
        double crossoverProbability = -0.1;
        new SBXCrossover(crossoverProbability, 2.0);
    }

    @Test(expected = JMetalException.class)
    public void shouldConstructorFailWhenPassedANegativeDistributionIndex() {
        double distributionIndex = -0.1;
        new SBXCrossover(0.1, distributionIndex);
    }

    @Test
    public void shouldGetProbabilityReturnTheRightValue() {
        SBXCrossover crossover = new SBXCrossover(0.1, 20.0);
        assertEquals(0.1, crossover.getCrossoverProbability(), EPSILON);
    }

    @Test
    public void shouldGetDistributionIndexReturnTheRightValue() {
        SBXCrossover crossover = new SBXCrossover(0.1, 30.0);
        assertEquals(30.0, crossover.getDistributionIndex(), EPSILON);
    }

    @Test(expected = JMetalException.class)
    public void shouldExecuteWithNullParameterThrowAnException() {
        SBXCrossover crossover = new SBXCrossover(0.1, 20.0);

        crossover.execute(null);
    }

    @Test(expected = JMetalException.class)
    public void shouldExecuteWithInvalidSolutionListSizeThrowAnException() {
        DoubleProblem problem = new MockDoubleProblem(1);

        SBXCrossover crossover = new SBXCrossover(0.1, 20.0);

        crossover.execute(Arrays.asList(problem.createSolution(), problem.createSolution(), problem.createSolution()));
    }

    @Test
    public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfProbabilityIsZero() {
        double crossoverProbability = 0.0;
        double distributionIndex = 20.0;

        SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex);
        DoubleProblem problem = new MockDoubleProblem(1);
        List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution());

        List<DoubleSolution> newSolutions = crossover.execute(solutions);

        assertEquals(solutions.get(0), newSolutions.get(0));
        assertEquals(solutions.get(1), newSolutions.get(1));
    }

    @Test
    public void shouldCrossingTwoSingleVariableSolutionsReturnTheSameSolutionsIfNotCrossoverIsApplied() {
        JMetalRandom randomGenerator = mock(JMetalRandom.class);

        double crossoverProbability = 0.9;
        double distributionIndex = 20.0;

        SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex);
        DoubleProblem problem = new MockDoubleProblem(1);
        List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution());

        Mockito.when(randomGenerator.nextDouble()).thenReturn(1.0);

        ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);
        List<DoubleSolution> newSolutions = crossover.execute(solutions);

        assertEquals(solutions.get(0), newSolutions.get(0));
        assertEquals(solutions.get(1), newSolutions.get(1));
        verify(randomGenerator).nextDouble();
    }

    @Test
    public void shouldCrossingTwoSingleVariableSolutionsReturnValidSolutions() {
        JMetalRandom randomGenerator = mock(JMetalRandom.class);
        double crossoverProbability = 0.9;
        double distributionIndex = 20.0;

        Mockito.when(randomGenerator.nextDouble()).thenReturn(0.2, 0.2, 0.6, 0.6);

        SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex);
        DoubleProblem problem = new MockDoubleProblem(1);
        List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
                problem.createSolution());

        ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

        List<DoubleSolution> newSolutions = crossover.execute(solutions);

        assertThat(newSolutions.get(0).getVariableValue(0), Matchers
                .greaterThanOrEqualTo(solutions.get(0).getLowerBound(0)));
        assertThat(newSolutions.get(0).getVariableValue(0), Matchers
                .lessThanOrEqualTo(solutions.get(1).getUpperBound(0)));
        assertThat(newSolutions.get(1).getVariableValue(0), Matchers
                .lessThanOrEqualTo(solutions.get(0).getUpperBound(0)));
        assertThat(newSolutions.get(1).getVariableValue(0), Matchers
                .greaterThanOrEqualTo(solutions.get(1).getLowerBound(0)));
        verify(randomGenerator, times(4)).nextDouble();
    }

    @Test
    public void shouldCrossingTwoSingleVariableSolutionsWithSimilarValueReturnTheSameVariables() {
        JMetalRandom randomGenerator = mock(JMetalRandom.class);
        double crossoverProbability = 0.9;
        double distributionIndex = 20.0;

        Mockito.when(randomGenerator.nextDouble()).thenReturn(0.2, 0.2);

        SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex);
        DoubleProblem problem = new MockDoubleProblem(1);
        List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(),
                problem.createSolution());
        solutions.get(0).setVariableValue(0, 1.0);
        solutions.get(1).setVariableValue(0, 1.0);

        ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

        List<DoubleSolution> newSolutions = crossover.execute(solutions);

        assertEquals(solutions.get(0).getVariableValue(0), newSolutions.get(0).getVariableValue(0), EPSILON);
        assertEquals(solutions.get(1).getVariableValue(0), newSolutions.get(1).getVariableValue(0), EPSILON);
        verify(randomGenerator, times(2)).nextDouble();
    }

    @Test
    public void shouldCrossingTwoDoubleVariableSolutionsReturnValidSolutions() {
        JMetalRandom randomGenerator = mock(JMetalRandom.class);
        double crossoverProbability = 0.9;
        double distributionIndex = 20.0;

        Mockito.when(randomGenerator.nextDouble()).thenReturn(0.2, 0.2, 0.8, 0.3, 0.2, 0.8, 0.3);

        SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex);
        DoubleProblem problem = new MockDoubleProblem(2);
        DoubleSolution solution1 = problem.createSolution();
        DoubleSolution solution2 = problem.createSolution();
        solution1.setVariableValue(0, 1.0);
        solution1.setVariableValue(1, 2.0);
        solution2.setVariableValue(0, 2.0);
        solution2.setVariableValue(1, 1.0);
        List<DoubleSolution> solutions = Arrays.asList(solution1, solution2);

        ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);

        List<DoubleSolution> newSolutions = crossover.execute(solutions);

        assertThat(newSolutions.get(0).getVariableValue(0), Matchers
                .greaterThanOrEqualTo(solutions.get(0).getLowerBound(0)));
        assertThat(newSolutions.get(0).getVariableValue(0), Matchers
                .lessThanOrEqualTo(solutions.get(1).getUpperBound(0)));
        assertThat(newSolutions.get(1).getVariableValue(0), Matchers
                .lessThanOrEqualTo(solutions.get(0).getUpperBound(0)));
        assertThat(newSolutions.get(1).getVariableValue(0), Matchers
                .greaterThanOrEqualTo(solutions.get(1).getLowerBound(0)));
        assertThat(newSolutions.get(0).getVariableValue(1), Matchers
                .greaterThanOrEqualTo(solutions.get(0).getLowerBound(0)));
        assertThat(newSolutions.get(0).getVariableValue(1), Matchers
                .lessThanOrEqualTo(solutions.get(1).getUpperBound(0)));
        assertThat(newSolutions.get(1).getVariableValue(1), Matchers
                .lessThanOrEqualTo(solutions.get(0).getUpperBound(0)));
        assertThat(newSolutions.get(1).getVariableValue(1), Matchers
                .greaterThanOrEqualTo(solutions.get(1).getLowerBound(0)));
        verify(randomGenerator, times(7)).nextDouble();
    }

    @Test
    public void shouldCrossingTheSecondVariableReturnTheOtherVariablesUnchanged() {
        JMetalRandom randomGenerator = mock(JMetalRandom.class);
        double crossoverProbability = 0.9;
        double distributionIndex = 20.0;

        Mockito.when(randomGenerator.nextDouble()).thenReturn(0.3, 0.7, 0.2, 0.2, 0.2, 0.7);

        SBXCrossover crossover = new SBXCrossover(crossoverProbability, distributionIndex);
        DoubleProblem problem = new MockDoubleProblem(3);

        List<DoubleSolution> solutions = Arrays.asList(problem.createSolution(), problem.createSolution());

        ReflectionTestUtils.setField(crossover, "randomGenerator", randomGenerator);
        List<DoubleSolution> newSolutions = crossover.execute(solutions);

        assertEquals(solutions.get(0).getVariableValue(0), newSolutions.get(0).getVariableValue(0), EPSILON);
        assertNotEquals(solutions.get(0).getVariableValue(1), newSolutions.get(0).getVariableValue(1), EPSILON);
        assertEquals(solutions.get(0).getVariableValue(2), newSolutions.get(0).getVariableValue(2), EPSILON);

        verify(randomGenerator, times(6)).nextDouble();
    }

    /**
     * Mock class representing a double problem
     */
    @SuppressWarnings("serial")
    private class MockDoubleProblem extends AbstractDoubleProblem {

        /**
         * Constructor
         */
        public MockDoubleProblem(Integer numberOfVariables) {
            setNumberOfVariables(numberOfVariables);
            setNumberOfObjectives(2);

            List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
            List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

            for (int i = 0; i < getNumberOfVariables(); i++) {
                lowerLimit.add(-4.0);
                upperLimit.add(4.0);
            }

            setLowerLimit(lowerLimit);
            setUpperLimit(upperLimit);
        }

        /**
         * Evaluate() method
         */
        @Override
        public void evaluate(DoubleSolution solution) {
            solution.setObjective(0, 0.0);
            solution.setObjective(1, 1.0);
        }
    }
}
