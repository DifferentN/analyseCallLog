package exhibit;

import FileUtil.MyFileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Set;

public class Test {
    public static void main(String args[]){
        GenerateAPIForExhibit generateAPIForExhibit = new GenerateAPIForExhibit();
        String path = "C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem/methodLog1.txt";//C:/Users/17916/Desktop/testVideo/
        String userInput = "Front:猫/Back:cat";
        String userOutput = null;
        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
        path = "C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem/methodLog2.txt";//D:/APIGenerateExperiment/3APP/shipudaquan/searchfood/
        userInput = "Front:狗/Back:dog";
        userOutput = null;
        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
        path = "C:/Users/17916/Desktop/APIGenerate/AnkiDroid/Instances/addItem/methodLog3.txt";//D:/APIGenerateExperiment/3APP/anki/addItem/
        userInput = "Front:鱼/Back:fish";
        userOutput = "";
        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
        generateAPIForExhibit.executeGenerateAPI();
    }
}
