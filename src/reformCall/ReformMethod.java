package reformCall;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import FileUtil.MyFileWriter;
import analyseMethodCall.MyMethod;

public class ReformMethod {
	private String path = null;
	public MyFileWriter myFileWriter;
	public ReformMethod(String path) {
		myFileWriter = new MyFileWriter(path);
	}
	public void reformMethod(List<MyMethod> list) {
		MyMethod cur = null;
		for(int i=0;i<list.size();i++) {
			cur = list.get(i);
			analyseDirectMethod(cur,cur.childs);
		}
		myFileWriter.close();
	}
	
	private void analyseDirectMethod(MyMethod myMethod,List<MyMethod> childs) {
		if(childs.isEmpty()) {
			return;
		}
		String firstMethod = null,secondMethod = null;
		firstMethod = getMethodInfo(myMethod);
		String line = null;
		MyMethod child = null;
		for(int i=0;i<childs.size();i++) {
			child = childs.get(i);
			secondMethod = getMethodInfo(child);
			line = firstMethod+"->"+secondMethod;
			myFileWriter.write(line);
			analyseDirectMethod(child,child.childs);
		}
	}
	private String getMethodInfo(MyMethod myMethod) {
		return TransformMethodUtil.getMethodInfo(myMethod);
	}
	private String getResult(MyMethod myMethod) {
		JSONObject result = myMethod.getOutputJSON();
		String resultName = null;
		if(result.getString("resultClassName")==null) {
			resultName = "void";
		}else {
			resultName = result.getString("resultClassName");
		}
		return resultName;
	}
	private String getParameter(MyMethod myMethod) {
		JSONObject json = myMethod.selfJson;
		JSONArray parameters = json.getJSONArray("methodParameter");
		JSONObject paramItem = null;
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<parameters.size();i++) {
			paramItem = parameters.getJSONObject(i);
			if(paramItem.getString("parameterClassName")!=null) {
				buf.append(paramItem.getString("parameterClassName"));
			}
			if(i!=parameters.size()-1) {
				buf.append(",");
			}
		}
		String res = "("+buf.toString()+")";
		return res;
	}
}
