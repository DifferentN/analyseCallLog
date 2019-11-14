package eventSimilarity;

import FileUtil.MyFileWriter;
import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONArray;
import graph.GenerateGNode;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StartSimilarity {
    public static void main(String[] args){
        List<MyMethod> invokes1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog_original_all.txt");
        List<MyMethod> invokes2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog_change_all.txt");
//        for(MyMethod myMethod:invokes1){
//            System.out.println(myMethod.methodName+" "+myMethod.childs.size());
//        }
        //方法相似度
        HashMap<String,Float> hash = ReadMethodSimilarityUtil.readMethodSimilarityToHash("C:/Users/17916/Desktop/anki/method-3.txt");
        //转化为event序列
        List<Event> events1 = GenerateEventUtil.generateEvents(invokes1);
        List<Event> events2 = GenerateEventUtil.generateEvents(invokes2);

        List<EventMap> eventMaps = getEventMap(events1,events2,hash);
        GenerateGNode generateGNode = new GenerateGNode();
//        for(EventMap eventMap:eventMaps){
//            String info = "oldEvent: "+eventMap.oldEvent.getComponentId()+"-"+eventMap.oldEvent.getMethodName();
//            info += "; maxSim: "+eventMap.maxSim;
//            info += "; newEvent: ";
//            for(Event event:eventMap.newEvents){
//                info+=event.getComponentId()+"-"+event.getMethodName()+"  ";
//            }
////            info+=generateGNode.getNodeSeq(eventMap.newEvents.get(0).getInvokeList());
//            System.out.println(info);
//        }

        List<MyMethod> oldInvokes = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/anki/methodLog.txt");
        List<Event> oldApiEvent = GenerateEventUtil.generateEvents(oldInvokes);

        ReplaceModuleAPI replaceModuleAPI = new ReplaceModuleAPI();
        List<Event> apiEvents = replaceModuleAPI.replaceEvent(oldApiEvent,eventMaps);
        for(Event event:apiEvents){
            System.out.println(event.getComponentId()+" "+generateGNode.getNodeSeq(event.getInvokeList()));
        }
        JSONArray jsonArray = ProcessEventUtil.transformAPIEventsToJSONArray(apiEvents);
        MyFileWriter.writeEventJSONArray("execute.txt",jsonArray);
    }

    /**
     * 旧版本event对应的新版本event的列表
     * @param rawEvents1 旧版本event
     * @param rawEvents2 新版本event
     * @param hash
     * @return
     */
    private static List<EventMap> getEventMap(List<Event> rawEvents1,List<Event> rawEvents2,HashMap<String,Float> hash){
        List<Event> modelEvents1 = ProcessEventUtil.processRawEvents(rawEvents1);
        List<Event> modelEvents2 = ProcessEventUtil.processRawEvents(rawEvents2);
        int len1 = modelEvents1.size(),len2 = modelEvents2.size();
        float[][] grid = new float[len1][len2];
        EventSimilarityReckon similarityReckon = new EventSimilarityReckon(hash);
        for(int i=0;i<len1;i++){
            for(int j=0;j<len2;j++){
                grid[i][j] = similarityReckon.reckonEventSimilarity(modelEvents1.get(i),modelEvents2.get(j));
            }
        }

        float max=0;
        List<EventMap> res = new ArrayList<>();
        for(int i=0;i<len1;i++){
            max = 0;
            for(int j=0;j<len2;j++){
                if(grid[i][j]>max){
                    max = grid[i][j];
                }
            }
            EventMap eventMap = new EventMap(modelEvents1.get(i));
            eventMap.maxSim = max;
            for(int j=0;j<len2;j++){
                if(grid[i][j] == max){
                    eventMap.addMap(modelEvents2.get(j));
                }
            }
            res.add(eventMap);
        }
        return res;
    }
}
