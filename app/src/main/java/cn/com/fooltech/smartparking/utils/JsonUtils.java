package cn.com.fooltech.smartparking.utils;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by YY on 2016/7/4.
 */
public class JsonUtils {

    /**
     * 解析返回map
     * @param jsonStr
     * @return
     */
    public static Map<String,Object> jsonToMap(String jsonStr){
        Map<String,Object> map = null;
        if(jsonStr.isEmpty()) return null;
        try {
            map = new HashMap<String,Object>();
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()){
                String key = (String) keys.next();
                Object obj = jsonObject.get(key);
                map.put(key, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Object jsonToObject(String jsonStr, Class cls){
        Object object = null;
        try {
            object = JSON.parseObject(jsonStr, cls);
            return object;
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 返回code码
     * @param json
     * @return
     */
    public static int getCodeResult(String json){
         int code = -1;
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static Map<String,Object> json2Map(JSONObject object){
        Map<String,Object> map = null;
        if(object == null) return map;
        try {
            map = new HashMap<String,Object>();
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = object.get(key);
                map.put(key,value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
