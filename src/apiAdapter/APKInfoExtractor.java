package apiAdapter;

import apiAdapter.data.MyAPK;
import apiAdapter.data.MyClass;
import apiAdapter.data.MyMethod;
import soot.*;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.util.Chain;

import java.io.*;
import java.util.*;

public class APKInfoExtractor {
    //soot分析初始化所需参数
    private boolean SOOT_INITIALIZED=false;
    private final static String androidJAR="D:\\Android\\sdk\\platforms\\android-28\\android.jar";
    private final static String androidJarDir = "D:\\Android\\sdk\\platforms";

    private String apkPath ;
    private List<String> filter;
    private List<SootClass> clazzList;
    private List<SootMethod> methodList;
    private HashMap<String,List<String>> callerMap,calleeMap;
    public APKInfoExtractor(String apkPath, List<String> filter){
        this.apkPath = apkPath;
        this.filter = filter;
        init(apkPath);
    }

    private void init(String apkPath){
//        if(SOOT_INITIALIZED)
//            return;
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);
//        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_process_dir(Collections.singletonList(apkPath));
        Options.v().set_force_android_jar(androidJAR);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_soot_classpath(androidJAR);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_multiple_dex(true);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
//        SOOT_INITIALIZED=true;
    }
    public MyAPK getAPK(){
        MyAPK myAPK = new MyAPK();

        List<MyClass> myClasses = new ArrayList<>();
        List<SootClass> sootClasses = getClazzs();
        for(SootClass sootClass:sootClasses){
            myClasses.add(Builder.buildClass(sootClass));
        }
        myAPK.setMyClasses(myClasses);

        List<MyMethod> myMethods = new ArrayList<>();
        for(MyClass myClass:myClasses){
            myMethods.addAll(myClass.getMethods());
        }
        myAPK.setMyMethods(myMethods);

        myAPK.setCallerMap(getCallerMap());
        myAPK.setCalleeMap(getCalleeMap());
        return myAPK;
    }



    public HashMap<String,List<String>> getCallerMap(){
        if(callerMap!=null){
            return callerMap;
        }
        reckonCallerCallee();
        return callerMap;
    }

    public HashMap<String,List<String>> getCalleeMap(){
        if(calleeMap!=null){
            return calleeMap;
        }
        reckonCallerCallee();
        return calleeMap;
    }

    public void reckonCallerCallee(){
        callerMap = new HashMap<>();
        calleeMap = new HashMap<>();
        List<SootMethod> methodList = getMethods();
        for(SootMethod method:methodList){
            String signature1 = method.getSignature();
            if(method.hasActiveBody()){
                UnitPatchingChain chain = method.getActiveBody().getUnits();
                Iterator<Unit> iterator = chain.snapshotIterator();
                while(iterator.hasNext()){
                    Unit unit = iterator.next();
                    Stmt stmt = (Stmt) unit;
                    if(!stmt.containsInvokeExpr()){
                        continue;
                    }
                    String methodSignature = stmt.getInvokeExpr().getMethod().getSignature();
                    String signature2 = methodSignature;
                    addMethod(signature1,signature2,callerMap);
                    if(isTargetClass(stmt.getInvokeExpr().getMethod().getDeclaringClass(),filter)){
                        addMethod(signature2,signature1,calleeMap);
                    }
                }
            }
        }
    }

    public List<SootMethod> getMethods(){
        if(methodList!=null){
            return methodList;
        }
        methodList = new ArrayList<>();
        List<SootClass> classList = getClazzs();
        for(SootClass clazz:classList){
            methodList.addAll(clazz.getMethods());
        }
        return methodList;
    }

    public List<SootClass> getClazzs(){
        if(clazzList!=null){
            return clazzList;
        }
        clazzList = new ArrayList<>();
        Chain<SootClass> classes = null;//类链表
        classes = Scene.v().getApplicationClasses();   //加载所有类
        Iterator<SootClass> cit = classes.iterator();//类的迭代器
        while(cit.hasNext()){
            SootClass clazz = cit.next();
            //去掉内部类
            if(clazz.getName().contains("$")){
                continue;
            }
            if(isTargetClass(clazz,filter)){
                clazzList.add(clazz);
            }
        }
        return clazzList;
    }
    private boolean isTargetClass(SootClass sootClass, List<String> filter){
        String name = sootClass.getName();
        for(String str:filter){
            if(name.contains(str)){
                return true;
            }
        }
        return false;
    }

    private String reformSignatureStale(String signature){
        int startPos = signature.indexOf(" ")+1;//去掉第一个空格之前的类名
        int endPos = signature.length()-1;//去掉最后一个“>”；
        String newSignature = signature.substring(startPos,endPos);
        return newSignature;
    }

    private String getClassName(String signature){
        int start = 1;
        int end = signature.indexOf(":");
        return signature.substring(start,end);
    }

    private void addMethod(String signatureKey,String signatureValue,HashMap<String,List<String>> hashMap){
        List<String> list = hashMap.get(signatureKey);
        if(list==null){
            list = new ArrayList<>();
            hashMap.put(signatureKey,list);
        }
        list.add(signatureValue);
    }
}
