package com.zl.behaviorstat;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PF-07GLA9 on 2017/2/9.
 */

public class LoginStat extends BaseStat {

    public static void onLogin(Context context, String corpName, String userName) {
        onEventWithAction(context, corpName, userName);
    }

    private static void onEventWithAction(Context context, String corpName, String userName) {
        Map<String, String> params = new HashMap<>(2);
        params.put("corpName", corpName);
        params.put("userName", userName);
        MobclickAgent.onEvent(context, "login", params);
    }
}
