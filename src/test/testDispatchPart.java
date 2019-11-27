package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import graph.CommonBlockSequence;
import graph.GenerateGNode;
import graph.LongCommonSequence;

public class testDispatchPart {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<MyMethod> callSeq1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/shipudaquan/methodLog-1.txt");
		List<MyMethod> callSeq2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/shipudaquan/methodLog-2.txt");
		List<MyMethod> callSeq3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/shipudaquan/methodLog-3.txt");
		List<MyMethod> callSeq4 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/shipudaquan/methodLog-4.txt");
		List<MyMethod> callSeq5 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/shipudaquan/methodLog-5.txt");
		callSeq1 = trimForShiPuSearch(callSeq1);
		callSeq2 = trimForShiPuSearch(callSeq2);
		callSeq3 = trimForShiPuSearch(callSeq3);
		callSeq4 = trimForShiPuSearch(callSeq4);
		callSeq5 = trimForShiPuSearch(callSeq5);
		
		List<List<MyMethod>> src = new ArrayList<>();
//		src.add(callSeq1);
		src.add(callSeq2);
		src.add(callSeq3);
		src.add(callSeq4);
		src.add(callSeq5);
		//计算最长公共子调用序列
//		LongCommonSequence loSequence = new LongCommonSequence();
//		List<String> res = loSequence.getLongCommonSeq(src);
		
//		List<List<String>> list = loSequence.transformListString(src);
//		List<String> res = reckonCommonMethod(list);
		
//		list = removeHashCode(list);
//		System.out.println((double)list.size()/callSeq5.size());
		
		//计算公共子调用序列
		CommonBlockSequence common = new CommonBlockSequence();
		List<String> res = common.reckonCommonBlock(src);
		for(String str:res) {
			System.out.println(str);
		}
		List<Integer> list = new ArrayList<>();
		Collections.sort(list,(Integer a,Integer b)->a-b);
	}
	private static List<MyMethod> trimForShiPuSearch(List<MyMethod> list){
		MyMethod myMethod = null;
		int size = list.size();
		for(int i=0;i<size;i++) {
			myMethod = list.get(i);
			if(myMethod.methodName.contains("dispatchTouchEvent")) {
				break;
			}else {
				list.remove(0);
			}
		}
		return list;
	}
	//保留第一个与第二个dispatch之间的部分
	private static List<MyMethod> trimMethods(List<MyMethod> list){
		ArrayList<MyMethod> res = new ArrayList<>();
		boolean add = false;
		int time = 0;
		MyMethod temp = null;
		String pre = "";
		for(int i=0;i<list.size();i++) {
			temp = list.get(i);
			if(temp.methodName.contains("dispatchTouchEvent")) {
				pre = "dispatchTouchEvent";
				if(add) {
					break;
				}
				continue;
			}else if(pre.equals("dispatchTouchEvent")) {
				add = true;
			}
			if(add) {
				res.add(temp);
			}
		}
		return res;
	}
	private static List<String> reckonCommonMethod(List<List<String>> list){
		List<String> res = new ArrayList<>();
		if(list.size()==1) {
			res = list.get(0);
		}else {
			res = getCommonList(list.get(0),list.get(1));
			for(int i=2;i<list.size();i++) {
				res = getCommonList(res,list.get(i));
			}
		}
		return res;
	}
	private static List<String> getCommonList(List<String> list1,List<String> list2){
		ArrayList<String> com = new ArrayList<>();
		List<String> cur = null,pre = null;
		String str2 = listToString(list2);
		String temp = null;
		int index = 0;
		for(int i=0;i<list1.size();i++) {
			pre = null;
			for(int j=i+1;j<=list1.size();j++) {
				cur = list1.subList(i, j);
				temp = listToString(cur);
				if((index=str2.indexOf(temp, index))>0) {
					pre = cur;
				}else if(pre!=null){
					com.addAll(pre);
					i = j;
					break;//不全面
				}
			}
		}
		return com;
	}
	private static String listToString(List<String> list) {
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<list.size();i++) {
			buf.append(list.get(i));
		}
		return buf.toString();
	}
}
