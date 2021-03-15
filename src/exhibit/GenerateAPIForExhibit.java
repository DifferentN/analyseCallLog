package exhibit;

import FileUtil.MyFileUtil;
import buildModel.StartBuildModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import eventSimilarity.Event;
import eventSimilarity.MyParameter;
import eventSimilarity.ProcessEventUtil;

import java.io.File;
import java.util.*;

public class GenerateAPIForExhibit {
    public static final String API_LINK = "APILINK";
    public static final String API_MODEL = "APIMODEL";
    public static final String API_OUTPUT = "APIOUTPUT";
    public static final String OUTPUT_VIEW_PATH ="OutputViewPath";
    public static final String OUTPUT_LABEL = "OutputLabel";
    private List<InstanceInfo> instanceInfos;
    private List<String> instancePaths;
    private List<List<String>> userInputs;
    public GenerateAPIForExhibit(){
        instancePaths = new ArrayList<>();
        userInputs = new ArrayList<>();
        instanceInfos = new ArrayList<>();
    }
    public void addInstanceInfo(String path,String userInputStr,String userOutputStr){
        System.out.println("path: "+path);
        System.out.println("userInput: "+userInputStr);
        System.out.println("userOutput: "+userOutputStr);
        List<LabelValueNode> userInput = transformToListNode(userInputStr);
//        System.out.println("get userInput");
        List<LabelValueNode> userOutput = transformToListNode(userOutputStr);
//        System.out.println("get userOutput");
        InstanceInfo instanceInfo = new InstanceInfo(path,userInput,userOutput);
        instanceInfos.add(instanceInfo);
//        System.out.println("instanceInfos size: "+instanceInfos.size());
    }

    /**
     * 将c++层传过来的用户输入或输出转化为list
     * @param src
     * @return
     */
    private List<LabelValueNode> transformToListNode(String src){
        List<LabelValueNode> res = new ArrayList<>();
        if(src==null){
            return res;
        }
        //对用户给的输入/输出进行分割
        String[] str = src.split("/");
        for(int i=0;i<str.length;i++){
            if(str[i].equals("")){
                break;
            }
            String labelValue[] = str[i].split(":");
            //labelValue中第一个表示 标签，第二个表示 值
            LabelValueNode node = new LabelValueNode(labelValue[0],labelValue[1]);
            res.add(node);
        }
        return res;
    }
    public void addInstance(String path,String userInput){
        instancePaths.add(path);
        List<String> list = new ArrayList<>();
        String strs[] = userInput.split("/");
        for(String item:strs){
            list.add(item);
        }
        userInputs.add(list);
    }

    /**
     * 给path指定的API文件添加一个API链接
     * 链接的格式为content://APIName?参数名1&参数名2
     * @param path API文件的路径
     * @param apiName APIName
     * @param params 参数名1&参数名2
     */
    public static void addAPILink(String path,String apiName,String params){
        System.out.println(path);
        System.out.println(apiName);
        System.out.println(params);
        JSONObject APIJSON = MyFileUtil.readJSONObject("execute.json");
        String APILink = "content://"+apiName+"?"+params;
        //添加API链接
        APIJSON.put(API_LINK,APILink);
        MyFileUtil.writeJSONObject(path,APIJSON);
    }

    /**
     * 生成API
     * @return
     */
    public String executeGenerateAPI(){
        //先清空之前生成API时用的数据
        userInputs.clear();
        instancePaths.clear();
        //读取用户实例文件路径和用户输入
        for(InstanceInfo instanceInfo:instanceInfos){
            List<String> items = new ArrayList<>();
            for(LabelValueNode node:instanceInfo.getUserInput()){
                items.add(node.getValue());
            }
            userInputs.add(items);
            instancePaths.add(instanceInfo.getPath());
        }
        StartBuildModel startBuildModel = new StartBuildModel();
        System.out.println("微服务模板生成中......");
        List<Event> modelEvents = startBuildModel.generateAPI(instancePaths,userInputs);
        System.out.println("微服务模板生成完成");
        System.out.println("微服务模板标签分配中......");
        //modelEvents使用的是第一个用户实例的输入，所以用第一个用户给的输入标签-值来设置modelEvent中输入参数的标签
        for(Event event:modelEvents){
            if(event.getMethodName().equals(Event.SETTEXT)){
                //给输入参数分配标签
                for(MyParameter myParameter:event.getParameters()){
                    //在第一个用户实例中寻找标签
                    for(LabelValueNode node:instanceInfos.get(0).getUserInput()){
                        //用户给的值相同于用户实例中的值相同，分配标签
                        if(node.getValue().equals(myParameter.value)){
                            myParameter.type = node.getLabel();
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("微服务模板标签分配完成");

        //将modelEvent转化为JSON文件
        JSONArray APIModel = ProcessEventUtil.transformAPIEventsToJSONArray(modelEvents);
        String apiFileName = "execute.json";
//        apiFileName = "C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem.txt";
        //将API的信息(链接,model,输出)保存到文件中
        JSONObject APIJson = new JSONObject();
        APIJson.put(API_LINK,"");
        APIJson.put(API_MODEL,APIModel);

        //根据用户操作实例文件路径获取对应的输出页面文件路径
        String userInstancePath = instancePaths.get(0);
        String pre = userInstancePath.substring(0,userInstancePath.lastIndexOf("/")+1);
        String pageContentPath = pre+"pageContent.txt";
//        pageContentPath = "C:/Users/17916/Desktop/testVideo/userPageContent1.txt";
        System.out.println("pageContent: "+pageContentPath);
        //根据用户给定的输出数据，生成API的输出
        JSONArray userRequired = obtainUserRequired(pageContentPath,instanceInfos.get(0).getUserOutput());
        System.out.println(userRequired.toString());
        APIJson.put(API_OUTPUT,userRequired);
        MyFileUtil.writeJSONObject(apiFileName,APIJson);
        //清空instanceInfos
        instanceInfos.clear();
        return apiFileName;
    }

    /**
     * 根据页面的内容和用户给定的输出，生成API对应的输出（用户要求的输出）
     * 如果用户没有给定输出要求，则会产生一个空的JSONArray
     * @param filePath
     * @param userOutput
     * @return JSONArray包含了用户要求的输出集合，每一个JSONObject代表一个用户要求的输出项，包含了标签和视图路径
     */
    private JSONArray obtainUserRequired(String filePath,List<LabelValueNode> userOutput){
        JSONArray userRequiredArray = new JSONArray();
        //检查文件是否存在
        File file = new File(filePath);
        if(!file.exists()){
            System.out.println("找不到页面文件");
            return userRequiredArray;
        }
        JSONObject pageContentJSON = MyFileUtil.readJSONObject(filePath);

        //页面中全部视图的路径的集合
        Set<String> viewPaths = pageContentJSON.keySet();

        //将用户要求的输出临时保存到HashMap中,保存形式为ViewPath-Label
        HashMap<String,String> userRequiredHash = new HashMap<>();
        for(LabelValueNode node:userOutput){
            String text = node.getValue();
            String label = node.getLabel();
            for(String path:viewPaths){
                String contentItem = pageContentJSON.getString(path);
                if(contentItem.contains(text)){
                    userRequiredHash.put(path,label);
                    break;
                }
            }
        }
        //将API的输出保存成JSONArray的形式
        Set<Map.Entry<String,String>> entrySet = userRequiredHash.entrySet();
        for(Map.Entry<String,String> entry:entrySet){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(OUTPUT_VIEW_PATH,entry.getKey());
            jsonObject.put(OUTPUT_LABEL,entry.getValue());
            userRequiredArray.add(jsonObject);
        }
        return userRequiredArray;
    }
    /**
     * 使用用户给定的第一个输出标签-值集合 过滤得到的页面中的内容
     * @param outputJson
     * @return
     */
    private JSONObject filterPageByUserOutput(JSONObject outputJson){
        JSONObject resJSON = new JSONObject();
        List<LabelValueNode> userOutput = instanceInfos.get(0).getUserOutput();
        Set<String> keySet = outputJson.keySet();
        //遍历页面中的路径-值
        for(String pathItem:keySet){
            String value = outputJson.getString(pathItem);
            //根据用户给定的输入标签-值集合，检查页面某个值是否是用户需要的，并将此值对应的路径加入到resJSON中
            for(LabelValueNode node:userOutput){
                if(node.getValue().equals(value)){
                    resJSON.put(pathItem,value);
                }
            }
        }
        return resJSON;
    }
}
