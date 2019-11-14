package InputMatch;

import FileUtil.MyFileWriter;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartMatch {
    private static List<String> inputPath,eventPath;
    public static void main(String []args){
        MatchInputEvent matchInputEvent = new MatchInputEvent();
        init();
        JSONObject json = matchInputEvent.matchInputAndEvent(inputPath,eventPath);
        String relationShipMapPath = "C:/Users/17916/Desktop/anki/rel.txt";
        MyFileWriter myFileWriter = new MyFileWriter(relationShipMapPath);
        myFileWriter.write(json.toJSONString());
    }
    private static void init(){
        inputPath = new ArrayList<>();
        eventPath = new ArrayList<>();
        inputPath.add("C:/Users/17916/Desktop/anki/json1.txt");
        inputPath.add("C:/Users/17916/Desktop/anki/json2.txt");
        inputPath.add("C:/Users/17916/Desktop/anki/json3.txt");
        inputPath.add("C:/Users/17916/Desktop/anki/json4.txt");
        inputPath.add("C:/Users/17916/Desktop/anki/json1-1.txt");

        eventPath.add("C:/Users/17916/Desktop/anki/methodLog-1.txt");
        eventPath.add("C:/Users/17916/Desktop/anki/methodLog-2.txt");
        eventPath.add("C:/Users/17916/Desktop/anki/methodLog-3.txt");
        eventPath.add("C:/Users/17916/Desktop/anki/methodLog-4.txt");
        eventPath.add("C:/Users/17916/Desktop/anki/methodLog-1-1.txt");
    }
}
