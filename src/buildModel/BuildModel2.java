package buildModel;

import analyseMethodCall.MyMethod;
import eventSimilarity.Event;
import eventSimilarity.GenerateEventUtil;
import eventSimilarity.ProcessEventUtil;

import java.util.ArrayList;
import java.util.List;

public class BuildModel2 {
    private List<List<MyMethod>> callLogSet;
    private List<Event> eventDataSet;
    private List<Event[]> singleEvents;
    private PreProcess preProcess ;
    public BuildModel2(){
        callLogSet = new ArrayList<>();
        preProcess = new PreProcess();
        eventDataSet = new ArrayList<>();
    }
    public void addCallLogData(List<MyMethod> methodData,List<String> userInput){
        List<MyMethod> newMethodData = preProcess.processDataLink(methodData,userInput);
        List<Event> events = GenerateEventUtil.generateEvents(newMethodData);
        if(singleEvents==null){
            singleEvents = GenerateEventUtil.extractEvent(newMethodData);
        }
        eventDataSet.addAll(events);
    }
    public List<Event> obtainModel(){
        if(eventDataSet.isEmpty()){
            return null;
        }
        List<Event> events = ProcessEventUtil.processRawEvents(eventDataSet);
        List<Event> modelEvents = new ArrayList<>();
        for(Event itemEvents[]:singleEvents){
            Event temp = getCorrespondingEvent(itemEvents[1],events);
            if(temp==null){
                System.out.println("不能找到与用户Event对应的event(候选模板中的)");
                return null;
            }
            //更换event(候选模板)中的componentId和path
            temp.setComponentId(itemEvents[0].getComponentId());
            temp.setPath(itemEvents[0].getPath());
            modelEvents.add(temp);
        }
        return modelEvents;
    }

    /**
     * 返回events 中与event有相同view且相同方法的event
     * @param event 目标event 包含view和methodName 筛选条件
     * @param events
     * @return
     */
    private Event getCorrespondingEvent(Event event,List<Event> events){
        int sameNum = 0;
        Event res = null;
        for(Event item:events){
            if(ProcessEventUtil.checkEvent(event,item)){
                res = item;
                sameNum++;
            }
        }
        if(sameNum>1){
            System.out.println("找到与用户操作的Event对应的Event时，发现重复");
        }
        return res;
    }
}
