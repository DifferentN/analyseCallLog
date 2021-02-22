package eventSimilarity;

import java.util.ArrayList;
import java.util.List;

public class ReplaceModuleAPI {
    /**
     * 用新的event替换apiEvent中的Event
     * @param apiEvents
     * @param list
     * @return
     */
    public List<Event> replaceEvent(List<Event> apiEvents,List<EventMap> list){
        Event newEvent = null;
        for(int i=0;i<apiEvents.size();i++){
//            System.out.println(apiEvents.get(i).getMethodName());
            newEvent = findCorrespondingEvent(apiEvents.get(i),list);
//            if(newEvent!=apiEvents.get(i)){
//                System.out.println("find event");
//            }
            apiEvents.remove(i);
            apiEvents.add(i,newEvent);
            if(newEvent.getInvokeList()==null){
                System.out.println("invokeList is null");
            }
        }
        return apiEvents;
    }
    private Event findCorrespondingEvent(Event event,List<EventMap> list){
//        System.out.println(".........");
        for(EventMap eventMap:list){
//            System.out.println(event.getActivityId());
//            System.out.println(eventMap.oldEvent.getActivityId());
//            System.out.println("-----");
            if(ProcessEventUtil.checkEvent(event,eventMap.oldEvent)){
                if(eventMap.newEvents==null||eventMap.newEvents.isEmpty()){
                    System.out.println("newEvent is null");
                    continue;
                }
//                List<Event> newEvents = eventMap.newEvents;
//                for(Event newEvent:newEvents){
//                    System.out.println(newEvent.getPath());
//                    if(ProcessEventUtil.checkEvent(event,newEvent)){
//                        return newEvent;
//                    }
//                }
//                System.out.println("not appropriate Event");
//                System.out.println("find correspondingEvent and replace Event");
                return eventMap.newEvents.get(0);
            }
        }
        System.out.println("not find correspondingEvent not replace Event");
        //未在新版中找到对应event，使用原来的event
        return event;
    }
}
