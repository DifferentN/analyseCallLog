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
//        System.out.println("before processDataLink: "+methodData.size());
        List<MyMethod> newMethodData = preProcess.processDataLink(methodData,userInput);
//        System.out.println("after processDataLink: "+methodData.size());
        newMethodData = methodData;
        List<Event> events = GenerateEventUtil.generateEvents(newMethodData);
        for(Event event:events){
            System.out.println(event.getInvokeList().size());
        }
        //eliminate duplicate SetTExt
        events = eliminateDuplicateSetText(events);

        if(singleEvents==null){
            singleEvents = GenerateEventUtil.extractEvent(newMethodData);
            singleEvents = eliminateDuplicateSetTextForSingleEvent(singleEvents);
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

    /**
     * eventList中可能包含多个连续的且在同一个EditView上的setText事件，我们需要对这些连续的setText操作进行去重，
     * 即只保留最后一个setText操作
     * *@param eventList
     * @return
     */
    private List<Event> eliminateDuplicateSetText(List<Event> eventList){
        List<Event> resList = new ArrayList<>();
        for(Event event:eventList){
            if(resList.isEmpty()){
                resList.add(event);
                continue;
            }
            Event preEvent = resList.get(resList.size()-1);
            if( preEvent.getMethodName().equals(event.getMethodName()) && preEvent.getMethodName().equals(Event.SETTEXT)
                    && preEvent.getPath().equals(event.getPath())){
                resList.remove(resList.size()-1);
                resList.add(event);
            }else{
                resList.add(event);
            }
        }
        return resList;
    }

    private List<Event[]> eliminateDuplicateSetTextForSingleEvent(List<Event[]> list){
        List<Event[]> res = new ArrayList<>();
        Event[] pre = null;
        for(Event[] events:list){
            if(pre==null){
                res.add(events);
            }else if( pre[0].getMethodName().equals(events[0].getMethodName()) && pre[0].getPath().equals(events[0].getPath()) &&
                pre[0].getMethodName().equals(Event.SETTEXT) ){
                res.remove(res.size()-1);
                res.add(events);
            }else{
                res.add(events);
            }
            pre = events;
        }
        return res;
    }
}
