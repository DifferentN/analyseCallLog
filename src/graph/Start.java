package graph;

import java.util.ArrayList;
import java.util.List;

import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;

public class Start {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<MyMethod> callSeq1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/douban/methodLog-1.txt");
		List<MyMethod> callSeq2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/douban/methodLog-2.txt");
		List<MyMethod> callSeq3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/douban/methodLog-3.txt");
		List<MyMethod> callSeq4 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/douban/methodLog-4.txt");
		List<MyMethod> callSeq5 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/douban/methodLog-5.txt");
		LongCommonSequence loSequence = new LongCommonSequence();
		List<List<MyMethod>> src = new ArrayList<>();
		src.add(callSeq1);
		src.add(callSeq2);
		src.add(callSeq3);
		src.add(callSeq4);
		src.add(callSeq5);
		List<MyMethod> list = loSequence.getLongCommonSeq(src);
//		list = removeHashCode(list);
//		System.out.println((double)list.size()/callSeq5.size());
//		for(String str:list) {
//			System.out.println(str);
//		}
	}
	private static List<String> removeHashCode(List<String> list){
		ArrayList<String> res = new ArrayList<>();
		String strs[],temp;
		for(int i=0;i<list.size();i++) {
			temp = list.get(i);
			strs = temp.split(":");
			res.add(strs[0]);
		}
		return res;
	}
}
