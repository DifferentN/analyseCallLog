package eventSimilarity;

import analyseMethodCall.MyMethod;

import java.util.List;

public class Event {
    public static final String DISPATCH = "dispatchTouchEvent",SETTEXT = "setText";
    private String activityId,componentId,path;
    private String methodName;
    private List<MyParameter> parameters;
    private List<MyMethod> invokeList;
    public Event(String activityId,String componentId,String path,String methodName){
        this.activityId = activityId;
        this.componentId = componentId;
        this.path = path;
        this.methodName = methodName;
    }

    public void setParameters(List<MyParameter> parameters) {
        this.parameters = parameters;
    }

    public void setInvokeList(List<MyMethod> invokeList) {
        this.invokeList = invokeList;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getPath() {
        return path;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<MyParameter> getParameters() {
        return parameters;
    }

    public List<MyMethod> getInvokeList() {
        return invokeList;
    }
}
