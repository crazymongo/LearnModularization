package com.zl.network;

import java.io.IOException;

/**
 * Created by PF-07GLA9 on 2017/4/13.
 */

public class HttpException extends IOException {

    public int code;
    public String detail;

    public HttpException(int errorCode){
        this(errorCode, null);
    }

    public HttpException(int errorCode, String detail){
        code=errorCode;
        this.detail=detail;
    }

}
