package com.example.news.support;

import android.content.Context;
import android.util.Log;

import com.example.news.data.UserConfig;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class ServerInteraction {
    private static final String LOG_TAG =
            ServerInteraction.class.getSimpleName();
    private ServerInteraction() {}
    private static ServerInteraction instance = new ServerInteraction();
    public static ServerInteraction getInstance() {
        return instance;
    }

    private String name = "";
    public enum ResultCode {
        success, unknownError, wrongUserNameorPassWord, nameUsed, noSuchUser
    }
    ResultCode result;

    public ResultCode login(String name, String passwd) {
        synchronized (this) {
            RequestParams params = new RequestParams();
            params.add("name", name);
            params.add("passwd", passwd);
            result = ResultCode.unknownError;
            HostClient.post("login", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    result = ResultCode.success;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    switch (statusCode) {
                        case 401:
                            result = ResultCode.wrongUserNameorPassWord;
                            break;
                        default:
                            result = ResultCode.unknownError;
                    }
                }
            });
            Log.d(LOG_TAG, String.format("Login %s %s: %s", name, passwd, result.toString()));
            return result;
        }
    }

    public ResultCode register(String name, String passwd) {
        synchronized (this) {
            RequestParams params = new RequestParams();
            params.add("name", name);
            params.add("passwd", passwd);
            result = ResultCode.unknownError;
            HostClient.post("register", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    result = ResultCode.success;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    switch (statusCode) {
                        case 401:
                            result = ResultCode.nameUsed;
                            break;
                        default:
                            result = ResultCode.unknownError;
                            break;
                    }
                }
            });
            Log.d(LOG_TAG, String.format("Register %s %s: %s", name, passwd, result.toString()));
            return result;
        }
    }

    public ResultCode logout() {
        name = "";
        return ResultCode.success;
    }

    public ResultCode uploadIcon(File f, String name) {
        synchronized (this) {
            RequestParams params = new RequestParams();
            try {
                params.put("icon", f);
                params.put("name", name);
            } catch(Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                return ResultCode.unknownError;
            }
            result = ResultCode.unknownError;
            HostClient.post("uploadIcon", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    result = ResultCode.success;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(LOG_TAG + "uploadIcon", String.valueOf(statusCode));
                    switch (statusCode) {
                        case 401:
                            result = ResultCode.noSuchUser;
                            break;
                        default:
                            result = ResultCode.unknownError;
                            break;
                    }
                }

            });
            return result;
        }
    }
    public File getIcon(String name, boolean large, Context ctx) {
        synchronized (this) {
            RequestParams params = new RequestParams();
            params.put("name", name);
            params.put("size", large ? "large" :"small" );
            downloaded = null;
            HostClient.get("getIcon", params, new FileAsyncHttpResponseHandler(ctx) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    Log.e(LOG_TAG + "getIcon", String.valueOf(statusCode));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    downloaded = file;
                }
            });
            return downloaded;
        }
    }

    public ResultCode postComment(String name, String newsID, String text) {
        synchronized (this) {
            RequestParams params = new RequestParams();
            params.add("name", name);
            params.add("newsID", newsID);
            params.add("text", text);
            result = ResultCode.unknownError;
            HostClient.post("postComment", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    result = ResultCode.success;
                }
            });
            return result;
        }
    }

    public JSONArray getComment(String newsID) {
        synchronized (this) {
            RequestParams params = new RequestParams();
            params.put("newsID", newsID);
            comments = new JSONArray();
            HostClient.get("getComment", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    result = ResultCode.success;
                    Log.d(LOG_TAG, response.toString());
                    try {
                        comments = response.getJSONArray("msg");
                    } catch (JSONException e) {}
                }
            });
            return comments;
        }
    }

    File downloaded;
    JSONArray comments;
}