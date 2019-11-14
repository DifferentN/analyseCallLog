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
//            if(i==0){
//                continue;
//            }
            newEvent = findCorrespondingEvent(apiEvents.get(i),list);
            if(i==2||i==4){
                newEvent.setInvokeList(new ArrayList<>());
            }

            apiEvents.remove(i);
            apiEvents.add(i,newEvent);
        }
        return apiEvents;
    }
    private Event findCorrespondingEvent(Event event,List<EventMap> list){
        for(EventMap eventMap:list){
            if(ProcessEventUtil.checkEvent(event,eventMap.oldEvent)){
                if(eventMap.newEvents==null||eventMap.newEvents.isEmpty())
                    continue;
                return eventMap.newEvents.get(0);
            }
        }
        //未在新版中找到对应event，使用原来的event
        return event;
    }
}
