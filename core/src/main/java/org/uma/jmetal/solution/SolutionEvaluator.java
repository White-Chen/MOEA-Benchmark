package org.uma.jmetal.solution;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.util.naming.DescribedEntity;

import java.util.Collection;

/**
 * A {@link SolutionEvaluator} allows to evaluate a {@link Solution} on one or
 * several dimensions, in other words to compute its {@link Objective} values.
 *
 * @param <Solution>
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 */
public interface SolutionEvaluator<Solution> {

    /**
     * @return the list of {@link Objective}s managed by this
     * {@link SolutionEvaluator}
     */
    Collection<Objective<Solution, ?>> getObjectives();

    /**
     * An {@link Objective} represents the evaluation information of a set of
     * homogeneous {@link Solution}s (e.g. a population of solutions returned by
     * an {@link Algorithm}). For instance, an {@link Algorithm} used to solve a
     * TSP problem would manage a whole population of {@link Solution}s, each
     * representing a different path, and an {@link Objective} would represent a
     * type of information which evaluates these {@link Solution}s, like the
     * length of the path, the time needed to travel through this path, or the
     * amount of fuel consumed.
     *
     * @param <Solution>
     * @param <Value>
     * @author Matthieu Vergne <matthieu.vergne@gmail.com>
     */
    interface Objective<Solution, Value> extends DescribedEntity {
        /**
         * @param solution the {@link Solution} to read
         * @return the {@link Value} of the {@link Objective} for this
         * {@link Solution}
         */
        Value get(Solution solution);
    }
}
