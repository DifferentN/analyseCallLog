package analyseMethodCall;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyInvoke {
    private HashMap<Long, String> parameters, results;
    private MyMethod myMethod;
    private MyInvoke parent;
    private List<MyInvoke> childs;
    public MyInvoke(MyMethod myMethod) {
        this.myMethod = myMethod;
        childs = new ArrayList<>();
        parameters = new HashMap<>();
        results = new HashMap<>();
    }

    public void addParameters(JSONArray jsonArray) {
        JSONObject param = null;
        String name = "";
        long hashCode = 0;
        for(int i=0;i<jsonArray.size();i++){
            param = jsonArray.getJSONObject(i);
            if(param.getString("parameterClassName")==null||param.getInteger("parameterHashCode")==null){
                continue;
            }
            name = param.getString("parameterClassName");
            hashCode = param.getInteger("parameterHashCode");
            parameters.put(hashCode,name);
        }
    }

    public void addResult(JSONObject jsonObject) {
        if(jsonObject.getString("resultHashName")==null|jsonObject.getInteger("resultHashCode")==null){
            return;
        }
        String name = jsonObject.getString("resultHashName");
        long hashCode = jsonObject.getInteger("resultHashCode");
        results.put(hashCode,name);
    }

    public void addChildMyInvoke(MyInvoke child){
        parameters.putAll(child.parameters);
        results.putAll(child.results);
        childs.add(child);
    }

    public HashMap<Long, String> getParameters() {
        return parameters;
    }

    public HashMap<Long, String> getResults() {
        return results;
    }

    public MyMethod getMyMethod() {
        return myMethod;
    }

    public String getMethodName(){
        return myMethod.methodName;
    }

    public void setParent(MyInvoke parent) {
        this.parent = parent;
    }
}
