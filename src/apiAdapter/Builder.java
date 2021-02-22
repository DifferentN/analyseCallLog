package apiAdapter;

import apiAdapter.data.MyClass;
import apiAdapter.data.MyField;
import apiAdapter.data.MyMethod;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Builder {
    public static MyField buildField(SootField sootField){
        MyField myField = new MyField();
        myField.setName(sootField.getName());
        return myField;
    }
    public static MyMethod buildMethod(MyClass myClass,SootMethod sootMethod){
        MyMethod myMethod = new MyMethod();
        myMethod.setClassName(sootMethod.getDeclaringClass().getName());
        myMethod.setDeclaringClass(myClass);
        myMethod.setMethodName(sootMethod.getName());
        myMethod.setModifiers(sootMethod.getModifiers());
        myMethod.setSignature(sootMethod.getSignature());
        return myMethod;
    }
    public static MyClass buildClass(SootClass sootClass){
        MyClass myClass = new MyClass();
        myClass.setName(sootClass.getName());

        List<MyField> myFields = new ArrayList<>();
        Chain<SootField> sootFields = sootClass.getFields();
        Iterator<SootField> iteratorField = sootFields.snapshotIterator();
        while(iteratorField.hasNext()){
            SootField sootField = iteratorField.next();
            MyField myField = buildField(sootField);
            myFields.add(myField);
        }
        myClass.setFields(myFields);

        List<MyMethod> myMethods = new ArrayList<>();
        List<SootMethod> sootMethods = sootClass.getMethods();
        for(SootMethod sootMethod:sootMethods){
            MyMethod myMethod = buildMethod(myClass,sootMethod);
            myMethods.add(myMethod);
        }
        myClass.setMethods(myMethods);

        return myClass;
    }
}
