package reformCall;

import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TransformMethodUtil {
    public static String getMethodInfo(MyMethod myMethod){
        StringBuffer buf = new StringBuffer();
        buf.append(myMethod.methodCaller+":");
        buf.append(getResult(myMethod));
        buf.append(" ");
        buf.append(myMethod.methodName);
        buf.append(getParameter(myMethod));
        return buf.toString();
    }
    private static String getResult(MyMethod myMethod) {
        JSONObject result = myMethod.getOutputJSON();
        String resultName = null;
        if(result.getString("resultClassName")==null) {
            resultName = "void";
        }else {
            resultName = result.getString("resultClassName");
        }
        return resultName;
    }
    private static String getParameter(MyMethod myMethod) {
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
