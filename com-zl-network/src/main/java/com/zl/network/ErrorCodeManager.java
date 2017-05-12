package com.zl.network;

import android.util.SparseArray;

/**
 * Created by PF-07GLA9 on 2017/4/13.
 */

public class ErrorCodeManager {

    public static final int RESERVED_ERROR_CODE=999;
    public static final int UNKNOW_REASON_ERROR_CODE=1000;
    public static final int NETWORK_COMMUNICATION_EXCEPTION_ERROR_CODE=1001;
    public static final int JSON_STRING_EXCEPTION_ERROR_CODE=1002;
    public static final int EXTERNAL_STORAGE_EXCEPTION_ERROR_CODE=1003;

    public static SparseArray<String> detailMap;

    static {
        detailMap=new SparseArray<>(2);
        detailMap.append(UNKNOW_REASON_ERROR_CODE, "未知原因");
        detailMap.append(NETWORK_COMMUNICATION_EXCEPTION_ERROR_CODE, "网络通信异常");
        detailMap.append(JSON_STRING_EXCEPTION_ERROR_CODE, "Json字符串异常");
        detailMap.append(EXTERNAL_STORAGE_EXCEPTION_ERROR_CODE, "外部存储不可用");
    }

    public static String getErrorCodeDescription(int errorCode){
        return getErrorCodeDescription(errorCode, false);
    }

    public static String getErrorCodeDescription(int errorCode, boolean nullable){
        String des=detailMap.get(errorCode);

        return des==null?(nullable?null:"未知错误码"):des;
    }

}
