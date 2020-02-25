package buildModel;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import analyseMethodCall.MyMethod;

/*
 *  **对于连续的用户操作只保留一个方法
 */
public class PreProcess {
	public List<List<MyMethod>> preProcess(List<List<MyMethod>> list,List<String> userInput){
		List<List<MyMethod>> res = new ArrayList<>();
		List<MyMethod> temp = null;
		for(int i=0;i<list.size();i++) {
			res.add(processDataLink(list.get(i),userInput));
		}
		list = res;
		res = new ArrayList<>();
		for(int i=0;i<list.size();i++) {
			temp = list.get(i);
			temp = processSingleList(temp);
			res.add(temp);
		}
		return res;
	}
	private List<MyMethod> processSingleList(List<MyMethod> src){
		List<MyMethod> res = reduceSetText(src);
//		res = reduceDispatch(res,1);
		res = mergeDispatch(res);
		return res;
	}
	/**
	 *  去除多余的setText和第一个setText之前的内容（之前无dispatch）
	 * @param list
	 * @return
	 */
	private List<MyMethod> reduceSetText(List<MyMethod> list){
		List<MyMethod> res = new ArrayList<>();
		List<MyMethod> temp = new ArrayList<>();
		boolean start = true,partStart = false;
		
		MyMethod method = null;
		for(int i=0;i<list.size();i++) {
			method = list.get(i);
			if(method.methodName.equals("setText")) {
				if(start) {
					temp.clear();
					start = false;
				}else if(partStart){
					res.addAll(temp);
					partStart = false;
					temp.clear();
				}else {
					temp.clear();
				}
				temp.add(method);
			}else if(method.methodName.equals("dispatchTouchEvent")){
				temp.add(method);
				res.addAll(temp);
				partStart = true;
				temp.clear();
			}else {
				temp.add(method);
			}
		}
		//add last temp
		if(!temp.isEmpty()) {
			res.addAll(temp);
		}
		return res;
	}
	/**
	 * 
	 * @param list
	 * @param stepSize 两个dispatchTouchEvent之间的距离小于stepSize 算作一个dispatchTouchEvent
	 * 并且以最后一个dispatchTouchEvent为准
	 * @return
	 */
	private List<MyMethod> reduceDispatch(List<MyMethod> list,int stepSize){
		MyMethod method = null;
		int curSize = Integer.MAX_VALUE;
		ArrayList<MyMethod> res = new ArrayList<>();
		ArrayList<MyMethod> temp = new ArrayList<>();
		for(int i=0;i<list.size();i++) {
			method = list.get(i);
			if(method.methodName.equals("dispatchTouchEvent")) {
				
				if(curSize>stepSize||curSize<0) {
					res.addAll(temp);
					temp.clear();
					temp.add(method);
				}else {
					temp.clear();
					temp.add(method);
				}
				curSize = 0;
			}else {
				curSize++;
				temp.add(method);
			}
		}
		if(!temp.isEmpty()) {
			res.addAll(temp);
		}
		return res;
	}
	private List<MyMethod> mergeDispatch(List<MyMethod> list) {
		MyMethod method = null;
		int curSize = 0;
		ArrayList<MyMethod> res = new ArrayList<>();
		ArrayList<MyMethod> temp = new ArrayList<>();
		for(int i=0;i<list.size();i++) {
			method = list.get(i);
			if(method.methodName.equals("dispatchTouchEvent")) {
				curSize++;
				if(curSize==2) {
					temp.add(method);
					curSize = 0;
				}
			}else {
				temp.add(method);
			}
		}
		if(!temp.isEmpty()) {
			res.addAll(temp);
		}
		return res;
	}
	/**
	 * 留下有数据联系的方法
	 * @param myMethods
	 * @param userInput
	 */
	public List<MyMethod> processDataLink(List<MyMethod> myMethods,List<String> userInput) {
		List<MyMethod> res = new ArrayList<>();
		List<Integer> dataPool = new ArrayList<>();
		buildDataPool(myMethods,dataPool,userInput);
		for(MyMethod myMethod:myMethods) {
			if(isDataLinkFunAndAdd(myMethod,dataPool)) {
				res.add(myMethod);
			}else if(myMethod.methodName.equals("dispatchTouchEvent")) {
				res.add(myMethod);
			}else{
//				System.out.println("discard: "+myMethod.methodCaller+"/"+myMethod.methodName);
			}
		}
		return res;
	}
	private boolean isOriginal(MyMethod myMethod,List<String> userInput) {
		JSONArray jsonArray = myMethod.getInputJSON();
		JSONObject json = null;
		for(int i=0;i<jsonArray.size();i++) {
			json = jsonArray.getJSONObject(i);
			if(json.get("parameterValue")==null) {
				continue;
			}
			for(int j=0;j<userInput.size();j++) {
				if(userInput.get(j).contains(json.get("parameterValue").toString())) {
					return true;
				}
			}
		}
		for(MyMethod child:myMethod.childs) {
			if(isOriginal(child,userInput)) {
				return true;
			}
		}
		return false;
	}
	private boolean isDataLinkFunAndAdd(MyMethod myMethod,List<Integer> dataPool) {
		JSONArray parameters = myMethod.getInputJSON();
		JSONObject result = myMethod.getOutputJSON();
		JSONObject methodJson = myMethod.selfJson;
		boolean res = false;
		if(methodJson.get("callerHashCode")!=null&&dataPool.contains(methodJson.getIntValue("callerHashCode"))) {
//			updateDataPoolByInput(dataPool,myMethod);
			
//			updateDataPoolByOutput(dataPool,myMethod);
//			System.out.println(methodJson.getString("callerClassName"));
			res = true;
		}
		if(result.get("resultHashCode")!=null&&result.getString("resultClassName")!=null) {
			if(dataPool.contains(result.getIntValue("resultHashCode"))) {
				if(!result.getString("resultClassName").contains("java.lang.Boolean")) {
//					updateDataPoolByInput(dataPool,myMethod);
					
//					updateDataPoolByCaller(dataPool,myMethod);
//					System.out.println(myMethod.methodCaller+" "+result.getString("resultClassName"));
					res = true;
				}

			}
		}
		JSONObject item = null;
		for(int i=0;i<parameters.size();i++) {
			item = parameters.getJSONObject(i);
			if(item.get("parameterHashCode")==null) {
				continue;
			}else if(dataPool.contains(item.getIntValue("parameterHashCode"))){
//				if(item.getString("parameterClassName").equals("java.lang.Integer")) {
//					continue;
//				}
//				System.out.println(item.get("parameterClassName"));
//				if(result.get("resultHashCode")!=null&&result.getString("resultClassName")!=null) {
//					if(result.getString("resultClassName").equals("com.douban.amonsul.core.MobileStatManager")) {
//						System.out.println(myMethod.methodCaller+"/"+myMethod.methodName+" "+item.get("parameterClassName"));
//					}
//				}
				
//				updateDataPoolByOutput(dataPool,myMethod);
				
//				updateDataPoolByCaller(dataPool,myMethod);
				res = true;
				break;
			}
		}
		for(MyMethod child:myMethod.childs) {
			if(isDataLinkFunAndAdd(child,dataPool)) {
				res = true;
			}
		}
		return res;
	}
	private void updateDataPoolByInput(List<Integer> dataPool,MyMethod myMethod) {
		JSONArray jsonArray = myMethod.getInputJSON();
		JSONObject item ;
		for(int i=0;i<jsonArray.size();i++) {
			item = jsonArray.getJSONObject(i);
			if(item.get("parameterHashCode")!=null) {
				dataPool.add(item.getIntValue("parameterHashCode"));
//				if(item.getString("parameterClassName").equals("com.douban.amonsul.core.MobileStatManager")) {
//					System.out.println(myMethod.methodCaller+"/"+myMethod.methodName);
//				}
			}
		}
	}
	private void updateDataPoolByOutput(List<Integer> dataPool,MyMethod myMethod) {
		JSONObject res = myMethod.getOutputJSON();
		if(res.get("resultHashCode")!=null&&res.getString("resultClassName")!=null) {
			dataPool.add(res.getIntValue("resultHashCode"));
//			if(res.getString("resultClassName").equals("com.douban.amonsul.core.MobileStatManager")) {
//				System.out.println(myMethod.methodCaller+"/"+myMethod.methodName);
//			}
		}
	}
	private void updateDataPoolByCaller(List<Integer> dataPool,MyMethod myMethod) {
		if(myMethod.selfJson.get("callerHashCode")!=null) {
			dataPool.add( myMethod.selfJson.getIntValue("callerHashCode") );
//			if(myMethod.selfJson.getString("callerClassName").equals("com.douban.amonsul.core.MobileStatManager")) {
//				System.out.println(myMethod.methodCaller+"/"+myMethod.methodName);
//			}
		}
	}
	/**
	 * **添加与view 有关的数据---方法的调用者，输入，输出
	 * **添加与用户输入有关的数据
	 * @param myMethods
	 * @param dataPool
	 */
	private void buildDataPool(List<MyMethod> myMethods,List<Integer> dataPool,List<String> userInput) {
		for(MyMethod myMethod:myMethods) {
			if(myMethod.selfJson.getBooleanValue("ViewFlag")) {
				adAllInMethodToDataPool(myMethod,dataPool);
			}else if(isOriginal(myMethod,userInput)) {
				adAllInMethodToDataPool(myMethod,dataPool);
			}
		}
		
	}
	/**
	 *  ** 将方法中的所有输入数据添加到datapool
	 * @param myMethod
	 * @param dataPool
	 */
	private void adAllInMethodToDataPool(MyMethod myMethod,List<Integer> dataPool) {
		JSONArray jsonArray = myMethod.getInputJSON();
		JSONObject item ;
		for(int i=0;i<jsonArray.size();i++) {
			item = jsonArray.getJSONObject(i);
			if(item.get("parameterHashCode")!=null) {
				dataPool.add(item.getIntValue("parameterHashCode"));
//				System.out.println(item.getString("parameterClassName"));
//				System.out.println(myMethod.methodCaller+"/"+myMethod.methodName+" "+item.getString("parameterClassName"));
//				if(item.getString("parameterClassName").equals("com.douban.amonsul.core.MobileStatManager")) {
//					System.out.println(myMethod.methodCaller+"/"+myMethod.methodName);
//				}
			}
		}
		if(myMethod.selfJson.get("callerHashCode")!=null) {
			dataPool.add(myMethod.selfJson.getIntValue("callerHashCode"));
		}
		JSONObject result = myMethod.getOutputJSON();
		if(result.get("resultHashCode")!=null&&result.getString("resultClassName")!=null) {
			dataPool.add(result.getIntValue("resultHashCode"));
		}
		for(MyMethod child:myMethod.childs) {
//			adAllInMethodToDataPool(child,dataPool);
			isDataLinkFunAndAdd(child,dataPool);
		}
	}
}
