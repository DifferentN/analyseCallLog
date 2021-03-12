package exhibit;

import FileUtil.MyFileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Set;

public class Test {
    public static void main(String args[]){
//        GenerateAPIForExhibit generateAPIForExhibit = new GenerateAPIForExhibit();
//        String path = "C:/Users/17916/Desktop/exprience/anki/openCard/methodLog-1.txt";//C:/Users/17916/Desktop/testVideo/
//        String userInput = "电影名称:少年";
//        String userOutput = null;
//        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
//        path = "C:/Users/17916/Desktop/exprience/anki/openCard/methodLog-2.txt";//D:/APIGenerateExperiment/3APP/shipudaquan/searchfood/
//        userInput = "电影名称:千与千寻";
//        userOutput = null;
//        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
//        path = "C:/Users/17916/Desktop/exprience/anki/openCard/methodLog-3.txt";//D:/APIGenerateExperiment/3APP/anki/addItem/
//        userInput = "电影名称:钢铁侠";
//        userOutput = "";
//        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
//        generateAPIForExhibit.executeGenerateAPI();
        String s = "{\"ViewFlag\":false,\"callerClassName\":\"android.app.Activity\",\"callerHashCode\":-1,\"methodName\":\"dispatchTouchEvent\",\"methodParameter\":[{\"parameterClassName\":\"android.view.MotionEvent\"}],\"methodResult\":{},\"threadId\":1}";
        JSONObject jsonObject = JSONObject.parseObject(s);
        System.out.println(jsonObject.toJSONString());
    }
}
