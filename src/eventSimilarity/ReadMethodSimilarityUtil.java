package eventSimilarity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReadMethodSimilarityUtil {
    public static HashMap<String,Float> readMethodSimilarityToHash(String path) {
        HashMap<String,Float> hash = new HashMap<>();
        File file = new File(path);
        if(!file.exists()){
            return hash;
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fileReader==null){
            return hash;
        }
        BufferedReader reader = new BufferedReader(fileReader);
        String line = null;
        StringBuffer buf = new StringBuffer();
        try {
            while( (line = reader.readLine())!=null ){
                buf.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = JSONArray.parseArray(buf.toString());
        JSONObject jsonObject = null;
        Set<String> keys = null;
        for(int i=0;i<jsonArray.size();i++){
            jsonObject = jsonArray.getJSONObject(i);
            keys = jsonObject.keySet();
            for(String key:keys){
                String similarity  = jsonObject.getString(key);
                hash.put(key,Float.valueOf(similarity));
            }
        }
        return hash;
    }
}
