package test;

import FileUtil.MyFileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class transformMethodSimilarity {
    public static void main(String args[]){
        MyFileUtil myFileUtil = new MyFileUtil("methodSimilarity.txt");
        JSONArray jsonArray = MyFileUtil.readJSONArray("C:/Users/17916/Desktop/method-2.txt");
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Set<String> keySet = jsonObject.keySet();
            Iterator<String> iterator = keySet.iterator();
            String key = iterator.next();
            String value = jsonObject.getString(key);
            String strs[] = key.split("<->");
            JSONObject newJson = new JSONObject();
            newJson.put("methodA",strs[0]);
            newJson.put("methodB",strs[1]);
            newJson.put("similarity",value);
            String line = newJson.toJSONString();
            myFileUtil.write(line);
        }
        myFileUtil.close();
    }
}
