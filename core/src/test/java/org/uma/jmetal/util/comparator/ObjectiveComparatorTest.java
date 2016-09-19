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

package org.uma.jmetal.util.comparator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class ObjectiveComparatorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private ObjectiveComparator<Solution<?>> comparator;

    @Test
    public void shouldCompareReturnOneIfTheFirstSolutionIsNull() {
        comparator = new ObjectiveComparator<Solution<?>>(0);

        Solution<?> solution2 = mock(Solution.class);

        assertEquals(1, comparator.compare(null, solution2));
    }

    @Test
    public void shouldCompareReturnMinusOneIfTheSecondSolutionIsNull() {
        comparator = new ObjectiveComparator<Solution<?>>(0);

        Solution<?> solution1 = mock(Solution.class);

        assertEquals(-1, comparator.compare(solution1, null));
    }

    @Test
    public void shouldCompareReturnZeroIfBothSolutionsAreNull() {
        comparator = new ObjectiveComparator<Solution<?>>(0);

        assertEquals(0, comparator.compare(null, null));
    }

    @Test
    public void shouldCompareReturnMinusOneIfTheObjectiveOfSolution1IsLower() {
        comparator = new ObjectiveComparator<Solution<?>>(0);

        DoubleSolution solution1 = mock(DoubleSolution.class);
        DoubleSolution solution2 = mock(DoubleSolution.class);

        when(solution1.getNumberOfObjectives()).thenReturn(4);
        when(solution2.getNumberOfObjectives()).thenReturn(4);

        when(solution1.getObjective(0)).thenReturn(-4.0);
        when(solution2.getObjective(0)).thenReturn(5.0);

        assertEquals(-1, comparator.compare(solution1, solution2));
        verify(solution1).getObjective(0);
        verify(solution2).getObjective(0);
    }

    @Test
    public void shouldCompareReturnOneIfTheObjectiveOfSolution2IsLower() {
        comparator = new ObjectiveComparator<Solution<?>>(2);

        DoubleSolution solution1 = mock(DoubleSolution.class);
        DoubleSolution solution2 = mock(DoubleSolution.class);

        when(solution1.getNumberOfObjectives()).thenReturn(4);
        when(solution2.getNumberOfObjectives()).thenReturn(4);

        when(solution1.getObjective(2)).thenReturn(7.0);
        when(solution2.getObjective(2)).thenReturn(5.0);

        assertEquals(1, comparator.compare(solution1, solution2));
    }

    @Test
    public void shouldCompareReturnZeroIfTheObjectiveOfTheSolutionsIsTheSame() {
        comparator = new ObjectiveComparator<Solution<?>>(2);

        DoubleSolution solution1 = mock(DoubleSolution.class);
        DoubleSolution solution2 = mock(DoubleSolution.class);

        when(solution1.getNumberOfObjectives()).thenReturn(4);
        when(solution2.getNumberOfObjectives()).thenReturn(4);

        when(solution1.getObjective(2)).thenReturn(7.0);
        when(solution2.getObjective(2)).thenReturn(7.0);

        assertEquals(0, comparator.compare(solution1, solution2));
    }

    @Test
    public void shouldCompareReturnMinusOneIfTheObjectiveOfSolution1IsGreaterInDescendingOrder() {
        comparator = new ObjectiveComparator<Solution<?>>(0, ObjectiveComparator.Ordering.DESCENDING);

        DoubleSolution solution1 = mock(DoubleSolution.class);
        DoubleSolution solution2 = mock(DoubleSolution.class);

        when(solution1.getNumberOfObjectives()).thenReturn(4);
        when(solution2.getNumberOfObjectives()).thenReturn(4);

        when(solution1.getObjective(0)).thenReturn(25.0);
        when(solution2.getObjective(0)).thenReturn(5.0);

        assertEquals(-1, comparator.compare(solution1, solution2));
        verify(solution1).getObjective(0);
        verify(solution2).getObjective(0);
    }

    @Test
    public void shouldCompareReturnOneIfTheObjectiveOfSolution2IsGreaterInDescendingOrder() {
        comparator = new ObjectiveComparator<Solution<?>>(2, ObjectiveComparator.Ordering.DESCENDING);

        DoubleSolution solution1 = mock(DoubleSolution.class);
        DoubleSolution solution2 = mock(DoubleSolution.class);

        when(solution1.getNumberOfObjectives()).thenReturn(4);
        when(solution2.getNumberOfObjectives()).thenReturn(4);

        when(solution1.getObjective(2)).thenReturn(7.0);
        when(solution2.getObjective(2)).thenReturn(25.0);

        assertEquals(1, comparator.compare(solution1, solution2));

        verify(solution1).getObjective(2);
        verify(solution2).getObjective(2);
        verify(solution1).getNumberOfObjectives();
        verify(solution2).getNumberOfObjectives();
    }

    @Test
    public void shouldCompareRaiseAnExceptionIfSolution1HasLessObjectivesThanTheOneRequested() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution1 has 3 objectives and the objective "
                + "to sort is 5"));

        comparator = new ObjectiveComparator<Solution<?>>(5, ObjectiveComparator.Ordering.DESCENDING);

        DoubleSolution solution1 = mock(DoubleSolution.class);
        DoubleSolution solution2 = mock(DoubleSolution.class);

        when(solution1.getNumberOfObjectives()).thenReturn(3);
        when(solution2.getNumberOfObjectives()).thenReturn(6);

        comparator.compare(solution1, solution2);

        verify(solution1).getNumberOfObjectives();
        verify(solution2).getNumberOfObjectives();
    }

    @Test
    public void shouldCompareRaiseAnExceptionIfSolution2HasLessObjectivesThanTheOneRequested() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution2 has 5 objectives and the objective "
                + "to sort is 5"));

        comparator = new ObjectiveComparator<Solution<?>>(5, ObjectiveComparator.Ordering.DESCENDING);

        DoubleSolution solution1 = mock(DoubleSolution.class);
        DoubleSolution solution2 = mock(DoubleSolution.class);

        when(solution1.getNumberOfObjectives()).thenReturn(7);
        when(solution2.getNumberOfObjectives()).thenReturn(5);

        comparator.compare(solution1, solution2);

        verify(solution1).getNumberOfObjectives();
        verify(solution2).getNumberOfObjectives();
    }
}
