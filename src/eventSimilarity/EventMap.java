package eventSimilarity;

import java.util.ArrayList;
import java.util.List;
/**
 * 表示新旧event之间的映射，理想状态下newEvents的size为1
 */
public class EventMap {
    public Event oldEvent;
    public List<Event> newEvents;
    public float maxSim = 0;
    public EventMap(Event event){
        oldEvent = event;
        newEvents = new ArrayList<>();
    }
    public void addMap(Event event){
        newEvents.add(event);
    }
}
