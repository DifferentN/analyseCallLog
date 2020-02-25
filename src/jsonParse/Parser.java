package jsonParse;

import com.alibaba.fastjson.JSONObject;

public interface Parser<T> {
    public JSONObject parseToJSON(T t);
}
