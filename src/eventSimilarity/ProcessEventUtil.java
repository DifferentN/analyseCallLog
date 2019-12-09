package eventSimilarity;

import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import graph.GenerateGNode;
import graph.LongCommonSequence;

import java.util.*;

public class ProcessEventUtil {
    /**
     * 1、判断event是否是相同的的，根据组件和用户操作来判断
     * 2、对event进行分类，相同的归为一组
     * 3、对同组event的invokeTree取交集
     * 4、判断新旧版本event的相似度，判断相似度的方法使用Id和invokeTree（在EventSimilarityReckon）
     */
    /**
     * 判断event是否是相同的的，根据组件和用户操作来判断
     * @param event1
     * @param event2
     * @return true 表示两个event相同
     */
    public static boolean checkEvent(Event event1,Event event2){
        //去掉Id判断，setText方法的Id可能会不同 （目前）
        if(event1.getPath().equals(event2.getPath())&&event1.getActivityId().equals(event2.getActivityId())){
            if(event1.getMethodName().equals(event2.getMethodName())){
                return true;
            }
        }
//        if(event1.getComponentId().equals(event2.getComponentId())&&event1.getPath().equals(event2.getPath())){
//            if(event1.getMethodName().equals(event2.getMethodName())){
//                return true;
//            }
//        }
        return false;
    }

    /**
     * 对event进行分类，相同的归为一组
     * @param events
     * @return
     */
    private static HashMap<Integer, List<Event>> classifyEvent(List<Event> events){
        int num = 0,key;
        boolean hasAdd =false;
        List<Event> list;
        Set keySet = null;
        Iterator<Integer> iterator = null;
        HashMap<Integer,List<Event>> hash = new HashMap<>();
        for(Event event:events){
            hasAdd = false;
            keySet = hash.keySet();
            iterator = keySet.iterator();
            while (iterator.hasNext()){
                key = iterator.next();
                if(checkEvent(event,hash.get(key).get(0))){
                    hasAdd = true;
                    hash.get(key).add(event);
                    break;
                }
            }
            if(!hasAdd){
                list = new ArrayList<>();
                list.add(event);
                hash.put(num++,list);
            }
        }
        return hash;
    }

    /**
     * 获取InvokeTree序列的最大公共子序列
     * @param src
     * @return
     */
    private static List<MyMethod> getMostCommonInvoke(List<List<MyMethod>> src){
        LongCommonSequence longCommonSequence = new LongCommonSequence();
        return longCommonSequence.getLongCommonSeq(src);
    }

    /**
     * 获取同类型events的交集
     * @return
     */
    private static Event getCommonEvent(List<Event> events){
//        System.out.println("--------");
        if(events==null||events.size()<1) return null;
        if(events.size()==1) return events.get(0);
        Event temp = events.get(0);
        Event sample = new Event(temp.getActivityId(),temp.getComponentId(),temp.getPath(),temp.getMethodName());

        sample.setParameters(temp.getParameters());
        List<List<MyMethod>> methodsList = new ArrayList<>();
        for(Event event:events){
//            System.out.println(event.getInvokeList().size());
//            System.out.println(event.getComponentId()+" "+event.getMethodName());
            methodsList.add( event.getInvokeList() );
        }
//        System.out.println("----");
        List<MyMethod> invokeTrees = getMostCommonInvoke(methodsList);
        sample.setInvokeList(invokeTrees);
//        System.out.println("--------");
        return sample;
    }

    /**
     * 处理event，获取模板event
     * 对相同的event取他们的invokeTree的公共集，然后返回互不相同的Event链表
     * @param raw
     * @return
     */
    public static List<Event> processRawEvents(List<Event> raw){
        HashMap<Integer,List<Event>> hash = classifyEvent(raw);
        List<Event> res = new ArrayList<>();
        Set<Map.Entry<Integer,List<Event>>> entrySet = hash.entrySet();
        for(Map.Entry<Integer,List<Event>> entry:entrySet){
            res.add( getCommonEvent(entry.getValue()) );
        }
        return res;
    }

    /**
     * 将APIEvent转化为jsonArray 进行保存
     * @param apiEvent
     * @return
     */
    public static JSONArray transformAPIEventsToJSONArray(List<Event> apiEvent){
        JSONArray eventArray = new JSONArray();
        for(int i=0;i<apiEvent.size();i++){
            eventArray.add(transformEventToJSONObject(apiEvent.get(i),i));
        }
        return eventArray;
    }
    private static JSONObject transformEventToJSONObject(Event event,int seqId){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seqId",seqId);
        jsonObject.put("ActivityID",event.getActivityId());
        jsonObject.put("viewId",event.getComponentId());
        jsonObject.put("viewPath",event.getPath());
        jsonObject.put("methodName",event.getMethodName());
        jsonObject.put("parameterValue","");
        GenerateGNode generateGNode = new GenerateGNode();
        List<String> invokes = generateGNode.getNodeSeq(event.getInvokeList());
//        List<String> invokes = new ArrayList<>();
//        for(MyMethod myMethod:event.getInvokeList()){
//            invokes.add("("+myMethod.methodCaller+"/"+myMethod.methodName+")");
//        }
        JSONObject invokeObject = new JSONObject();
        int num=0;
        for(String invokeItem:invokes){
            invokeObject.put(num+"",invokeItem);
            num++;
        }
        invokeObject.put("invokeSize",num);
        jsonObject.put("invoke",invokeObject);
        return jsonObject;
    }
}
