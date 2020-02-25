package eventSimilarity;

import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

import static eventSimilarity.Event.DISPATCH;
import static eventSimilarity.Event.SETTEXT;

public class GenerateEventUtil {
    public static List<Event> generateEvents(List<MyMethod> callSequence){
        int start = 0;
        int range[] = null;
        List<Event> list = new ArrayList<>();
        Event event = null;
        //去除操作开始之前的方法调用信息
        MyMethod curMyMethod = callSequence.get(start);
        while(!curMyMethod.methodName.equals(SETTEXT)&&!curMyMethod.methodName.equals(DISPATCH)&&start<callSequence.size()){
            start++;
            curMyMethod = callSequence.get(start);
        }
        int seqSize = callSequence.size();
        while(start<seqSize){
            range = getEventCallRange(start,callSequence);
//            System.out.println(range[0]+","+range[1]);
            event = getEventFrom(range[0],range[1],callSequence);
            list.add(event);
            start = range[1]+1;
        }
        return list;
    }
    private static Event getEventFrom(int start,int end,List<MyMethod> callSequences){

        Event event = null;
        MyMethod firstMethod = callSequences.get(start);
//        System.out.println(start+" "+end+" "+firstMethod.methodName);
        if(firstMethod.methodName.equals(DISPATCH)){
            event = initDispatchEventByMyMethod(firstMethod);
        }else{
            event = initSetTextEventByMyMethod(firstMethod);
            //获取setText输入的文字
            JSONArray params = firstMethod.selfJson.getJSONArray("methodParameter");
            String text = params.getJSONObject(0).getString("parameterValue");
            MyParameter parameter = new MyParameter("String",text);
            //设置setText的参数
            List<MyParameter> list = new ArrayList<MyParameter>();
            list.add(parameter);
            event.setParameters(list);
        }
        List<MyMethod> invokes = callSequences.subList(start+1,end+1);
        event.setInvokeList(invokes);
        return event;
    }

    /**
     * 得到从pos开始的，当前Event的调用方法的范围
     * @param pos
     * @return
     */
    private static int[] getEventCallRange(int pos,List<MyMethod> callSequences){
        MyMethod curMyMethod = callSequences.get(pos);
        String curMethodName = curMyMethod.methodName;
        int start=pos,end=Integer.MAX_VALUE;
        //过滤Event的前缀方法
        if(curMethodName.equals(SETTEXT)){
            //过滤连续的setText方法
            while(start<callSequences.size()&&callSequences.get(start).methodName.equals(SETTEXT)){
                start++;
            }
            //定位到最后一个setText的位置
            start--;
        }else if(curMethodName.equals(DISPATCH)){
            //过滤掉dispatchTouchEvent的action=2的情况，直到遇到action=1
            //获取处理事件的view
            boolean checking = true;//用来检查down up的点击事件是否完整找到
            boolean findDown = false;
            while (checking&&start<callSequences.size()){
                if(callSequences.get(start).methodName.equals(DISPATCH)&&
                        checkEventAction(callSequences.get(start))==1){
                    checking = false;
                    break;
                }
                //找到了点击事件的down部分
                if(!findDown&&
                        (callSequences.get(start).methodName.equals(DISPATCH)&&checkEventAction(callSequences.get(start))==0)){
                    findDown = true;
                    start++;
                    continue;
                }
                //由于某些原因，没有action=1的dispatchTouchEvent,停止
                //且令checking为true，退回到上一个dispatchTouchEvent
                if(callSequences.get(start).methodName.equals(SETTEXT)||
                        (callSequences.get(start).methodName.equals(DISPATCH)&&checkEventAction(callSequences.get(start))==0)){
                    checking = true;
                    System.out.println(start);
//                    System.out.println("stop because not find action =1 dispatchTouchEvent");
                    break;
                }
                start++;
            }
            //定位到最后一个dispatchTouchEvent的位置
            if(checking){
               start--;
            }
        }else {
            System.out.println("method log error ");
        }
        //找到此动作的方法调用的结束位置，即下一个动作的开始，或整个调用序列的结束
        end = start+1;
        while( end<callSequences.size()&&!callSequences.get(end).methodName.equals(DISPATCH)&&
                !(callSequences.get(end).methodName.equals(SETTEXT)&&callSequences.get(end).methodCaller.contains("MyTextWatcher")) ){
            end++;
        }
        //定位到下一个动作的开始之间
        end--;
        return new int[]{start,end};
    }

    /**
     *
     * @param myMethod 是一个dispatchTouchEvent方法
     * @return 点击时间的类型 0：按下 2：滑动 1：释放 ;返回-1表示出错
     */
    private static int checkEventAction(MyMethod myMethod){
        List<MyMethod> queue = new ArrayList<>();
        queue.add(myMethod);
        MyMethod cur = null;
        JSONArray params;
        int action = -1;
        while(!queue.isEmpty()){
            cur = queue.remove(0);
            if(!cur.methodName.equals(DISPATCH)){
                continue;
            }
            params = cur.selfJson.getJSONArray("methodParameter");
            Object paramValue = params.getJSONObject(0).get("parameterValue");
            if(paramValue instanceof JSONObject){
                action = ((JSONObject)paramValue).getIntValue("action");
                return action;
            }
            for(int i=0;i<cur.childs.size();i++){
                queue.add(cur.childs.get(i));
            }
        }
        return -1;
    }

    /**
     * 对于dispatchTouchEvent方法获取发生在最底层组件的点击MyMethod
     */
    private static MyMethod getViewAboutMyMethod(MyMethod myMethod){
        List<MyMethod> queue = new ArrayList<>();
        queue.add(myMethod);
        MyMethod target = null;
        MyMethod cur = null;
        while(!queue.isEmpty()){
            cur = queue.remove(0);
            if(cur.selfJson.getBooleanValue("ViewFlag")&&cur.methodName.equals(DISPATCH)){
                if(target==null){
                    target = cur;
                }else if(getPath(cur.selfJson).length()>getPath(target.selfJson).length()){
                    target = cur;
                }
//                target = cur;
            }
            for(MyMethod next:cur.childs){
                queue.add(next);
            }
        }
        return target;
    }

    /**
     * 提取callSequence中的用户操作
     * 对于点击事件，数组的第一个表示包含按下时的view的event，第二个表示 包含处理事件view的event
     * 对于输入，数组的第一个。第二个都为同一个event
     * @param callSequence
     * @return
     */
    public static List<Event[]> extractEvent(List<MyMethod> callSequence){
        int start = 0;
        int range[] = null;
        List<Event[]> list = new ArrayList<>();
        Event events[] = null;
        //去除操作开始之前的方法调用信息
        MyMethod curMyMethod = callSequence.get(start);
        while(!curMyMethod.methodName.equals(SETTEXT)&&!curMyMethod.methodName.equals(DISPATCH)&&start<callSequence.size()){
            start++;
            curMyMethod = callSequence.get(start);
        }
        int seqSize = callSequence.size();
        while(start<seqSize){
            range = getEventCallRange(start,callSequence);
            curMyMethod = callSequence.get(start);
            if(curMyMethod.methodName.equals(DISPATCH)){
                Event down = initDispatchEventByMyMethod(curMyMethod);
                Event up = initDispatchEventByMyMethod(callSequence.get(range[0]));
                events = new Event[2];
                events[0] = down;
                events[1] = up;
                list.add(events);
            }else{
                Event textEvent = initSetTextEventByMyMethod(callSequence.get(range[0]));
                events = new Event[2];
                events[0] = textEvent;
                events[1] = textEvent;
                list.add(events);
            }
            start = range[1]+1;
        }
        return list;
    }

    /**
     * 针对自适应部分 生成点击事件
     * dispatch的event不同：用第一个dispatchTouch方法的viewPath 和 viewId替换生成的event中的viewPath和viewId
     * setText的event相同
     * @param callSequence
     * @return
     */
    public static List<Event> generateEventsForSelfAdapter(List<MyMethod> callSequence){
        int start = 0;
        int range[] = null;
        List<Event> list = new ArrayList<>();
        Event event = null;
        //去除操作开始之前的方法调用信息
        MyMethod curMyMethod = callSequence.get(start);
        while(!curMyMethod.methodName.equals(SETTEXT)&&!curMyMethod.methodName.equals(DISPATCH)&&start<callSequence.size()){
            start++;
            curMyMethod = callSequence.get(start);
        }
        int seqSize = callSequence.size();
        while(start<seqSize){
            range = getEventCallRange(start,callSequence);
            //第一个dispatch方法的event
            Event event2 = initDispatchEventByMyMethod(callSequence.get(start));
            //将event的viewPath和viewId设置为第一个dispatch方法的viewPath和viewId
            event = getEventFrom(range[0],range[1],callSequence);
            event.setPath(event2.getPath());
            event.setComponentId(event2.getComponentId());
            list.add(event);
            start = range[1]+1;
        }
        return list;
    }
    private static Event initDispatchEventByMyMethod(MyMethod myMethod){
        myMethod = getViewAboutMyMethod(myMethod);
        JSONObject json = myMethod.selfJson;
        return new Event(getActivityId(json),getComponentId(json),getPath(json),DISPATCH);
    }
    private static Event initSetTextEventByMyMethod(MyMethod myMethod){
//        System.out.println(myMethod.methodName+" "+myMethod.methodCaller);
        JSONObject json = myMethod.selfJson;
        return new Event(getActivityId(json),getComponentId(json),getPath(json),SETTEXT);
    }
    private static String getActivityId(JSONObject jsonObject){
        return jsonObject.getString("ActivityID");
    }
    private static String getComponentId(JSONObject jsonObject){
        if (jsonObject==null){
            System.out.println("jsonObject is null");
        }
        return jsonObject.getJSONObject("viewInfo").getString("viewId");
    }
    private static String getPath(JSONObject jsonObject){
        return jsonObject.getJSONObject("viewInfo").getString("viewPath");
    }

}
