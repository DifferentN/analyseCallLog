package apiAdapter;

import FileUtil.MyFileUtil;
import apiAdapter.data.MyAPK;
import apiAdapter.data.MyClass;
import apiAdapter.data.MyMethod;
import apiAdapter.data.MyMethodPair;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import reformCall.TransformMethodUtil;
import soot.SootMethod;

import java.io.*;
import java.util.*;

public class ApiAdapter {
    public static void main(String args[]){
        String apkPath1 = "C:\\Users\\17916\\Desktop\\APIGenerate\\AnkiDroid\\APK/AnkiDroid1.0-release.apk";
        String apkPath2 = "C:\\Users\\17916\\Desktop\\APIGenerate\\AnkiDroid\\APK/AnkiDroid2.0-release.apk";

        //提取APK中的类方法信息 （因为Soot的原因，每次运行只能提取1个APK中的信息）
        String path = null;
        if(args==null||args.length==0||args[0]==null){
            path = apkPath1;
        }else if(args.length==1){
            path = args[0];
        }
        String apkInfoPath = extractAPKInfo(path);

        String oldAPIPath = "C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem/addItem.json";
        //对oldAPI进行调整
//        String newAPIPath = APISelfAdapt(oldAPIPath,"oldAPK.txt","newAPK.txt");
    }

    public static String APISelfAdapt(String oldAPIPath,String oldInfoPath,String newInfoPath){

        File file = new File(oldAPIPath);
        String parentPath = file.getParentFile().getAbsolutePath();
        String name = file.getName();
        //新API的地址
        String newAPIPath = parentPath+"\\"+"new_"+name;

        //调整API
        apiAdapter(oldAPIPath,newAPIPath,oldInfoPath,newInfoPath);

        String methodPairPath = parentPath+"\\"+"method.txt";
        //生成用于展示的方法映射对
        TransformMethodUtil.writeMyMethodPairs(methodPairPath,reckonMyMethodSimPair(oldInfoPath,newInfoPath));

        return newAPIPath;
    }

    private static void apiAdapter(String oldAPIPath,String newAPIPath,String oldAPKPath,String newAPKPath){
        HashMap<String,String> hash = reckonMethodSimilarity(oldAPKPath,newAPKPath);
        HashMap<String,String> changeHash = new HashMap<>();
        Set<String> keySet = hash.keySet();
        for(String key:keySet){
            if(key.equals(hash.get(key))){
                continue;
            }
//            System.out.println(key+"\n"+hash.get(key));
            changeHash.put(key,hash.get(key));
        }
        JSONObject oldAPI = MyFileUtil.readJSONObject(oldAPIPath);
        JSONArray apiModel = oldAPI.getJSONArray("APIMODEL");

        for(int i=0;i<apiModel.size();i++){
            JSONObject jsonObject = apiModel.getJSONObject(i);
            JSONObject invoke = jsonObject.getJSONObject("invoke");
            int invokeSize = invoke.getInteger("invokeSize");
            for(int j=0;j<invokeSize;j++){
                System.out.println("invoke "+j);
                String invokeCall = invoke.getString(j+"");
                for(Map.Entry<String,String> entry:changeHash.entrySet()){
                    int pos = 0;
                    while(( pos=invokeCall.indexOf(entry.getKey(),pos) )>=0){
                        char c = invokeCall.charAt(pos+entry.getKey().length());
                        if(c!=':'&&c!=')'){
                            pos+=entry.getKey().length();
                            continue;
                        }
                        String head = invokeCall.substring(0,pos);
                        String tail = invokeCall.substring(pos+entry.getKey().length());
                        invokeCall = head+entry.getValue()+tail;
                        pos+=entry.getValue().length();
//                        System.out.println("search "+pos+" "+invokeCall.length()+" "+entry.getKey()+" "+entry.getValue());
//                        invokeCall.replace(entry.getKey(),entry.getValue());
                    }
                }
                invoke.put(j+"",invokeCall);
            }
        }

        MyFileUtil.writeJSONObject(newAPIPath,oldAPI);
    }

    private static HashMap<String,String> reckonMethodSimilarity(String oldAPKPath, String newAPKPath){

        MyAPK myAPK1 = readAPK(oldAPKPath);
        MyAPK myAPK2 = readAPK(newAPKPath);

        List<MyMethod> methodList1 = myAPK1.getMyMethods();
        List<MyMethod> methodList2 = myAPK2.getMyMethods();
        for(MyMethod myMethod : methodList2){
//            System.out.println(myMethod.getSignature());
            if(myMethod.getMethodName().contains("LZH")){
                System.out.println(myMethod.getSignature());
            }
        }

        HashMap<String,List<String>> callerMap1 = myAPK1.getCallerMap();
        HashMap<String,List<String>> calleeMap1 = myAPK1.getCalleeMap();

        HashMap<String,List<String>> callerMap2 = myAPK2.getCallerMap();
        HashMap<String,List<String>> calleeMap2 = myAPK2.getCalleeMap();

        HashMap<String,Double> hashMap = new HashMap<>();
        HashMap<String,String> methodPair = new HashMap<>();
        for(MyMethod method1:methodList1){
            MyMethod targetMethod = null;
            double max = 0;
            for(MyMethod method2:methodList2){
                if(!method1.getClassName().equals(method2.getClassName())){
                    continue;
                }
                double sim = SimilarityUtil.reckonMethodSim(method1,method2,callerMap1,calleeMap1,
                        callerMap2,calleeMap2,hashMap,false);
                if(sim>max){
                    max = sim;
                    targetMethod = method2;
//                    System.out.println(sim);
                }
            }
//            System.out.println(extractMethodInfo(method1)+"\n"+extractMethodInfo(targetMethod));
            methodPair.put(extractMethodInfo(method1),extractMethodInfo(targetMethod));
        }

        return methodPair;
    }

    public static List<MyMethodPair> reckonMyMethodSimPair(String oldAPKPath, String newAPKPath){
        MyAPK myAPK1 = readAPK(oldAPKPath);
        MyAPK myAPK2 = readAPK(newAPKPath);

        List<MyMethod> methodList1 = myAPK1.getMyMethods();
        List<MyMethod> methodList2 = myAPK2.getMyMethods();
        for(MyMethod myMethod : methodList2){
//            System.out.println(myMethod.getSignature());
            if(myMethod.getMethodName().contains("LZH")){
                System.out.println(myMethod.getSignature());
            }
        }

        HashMap<String,List<String>> callerMap1 = myAPK1.getCallerMap();
        HashMap<String,List<String>> calleeMap1 = myAPK1.getCalleeMap();

        HashMap<String,List<String>> callerMap2 = myAPK2.getCallerMap();
        HashMap<String,List<String>> calleeMap2 = myAPK2.getCalleeMap();

        HashMap<String,Double> hashMap = new HashMap<>();
        List<MyMethodPair> res = new ArrayList<>();
        for(MyMethod method1:methodList1){
            MyMethod targetMethod = null;
            double max = 0;
            for(MyMethod method2:methodList2){
                if(!method1.getClassName().equals(method2.getClassName())){
                    continue;
                }
                double sim = SimilarityUtil.reckonMethodSim(method1,method2,callerMap1,calleeMap1,
                        callerMap2,calleeMap2,hashMap,false);
                if(sim>max){
                    max = sim;
                    targetMethod = method2;
                }
            }
            res.add(new MyMethodPair(method1,targetMethod,max));
        }

        return res;
    }

    /**
     * 提取APK中的类方法信息
     * @param apkPath
     * @return 存储类方法信息的文件的路径
     */
    public static String extractAPKInfo(String apkPath){
        List<String> filter = new ArrayList<>();
        filter.add("anki");

        APKInfoExtractor extractor2 = new APKInfoExtractor(apkPath,filter);
        System.out.println(Runtime.getRuntime().totalMemory());

        File file = new File(apkPath);
        String parentPath = file.getParentFile().getAbsolutePath();
        String infoFileName = file.getName().substring(0,file.getName().lastIndexOf("."))+".txt";
        String infoFilePath = parentPath+"\\"+infoFileName;
        writeAPK(infoFilePath,extractor2.getAPK());

        return infoFilePath;
    }

    private static String extractMethodInfo(MyMethod sootMethod){
        return sootMethod.getDeclaringClass().getName()+"/"+sootMethod.getMethodName();
    }

    public static MyAPK readAPK(String path){
        MyAPK myAPK = null;
        FileInputStream fileInputStream1 = null;
        try {
            fileInputStream1 = new FileInputStream(new File(path));
            ObjectInputStream objectInputStream1 = new ObjectInputStream(fileInputStream1);
            myAPK = (MyAPK) objectInputStream1.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return myAPK;
    }

    public static void writeAPK(String path,MyAPK myAPK){
        FileOutputStream f1 = null;
        try {
            f1 = new FileOutputStream(new File(path));
            ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(f1);
            objectOutputStream1.writeObject(myAPK);
            objectOutputStream1.flush();
            objectOutputStream1.close();
            f1.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int KMP(String ts, String ps) {

        char[] t = ts.toCharArray();

        char[] p = ps.toCharArray();

        int i = 0; // 主串的位置

        int j = 0; // 模式串的位置

        int[] next = getNext(ps);

        while (i < t.length && j < p.length) {

            if (j == -1 || t[i] == p[j]) { // 当j为-1时，要移动的是i，当然j也要归0

                i++;

                j++;

            } else {

                // i不需要回溯了

                // i = i - j + 1;

                j = next[j]; // j回到指定位置

            }

        }

        if (j == p.length) {

            return i - j;

        } else {

            return -1;

        }

    }

    public static int[] getNext(String ps) {

        char[] p = ps.toCharArray();

        int[] next = new int[p.length];

        next[0] = -1;

        int j = 0;

        int k = -1;

        while (j < p.length - 1) {

            if (k == -1 || p[j] == p[k]) {

                next[++j] = ++k;

            } else {

                k = next[k];

            }

        }

        return next;

    }
}
