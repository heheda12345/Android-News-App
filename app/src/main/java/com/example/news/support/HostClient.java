package com.example.news.support;

import com.example.news.data.UserConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public class HostClient {
    private static final String BASE_URL = "http://95.179.200.164/";

    private static AsyncHttpClient client = new SyncHttpClient(5000);

    public static void get(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        if (!UserConfig.isNetworkAvailable()) {
            responseHandler.sendFailureMessage(404, null, null, null);
            return;
        }

        client.setTimeout(3000);
        Thread td = new Thread() {
            @Override
            public void run() {
                client.get(getAbsoluteUrl(url), params, responseHandler);
            }
        };
        td.start();
        try {
            td.join();
        } catch (Exception ignored) {}
    }

    static void post(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
//        client.setConnectTimeout(1000);
//        client.setResponseTimeout(1000);
        if (!UserConfig.isNetworkAvailable()) {
            responseHandler.sendFailureMessage(404, null, null, null);
            return;
        }
        Thread td = new Thread() {
            @Override
            public void run() {
                client.post(getAbsoluteUrl(url), params, responseHandler);
            }
        };
        td.start();
        try {
            td.join();
        } catch (Exception ignored) {}
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
