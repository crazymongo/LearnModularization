package com.zl.behaviorstat;

import android.content.Context;
import android.content.SharedPreferences;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by PF-07GLA9 on 2017/2/17.
 */

public class AppMobclickAgent {

    private static AtomicBoolean abandoned;
    private static String customerName;

    public static void onEvent(Context context, String eventName, Map<String, String> params) {

        if (filter(context)) {
            return;
        }

        params.put("corpName", customerName);
        MobclickAgent.onEvent(context, eventName, params);
    }

    public static void onResume(Context context) {
        if (filter(context)) {
            return;
        }
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context) {
        if (filter(context)) {
            return;
        }
        MobclickAgent.onPause(context);
    }

    private static boolean filter(Context context) {

        if (abandoned == null) {
            SharedPreferences sp = context.getSharedPreferences("qxapp", Context.MODE_PRIVATE);
            String result = sp.getString("userVO", null);
            if ((result == null) || (result.length() == 0)) {
                return false;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);

                if ("C0000000958".equals(jsonObject.getString("customerId"))) {
                    abandoned = new AtomicBoolean(true);
                } else {
                    abandoned = new AtomicBoolean(false);
                }

                customerName = jsonObject.getString("customerName");

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return abandoned.get();
    }
}
