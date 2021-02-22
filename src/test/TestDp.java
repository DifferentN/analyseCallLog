package test;

import java.util.*;

public class TestDp {
    public static void main(String args[]){
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b1");
        list1.add("c");
        list1.add("c");
        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b2");
        list2.add("c");
        List<Integer> res = reckonLongCommonSeq(list1,list2);
        for(int i:res){
            System.out.println(i);
        }
    }
    private static List<Integer> reckonLongCommonSeq(List<String> list1, List<String> list2){
        int len1 = list1.size(),len2 = list2.size();
        List<Integer> indexs = dp(list1,list2,len1-1,len2-1,new HashMap<String, ArrayList<Integer>>());
        return indexs;
    }
    private static ArrayList<Integer> dp(List<String> list1,List<String> list2,int x,int y,HashMap<String,ArrayList<Integer>> hash){
        String key = x+"/"+y;
        if(x<0||y<0) {
            return new ArrayList<Integer>();
        }
        if(hash.get(key)!=null) {
            return hash.get(key);
        }
        ArrayList<Integer> temp1 = null,temp2 = null,temp3=null,maxTemp;
        if( list1.get(x).equals(list2.get(y)) ) {
            temp1 =(ArrayList<Integer>)  dp(list1,list2,x-1,y-1,hash).clone();
            temp1.add(x);
        }
        temp2 = dp(list1,list2,x-1,y,hash);
        temp3 = dp(list1,list2,x,y-1,hash);

        if(temp2.size()>temp3.size()) {
            maxTemp = temp2;
        }else {
            maxTemp = temp3;
        }

        if(temp1!=null&&temp1.size()>maxTemp.size()) {
            maxTemp = temp1;
        }
        hash.put(key, maxTemp);
        return maxTemp;
    }
}
