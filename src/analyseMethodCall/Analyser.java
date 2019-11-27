package analyseMethodCall;

import com.alibaba.fastjson.JSONObject;

import FileUtil.MyFileWriter;
import graph.GenerateGNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.transform.sax.TemplatesHandler;

public class Analyser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//读取指定文件中的打印序列
		List<MyMethod> callSeq = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/3APP/timberX/changeTheme/methodLog-3.txt");
		String pre = "";
		GenerateGNode generateGNode = new GenerateGNode();

		for(MyMethod m:callSeq) {
			System.out.println(m.methodCaller+"/"+m.methodName+"--"+generateGNode.getNodeSeq(m.childs).toString());
		}
	}
	private static void reckonRelationInvoke(List<MyInvoke> myInvokes,String name){
		HashMap<Long ,String> parameter = new HashMap<>(),result = new HashMap<>();
		MyInvoke myInvoke = null,next = null,pre = null;
		for(int i=0;i<myInvokes.size();i++){
			myInvoke = myInvokes.get(i);
			if(myInvoke.getMethodName().contains(name)){
				parameter.putAll(myInvoke.getParameters());
				result.putAll(myInvoke.getResults());
				for(int j=i+1;j<myInvokes.size();j++){
					if(checkInvoke(result,myInvokes.get(j).getParameters())){
						result.putAll(myInvokes.get(j).getResults());
						parameter.putAll(myInvokes.get(j).getParameters());
					}
				}
			}
		}
		for(int i=myInvokes.size()-1;i>=0;i--){
			if(checkInvoke(parameter,myInvokes.get(i).getResults())){
				parameter.putAll(myInvokes.get(i).getParameters());
			}
		}
		for(int i=0;i<myInvokes.size();i++){
			if(checkInvoke(parameter,myInvokes.get(i).getResults())){
				System.out.println(myInvokes.get(i).getMyMethod().name);
			}
		}
	}
	private static void checkInput(List<MyMethod> callSeq,List<String> inputStrs){
		for(MyMethod myMethod:callSeq){
			if(myMethod.name.contains("onClick")){
				MethodDataUtil.findMethodInput(myMethod,inputStrs);
			}
		}
	}
	private static boolean checkInvoke(HashMap<Long,String> hash1,HashMap<Long,String> hash2){
		Set<Long> keys = hash1.keySet();
		for(Long key:keys){
			if(hash2.get(key)!=null){
				return true;
			}
		}
		return false;
	}
	private static void checkSameMethod(List<MyMethod> callSeq) {
		HashMap<String,List<MyMethod>> hash = new HashMap<>();
		String name = "";
		MyMethod myMethod;
		List<MyMethod> list;
		for(int i=0;i<callSeq.size();i++) {
			myMethod = callSeq.get(i);
			name = myMethod.methodCaller+"/"+myMethod.methodName;
			if(hash.get(name)==null) {
				list = new ArrayList<>();
				list.add(myMethod);
				hash.put(name, list);
			}else {
				list = hash.get(name);
				list.add(myMethod);
			}
		}
	}
	private static void printSubTree(String space,MyMethod root, MyFileWriter myFileWriter) {
		String name = "";
		List<MyMethod> childs = root.childs;
		MyMethod child = null;
		if(childs==null) {
			return;
		}
		
		for(int i=0;i<childs.size();i++) {
			child = childs.get(i);
			name = child.methodCaller+"/"+child.methodName;
			System.out.println(space+""+name);
			myFileWriter.write(space+""+name);
			printSubTree(space+" ",child, myFileWriter);
		}
	}

}
