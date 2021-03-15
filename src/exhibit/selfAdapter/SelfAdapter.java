package exhibit.selfAdapter;

import apiAdapter.APKInfoExtractor;
import apiAdapter.ApiAdapter;
import apiAdapter.SimilarityUtil;
import FileUtil.MyFileUtil;
import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import buildModel.PreProcess;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import eventSimilarity.*;
import soot.SootMethod;

import java.io.File;
import java.util.*;

public class SelfAdapter {
    public static void main(String args[]){
        String oldPath = "C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem/addItem.json";
        String oldInvokePath = "C:/Users/17916/Desktop/selfAdapter/AnkiDroid/methodLog_original.txt";
        String newInvokePath = "C:\\Users\\17916\\Desktop\\selfAdapter\\temp\\anki/methodLog_change.txt";
        String res = adjustOldAPI(oldPath,oldInvokePath,newInvokePath);
        System.out.println(res);
    }
    public static String adjustOldAPI2(String oldAPIPath,String oldVersionInvokePath,String newVersionInvokePath){
        String oldInfoPath = "C:\\Users\\17916\\Desktop\\APIGenerate\\AnkiDroid\\APK/AnkiDroid1.0-release.txt";
        String newInfoPath = "C:\\Users\\17916\\Desktop\\APIGenerate\\AnkiDroid\\APK/AnkiDroid2.0-release.txt";
        String newAPIPath = ApiAdapter.APISelfAdapt(oldAPIPath,oldInfoPath,newInfoPath);
        return newAPIPath;
    }
    public static String adjustOldAPI(String oldAPIPath,String oldVersionInvokePath,String newVersionInvokePath){
        System.out.println("开始自适应替换......");
        List<MyMethod> invokes1 = MethodSequenceUtil.getSequence(oldVersionInvokePath);
        List<MyMethod> invokes2 = MethodSequenceUtil.getSequence(newVersionInvokePath);
        //方法相似度
        HashMap<String,Float> hash = ReadMethodSimilarityUtil.readMethodSimilarityToHash("C:/Users/17916/Desktop/selfAdapter/temp/anki/method-2.txt");
        //转化为event序列
        List<Event> events1 = GenerateEventUtil.generateEventsForSelfAdapter(invokes1);

        ArrayList<String> userInput = new ArrayList<>();
        userInput.add("1");
        userInput.add("2");
        userInput.add("3");
        PreProcess preProcess = new PreProcess();
        invokes2 = preProcess.processDataLink(invokes2,userInput);

        List<Event> events2 = GenerateEventUtil.generateEventsForSelfAdapter(invokes2);
        System.out.println("开始匹配新旧操作事件");
        StartSimilarity startSimilarity = new StartSimilarity();
        List<EventMap> eventMaps = startSimilarity.getEventMap(events1,events2,hash);

        List<Event> oldApiEvent = null;
        //读取旧版本的API执行序列
        oldApiEvent = extractOldAPI(oldAPIPath);

        System.out.println("替换旧版API的操作");
        ReplaceModuleAPI replaceModuleAPI = new ReplaceModuleAPI();
        //对旧版本的API序列进行替换，得到新版本的API序列
        List<Event> newAPIEvents = replaceModuleAPI.replaceEvent(oldApiEvent,eventMaps);

        //将API执行序列转化为JSONArray格式
        JSONArray newAPIJsonArray = ProcessEventUtil.transformAPIEventsToJSONArray(newAPIEvents);

        //为新版本API中的输入操作重新分配标签
        JSONObject oldAPI = MyFileUtil.readJSONObject(oldAPIPath);
        JSONArray oldAPIModel = oldAPI.getJSONArray("APIMODEL");
        for(int i=0;i<oldAPIModel.size();i++){
            JSONObject oldAPIEvent = oldAPIModel.getJSONObject(i);
            if(oldAPIEvent.getString("methodName").equals("setText")){
                JSONObject newAPIEvent = newAPIJsonArray.getJSONObject(i);
                newAPIEvent.put("parameterType",oldAPIEvent.getString("parameterType"));
            }
        }
        //用新的APIModel替换掉旧版本API中的APIModel，然后保存
        oldAPI.put("APIMODEL",newAPIJsonArray);
        //新建一个保存新版本API的文件夹
        int index = oldAPIPath.lastIndexOf("/");
        String newAPIFolderPath = oldAPIPath.substring(0,index+1)+"newAPI";
        System.out.println(newAPIFolderPath);
        File file = new File(newAPIFolderPath);
        if(!file.exists()){
            file.mkdir();
        }
        String newAPIPath = newAPIFolderPath+"/"+oldAPIPath.substring(index+1);
        MyFileUtil.writeJSONObject(newAPIPath,oldAPI);
        return newAPIPath;
    }
    /**
     * 根据自适应得到的APIModel 保存新的API文件
     * @param path
     * @param newAPIModel
     */
    private static void saveNewAPI(String path,JSONArray newAPIModel){
        JSONObject jsonObject = MyFileUtil.readJSONObject(path);
        JSONArray oldAPIModel = jsonObject.getJSONArray("APIMODEL");
        for(int i=0;i<oldAPIModel.size();i++){
            JSONObject oldAPIEvent = oldAPIModel.getJSONObject(i);
            if(oldAPIEvent.getString("methodName").equals("setText")){
                JSONObject newAPIEvent = newAPIModel.getJSONObject(i);
                newAPIEvent.put("parameterType",oldAPIEvent.getString("parameterType"));
            }
        }
        jsonObject.put("APIMODEL",newAPIModel);
        MyFileUtil.writeJSONObject("execute.txt",jsonObject);
    }

    /**
     * 提取旧版本API文件中的APIModel
     * @param path
     * @return
     */
    private static List<Event> extractOldAPI(String path){
        JSONObject jsonObject = MyFileUtil.readJSONObject(path);
        JSONArray apiModel = jsonObject.getJSONArray("APIMODEL");
        MyFileUtil.writeEventJSONArray("execute.txt",apiModel);
        List<Event> res = StartSimilarity.readOldService("execute.txt");
        return res;
    }




}
