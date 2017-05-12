package com.zl.network;

/**
 * Created by PF-07GLA9 on 2017/4/7.
 */

public interface HttpCallback<T> {

    void onSuccess(T t);

    void onFailure(HttpException e);
}
