package graph;

import java.util.ArrayList;
import java.util.List;

import analyseMethodCall.MyMethod;

public class GenerateGNode {
	private String getGN(MyMethod myMethod) {
		String node = myMethod.methodCaller+"/"+myMethod.methodName;
		ArrayList<String> childNodes = new ArrayList<>();
		List<MyMethod> childMethods = myMethod.childs;
		MyMethod childMethod = null;
		for(int i=0;i<childMethods.size();i++) {
			childMethod = childMethods.get(i);
			childNodes.add(getGN(childMethod));
		}
		for(int i=0;i<childNodes.size();i++) {
			node+=":"+childNodes.get(i);
		}
//		node += ":"+childNodes.hashCode();
		return "("+node+")";
	}
	public List<String> getNodeSeq(List<MyMethod> list){
		ArrayList<String> sequences = new ArrayList<>();
		for(int i=0;i<list.size();i++) {
			sequences.add( getGN(list.get(i)) );
		}
		return sequences;
	}

}
