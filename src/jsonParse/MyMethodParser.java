package jsonParse;

import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class MyMethodParser implements Parser<MyMethod> {
    public static final String METHOD_NAME = "methodName";
    public static final String CALLER = "caller";
    public static final String CHILD_METHOD = "childMethod";
    @Override
    public JSONObject parseToJSON(MyMethod myMethod) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(METHOD_NAME,myMethod.methodName);
        jsonObject.put(CALLER,myMethod.methodCaller);
        List<MyMethod> childMethods = myMethod.childs;
        JSONArray arrays = new JSONArray();
        for(MyMethod childMethod:childMethods){
            JSONObject childJson = parseToJSON(childMethod);
            arrays.add(childJson);
        }
        jsonObject.put(CHILD_METHOD,arrays);
        return jsonObject;
    }
}
