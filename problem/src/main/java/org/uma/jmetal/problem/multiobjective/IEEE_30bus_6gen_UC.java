package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * \* Created with Chen Zhe on 3/5/2017.
 * \* Description: This is a IEEE 30 Bus 6 Generator
 * optimization problem with 2 objective and 2 constraints
 * \* @author ChenZhe
 * \* @author q953387601@163.com
 * \* @version 1.0.0
 * \
 */
public class IEEE_30bus_6gen_UC extends AbstractDoubleProblem{

    private double[] a = {10, 10, 20, 10, 20, 10};
    private double[] b = {200, 150, 180, 100, 180, 150};
    private double[] c = {100, 120, 40, 60, 40, 100};
    private double[] alpha = {4.091, 2.543, 4.258, 5.326, 4.258, 6.131};
    private double[] beta = {-5.554, -6.047, -5.094, -3.55, -5.094, -5.555};
    private double[] gamma = {6.49, 5.638, 4.586, 3.38, 4.586, 5.151};
    private double[] sida = {0.0002, 0.0005, 0.000006, 0.003, 0.000001, 0.00001};
    private double[] lambda = {2.857, 3.333, 8.000, 2.000, 8.000, 6.667};
    private double[][] B = {
            {0.0218, 0.0107, -0.00036, -0.0011, 0.00055, 0.0033},
            {0.0107, 0.01704, -0.0001, -0.00179, 0.00026, 0.0028},
            {-0.0004, -0.0002, 0.02459, -0.01328, -0.0118, -0.0079},
            {-0.0011, -0.00179, -0.01328, 0.0265, 0.0098, 0.0045},
            {0.00055, 0.00026, -0.0118, 0.0098, 0.0216, -0.0001},
            {0.0033, 0.0028, -0.00792, 0.0045, -0.00012, 0.02978}
    };
    private double[] B_i0 = {0.000010731, 0.0017704, -0.0040645, 0.0038453, 0.0013832, 0.0055503};
    private double B_00 = 0.0014;
    private double Pd = 0.5 + 3.4 * 24 / 24;

    /**
     * Instantiates a new Ieee 30 bus 6 gen uc.
     */
    public IEEE_30bus_6gen_UC(){this(6);}

    /**
     * Instantiates a new Ieee 30 bus 6 gen uc.
     *
     * @param numberOfVariable the number of variable
     */
    public IEEE_30bus_6gen_UC(Integer numberOfVariable){
        this.setNumberOfVariables(numberOfVariable);
        this.setNumberOfObjectives(2);
        setName("IEEE_30Bus_6Generator");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        lowerLimit.add(0.05);
        lowerLimit.add(0.05);
        lowerLimit.add(0.05);
        lowerLimit.add(0.05);
        lowerLimit.add(0.05);
        lowerLimit.add(0.05);

        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());
        upperLimit.add(0.05);
        upperLimit.add(0.06);
        upperLimit.add(1.00);
        upperLimit.add(1.20);
        upperLimit.add(1.00);
        upperLimit.add(0.06);

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        solution.setObjective(0,evalFuelCost(solution) + evalLoss(solution) + evalBalance(solution));
        solution.setObjective(1,evalEnvironment(solution) + evalLoss(solution) + evalBalance(solution));
        //solution.setObjective(2,evalBalance(solution) + evalLoss(solution));
    }

    private double evalFuelCost(DoubleSolution solution){
        double sum = 0;
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            sum = sum
                    + a[i]
                    + b[i]*solution.getVariableValue(i)
                    + c[i]*Math.pow(solution.getVariableValue(i),2);
        }
        return sum;
    }

    private double evalEnvironment(DoubleSolution solution){
        double sum = 0;
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            sum = sum
                    + 0.01*(
                            alpha[i]
                            + beta[i]*solution.getVariableValue(i)
                            + gamma[i]*Math.pow(solution.getVariableValue(i),2))
                    + sida[i]*Math.exp(lambda[i]*solution.getVariableValue(i));
        }
        return sum;
    }

    private double evalLoss(DoubleSolution solution){
        double loss = 0;
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            for (int j = 0; j < solution.getNumberOfVariables(); j++) {
                loss = loss
                        + solution.getVariableValue(i) * B[i][j] * solution.getVariableValue(j);
            }
            loss = loss
                    + B_i0[i]*solution.getVariableValue(i);
        }
        loss += B_00;
        return loss;
    }

    private double evalBalance(DoubleSolution solution){
        double balance = 0;
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            balance += solution.getVariableValue(i);
        }
        return Math.abs(balance - Pd - evalLoss(solution));
    }
}
