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

package org.uma.jmetal.operator.impl.selection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RankingAndCrowdingSelectionTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsNull() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is null"));

        RankingAndCrowdingSelection<Solution<?>> selection = new RankingAndCrowdingSelection<Solution<?>>(4);
        selection.execute(null);
    }

    @Test
    public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsEmpty() {
        exception.expect(JMetalException.class);
        exception.expectMessage(containsString("The solution list is empty"));

        RankingAndCrowdingSelection<DoubleSolution> selection = new RankingAndCrowdingSelection<DoubleSolution>(4);
        List<DoubleSolution> list = new ArrayList<>();

        selection.execute(list);
    }

    @Test
    public void shouldDefaultConstructorReturnASingleSolution() {
        RankingAndCrowdingSelection<Solution<?>> selection = new RankingAndCrowdingSelection<Solution<?>>(1);

        int result = (int) ReflectionTestUtils.getField(selection, "solutionsToSelect");
        int expectedResult = 1;
        assertEquals(expectedResult, result);
    }

    @Test
    public void shouldNonDefaultConstructorReturnTheCorrectNumberOfSolutions() {
        int solutionsToSelect = 4;
        RankingAndCrowdingSelection<Solution<?>> selection = new RankingAndCrowdingSelection<Solution<?>>(solutionsToSelect);

        int result = (int) ReflectionTestUtils.getField(selection, "solutionsToSelect");
        assertEquals(solutionsToSelect, result);
    }
}
