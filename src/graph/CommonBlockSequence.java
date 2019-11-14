package graph;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import analyseMethodCall.MyMethod;

public class CommonBlockSequence {
	/**
	 * **获取调用序列的公共子集
	 * @param list
	 * @return
	 */
	public List<String> reckonCommonBlock(List<List<MyMethod>> list){
		ArrayList<String> res = new ArrayList<>();
		ArrayList<List<List<MyMethod>>> src = new ArrayList<>();
		List<List<MyMethod>> blockSequence = null;
		for(int i=0;i<list.size();i++) {
			blockSequence = fragmentRawList(list.get(i));
			src.add(blockSequence);
		}
		
		int blockNum = checkBlockNum(src);
		
		if(blockNum<0) {
			System.out.println("长度不一");
		}
		for(int i=0;i<blockNum;i++) {
			List<List<MyMethod>> temp = new ArrayList<>();
			if(isUserAction(src,i)) {
				addUserAction(res,src.get(0).get(i));
			}else {
				for(int j=0;j<src.size();j++) {
					temp.add( src.get(j).get(i) );
				}
//				List<List<String>> blockString = transformListString(temp);
//				List<String> blockCommon = reckonCommonMethod(blockString);
				//获取某一块调用序列的最长公共子序列
				LongCommonSequence longCommonSequence = new LongCommonSequence();
				List<MyMethod>  commonMethods = longCommonSequence.getLongCommonSeq(temp);
				GenerateGNode generateGNode = new GenerateGNode();
				List<String> blockCommon = generateGNode.getNodeSeq(commonMethods);
				res.addAll(blockCommon);
			}
		}
		return res;
 	}
	/**
	 * 向模板中添加用户操作
	 * @param res
	 * @param list
	 */
	private void addUserAction(ArrayList<String> res, List<MyMethod> list) {
		MyMethod userAction = list.get(0);
		userAction = getUserAction(userAction);
		JSONObject viewInfo = userAction.selfJson.getJSONObject("viewInfo");
		
		JSONObject actionJson = new JSONObject();
		actionJson.put("componentID", viewInfo.getIntValue("viewId"));
		actionJson.put("path", viewInfo.getString("viewPath"));
		actionJson.put("ActivityID", userAction.selfJson.getString("ActivityID"));
		if(userAction.methodName.contains("setText")) {
			actionJson.put("method", "setText");
			actionJson.put("parameter",getTextParameter(userAction.getInputJSON()));
		}else {
			//dispatchTouchEvent
			actionJson.put("method", "dispatchTouchEvent");
			actionJson.put("parameter",viewInfo.getIntValue("viewId"));
		}
		res.add(actionJson.toJSONString());
		
	}
	private MyMethod getUserAction(MyMethod myMethod) {
		if(myMethod.selfJson.getJSONObject("viewInfo")!=null) {
			return myMethod;
		}
		
		for(MyMethod child:myMethod.childs) {
			if(getUserAction(child)!=null) {
				return child;
			}
		}
		return null;
	}
	private String getTextParameter(JSONArray jarray) {
		JSONObject jobject = jarray.getJSONObject(0);
		String text = jobject.getString("parameterValue");
		return text;
	}
	/**
	 * 判断当前pos的block是不是一个userAction
	 * @param src
	 * @return
	 */
	private boolean isUserAction(ArrayList<List<List<MyMethod>>> src,int pos) {
		if(src.get(0).get(pos).size()==1) {
			MyMethod myMethod = src.get(0).get(pos).get(0);
			if(myMethod.methodName.equals("dispatchTouchEvent")||myMethod.methodName.equals("setText")) {
				return true;
			}else {
				return false;
			}
			
		}else {
			return false;
		}
	}
	/**
	 * **以dispatch/setText(用户操作) 分割调用序列
	 * @param rawList
	 * @return
	 */
	private List<List<MyMethod>> fragmentRawList(List<MyMethod> rawList){
		List<List<MyMethod>> fragments = new ArrayList<>();
		ArrayList<MyMethod> tempList = new ArrayList<>();
		MyMethod tempMethod = null;
		for(int i=0;i<rawList.size();i++) {
			tempMethod = rawList.get(i);
			//setText 加调用者判定，保证是用户的输入
			if(tempMethod.methodName.equals("dispatchTouchEvent")||(tempMethod.methodName.equals("setText")&&tempMethod.methodCaller.contains("MyTextWatcher"))
					) {
				if(tempList.size()>0) {
					fragments.add(tempList);
					tempList = new ArrayList<>();
				}
				List<MyMethod> actions = new ArrayList<>();
				actions.add(tempMethod);
				fragments.add(actions);
			}else {
				tempList.add(tempMethod);
			}
		}
		for(int i=0;i<fragments.size();i++) {
			System.out.println(fragments.get(i).size());
		}
//		System.out.println(fragments.size());
		return fragments;
	}
	private List<List<String>> transformListString(List<List<MyMethod>> src){
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
	private int exploreDispatch(int pos,List<MyMethod> rawList) {
		int end = -1;
		int time = 0;
		MyMethod myMethod;
		for(int i=pos;i<rawList.size();i++) {
			myMethod = rawList.get(i);
			if(myMethod.methodName.contains("dispatchTouchEvent")) {
				end = i;
				time++;
			}else{
				time--;
			}
			if(time<=-1) {
				break;
			}
		}
		
		return end;
	} 
	/**
	 * **检查分割后的调用序列长度是否一致(是否正确分割)
	 * @param src
	 * @return
	 */
	private int checkBlockNum(ArrayList<List<List<MyMethod>>> src) {
		int num = -1;
		for(int i=0;i<src.size();i++) {
			System.out.println("第"+i+"个序列的块长度"+src.get(i).size());
			if(num<0) {
				num = src.get(i).size();
			}else if(num!=src.get(i).size()) {
				num = -1;
				break;
			}
		}
		return num;
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
				
				if((index=str2.indexOf(temp, index))>=0) {
					pre = cur;
				}else if(pre!=null){
					com.addAll(pre);
					i = j;
//					index = index+listToString(pre).length();
					pre = null;
					break;//不全面
				}
//				if(index<0) {
//					break;
//				}
			}
			if(index>=0&&pre!=null) {
				com.addAll(pre);
				break;
			}else {
//				break;
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
