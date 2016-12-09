//  Utils.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

package org.uma.jmetal.algorithm.multiobjective.moead.util;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Utilities methods to used by MOEA/D
 */
public class MOEADUtils {

    /**
     * Quick sort procedure (ascending order)
     *
     * @param array
     * @param idx
     * @param from
     * @param to
     */
    public static void quickSort(double[] array, int[] idx, int from, int to) {
        if (from < to) {
            double temp = array[to];
            int tempIdx = idx[to];
            int i = from - 1;
            for (int j = from; j < to; j++) {
                if (array[j] <= temp) {
                    i++;
                    double tempValue = array[j];
                    array[j] = array[i];
                    array[i] = tempValue;
                    int tempIndex = idx[j];
                    idx[j] = idx[i];
                    idx[i] = tempIndex;
                }
            }
            array[to] = array[i + 1];
            array[i + 1] = temp;
            idx[to] = idx[i + 1];
            idx[i + 1] = tempIdx;
            quickSort(array, idx, from, i);
            quickSort(array, idx, i + 1, to);
        }
    }

    public static double distVector(double[] vector1, double[] vector2) {
        int dim = vector1.length;
        double sum = 0;
        for (int n = 0; n < dim; n++) {
            sum += (vector1[n] - vector2[n]) * (vector1[n] - vector2[n]);
        }
        return Math.sqrt(sum);
    }

    public static void minFastSort(double x[], int idx[], int n, int m) {
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < n; j++) {
                if (x[i] > x[j]) {
                    double temp = x[i];
                    x[i] = x[j];
                    x[j] = temp;
                    int id = idx[i];
                    idx[i] = idx[j];
                    idx[j] = id;
                }
            }
        }
    }

    public static void randomPermutation(int[] perm, int size) {
        JMetalRandom randomGenerator = JMetalRandom.getInstance();
        int[] index = new int[size];
        boolean[] flag = new boolean[size];

        for (int n = 0; n < size; n++) {
            index[n] = n;
            flag[n] = true;
        }

        int num = 0;
        while (num < size) {
            int start = randomGenerator.nextInt(0, size - 1);
            while (true) {
                if (flag[start]) {
                    perm[num] = index[start];
                    flag[start] = false;
                    num++;
                    break;
                }
                if (start == (size - 1)) {
                    start = 0;
                } else {
                    start++;
                }
            }
        }
    }
    /**
     * 计算两个权重间的距离
     * @param weight1
     * @param weight2
     * @return
     */
    public static double distance(double[] weight1, double[] weight2) {
        double sum = 0;
        for (int i = 0; i < weight1.length; i++) {
            sum += Math.pow((weight1[i] - weight2[i]), 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * 计算权重间的角度
     */
    public static double angle(double[] weight1, double[] weight2){
        double sum = 0;
        return acosine(weight1,weight2);

    }

    /**
     * Returns the angle between the objective vector and a reference vector.
     * This method assumes the line is a normalized weight vector; the point
     * does not need to be normalized.
     *
     * @param vector the line originating from the origin
     * @param point the point
     * @return the angle (acosine)
     */
    public static double acosine(double[] vector, double[] point) {
        return Math.acos(cosine(vector, point));
    }

    /**
     * Returns the cosine between the objective vector and a reference vector.
     * This method assumes the line is a normalized weight vector; the point
     * does not need to be normalized.
     * @param vector the line originating from the origin
     * @param point the point
     * @return the cosine
     */
    public static double cosine(double[] vector, double[] point) {

        return dot(point, vector) / (magnitude(vector)*magnitude(point));
    }

    /**
     * Returns the dot (inner) product of the two specified vectors. The two
     * vectors must be the same length.
     *
     * @param u the first vector
     * @param v the second vector
     * @return the dot (inner) product of the two specified vectors
     * @throws IllegalArgumentException if the two vectors are not the same length
     */
    public static double dot(double[] u, double[] v) {
        int n = length(u, v);
        double dot = 0.0;

        for (int i = 0; i < n; i++) {
            dot += u[i] * v[i];
        }

        return dot;
    }

    /**
     * Returns the length of the two specified vectors.
     *
     * @param u the first vector
     * @param v the second vector
     * @return the length of the two specified vectors
     * @throws IllegalArgumentException if the two vectors are not the same length
     */
    private static int length(double[] u, double[] v) {
        if (u.length != v.length) {
            throw new IllegalArgumentException("vectors must have same length");
        }

        return u.length;
    }

    /**
     * Returns the difference between the two specified vectors, {@code u - v}.
     * The two vectors must be of the same length.
     *
     * @param u the first vector
     * @param v the second vector
     * @return the difference between the two specified vectors, {@code u - v}
     * @throws IllegalArgumentException if the two vectors are not the same length
     */
    public static double[] subtract(double[] u, double[] v) {
        int n = length(u, v);
        double[] w = new double[n];

        for (int i = 0; i < n; i++) {
            w[i] = u[i] - v[i];
        }

        return w;
    }

    /**
     * Returns the magnitude (Euclidean norm) of the specified vector.
     *
     * @param u the vector
     * @return the magnitude (Euclidean norm) of the specified vector
     */
    public static double magnitude(double[] u) {
        return Math.sqrt(dot(u, u));
    }

    /**
     * 求数组U最小的值
     * @param u
     * @return
     */
    public static double min(double[] u){
        double tMin = u[0];
        for (int i=0;i<u.length;i++)
        {
            tMin = (u[i]<tMin)?u[i]:tMin;
        }
        return tMin;
    }

    /**
     * 标准化目标值，并返回标准化后的模值
     * @param u 目标值
     * @param idealPoint 理想点
     * @return
     */
    public static double normalize (double[] u,double[] idealPoint){
        double[] normalObjectiveValue = subtract(u,idealPoint);
        return magnitude(normalObjectiveValue);
    }
}
