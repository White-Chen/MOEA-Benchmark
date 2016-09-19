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

package org.uma.jmetal.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SolutionListUtilsTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /*****
     * Unit tests to method findBestSolution
     ****/
    @Test
    public void shouldFindBestSolutionRaiseAnExceptionIfTheSolutionListIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is null"));

        @SuppressWarnings("unchecked")
        Comparator<Solution<?>> comparator = mock(Comparator.class);

        SolutionListUtils.findBestSolution(null, comparator);
    }

    @Test
    public void shouldFindBestSolutionRaiseAnExceptionIfTheSolutionListIsEmpty() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is empty"));

        @SuppressWarnings("unchecked")
        Comparator<DoubleSolution> comparator = mock(Comparator.class);
        List<DoubleSolution> list = new ArrayList<>();

        SolutionListUtils.findBestSolution(list, comparator);
    }

    @Test
    public void shouldFindBestSolutionRaiseAnExceptionIfTheComparatorIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The comparator is null"));

        List<DoubleSolution> list = new ArrayList<>();
        list.add(mock(DoubleSolution.class));

        SolutionListUtils.findBestSolution(list, null);
    }

    @Test
    public void shouldFindBestSolutionReturnTheSolutionInTheListWhenItContainsOneSolution() {
        @SuppressWarnings("unchecked")
        Comparator<IntegerSolution> comparator = mock(Comparator.class);
        List<IntegerSolution> list = new ArrayList<>();
        IntegerSolution solution = mock(IntegerSolution.class);
        list.add(solution);

        assertSame(solution, SolutionListUtils.findBestSolution(list, comparator));
    }

    @Test
    public void shouldFindBestSolutionReturnTheSecondSolutionInTheListIfIsTheBestOufOfTwoSolutions() {
        @SuppressWarnings("unchecked")
        Comparator<IntegerSolution> comparator = mock(Comparator.class);
        List<IntegerSolution> list = new ArrayList<>();
        IntegerSolution solution1 = mock(IntegerSolution.class);
        list.add(solution1);
        IntegerSolution solution2 = mock(IntegerSolution.class);
        list.add(solution2);

        when(comparator.compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject())).thenReturn(1);

        assertSame(solution2, SolutionListUtils.findBestSolution(list, comparator));
    }

    @Test
    public void shouldFindBestSolutionReturnTheLastOneIfThisIsTheBestSolutionInALastInAListWithFiveSolutions() {
        @SuppressWarnings("unchecked")
        Comparator<IntegerSolution> comparator = mock(Comparator.class);
        List<IntegerSolution> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(mock(IntegerSolution.class));
        }

        when(comparator.compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject())).thenReturn(1, 0, 0, 1);
        assertSame(list.get(4), SolutionListUtils.findBestSolution(list, comparator));
        verify(comparator, times(4)).compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject());
    }

    /*****
     * Unit tests to method findIndexOfBestSolution
     ****/
    @Test
    public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheSolutionListIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is null"));

        @SuppressWarnings("unchecked")
        Comparator<Solution<?>> comparator = mock(Comparator.class);

        SolutionListUtils.findIndexOfBestSolution(null, comparator);
    }

    @Test
    public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheSolutionListIsEmpty() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is empty"));

        @SuppressWarnings("unchecked")
        Comparator<DoubleSolution> comparator = mock(Comparator.class);
        List<DoubleSolution> list = new ArrayList<>();

        SolutionListUtils.findIndexOfBestSolution(list, comparator);
    }

    @Test
    public void shouldFindIndexOfBestSolutionRaiseAnExceptionIfTheComparatorIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The comparator is null"));

        List<DoubleSolution> list = new ArrayList<>();
        list.add(mock(DoubleSolution.class));

        SolutionListUtils.findIndexOfBestSolution(list, null);
    }

    @Test
    public void shouldFindIndexOfBestSolutionReturnZeroIfTheListWhenItContainsOneSolution() {
        @SuppressWarnings("unchecked")
        Comparator<IntegerSolution> comparator = mock(Comparator.class);
        List<IntegerSolution> list = new ArrayList<>();
        IntegerSolution solution = mock(IntegerSolution.class);
        list.add(solution);

        assertEquals(0, SolutionListUtils.findIndexOfBestSolution(list, comparator));
    }

    @Test
    public void shouldFindIndexOfBestSolutionReturnZeroIfTheFirstSolutionItTheBestOutOfTwoSolutionsInTheList() {
        @SuppressWarnings("unchecked")
        Comparator<IntegerSolution> comparator = mock(Comparator.class);
        List<IntegerSolution> list = new ArrayList<>();
        IntegerSolution solution1 = mock(IntegerSolution.class);
        list.add(solution1);
        IntegerSolution solution2 = mock(IntegerSolution.class);
        list.add(solution2);

        when(comparator.compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject())).thenReturn(0);
        assertEquals(0, SolutionListUtils.findIndexOfBestSolution(list, comparator));
        verify(comparator).compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject());
    }

    @Test
    public void shouldFindIndexOfBestSolutionReturnOneIfTheSecondSolutionItTheBestOutOfTwoSolutionInTheList() {
        @SuppressWarnings("unchecked")
        Comparator<IntegerSolution> comparator = mock(Comparator.class);
        List<IntegerSolution> list = new ArrayList<>();
        IntegerSolution solution1 = mock(IntegerSolution.class);
        list.add(solution1);
        IntegerSolution solution2 = mock(IntegerSolution.class);
        list.add(solution2);

        when(comparator.compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject())).thenReturn(1);
        assertEquals(1, SolutionListUtils.findIndexOfBestSolution(list, comparator));
        verify(comparator).compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject());
    }

    @Test
    public void shouldFindIndexOfBestSolutionReturn4IfTheBestSolutionIsTheLastInAListWithFiveSolutions() {
        @SuppressWarnings("unchecked")
        Comparator<IntegerSolution> comparator = mock(Comparator.class);
        List<IntegerSolution> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(mock(IntegerSolution.class));
        }

        when(comparator.compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject())).thenReturn(1, 0, 0, 1);
        assertEquals(4, SolutionListUtils.findIndexOfBestSolution(list, comparator));
        verify(comparator, times(4)).compare(Matchers.<IntegerSolution>anyObject(), Matchers.<IntegerSolution>anyObject());
    }

    /*****
     * Unit tests to method selectNRandomDifferentSolutions
     ****/
    @Test
    public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheSolutionListIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is null"));

        SolutionListUtils.selectNRandomDifferentSolutions(1, null);
    }

    @Test
    public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheSolutionListIsEmpty() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is empty"));

        List<DoubleSolution> list = new ArrayList<>();

        SolutionListUtils.selectNRandomDifferentSolutions(1, list);
    }

    @Test
    public void shouldSelectNRandomDifferentSolutionsReturnASingleSolution() {
        List<Solution<?>> list = new ArrayList<>();
        list.add(mock(Solution.class));

        assertEquals(1, list.size());
    }

    @Test
    public void shouldSelectNRandomDifferentSolutionsRaiseAnExceptionIfTheListSizeIsOneAndTwoSolutionsAreRequested() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list size (1) is less than " +
                "the number of requested solutions (2)"));

        List<Solution<?>> list = new ArrayList<>(1);
        list.add(mock(Solution.class));

        SolutionListUtils.selectNRandomDifferentSolutions(2, list);
    }

    @Test
    public void shouldelectNRandomDifferentSolutionsRaiseAnExceptionIfTheListSizeIsTwoAndFourSolutionsAreRequested() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list size (2) is less than " +
                "the number of requested solutions (4)"));

        List<Solution<?>> list = new ArrayList<>(2);
        list.add(mock(Solution.class));
        list.add(mock(Solution.class));

        SolutionListUtils.selectNRandomDifferentSolutions(4, list);
    }

    @Test
    public void shouldExecuteReturnTheSolutionInTheListIfTheListContainsASolution() {
        List<IntegerSolution> list = new ArrayList<>(2);
        IntegerSolution solution = mock(IntegerSolution.class);
        list.add(solution);

        List<IntegerSolution> result = SolutionListUtils.selectNRandomDifferentSolutions(1, list);
        assertSame(solution, result.get(0));
    }

    @Test
    public void shouldSelectNRandomDifferentSolutionsReturnTheSolutionSInTheListIfTheListContainsTwoSolutions() {
        List<BinarySolution> list = new ArrayList<>(2);
        BinarySolution solution1 = mock(BinarySolution.class);
        BinarySolution solution2 = mock(BinarySolution.class);
        list.add(solution1);
        list.add(solution2);

        List<BinarySolution> result = SolutionListUtils.selectNRandomDifferentSolutions(2, list);

        assertTrue(result.contains(solution1));
        assertTrue(result.contains(solution2));
    }

    @Test
    public void shouldSelectNRandomDifferentSolutionsReturnTheCorrectNumberOfSolutions() {
        int listSize = 20;
        int solutionsToBeReturned = 4;

        List<BinarySolution> list = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            list.add(mock(BinarySolution.class));
        }

        List<BinarySolution> result = SolutionListUtils.selectNRandomDifferentSolutions(solutionsToBeReturned, list);
        assertEquals(solutionsToBeReturned, result.size());
    }

    /**
     * If the list contains 4 solutions, the result list must return all of them
     */
    @Test
    public void shouldSelectNRandomDifferentSolutionsReturnTheCorrectListOfSolutions() {
        int listSize = 4;
        int solutionsToBeReturned = 4;

        List<IntegerSolution> list = new ArrayList<>(listSize);
        IntegerSolution[] solution = new IntegerSolution[solutionsToBeReturned];
        for (int i = 0; i < listSize; i++) {
            solution[i] = (mock(IntegerSolution.class));
            list.add(solution[i]);
        }

        List<IntegerSolution> result = SolutionListUtils.selectNRandomDifferentSolutions(solutionsToBeReturned, list);
        assertTrue(result.contains(solution[0]));
        assertTrue(result.contains(solution[1]));
        assertTrue(result.contains(solution[2]));
        assertTrue(result.contains(solution[3]));
    }

    @Test
    public void shouldSolutionListsAreEqualsReturnIfTwoIdenticalSolutionListsAreCompared() {
        List<BinarySolution> list1 = new ArrayList<>(3);
        List<BinarySolution> list2 = new ArrayList<>(3);

        for (int i = 0; i < 3; i++) {
            BinarySolution solution = mock(BinarySolution.class);
            list1.add(solution);
            list2.add(solution);
        }

        assertTrue(SolutionListUtils.solutionListsAreEquals(list1, list2));
    }

    @Test
    public void shouldSolutionListsAreEqualsReturnIfTwoSolutionListsWithIdenticalSolutionsAreCompared() {
        List<BinarySolution> list1 = new ArrayList<>(3);
        List<BinarySolution> list2 = new ArrayList<>(3);

        List<BinarySolution> solutions = Arrays.asList(
                mock(BinarySolution.class),
                mock(BinarySolution.class),
                mock(BinarySolution.class));

        for (BinarySolution solution : solutions) {
            list1.add(solution);
        }

        list2.add(solutions.get(2));
        list2.add(solutions.get(1));
        list2.add(solutions.get(0));

        assertTrue(SolutionListUtils.solutionListsAreEquals(list1, list2));
    }
}
