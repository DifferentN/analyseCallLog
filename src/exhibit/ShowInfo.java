package exhibit;

import FileUtil.MyFileWriter;
import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import eventSimilarity.Event;
import eventSimilarity.GenerateEventUtil;
import jsonParse.ParseUtil;

import java.util.List;

public class ShowInfo {
    private final static String EVENT_NAME = "EVENT_NAME";
    private final static String ACTIVITY_NAME = "ACTIVITY_NAME";
    private final static String INVOKE_LIST = "INVOKE_LIST";
    private final static String METHOD_NAME = "METHOD_NAME";
    private final static String CALLER_NAME = "CALLER_NAME";
    private final static String CHILD_METHOD = "CHILD_METHOD";
    public static void main(String[] args){
        String filePath = "C:/Users/17916/Desktop/3APP/qqmusic/searchMusic/methodLog-1.txt";
        String savePath = "record1.txt";
        saveShowEvent(filePath,savePath);
        filePath = "C:/Users/17916/Desktop/3APP/qqmusic/searchMusic/methodLog-2.txt";
        savePath = "record2.txt";
        saveShowEvent(filePath,savePath);
    }

    /**
     * 将监听日志转化为Event列表，并保存起来
     * @param filePath 监听日志的路径
     * @param savePath 保存文件的路径
     */
    public static void saveShowEvent(String filePath,String savePath){
        System.out.println(filePath);
        System.out.println(savePath);
        List<MyMethod> myMethods = MethodSequenceUtil.getSequence(filePath);
        List<Event> events = GenerateEventUtil.generateEvents(myMethods);
        JSONArray eventArray = new JSONArray();
        JSONObject eventObject = null;
        for(Event event:events){
            eventObject = new JSONObject();
            eventObject.put(EVENT_NAME,event.getMethodName());
            eventObject.put(ACTIVITY_NAME,event.getActivityId());
            JSONArray invokeList = obtainInvokeList(event.getInvokeList());
            eventObject.put(INVOKE_LIST,invokeList);
            eventArray.add(eventObject);
        }
        MyFileWriter.writeEventJSONArray(savePath,eventArray);
    }
    private static JSONArray obtainInvokeList(List<MyMethod> myMethodList){
        JSONArray invokeArray = new JSONArray();
        if(myMethodList==null){
            return invokeArray;
        }
        JSONObject jsonObject = null;
        for(MyMethod myMethod:myMethodList){
            jsonObject = new JSONObject();
            jsonObject.put(METHOD_NAME,myMethod.methodName);
            jsonObject.put(CALLER_NAME,myMethod.methodCaller);
            jsonObject.put(CHILD_METHOD,obtainInvokeList(myMethod.childs));
            invokeArray.add(jsonObject);
        }
        return invokeArray;
    }

}
