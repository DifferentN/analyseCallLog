package reformCall;

import FileUtil.MyFileUtil;
import analyseMethodCall.MyMethod;
import apiAdapter.data.MyMethodPair;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

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

    /**
     * 将方法映射对写到路径为methodPairPath的文件中
     * @param methodPairPath
     * @param list
     */
    public static void writeMyMethodPairs(String methodPairPath,List<MyMethodPair> list){
        JSONObject jsonObject = new JSONObject();
        for(MyMethodPair myMethodPair:list){
            String key = extractMethodInfo(myMethodPair.getMyMethod1())+"<->"+extractMethodInfo(myMethodPair.getMyMethod2());
            jsonObject.put(key,myMethodPair.getSim());
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        MyFileUtil.writeJSONArray(methodPairPath,jsonArray);
    }
    private static String extractMethodInfo(apiAdapter.data.MyMethod myMethod){
        String signature = myMethod.getSignature();
        int pos = signature.indexOf(":");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(signature.substring(1,pos+1))
                .append(signature.substring(pos+2,signature.length()-1));
        return stringBuffer.toString();
    }

}
