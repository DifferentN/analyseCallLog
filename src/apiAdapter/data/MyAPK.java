package apiAdapter.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class MyAPK implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<MyClass> myClasses;
    private List<MyMethod> myMethods;
    private HashMap<String,List<String>> callerMap,calleeMap;

    public void setMyClasses(List<MyClass> myClasses) {
        this.myClasses = myClasses;
    }

    public void setMyMethods(List<MyMethod> myMethods) {
        this.myMethods = myMethods;
    }

    public void setCallerMap(HashMap<String, List<String>> callerMap) {
        this.callerMap = callerMap;
    }

    public void setCalleeMap(HashMap<String, List<String>> calleeMap) {
        this.calleeMap = calleeMap;
    }

    public List<MyClass> getMyClasses() {
        return myClasses;
    }

    public List<MyMethod> getMyMethods() {
        return myMethods;
    }

    public HashMap<String, List<String>> getCallerMap() {
        return callerMap;
    }

    public HashMap<String, List<String>> getCalleeMap() {
        return calleeMap;
    }
}
