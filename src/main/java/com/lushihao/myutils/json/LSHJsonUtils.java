package com.lushihao.myutils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

public final class LSHJsonUtils {

    /**
     * 字符串转JSONObject
     *
     * @param str
     * @return
     */
    public static JSONObject string2JsonObj(String str) {
        return JSONObject.parseObject(str);
    }

    /**
     * 字符串转JSONArray
     *
     * @param str
     * @return
     */
    public static JSONArray string2JsonArr(String str) {
        return JSONArray.parseArray(str);
    }

    /**
     * 功能描述：把java对象转换成JSON数据
     *
     * @param object java对象
     * @return JSON数据
     */
    public static String bean2Json(Object object) {
        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return 指定的java对象
     */
    public static <T> T json2Bean(String jsonData, Class<T> clazz) {
        return JSON.parseObject(jsonData, clazz);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return 指定的java对象
     */
    public static <T> T json2Bean(JSON jsonData, Class<T> clazz) {
        return JSON.parseObject(jsonData.toJSONString(), clazz);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return List<T>
     */
    public static <T> List<T> json2List(String jsonData, Class<T> clazz) {
        return JSON.parseArray(jsonData, clazz);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     *
     * @param jsonData JSON数据
     * @param clazz    指定的java对象
     * @return List<T>
     */
    public static <T> List<T> json2List(JSON jsonData, Class<T> clazz) {
        return JSON.parseArray(jsonData.toJSONString(), clazz);
    }

    /**
     * 功能描述：把JSON数据转换成较为复杂的List<Map<String, Object>>
     *
     * @param jsonData JSON数据
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> json2ListMap(String jsonData) {
        return JSON.parseObject(
                jsonData, new TypeReference<List<Map<String, Object>>>() {
                }
        );
    }

    /**
     * 功能描述：把JSON数据转换成较为复杂的List<Map<String, Object>>
     *
     * @param jsonData JSON数据
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> json2ListMap(JSON jsonData) {
        return JSON.parseObject(
                jsonData.toJSONString(), new TypeReference<List<Map<String, Object>>>() {
                }
        );
    }
}