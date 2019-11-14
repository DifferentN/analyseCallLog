package test;

import java.util.Arrays;
import java.util.Random;

public class TestDp {
    public static void main(String args[]){
        int len1 = 20,len2 = 26;
        float[] grid[] = new float[len1][len2];
        Random random = new Random();
        for(int i=0;i<len1;i++){
            for(int j=0;j<len2;j++){
//                grid[i][j] = random.nextInt(1000)*0.01f;
//                System.out.print(grid[i][j]+" ");
            }
//            System.out.println("");
        }
        float [][] flag = new float[len1][len2];
        for(int i=0;i<len1;i++){
            Arrays.fill(flag[i],-1f);
        }
        System.out.println(dp(0,0,len1,len2,flag,grid));
    }
    private static float dp(int x,int y,int len1,int len2,float[][] flag,float[][] grid){
        if(x>=len1||y>=len2){
            return 0;
        }
        if(flag[x][y]>=0){
            return flag[x][y];
        }
        float res = -1f;
        for(int i=y;i<len2;i++){
            res = Math.max(res,grid[x][i]+dp(x+1,i+1,len1,len2,flag,grid));
        }
        res = Math.max(res,dp(x+1,y,len1,len2,flag,grid));
        flag[x][y] = res;
        return res;
    }
}
