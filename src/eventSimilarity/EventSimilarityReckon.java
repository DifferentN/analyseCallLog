package eventSimilarity;

import analyseMethodCall.MyMethod;
import reformCall.TransformMethodUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EventSimilarityReckon {
    private HashMap<String,Float> similarHash;
    public EventSimilarityReckon(HashMap<String,Float> hashMap){
        similarHash = hashMap;
    }

    /**
     * 计算两个event之间的相似度
     * @param event1
     * @param event2
     * @return
     */
    public float reckonEventSimilarity(Event event1,Event event2){
        //不是同一个操作
        if(!event1.getMethodName().equals(event2.getMethodName())||!event1.getPath().equals(event2.getPath())){
            return -1;
        }
        //event没有调用序列时的判断方法
        if(event1.getInvokeList().size()==0&&event2.getInvokeList().size()==0){
            if(event1.getMethodName().equals(event2.getMethodName())
                    &&event1.getComponentId().equals( event2.getComponentId())
                    && event1.getPath().equals(event2.getPath())){
                return 2;
            }else{
                return 0;
            }
        }
        float similarity = reckonInvokeList(event1.getInvokeList(),event2.getInvokeList());
        return  similarity;
    }
    /**
     * 计算两个InvokeTree之间的相似度
     * @param myMethod1
     * @param myMethod2
     * @return
     */
    public float reckonInvokeTree(MyMethod myMethod1,MyMethod myMethod2){
        //节点的调用序列相似度
        float similarity1 = reckonInvokeList(myMethod1.childs,myMethod2.childs);
        //节点方法相似度
        float similarity2 = getMethodSimilarity(myMethod1,myMethod2);
        return similarity1+similarity2;
    }

    /**
     * 计算两个InvokeTree序列之间的相似度
     * @param invokes1 如果是两个不同的版本，表示旧版本的调用序列
     * @param invokes2 如果是两个不同的版本，表示新版本的调用序列
     * @return
     */
    public float reckonInvokeList(List<MyMethod> invokes1,List<MyMethod> invokes2){
        if(invokes1==null||invokes2==null||invokes1.size()==0||invokes2.size()==0){
            return 0;
        }
        int size1 = invokes1.size(),size2 = invokes2.size();
        //记录两个InvokeTree序列之间的相似度
        float[][] grid = new float[size1][size2];

        for(int i=0;i<size1;i++){
            for(int j=0;j<size2;j++){
                grid[i][j] = reckonInvokeTree(invokes1.get(i),invokes2.get(j));
            }
        }
        float similarity = dpSimilarity(grid);
        similarity/=(invokes1.size()+invokes2.size());
        return similarity;
    }

    /**
     * 获取两个方法之间的相似度，如果similarityHash有这两个方法的相似值，这返回此值；否则返回0
     * @param myMethod1
     * @param myMethod2
     * @return
     */
    private float getMethodSimilarity(MyMethod myMethod1,MyMethod myMethod2){
        String methodInfo1 = TransformMethodUtil.getMethodInfo(myMethod1);
        String methodInfo2 = TransformMethodUtil.getMethodInfo(myMethod2);
        String key = methodInfo1+"<->"+methodInfo2;
        if (similarHash.get(key)==null){
            return 0;
        }else{
            return similarHash.get(key);
        }
    }

    /**
     * 插花算法
     * grid的行相当于花，列相当于花瓶
     * @param grid 保存了两个InvokeTree序列之间的相似度
     * @return
     */
    private float dpSimilarity(float[][] grid){
        int len1 = grid.length,len2 = grid[0].length;
        float [][] flag = new float[len1][len2];
        for(int i=0;i<flag.length;i++){
            Arrays.fill(flag[i],-1f);
        }
        //计算两个序列（旧新序列）的相似度
        float max = dp(0,0,len1,len2,flag,grid);
        return max;
    }

    /**
     * 下面第一个序列指旧调用树序列集合中的序列
     * 第二个序列指新调用树序列集合中的序列
     * 相当于插画算法dp
     * 计算如何匹配可以使相似度最大
     * 计算第一个调用树序列从x位置开始的子序列与
     * 第二个调用树序列从y位置开始的子序列的相似度
     * @param x
     * @param y
     * @param len1
     * @param len2
     * @param flag flag[i][j] 表示第一个序列从第i个子树开始的子序列与第二个子树从第j个子树开始的子序列的相似度
     * @param grid grid[i][j] 表示第一个序列中第i个子树与第二个序列中第j个子树的相似度
     *             行可以看作第一个调用树序列（旧调用树序列）
     *             列可以看作第二个调用树序列（新调用树序列）
     * @return
     */
    private float dp(int x,int y,int len1,int len2,float[][] flag,float[][] grid){
        if(x>=len1||y>=len2){
            return 0;
        }
        if(flag[x][y]>=0){
            return flag[x][y];
        }
        float res = -1f;
        //匹配情况
        //与当前的y进行匹配
        res = grid[x][y]+dp(x+1,y+1,len1,len2,flag,grid);
        //不与当前的y进行匹配
        res = Math.max(res,dp(x,y+1,len1,len2,flag,grid));
        //不进行匹配
        res = Math.max(res,dp(x+1,y,len1,len2,flag,grid));
        flag[x][y] = res;
        return res;
    }

}
