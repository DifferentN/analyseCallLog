package reformCall;

import java.util.ArrayList;
import java.util.List;

import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;

public class StartReformMethod {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<MyMethod> list = loadMethod();
		String path = "reformLog.txt";
		ReformMethod reformMethod = new ReformMethod(path);
		reformMethod.reformMethod(list);
	}
	private static List<MyMethod> loadMethod() {
//		List<MyMethod> list1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog_add.txt");
//		List<MyMethod> list2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog_show.txt");
		List<MyMethod> list3 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog_original_all.txt");
		List<MyMethod> res = new ArrayList<MyMethod>();
//		res.addAll(list1);
//		res.addAll(list2);
		res.addAll(list3);
		return res;
	}

}
