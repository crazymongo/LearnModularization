package com.zl.network;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by PF-07GLA9 on 2017/4/7.
 */

public class NetworkUtil {

    private static final String Tag = NetworkUtil.class.getSimpleName();

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private static String token = "";

    private static NetworkExceptionListener mNetworkExceptionListener;

    public static OkHttpClient getHttpClient() {
        return getHttpClient(null);
    }

    public static OkHttpClient getHttpClient(InputStream certificate) {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.connectTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                    .writeTimeout(5 * 1000, TimeUnit.MILLISECONDS)
                    .readTimeout(5 * 1000, TimeUnit.MILLISECONDS);

            if (certificate != null) {
                setCertificate(builder, certificate);
            }

            client = builder.build();
        }
        return client;
    }

    public static void setNetworkExceptionListener(NetworkExceptionListener listener) {
        mNetworkExceptionListener = listener;
    }

    private static void setCertificate(OkHttpClient.Builder builder, InputStream inputStream) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("crazymongo", certificateFactory.generateCertificate(inputStream));

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0]);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory socketFactory = null;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }

        if (sslContext == null) {
            try {
                sslContext = SSLContext.getDefault();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        if (sslContext != null) {
            try {
                sslContext.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                socketFactory = sslContext.getSocketFactory();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

        }

        return socketFactory;

    }

    public static void setToken(String tk) {
        token = tk;
    }

    public static String getToken() {
        return token;
    }

    private static Request.Builder getRequestBuilder() {
        Request.Builder builder = new Request.Builder();
        if (token != null) {
            builder.addHeader("tk", token);
        }

        return builder;
    }

    public static <T> void get(String url, Map<String, Object> params, HttpCallback<T> callback, Class<T> tClass) {
        if ((url == null) || url.length() <= 0) {
            return;
        }

        if (params != null && params.size() >= 1) {
            StringBuilder stringBuilder = new StringBuilder();
            Set<String> keys = params.keySet();
            Iterator<String> iterator = keys.iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                stringBuilder.append("&");
                stringBuilder.append(key);
                stringBuilder.append("=");
                stringBuilder.append(params.get(key));
            }
            url = url + stringBuilder.toString();
        }

        get(url, callback, tClass);
    }

    public static <T> void get(String url, HttpCallback<T> callback) {
        if ((url == null) || url.length() <= 0) {
            return;
        }

        Request.Builder builder = getRequestBuilder();
        builder.url(url);

        get(url, callback, null);
    }

    public static <T> void get(String url, HttpCallback<T> callback, Class<T> tClass) {
        if ((url == null) || url.length() <= 0) {
            return;
        }

        Log.e(Tag, "get request: url=" + url);

        Request.Builder builder = getRequestBuilder();
        builder.url(url);

        enqueue(getHttpClient().newCall(builder.build()), callback, tClass);
    }

    public static <T> void postInForm(String url, Map<String, Object> params, HttpCallback callback, Class<T> tClass) {
        if ((url == null) || (url.length() <= 0)) {
            return;
        }

        FormBody.Builder formBodyBuidler = new FormBody.Builder();

        for (Map.Entry entry : params.entrySet()) {
            if (entry.getValue() instanceof Map) {
                formBodyBuidler.add(entry.getKey().toString(), new JSONObject((Map<String, Object>) entry.getValue()).toJSONString());
            } else {
                formBodyBuidler.add(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        Request.Builder builder = getRequestBuilder();
        builder.url(url).post(formBodyBuidler.build());
        Log.e(Tag, "postInForm : url=" + url);
        if (callback == null) {
            callback = EMPTY_HTTP_CALLBACK;
        }

        enqueue(getHttpClient().newCall(builder.build()), callback, tClass);
    }

    public static <T> void postJson(String url, Map<String, Object> params, HttpCallback callback, Class<T> tClass) {
        if ((url == null) || (url.length() <= 0)) {
            return;
        }

        postJson(url, new JSONObject(params).toJSONString(), callback, tClass);
    }

    public static <T> void postJson(final HttpCallback callback, Class<T> tClass) {

        if ((tClass.isAssignableFrom(List.class) || !(callback instanceof ListHttpCallback)) || (!tClass.isAssignableFrom(List.class) || (callback instanceof ListHttpCallback))) {
            throw new IllegalArgumentException("ListHttpCallback/HttpCallback接口使用有误");
        }

        String content = "[{\"id\":\"12\", \"name\":\"maizi\"},{\"id\":\"123\", \"name\":\"crazy\"}]";
        parseObject(content, callback, tClass);

    }

    public static <T> void postJsonWithToken(String url, Map<String, Object> params, HttpCallback callback, Class<T> tClass) {
        if ((url == null) || (url.length() <= 0)) {
            return;
        }

        JSONObject json = new JSONObject();
        json.put("accessToken", token);
        json.put("content", new JSONObject(params));
        postJson(url, json.toJSONString(), callback, tClass);
    }

    private static <T> void postJson(String url, String jsonStr, HttpCallback callback, Class<T> tClass) {

        Log.e(Tag, "postJson: url=" + url + "; jsonStr=" + jsonStr);

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonStr);

        Request.Builder builder = getRequestBuilder();
        builder.url(url).post(requestBody);

        if (callback == null) {
            callback = EMPTY_HTTP_CALLBACK;
        }

        enqueue(getHttpClient().newCall(builder.build()), callback, tClass);
    }

    public static <T> void retryCall(Call call, HttpCallback callback, Class<T> tClass) {
        enqueue(call, callback, tClass);
    }

    private static <T> void enqueue(final Call call, final HttpCallback callback, final Class<T> tClass) {
        if ((tClass.isAssignableFrom(List.class) || !(callback instanceof ListHttpCallback)) || (!tClass.isAssignableFrom(List.class) || (callback instanceof ListHttpCallback))) {
            throw new IllegalArgumentException("ListHttpCallback/HttpCallback接口使用有误");
        }
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {

                if (e instanceof HttpException) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure((HttpException) e);
                        }
                    });
                } else {
                    Log.e(Tag, "onFailure--IOException: " + e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(new HttpException(ErrorCodeManager.NETWORK_COMMUNICATION_EXCEPTION_ERROR_CODE));
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {

                    if ((tClass == null) || tClass.isAssignableFrom(String.class)) {
                        final String result = response.body().string();
                        Log.e(Tag, "onResponse--response: " + response.toString() + "; body=" + result);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                callback.onSuccess(result);
                            }
                        });
                    } else {
                        if (tClass.isAssignableFrom(File.class)) {
                            getFile(response, callback);
                        } else {
                            if (callback instanceof ListHttpCallback) {

                            } else {
                                parseObject(call, response, callback, tClass);
                            }

                        }
                    }

                } else {
                    if (mNetworkExceptionListener != null) {
                        mNetworkExceptionListener.onException(response.code(), call, callback, tClass);
                    }
                }

            }
        });
    }

    private static <T> void parseObject(Call call, Response response, final HttpCallback callback, final Class<T> tClass) throws IOException {
        String result = response.body().string();
        Log.e(Tag, "onResponse--response: " + response.toString() + "; body=" + result);

        if (!TextUtils.isEmpty(result)) {

            String url = call.request().url().toString();

            try {
                JSONObject jsonObject = JSON.parseObject(result);

                if (url.contains("@")) {
                    if (jsonObject.getBoolean("success")) {
                        parseObject(jsonObject.getString("content"), callback, tClass);
                    } else {
                        String errorMsg = jsonObject.getString("message");
                        int code = jsonObject.getIntValue("code");
                        throw new HttpException(code, errorMsg);
                    }
                } else {
                    if (jsonObject.getIntValue("ret") == 1) {
                        parseObject(jsonObject.getString("result"), callback, tClass);
                    } else {
                        String errorMsg = jsonObject.getString("message");
                        throw new HttpException(ErrorCodeManager.RESERVED_ERROR_CODE, errorMsg);
                    }
                }
            } catch (Exception e) {
                if (e instanceof HttpException) {
                    throw e;
                } else {
                    e.printStackTrace();
                    throw new HttpException(ErrorCodeManager.JSON_STRING_EXCEPTION_ERROR_CODE);
                }
            }

        } else {
            throw new HttpException(ErrorCodeManager.JSON_STRING_EXCEPTION_ERROR_CODE);
        }

    }

    private static <T> void parseObject(String content, final HttpCallback callback, final Class<T> tClass) {
        if (callback instanceof ListHttpCallback) {
            final List<T> list = JSON.parseArray(content, tClass);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(list);
                }
            });
        } else {
            final T t = JSON.parseObject(content, tClass);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(t);
                }
            });
        }
    }

    private static void getFile(Response response, final HttpCallback callback) throws IOException {
        InputStream is = response.body().byteStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        File fileCachDir = getFileCachDir();
        if (fileCachDir != null) {
            final File tempFile = new File(fileCachDir, "temp_" + System.currentTimeMillis());
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            while (bis.read(buffer) != -1) {
                fos.write(buffer, 0, buffer.length);
            }

            if (callback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(tempFile);
                    }
                });
            }
        } else {
            if (callback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(new HttpException(ErrorCodeManager.EXTERNAL_STORAGE_EXCEPTION_ERROR_CODE));
                    }
                });
            }
        }

    }

    private static File getFileCachDir() {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory(), "Android/data/com.dcf.qxapp/files/");
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        return file;
    }

    private static HttpCallback<Object> EMPTY_HTTP_CALLBACK = new HttpCallback<Object>() {

        @Override
        public void onSuccess(Object o) {

        }

        @Override
        public void onFailure(HttpException e) {

        }
    };

    private static class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


}
