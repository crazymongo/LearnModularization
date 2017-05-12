package com.zl.behaviorstat;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PF-07GLA9 on 2016/12/5.
 */

public class BaseStat {

    protected static void onEvent(Context context, String eventName, String itemName, String actionType) {
        Map<String, String> params = new HashMap<>(2);
        params.put("itemName", itemName);
        params.put("actionType", actionType);
        AppMobclickAgent.onEvent(context, eventName, params);
    }

    protected static void onEvent(Context context, String eventName, String itemName) {
        Map<String, String> params = new HashMap<>(1);
        params.put("itemName", itemName);
        AppMobclickAgent.onEvent(context, eventName, params);
    }

}
