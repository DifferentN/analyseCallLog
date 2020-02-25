package jsonParse;

import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import eventSimilarity.Event;

import java.util.List;

public class ParseUtil {
    public static JSONObject parseEvent(Event event){
        Parser<Event> parser = new EventParser();
        JSONObject jsonObject = parser.parseToJSON(event);
        return jsonObject;
    }
    public static JSONObject parseMyMethod(MyMethod myMethod){
        Parser<MyMethod> parser = new MyMethodParser();
        JSONObject jsonObject = parser.parseToJSON(myMethod);
        return jsonObject;
    }
    public static JSONArray parseEventList(List<Event> eventList){
        JSONArray jsonArray = new JSONArray();
        Parser<Event> parser = new EventParser();
        for(Event event:eventList){
            JSONObject item = parser.parseToJSON(event);
            jsonArray.add(item);
        }
        return jsonArray;
    }
    public static JSONArray parseMyMethodList(List<MyMethod> myMethodList){
        JSONArray jsonArray = new JSONArray();
        Parser<MyMethod> parser = new MyMethodParser();
        for(MyMethod myMethod:myMethodList){
            JSONObject item = parser.parseToJSON(myMethod);
            jsonArray.add(item);
        }
        return jsonArray;

    }
}
