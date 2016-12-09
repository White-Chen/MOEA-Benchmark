
package org.uma.jmetal.algorithm.multiobjective.moead;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by dy on 2016/11/23.
 */
public class KMmatching {
    private int maxN, n, lenX, lenY;     //lenX和lenY是其矩阵长度
    private double[][] kMweights;          //权重矩阵
    private boolean[] visitX, visitY;   //X、Y 匹配状态的列表
    private double[] lx, ly;             //X、Y的标杆值
    private double[] slack;              //寻找增广路径时，两次匹配的差值（需要和差值最小的点匹配）
    public int[] match;                 //匹配关系表,配对后X的序号

    public KMmatching(int maxN){
        this.maxN = maxN;
        visitX = new boolean[maxN];
        visitY = new boolean[maxN];
        lx = new double[maxN];
        ly = new double[maxN];
        slack = new double[maxN];
        match = new int[maxN];
    }

    public double getBestBipartie(double weight[][]){
        double result;
        if(!preProcess(weight))
        {
            result = 0.0;
            return result;
        }
        Arrays.fill(ly,0);
        Arrays.fill(lx,0);
        //设置初始标杆值，将X标杆值设为最大权重值
        for (int i=0;i<n;i++)
        {
            for (int j=0;j<n;j++)
            {
                if (lx[i] < kMweights[i][j])
                    lx[i] = kMweights[i][j];
            }
        }
        //find a match for each x point
        for (int u=0;u<n;u++)
        {
            Arrays.fill(slack,0x7fffffff);//0x7fffffff为int的最大值，
            while (true)
            {
                Arrays.fill(visitX,false);
                Arrays.fill(visitY,false);
                if(findPath(u))           //若为第U个点找到了增广路径，则结束while循环,若未找到增广路径，则需要重新需找匹配点
                    break;
                double inc = 0x7fffffff;
                for ( int v=0;v<n;v++)
                {
                    if(!visitY[v] && slack[v]<inc)          //若Y阵营第V未配对
                        inc = slack[v];
                }
                //找到最小损失后，将X的已配对点减去损失值（inc），Y已配对点加上损失值
                for (int i=0;i<n;i++)
                {
                    if(visitX[i])
                        lx[i] -= inc;
                    if(visitY[i])
                        ly[i] +=inc;
                }
            }
        }
        result = 0.0;
        for (int i=0;i<n; i++)
        {
            if(match[i] >=0)
                result +=kMweights[match[i]][i];   //将配对点的权重相加
        }
        return result;
    }

    //返回匹配的结果，第一维是X第二维是Y
    public int[][] matchResult(){
        int len = Math.min(lenX,lenY);
        int[][] res = new int[len][2];
        int count=0;
        for (int i=0;i<lenY;i++)
        {
            if (match[i]>=0 && match[i]<lenX)
            {
                res[count][0] = match[i];
                res[count++][1] = i;
            }
        }
        return res;
    }

    //预处理weight ,将weight转化为方阵（行列相等），空值处置0
    private boolean preProcess(double[][] weight){
        if(weight == null)
            return false;
        lenX = weight.length; lenY = weight[0].length;
        if( lenX>maxN || lenY>maxN)
            return false;
        Arrays.fill(match,-1);    //初始化match内全为-1
        n = Math.max(lenX,lenY);
        kMweights = new double[n][n];
        for(int i=0;i<n;i++)
            Arrays.fill(kMweights[i],0.0);
        for (int i=0;i<lenX;i++)
            for (int j=0;j<lenY;j++)
                kMweights[i][j]=weight[i][j];
        return true;
    }

    //为第u个点寻找增广矩阵
    private boolean findPath(int u){
        visitX[u] = true;
        double inc = 0x7fffffff;
        for ( int v=0;v<n;v++ )
        {
            if (!visitY[v])       //选取未配对的Y点
            {
               double temp = lx[u]+ly[v]-kMweights[u][v];
                //double temp = decimalSubtract(decimalAdd(lx[u],ly[v]),kMweights[u][v]);     //高精度计算
                if (temp <1E-10)
                {
                    visitY[v]=true;
                    if (match[v]==-1 || findPath(match[v]))
                    {
                        match[v]=u;
                        return true;
                    }
                }
                else
                {
                    slack[v] = Math.min(slack[v],temp);
                }
            }
        }
        return false;
    }
    private double decimalAdd(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    private double decimalSubtract(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static void main(String[] args)
    {
        double[][] cost = new double[][]{{3,4,6,4,9},{6,4,5,3,8},{7,5,3,4,2},{6,3,2,2,5},{8,4,5,4,7}};
//        for (int i=0;i<cost.length;i++)
//        {
//            for (int j=0;j<cost[0].length;j++)
//            {
//                cost[i][j] = -cost[i][j];
//            }
//        }
        KMmatching km = new KMmatching(5);
        double r = km.getBestBipartie(cost);
        int[] result =km.match;
        System.out.println("Bipartite Matching: " + r);
    }

}
