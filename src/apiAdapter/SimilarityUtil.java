package apiAdapter;

import apiAdapter.data.MyClass;
import apiAdapter.data.MyField;
import apiAdapter.data.MyMethod;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.util.Chain;

import java.util.*;

public class SimilarityUtil {

    public static double reckonMethodSim(MyMethod method1, MyMethod method2,
                                         HashMap<String,List<String>> callerMap1, HashMap<String,List<String>> calleeMap1,
                                         HashMap<String,List<String>> callerMap2, HashMap<String,List<String>> calleeMap2,
                                         HashMap<String,Double> hashMap,boolean flag){
        String methodSignature1 = method1.getSignature();
        String methodSignature2 = method2.getSignature();
        double q1 = reckonClassSim(method1.getDeclaringClass(),method2.getDeclaringClass(),hashMap);

        double q2 = 0,q3 =0,q4 = 0,q5 = 0;
        if(callerMap1.get(methodSignature1)!=null&&callerMap2.get(methodSignature2)!=null){
            if(flag){
                System.out.println(callerMap1.get(methodSignature1).size()+" "+callerMap2.get(methodSignature2).size());
            }
            if(callerMap1.get(methodSignature1).size()==callerMap2.get(methodSignature2).size()){
                q2 = 1;
                float sameNum = getSameMethodNum(callerMap1.get(methodSignature1),callerMap2.get(methodSignature2));
                if(sameNum>0){
                    q4 = sameNum/callerMap1.get(methodSignature1).size();
                    if(q4>1){
                        System.out.println(sameNum+" "+callerMap1.get(methodSignature1).size());
                    }
                }

            }
        }

        if(calleeMap1.get(methodSignature1)!=null&&calleeMap2.get(methodSignature2)!=null){
            if(calleeMap1.get(methodSignature1).size()==calleeMap2.get(methodSignature2).size()){
                q3 = 1;
                float sameNum = getSameMethodNum(calleeMap1.get(methodSignature1),calleeMap2.get(methodSignature2));
                if(sameNum>0){
                    q5 = sameNum/calleeMap1.get(methodSignature1).size();
                }

            }
        }
        double q6 = methodSignature1.equals(methodSignature2)?1:0;
        if(flag){
            System.out.println(q1+" "+q2+" "+q3+" "+q4+" "+q5+" "+q6);
        }
        return 0.1*q1+0.2*q2+0.2*q3+0.2*q4+0.2*q5+0.1*q6;
    }

    public static double reckonClassSim(MyClass clazz1, MyClass clazz2, HashMap<String,Double> hashMap){
        String key = clazz1.getName()+"/"+clazz2.getName();
        if(hashMap.get(key)!=null){
            return hashMap.get(key);
        }
        double sim = reckonClassSim(clazz1,clazz2);

        hashMap.put(key,sim);
        return sim;
    }

    public static double reckonClassSim(MyClass clazz1, MyClass clazz2){
        String className1 = clazz1.getName();
        String className2 = clazz2.getName();

        float fieldNum1 = clazz1.getFieldCount();
        float fieldNum2 = clazz2.getFieldCount();
        float sameFieldNum = getSameField(clazz1.getFields(),clazz2.getFields());

        float methodNum1 = clazz1.getMethodCount();
        float methodNum2 = clazz2.getMethodCount();
        float sameMethodNum = getSameMethod(clazz1.getMethods(),clazz2.getMethods());

        float p1 = className1.equals(className2)?1:0;
        float p2 = 0;
        if(Math.max(fieldNum1,fieldNum2)>0){
            p2 = Math.min(fieldNum1,fieldNum2)/Math.max(fieldNum1,fieldNum2);
        }
        float p3 = 0;
        if(Math.max(methodNum1,methodNum2)>0){
            p3 = Math.min(methodNum1,methodNum2)/Math.max(methodNum1,methodNum2);
        }
        float p4 = 0;
        if(fieldNum1>0&&fieldNum2>0){
            p4 = (sameFieldNum/fieldNum1+sameFieldNum/fieldNum2)/2;
        }
        float p5 = 0;
        if(methodNum1>0&&methodNum2>0){
            p5 = (sameMethodNum/methodNum1+sameMethodNum/methodNum2)/2;
        }
        return 0.3*p1+0.1*p2+0.1*p3+0.25*p4+0.25*p5;
    }
    private static int getSameField(List<MyField> myFields1, List<MyField> myFields2){
        int sameNum = 0;
        for(MyField myField1:myFields1){
            for(MyField myField2:myFields2){
                if(myField1.getName().equals(myField2)){
                    sameNum++;
                    break;
                }
            }
        }
        return sameNum;
    }
    private static int getSameMethod(List<MyMethod> methodList1, List<MyMethod> methodList2){
        int sameMethodNum = 0;
        for(MyMethod method1:methodList1){
            for(MyMethod method2:methodList2){
                if(isSameMethod(method1,method2)){
                    sameMethodNum++;
                }
            }
        }
        return sameMethodNum;
    }
    private static boolean isSameMethod(MyMethod method1, MyMethod method2){
        if(method1.getModifiers()==method2.getModifiers()&&
                method1.getSignature().equals(method2.getSignature())){
            return true;
        }
        return false;
    }
    private static String reformSignatureStale(String signature){
        int startPos = signature.indexOf(" ")+1;//去掉第一个空格之前的类名
        int endPos = signature.length()-1;//去掉最后一个“>”；
        String newSignature = signature.substring(startPos,endPos);
        return newSignature;
    }
    private static int getSameMethodNum(List<String> methodList1,List<String> methodList2){
        int sameMethodNum = 0;
        for(String method1:methodList1){
            for(String method2:methodList2){
                if(method1.equals(method2)){
                    sameMethodNum++;
                    break;
                }
            }
        }
        return sameMethodNum;
    }
}
