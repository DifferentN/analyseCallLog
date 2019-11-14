package InputMatch;

import analyseMethodCall.MethodSequenceUtil;
import analyseMethodCall.MyMethod;
import com.alibaba.fastjson.JSONObject;
import eventSimilarity.Event;
import eventSimilarity.GenerateEventUtil;
import eventSimilarity.MyParameter;
import eventSimilarity.ProcessEventUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class MatchInputEvent {
    //inputHash的key为用户提供的数据类型，eventHash的key为setText方法event中 方法名/componentId
    private HashMap<String,Integer> inputHash,eventHash;
    private int[][] relMap;
    private int inputKeySize,eventKeySize;

    /**
     * inputPath与eventPath中的数据一一对应。inputPath中的第一个输入数据对应eventPath中的第一个操作数据
     * @param inputPath 表示用户输入的数据存放路径
     * @param eventPath 表示用户操作Event数据存放路径
     * @return
     */
    public JSONObject matchInputAndEvent(List<String> inputPath,List<String> eventPath){
        if(inputPath.size()!=eventPath.size()){
            System.out.println("用户输入数量与实例的操作Event数量不同 inputPath size"+inputPath.size()+" eventSize "+eventPath.size());
            return null;
        }
        if(inputPath.size()<=0||eventPath.size()<=0){
            return null;
        }

        int size = inputPath.size();
        JSONObject jsonObject;
        List<Event> rawEvents =null,setTextEvents = null;
        String path1,path2;
        List<MyMethod> myMethods;
        for(int i=0;i<size;i++){
            path1 = inputPath.get(i);
            path2 = eventPath.get(i);
            jsonObject = readJSON(path1);
            myMethods = MethodSequenceUtil.getSequence(path2);
            rawEvents = GenerateEventUtil.generateEvents(myMethods);
            setTextEvents = reduceEven(rawEvents);
            if(i==0){
                //建立关系映射数组
                inputKeySize = initInputHash(jsonObject);
                eventKeySize = initEventHash(setTextEvents);
                relMap = new int[inputKeySize][eventKeySize];
            }
            Set<String> inputKeys = jsonObject.keySet();
            String value = null;
            for(String key:inputKeys){
                value = jsonObject.getString(key);
                for(int j = 0;j<setTextEvents.size();j++){
                    Event event = setTextEvents.get(j);
                    if( value.equals(getTextFromEvent(event)) ){
                        relMap[ inputHash.get(key) ][ eventHash.get(getKeyFromEvent(event)) ]++;
                    }
                }
            }
        }
        int max = 0,index = 0;
        String key1,key2;
        JSONObject relMapJSON = new JSONObject();
        for(int i=0;i<inputKeySize;i++){
            max = -1;
            for(int j=0;j<eventKeySize;j++){
                if(relMap[i][j]>max){
                    max = relMap[i][j];
                    index = j;
                }
            }
            key1 = getKeyFromHash(inputHash,i);
            key2 = getKeyFromHash(eventHash,index);
            relMapJSON.put(key1,key2);
        }
        return relMapJSON;
    }
    private String getKeyFromHash(HashMap<String,Integer> hashMap,int index){
        Set<HashMap.Entry<String,Integer>> entrySet = hashMap.entrySet();
        for(HashMap.Entry<String,Integer> entry:entrySet){
            if(entry.getValue()==index){
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 获取SetText方法Event中的用户输入
     * @param event
     * @return
     */
    private String getTextFromEvent(Event event){
        List<MyParameter> myParameters = event.getParameters();
        if(myParameters.isEmpty()){
            return null;
        }else{
            return myParameters.get(0).value;
        }
    }
    /**
     * 对event链表进行删减，只保留setText方法的event
     * @param events
     * @return
     */
    private List<Event> reduceEven(List<Event> events){
        List<Event> tarEvents = new ArrayList<>();
        for(Event event:events){
            if(event.getMethodName().equals(Event.SETTEXT)){
                tarEvents.add(event);
            }
        }
        return tarEvents;
    }
    private int initInputHash(JSONObject jsonObject){
        Set<String> keys = jsonObject.keySet();
        inputHash = new HashMap<>();
        Iterator<String> iterator = keys.iterator();
        int index = 0;
        while(iterator.hasNext()){
            inputHash.put(iterator.next(),index++);
        }
        return keys.size();
    }

    /**
     * 使用有SetText方法的event初始化eventHash
     * @param events
     * @return
     */
    private int initEventHash(List<Event> events){
        eventHash = new HashMap<>();
        int index = 0;
        String key = null;
        for(Event event:events){
            if(event.getMethodName().equals(Event.SETTEXT)){
                key = getKeyFromEvent(event);
                eventHash.put(key,index++);
            }
        }
        return index;
    }

    /**
     * 获取event中 方法名/componentId 以代表此event
     * @param event
     * @return
     */
    private String getKeyFromEvent(Event event){
        return event.getMethodName()+"/"+event.getComponentId()+"/"+event.getPath();
    }
    /**
     * 从指定路径中读取用户输入数据以json的形式返回
     * @param path
     * @return
     */
    private JSONObject readJSON(String path){
        File file = new File(path);
        if(!file.exists()){
            System.out.println("文件不存在 "+path);
            return null;
        }
        StringBuffer buf = new StringBuffer();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String temp = null;
            while((temp = reader.readLine())!=null){
                buf.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonStr = buf.toString();
        if(jsonStr==null){
            System.out.println("jsonString 为空");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        return jsonObject;
    }
}
