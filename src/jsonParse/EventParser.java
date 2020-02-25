package jsonParse;

import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import eventSimilarity.Event;

import java.util.List;

public class EventParser implements Parser<Event> {
    public final static String EVENT_NAME = "eventName";
    public final static String ACTIVITY_NAME = "activityName";
    public final static String INVOKE_LIST = "invokeList";
    @Override
    public JSONObject parseToJSON(Event event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT_NAME,event.getMethodName());
        jsonObject.put(ACTIVITY_NAME,event.getActivityId());
        List<MyMethod> myMethods = event.getInvokeList();
        JSONArray jsonArray = ParseUtil.parseMyMethodList(myMethods);
        jsonObject.put(INVOKE_LIST,jsonArray);
        return jsonObject;
    }
}
