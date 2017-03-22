package org.uma.jmetal.util.pca;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Random;

/**
 * Created by zhoulifa on 17-3-4.
 */
public class PrincipalComponentAnalysis {
    Random rand = new Random();

    public static void main(String[] args){
        double[][] x= { {2.5,2.4},{0.5,0.7},{2.2,2.9},{1.9,2.2},{3.1,3.0},{2.3,2.7},{2,1.6},{1,1.1},{1.5,1.6},{1.1,0.9}};
        PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
        double[] weight = pca.weightFromPCA(x,10,2);
        for (int i = 0; i < weight.length; i++){
            System.out.print(weight[i]+" ");
        }
        System.out.println();
    }

    public double[] weightFromPCA(double[][] data, int row ,int col){

        double[] weights = new double[col];
        // 数据标准化
        double[][] indatstd = Standardize(row, col, data);
        Matrix X = new Matrix(indatstd);

        // 矩阵转置（transpose），并相乘(times)
        Matrix Xprime = X.transpose();
        Matrix SSCP   = Xprime.times(X);
        for (int i = 0; i < SSCP.getRowDimension(); i++){
            for (int j = 0; j < SSCP.getColumnDimension(); j++){
                SSCP.set(i, j, SSCP.get(i, j) / (indatstd.length - 1));
            }
        }
        // 特征分解
        EigenvalueDecomposition evaldec = SSCP.eig();
        // 特征向量（getV） 特征值（getRealEigenvalues）
        Matrix evecs = evaldec.getV();
        double[] evals = evaldec.getRealEigenvalues();

        double tot = 0.0;
        for (int j = 0; j < evals.length; j++)  {
            tot += evals[j];
        }

        double runningtotal = 0.0;
        double[] percentEvals = new double[col];
        for (int j = 0; j < evals.length; j++) {
            percentEvals[j] = runningtotal + 100.0 * evals[j] / tot;
            runningtotal    = percentEvals[j];
        }
        double r = rand.nextDouble() * 100;

        int flat = percentEvals.length - 1;
        for (int j = 0; j < percentEvals.length; j++){
            if (r <= percentEvals[j]){
                flat = j;
                break;
            }
        }
        double[][] evecsArray = evecs.getArray();
        for (int j = 0; j < evecsArray.length; j++){
            weights[j] = evecsArray[j][flat] * evecsArray[j][flat] + 0.5;
        }

        //权重范围
        for (int i = 0; i < col; i++){
            if (weights[i] > 1.0){
                weights[i] = 1.0;
            }
        }

//        System.out.println("%%%%%%%%%%%%%");
//        for (int j = 0;j <col; j++){
//            System.out.print(evals[j]+"        ");
//            for (int i=0;i<col;i++){
//                System.out.print(evecsArray[j][i]+" ");
//            }
//            System.out.println();
//        }

        return weights;
    }

    public double[][] Standardize(int nrow, int ncol, double[][] A)
    {
        double[] colmeans = new double[ncol];
        double[] colstdevs = new double[ncol];
        // Adat will contain the standardized data and will be returned
        double[][] Adat = new double[nrow][ncol];
        double[] tempcol = new double[nrow];
        double tot;

        // Determine means and standard deviations of variables/columns
        for (int j=0; j<ncol; j++)
        {
            tot = 0.0;
            for (int i=0; i<nrow; i++)
            {
                tempcol[i] = A[i][j];
                tot += tempcol[i];
            }

            // For this col, det mean
            colmeans[j] = tot/(double)nrow;
            for (int i=0; i<nrow; i++) {
                colstdevs[j] += Math.pow(tempcol[i]-colmeans[j], 2.0);
            }
            colstdevs[j] = Math.sqrt(colstdevs[j]/((double)nrow));
            if (colstdevs[j] < 0.0001) { colstdevs[j] = 1.0; }
        }

        // Now ceter to zero mean, and reduce to unit standard deviation
        for (int j=0; j<ncol; j++)
        {
            for (int i=0; i<nrow; i++)
            {
                Adat[i][j] = (A[i][j] - colmeans[j]);
//                Adat[i][j] = (A[i][j] - colmeans[j])/
//                        (Math.sqrt((double)nrow)*colstdevs[j]);
            }
        }
        return Adat;
    } // Standardize
}
