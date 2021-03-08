package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import analyseMethodCall.MyMethod;

public class LongCommonSequence {
	private List<MyMethod> preProcessMyMethods(List<MyMethod> methods) {
		ArrayList<MyMethod> res = new ArrayList<>();
		ArrayList<MyMethod> temp = new ArrayList<>();
		MyMethod method = null;
		for(int i=0;i<methods.size();i++) {
			method = methods.get(i);
			temp.add(method);
			if(method.methodName.equals("dispatchTouchEvent")) {
				res.addAll(temp);
				temp.clear();
			}
		}
		return res;
	}

	/**
	 * 获取公共InvokeTree
	 * @param src
	 * @return
	 */
	public List<MyMethod> getLongCommonSeq(List<List<MyMethod>> src){
		List<List<String>> listStr = new ArrayList<>();
		List<MyMethod> method = null;
		List<String> strs = null;
		GenerateGNode generateGNode = new GenerateGNode();
		for(int i=0;i<src.size();i++) {
			method = src.get(i);
//			method = preProcessMyMethods(src.get(i));
			strs = generateGNode.getNodeSeq(method);
			listStr.add(strs);
		}
		//
		//通过一一对比各组实例中的调用序列，将相同的调用序列加入到模板中，并未使用最长公共子序列的方式
		int size = Integer.MAX_VALUE;
		for(int i=0;i<listStr.size();i++){
			size = Math.min(size,listStr.get(i).size());
		}
		List<MyMethod> res = new ArrayList<>();
		List<MyMethod> temple = src.get(0);
		boolean flag = true;
		for(int i=0;i<size;i++){
			flag = true;
			for(int j=1;j<listStr.size();j++){
				if(!listStr.get(j-1).get(i).equals(listStr.get(j).get(i))){
//					System.out.println(listStr.get(j).get(i));
					flag = false;
					break;
				}
			}
			if(flag){
				res.add(temple.get(i));
			}
		}
		flag = true;
		if(flag){
			return  res;
		}
		//
		List<Integer> commonSeq = null;
		List<String> commonStrList = null;
		List<MyMethod> commonMethod =  new ArrayList<>();
		if(listStr.size()==1) {
			commonMethod = src.get(0);
		}else if(listStr.size()>1){
			commonSeq = reckonLongCommonSeq(listStr.get(0),listStr.get(1));
			commonStrList = new ArrayList<>();
			ArrayList<String> temp = new ArrayList<>();
			ArrayList<MyMethod> tempMethods = new ArrayList<>();
			for(int i=0;i<commonSeq.size();i++){
				commonStrList.add( listStr.get(0).get(commonSeq.get(i)) );
				commonMethod.add( src.get(0).get( commonSeq.get(i) ));
			}
			for(int i=2;i<listStr.size();i++) {
				temp.clear();
				tempMethods.clear();
				commonSeq = reckonLongCommonSeq(commonStrList,listStr.get(i));
				for(int j=0;j<commonSeq.size();j++){
					temp.add( commonStrList.get( commonSeq.get(j) ) );
					tempMethods.add( commonMethod.get( commonSeq.get(j) ) );
				}
				commonStrList = (List<String>) temp.clone();
				commonMethod = (List<MyMethod>) tempMethods.clone();
			}
		}
		return commonMethod;
	}

	public List<List<String>> transformListString(List<List<MyMethod>> src){
		List<List<String>> listStr = new ArrayList<>();
		List<MyMethod> method = null;
		List<String> strs = null;
		GenerateGNode generateGNode = new GenerateGNode();
		for(int i=0;i<src.size();i++) {
			method = src.get(i);
			strs = generateGNode.getNodeSeq(method);
			listStr.add(strs);
		}
		return listStr;
	}
	private boolean IsEmpty(List<List<String>> list) {
		boolean flag = false;
		List<String> temp = null;
		for(int i=0;i<list.size();i++) {
			temp = list.get(i);
			if(temp.isEmpty()) {
				return true;
			}
		}
		return flag;
	}

	/**
	 * 获取最长公共子序列的索引
	 * @param list1
	 * @param list2
	 * @return 第一个list所代表的索引
	 */
	private List<Integer> reckonLongCommonSeq(List<String> list1,List<String> list2){
		int len1 = list1.size(),len2 = list2.size();
		List<Integer> indexs = dp(list1,list2,len1-1,len2-1,new HashMap<String,ArrayList<Integer>>());
		return indexs;
	}
	private ArrayList<Integer> dp(List<String> list1,List<String> list2,int x,int y,HashMap<String,ArrayList<Integer>> hash){
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
