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

package org.uma.jmetal.solution;

import java.io.Serializable;

/**
 * Interface representing a Solution
 *
 * @param <T> Type (Double, Integer, etc.)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Solution<T> extends Serializable {
    void setObjective(int index, double value);

    double getObjective(int index);

    T getVariableValue(int index);

    void setVariableValue(int index, T value);

    String getVariableValueString(int index);

    int getNumberOfVariables();

    int getNumberOfObjectives();

    Solution<T> copy();

    void setAttribute(Object id, Object value);

    Object getAttribute(Object id);
}
