package eventSimilarity;

import analyseMethodCall.MyMethod;

import java.util.ArrayList;
import java.util.List;

public class Event {
    public static final String DISPATCH = "dispatchTouchEvent",SETTEXT = "setText";
    private String activityId,componentId,path;
    private String methodName,packageName;
    private List<MyParameter> parameters;
    private List<MyMethod> invokeList;
    public Event(String activityId,String componentId,String path,String methodName,String packageName){
        this.activityId = activityId;
        this.componentId = componentId;
        this.path = path;
        this.methodName = methodName;
        this.packageName = packageName;
        invokeList = new ArrayList<>();
    }

    public void setParameters(List<MyParameter> parameters) {
        this.parameters = parameters;
    }

    public void setInvokeList(List<MyMethod> invokeList) {
        this.invokeList = invokeList;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
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
