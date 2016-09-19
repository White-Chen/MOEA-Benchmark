package org.uma.jmetal.operator.impl.mutation;


import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Created by Lenovo on 2015/11/6.
 */
public class RandomizeMutation implements MutationOperator<DoubleSolution> {
    private static final long serialVersionUID = 4993309555047815373L;
    private double mutationProbability;
    private JMetalRandom randomGenerator;
    private DominanceComparator<DoubleSolution> comparator;

    public RandomizeMutation(double mutationProbability) {
        if (mutationProbability < 0) {
            throw new JMetalException("Mutation probability is negative: " + mutationProbability);
        }

        this.mutationProbability = mutationProbability;
        randomGenerator = JMetalRandom.getInstance();
        comparator = new DominanceComparator<>();
    }

    /**
     * @param solution The data to process
     */
    @Override
    public DoubleSolution execute(DoubleSolution solution) {
        if (null == solution) {
            throw new JMetalException("Null parameter");
        }

        doMutation(mutationProbability, solution);

        return solution;
    }

    /**
     * Implements the mutation operation
     */
    private void doMutation(double mutationProbability, DoubleSolution solution) {

        int index = randomGenerator.nextInt(0, solution.getNumberOfVariables() - 1);
        double randomStepSize = mutationProbability *
                (solution.getUpperBound(index) -
                        solution.getLowerBound(index));
        double lower = solution.getVariableValue(index) - randomStepSize;
        double upper = solution.getVariableValue(index) + randomStepSize;

        if (lower < solution.getLowerBound(index)) lower = solution.getLowerBound(index);
        if (upper > solution.getUpperBound(index)) upper = solution.getUpperBound(index);

        Double value = randomGenerator.nextDouble(lower, upper);

        solution.setVariableValue(index, value);
    }

    public void setMutationProbability(Double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }
}
