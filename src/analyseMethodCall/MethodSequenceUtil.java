package analyseMethodCall;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class MethodSequenceUtil {
	public static List<MyMethod> getSequence(String fileName) {
		ArrayList<MyMethod> callSeq;
		ArrayList<String> allSeq = new ArrayList<>();
		File file = new File(fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
			while((line=reader.readLine())!=null) {
				allSeq.add(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		callSeq = analyseSequence(allSeq);
		callSeq = adjustSetTextCommitText(callSeq);
		return callSeq;
	}

	private static ArrayList<MyMethod> analyseSequence(ArrayList<String> allSeq) {
		// TODO Auto-generated method stub
		List<MyMethod> stack = new ArrayList<>();
		ArrayList<MyMethod> callSeq = new ArrayList<>();
		int beforeNum = 0,afterNum = 0;
		if(allSeq==null){
			System.out.println("allSeq is null");
		}else System.out.println("allSeq: "+allSeq.size());

		for(String methodName :allSeq) {
			System.out.println(methodName);
			if(methodName.startsWith("before: ")) {
				beforeNum++;
				System.out.println(beforeNum+" "+methodName.length());
				String input = methodName.substring("before: ".length(), methodName.length());
				System.out.println(input);
				if(!checkThreadId(input,1)) {
					System.out.println("continue");
					continue;
				}
				System.out.println("before create myMethod");
				MyMethod method = new MyMethod(input);
				System.out.println("after create myMethod");
				method.setInput(input);
				stack.add(method);
			}else if(methodName.startsWith("after: ")){
				afterNum++;
				String output = methodName.substring("after: ".length(), methodName.length());
				if(!checkThreadId(output,1)) {
					continue;
				}
				System.out.println("stack size: "+stack.size());
				MyMethod curM = stack.remove(stack.size()-1);
				curM.setOutput(output);
				if(stack.size()==0) {
					callSeq.add(curM);
					continue;
				}
				System.out.println("stack size: "+stack.size());
				MyMethod preM = stack.get(stack.size()-1);
				preM.addChild(curM);
				curM.parent = preM;
			}else {
				System.out.println("error: "+methodName);
			}
		}

		for(MyMethod method :stack){
			System.out.println(method.name);
		}
//		System.out.println(beforeNum+" "+afterNum);
		return callSeq;
	}
	private static boolean checkThreadId(String jsonString,int threadId) {
		System.out.println("before parse, "+JSONObject.class.getName());
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		System.out.println("after parse");
		int id = jsonObject.getIntValue("threadId");
		if(id==threadId) {
			return true;
		}
		return false;
	}

	/**
	 * 将myMethodList中的commitText的位置替换为setText
	 * @param myMethodList
	 * @return
	 */
	private static ArrayList<MyMethod> adjustSetTextCommitText(List<MyMethod> myMethodList){
		ArrayList<MyMethod> res = new ArrayList<>();
		MyMethod myMethod = null,temp = null;
		for(int i=0;i<myMethodList.size();i++){
			myMethod = myMethodList.get(i);

			if(myMethod.methodName.equals("commitText")&&myMethod.methodCaller.contains("android.view.inputmethod.BaseInputConnection")){
				List<MyMethod> childs = myMethod.childs;
				while (!childs.isEmpty()){
					MyMethod child = childs.remove(0);
					if(child.methodName.equals("setText")&&child.methodCaller.contains("MyTextWatcher")){
						res.add(child);
						break;
					}
					childs.addAll(child.childs);
				}
			}else{
				res.add(myMethod);
			}
		}
		return res;
	}
}
