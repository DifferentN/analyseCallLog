package apiAdapter.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyClass implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<MyField> myFields;
    private List<MyMethod> myMethods;

    public MyClass() {
        myFields = new ArrayList<>();
        myMethods = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<MyField> getFields() {
        return myFields;
    }

    public List<MyMethod> getMethods() {
        return myMethods;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFields(List<MyField> myFields) {
        this.myFields = myFields;
    }

    public void setMethods(List<MyMethod> myMethods) {
        this.myMethods = myMethods;
    }
    public int getFieldCount(){
        if(myFields==null){
            return 0;
        }
        return myFields.size();
    }
    public int getMethodCount(){
        if(myMethods==null){
            return 0;
        }
        return myMethods.size();
    }
}
