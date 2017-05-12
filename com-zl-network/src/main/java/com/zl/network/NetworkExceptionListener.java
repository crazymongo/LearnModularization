package com.zl.network;

import okhttp3.Call;

/**
 * Created by PF-07GLA9 on 2017/4/18.
 */

public interface NetworkExceptionListener {
    <T> void onException(int code, Call call, HttpCallback<T> callback, Class<T> tClass);
}
