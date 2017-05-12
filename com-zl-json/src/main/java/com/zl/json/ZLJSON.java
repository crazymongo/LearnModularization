package com.zl.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by PF-07GLA9 on 2017/3/3.
 */

public class ZLJSON {

    /**
     * @param value
     * @return
     */
    public static <T> JSONObject parseObjectOnNetworkReponse(String value, Class<T> tClass) {
        return parseObjectOnNetworkReponse(value, tClass, EmptyCallback);
    }


//    public static <T> T parseObjectOnNetworkReponse(String value, Class<T> tClass, Callback callback) {
//        T t = null;
//        if ((value == null) || (value.length() == 0)) {
//            return t;
//        }
//
//        if (callback == null) {
//            callback = EmptyCallback;
//        }
//
//        JSONObject jsonObject = JSON.parseObject(value);
//        if (jsonObject.getIntValue("ret") == 1) {//成功
//            t = JSON.parseObject(jsonObject.getString("result"), tClass);
//        } else {
//            String message = jsonObject.getString("msg");
//        }
//        return t;
//
//
//    }


    public static <T> JSONObject parseObjectOnNetworkReponse(String value, Class<T> tClass, Callback callback) {
        if ((value == null) || (value.length() == 0)) {
            return null;
        }

        if (callback == null) {
            callback = EmptyCallback;
        }

        JSONObject jsonObject = JSON.parseObject(value);

        if (jsonObject != null) {
            handleNetworkReponse(jsonObject, tClass, callback);
        } else {
            callback.onFailure("JSON解析失败");
        }
        return jsonObject;
    }


    /**
     * {"ret":1,"result":{".....}}
     * {"ret":1,"msg":"message"}
     *
     * @param jsonObject
     */
    private static <T> void handleNetworkReponse(JSONObject jsonObject, Class<T> tClass, Callback<T> callback) {
        if (jsonObject.getIntValue("ret") == 1) {//成功
            T t = JSON.parseObject(jsonObject.getString("result"), tClass);
            callback.onSuccess(t);
        } else {
            String message = jsonObject.getString("msg");
            callback.onFailure(message);
        }
    }

    public interface Callback<T> {
        void onSuccess(T t);

        void onFailure(String errorMsg);
    }

    public static Callback EmptyCallback = new Callback() {

        @Override
        public void onSuccess(Object o) {

        }

        @Override
        public void onFailure(String errorMsg) {

        }
    };

}
