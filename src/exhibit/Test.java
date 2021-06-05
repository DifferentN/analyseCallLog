package exhibit;

import FileUtil.MyFileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Set;

public class Test {
    public static void main(String args[]){
        GenerateAPIForExhibit generateAPIForExhibit = new GenerateAPIForExhibit();
        String path = "C:\\Users\\17916\\Desktop\\APIGenerate\\musicPlayer\\searchAlbum/methodLog1.txt";//C:/Users/17916/Desktop/testVideo/
        String userInput = "album:李荣浩/newContent:11a";//Front:猫/Back:cat
        String userOutput = null;
        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
        path = "C:\\Users\\17916\\Desktop\\APIGenerate\\musicPlayer\\searchAlbum/methodLog2.txt";//D:/APIGenerateExperiment/3APP/shipudaquan/searchfood/
        userInput = "album:薛之谦/newContent:22b";
        userOutput = null;
        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
        path = "C:\\Users\\17916\\Desktop\\APIGenerate\\musicPlayer\\searchAlbum/methodLog3.txt";//D:/APIGenerateExperiment/3APP/anki/addItem/
        userInput = "album:彩虹/newContent:33c";
        userOutput = "";
        generateAPIForExhibit.addInstanceInfo(path,userInput,userOutput);
        generateAPIForExhibit.executeGenerateAPI();
    }
}
