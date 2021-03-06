package eventSimilarity;

import FileUtil.MyFileUtil;
import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import buildModel.PreProcess;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import graph.GenerateGNode;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartSimilarity {
    public static void main(String[] args){
        System.out.println("开始自适应替换......");
        List<MyMethod> invokes1 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/selfAdapter/AnkiDroid/methodLog_original.txt");//methodLog_original
        List<MyMethod> invokes2 = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/selfAdapter/AnkiDroid/methodLog_change.txt");//methodLog_change
        //方法相似度
        HashMap<String,Float> hash = ReadMethodSimilarityUtil.readMethodSimilarityToHash("C:/Users/17916/Desktop/selfAdapter/anki/method-2.txt");
        //转化为event序列
        List<Event> events1 = GenerateEventUtil.generateEventsForSelfAdapter(invokes1);

        ArrayList<String> userInput = new ArrayList<>();
        userInput.add("q1");
        userInput.add("q2");
        userInput.add("q3");
        userInput.add("a1");
        userInput.add("a2");
        userInput.add("a3");
        PreProcess preProcess = new PreProcess();
        invokes2 = preProcess.processDataLink(invokes2,userInput);
        List<Event> events2 = GenerateEventUtil.generateEventsForSelfAdapter(invokes2);
//        for(Event event:events2){
//            System.out.println("method Name: "+event.getMethodName());
//            List<MyMethod> methodList = event.getInvokeList();
//            for(MyMethod myMethod:methodList){
//                System.out.println(myMethod.methodName);
//            }
//        }
        GenerateGNode generateGNode = new GenerateGNode();
        List<EventMap> eventMaps = getEventMap(events1,events2,hash);
        for(EventMap eventMap:eventMaps){
//            System.out.println(eventMap.oldEvent.getActivityId());
            String info = "oldEvent: "+eventMap.oldEvent.getComponentId()+"-"+eventMap.oldEvent.getMethodName();
            info += "; maxSim: "+eventMap.maxSim;
            info += "; newEvent: ";
            for(Event event:eventMap.newEvents){
                info+=event.getComponentId()+"-"+event.getMethodName()+"  ";
            }
            System.out.println(info);
        }
        List<Event> oldApiEvent;
//        List<Event> oldApiEvent = obtain("C:/Users/17916/Desktop/selfAdapter/TimberX/execute.txt");
//        oldApiEvent = GenerateEventUtil.generateEvents(MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/selfAdapter/anki/addItem.txt"));
        oldApiEvent = extractOldAPI("C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem.txt");

        ReplaceModuleAPI replaceModuleAPI = new ReplaceModuleAPI();
        List<Event> apiEvents = replaceModuleAPI.replaceEvent(oldApiEvent,eventMaps);

        JSONArray jsonArray = ProcessEventUtil.transformAPIEventsToJSONArray(apiEvents);
//        MyFileUtil.writeEventJSONArray("execute.txt",jsonArray);
        saveNewAPI("C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem.txt",jsonArray);
        System.out.println("自适应替换结束");
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
        List<Event> res = readOldService("execute.txt");
        return res;
    }
    /**
     * 旧版本event对应的新版本event的列表
     * @param rawEvents1 旧版本event
     * @param rawEvents2 新版本event
     * @param hash
     * @return
     */
    public static List<EventMap> getEventMap(List<Event> rawEvents1,List<Event> rawEvents2,HashMap<String,Float> hash){
        List<Event> modelEvents1 = ProcessEventUtil.processRawEvents(rawEvents1);
        List<Event> modelEvents2 = ProcessEventUtil.processRawEvents(rawEvents2);
//        for(Event event:modelEvents2){
//            System.out.println(event.getInvokeList().size());
//        }
        int len1 = modelEvents1.size(),len2 = modelEvents2.size();
        float[][] grid = new float[len1][len2];
        EventSimilarityReckon similarityReckon = new EventSimilarityReckon(hash);
        for(int i=0;i<len1;i++){
            for(int j=0;j<len2;j++){
                grid[i][j] = similarityReckon.reckonEventSimilarity(modelEvents1.get(i),modelEvents2.get(j));
            }
        }

        float max=Integer.MIN_VALUE;
        List<EventMap> res = new ArrayList<>();
        for(int i=0;i<len1;i++){
//            System.out.println(modelEvents1.get(i).getMethodName()+":"+modelEvents1.get(i).getPath());
            max = 0;
            for(int j=0;j<len2;j++){
                if(grid[i][j]>max){
                    max = grid[i][j];
                }
            }
            EventMap eventMap = new EventMap(modelEvents1.get(i));
            eventMap.maxSim = max;
            for(int j=0;j<len2;j++){
                if(grid[i][j] == max){
                    eventMap.addMap(modelEvents2.get(j));
//                    System.out.println(max +"add: "+modelEvents2.get(j).getPath());
                }
            }
            res.add(eventMap);
        }
        return res;
    }

    /**
     * 读取旧的服务版本
     * @param path 旧服务的路径
     * @return
     */
    public static List<Event> readOldService(String path){
        File file = new File(path);
        if(!file.exists()){
            System.out.println("旧的服务文件不存在");
            return new ArrayList<Event>();
        }
        StringBuffer serviceJSON = new StringBuffer();
        FileReader fileReader = null;
        String line = null;
        try {
            fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            while((line=reader.readLine())!=null){
                serviceJSON.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = JSONArray.parseArray(serviceJSON.toString());
        List<Event> oldServiceEvent = new ArrayList<>();
        Event event = null;
        int index = 0;
        for(int i=0;i<jsonArray.size();i++){
            index = jsonArray.getJSONObject(i).getInteger("seqId");
            event = transformJSONToEvent(jsonArray.getJSONObject(i));
            oldServiceEvent.add(index,event);
        }
        return oldServiceEvent;
    }

    /**
     * 将服务中的jsonObject转化为event
     * @param jsonObject
     * @return
     */
    public static Event transformJSONToEvent(JSONObject jsonObject){
        String activityId = jsonObject.getString("ActivityID");
        String viewId = jsonObject.getString("viewId");
        String viewPath = jsonObject.getString("viewPath");
        String methodName = jsonObject.getString("methodName");
        String parameterValue = jsonObject.getString("parameterValue");
        String packageName = jsonObject.getString("packageName");
        Event event = new Event(activityId,viewId,viewPath,methodName,packageName);

        List<MyParameter> list = new ArrayList<>();
        list.add(new MyParameter("String",parameterValue));
        event.setParameters(list);

        List<String> invokeListStr = new ArrayList<>();
        JSONObject invokeJson = jsonObject.getJSONObject("invoke");
        int size = invokeJson.getInteger("invokeSize");
        for(int i=0;i<size;i++){
            invokeListStr.add(invokeJson.getString(""+i));
        }
//        event.setInvokeList(invokeListStr);
        return event;
    }
    private static List<Event> reform(List<Event> oldEvents,String oldServicePath){
        List<Event> oldApiEvent = readOldService(oldServicePath);
        if(oldApiEvent.size()!=oldEvents.size()){
            return oldApiEvent;
        }
        for(int i=0;i<oldApiEvent.size();i++){
            oldApiEvent.get(i).setPath(oldEvents.get(i).getPath());
        }
        return oldApiEvent;
    }
    private static List<Event> obtain(String path){
        List<MyMethod> oldInvokes = MethodSequenceUtil.getSequence("C:/Users/17916/Desktop/selfAdapter/TimberX/methodLog-2.txt");
        List<Event> oldApiEvent = GenerateEventUtil.generateEventsForSelfAdapter(oldInvokes);
        oldApiEvent = reform(oldApiEvent,path);
        return oldApiEvent;
    }
}
