package analyseMethodCall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
			BufferedReader reader = new BufferedReader(fileReader);
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
		for(String methodName :allSeq) {
			if(methodName.startsWith("before: ")) {
				beforeNum++;
				String input = methodName.substring("before: ".length(), methodName.length());
				if(!checkThreadId(input,1)) {
					continue;
				}
				MyMethod method = new MyMethod(input);
				method.setInput(input);
				stack.add(method);
			}else if(methodName.startsWith("after: ")){
				afterNum++;
				String output = methodName.substring("after: ".length(), methodName.length());
				if(!checkThreadId(output,1)) {
					continue;
				}
				MyMethod curM = stack.remove(stack.size()-1);
				curM.setOutput(output);
				if(stack.size()==0) {
					callSeq.add(curM);
					continue;
				}
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
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
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
