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

package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.SolutionAttribute;

/**
 * Generic class for implementing {@link SolutionAttribute} classes. By default, the identifier
 * of a {@link SolutionAttribute} is the class name, but it can be set to a different value
 * when constructing an instance.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenericSolutionAttribute<S extends Solution<?>, V> implements SolutionAttribute<S, V> {
    private Object id;
    private Object identifier;

    /**
     * Constructor
     */
    public GenericSolutionAttribute() {
        id = this.getClass();
    }

    /**
     * Constructor
     *
     * @param id Attribute identifier
     */
    public GenericSolutionAttribute(Object id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getAttribute(S solution) {
        return (V) solution.getAttribute(getAttributeID());
    }

    @Override
    public void setAttribute(S solution, V value) {
        solution.setAttribute(getAttributeID(), value);
    }

    @Override
    public Object getAttributeID() {
        return id;
    }

    @Override
    public Object getAttributeIdentifier() {
        return identifier;
    }
}
