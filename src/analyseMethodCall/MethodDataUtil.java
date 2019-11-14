package analyseMethodCall;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MethodDataUtil {
    public static void findMethodInput(MyMethod myMethod, List<String> inputStr){
        JSONArray parameters = myMethod.getInputJSON();
        JSONObject parameter = null;
        for(int i=0;i<parameters.size();i++){
            parameter = parameters.getJSONObject(i);
            if(parameter.get("parameterValue")==null){
//                System.out.println("parameter is null");
                continue;
            }
            String str = parameter.get("parameterValue").toString();
            if(matchInput(str,inputStr)){
                System.out.println(myMethod.name);
            }
        }
        JSONObject result = myMethod.getOutputJSON();

        for(int i=0;i<myMethod.childs.size();i++){
            findMethodInput(myMethod.childs.get(i),inputStr);
        }
    }
    private static boolean matchInput(String str,List<String> inputStr){
        for(String input:inputStr){
            if(str.equals(input)){
                return true;
            }
        }
        return false;
    }
    public static List<MyInvoke> MyMethodsToInvokes(List<MyMethod> myMethods){
        ArrayList<MyInvoke> myInvokes = new ArrayList<>();
        MyInvoke myInvoke = null;
        for(int i=0;i<myMethods.size();i++){
            myInvoke = getMyInvokeByMeMethod(myMethods.get(i));
            myInvokes.add(myInvoke);
        }
        return myInvokes;
    }
    private static MyInvoke getMyInvokeByMeMethod(MyMethod myMethod){
        MyInvoke myInvoke = new MyInvoke(myMethod);
        myInvoke.addParameters(myMethod.getInputJSON());
        myInvoke.addResult(myMethod.getOutputJSON());

        List<MyMethod> childMethods = myMethod.childs;
        MyInvoke childInvoke = null;
        for(int i=0;i<childMethods.size();i++){
            childInvoke = getMyInvokeByMeMethod(childMethods.get(i));
            childInvoke.setParent(myInvoke);
            myInvoke.addChildMyInvoke(childInvoke);
        }
        //认为输出包含输入
        myInvoke.getResults().putAll(myInvoke.getParameters());
        return myInvoke;
    }
}
