package com.example.news.support;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.news.MainActivity;
import com.example.news.data.ConstantValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class NewsCrawler extends Thread {
    private static final String LOG_TAG =
            NewsCrawler.class.getSimpleName();
    private ArrayList<JSONObject> newsResp = new ArrayList<>();
    private ConstantValues.NetWorkStatus netWorkStatus = ConstantValues.NetWorkStatus.NORMAL;
    private CrawlerInfo crawlerInfo;

    private void parse(String response) throws JSONException {
        JSONTokener jsonTokener = new JSONTokener(response);
        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            newsResp.add(data.getJSONObject(i));
        }
    }
    public static class CrawlerInfo {
        String keyWords;
        String startTime;
        String endTime;
        String category;
        public CrawlerInfo(String keyWords, String startTime, String endTime, String category) {
            this.keyWords = keyWords;
            this.startTime = startTime;
            this.endTime = endTime;
            this.category = category;
        }
    }

    public NewsCrawler(CrawlerInfo info) {
        crawlerInfo = info;
    }

    @Override
    public void run() {
        StringBuilder response = new StringBuilder();
        try {
            String urlStr = "https://api2.newsminer.net/svc/news/queryNewsList?" +
                    "words=" + crawlerInfo.keyWords + "&" +
                    "categories=" + crawlerInfo.category + "&" +
                    "startDate=" + crawlerInfo.startTime + "&" +
                    "endDate=" + crawlerInfo.endTime;
            Log.d("Crawler", urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setConnectTimeout(3000);
            httpUrlConn.setReadTimeout(3000);
            InputStream input = httpUrlConn.getInputStream();
            InputStreamReader read = new InputStreamReader(input, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(read);
            String data = br.readLine();
            while(data!=null)  {
                response.append(data);
                data=br.readLine();
            }
            br.close();
            read.close();
            input.close();
            httpUrlConn.disconnect();
            parse(response.toString());
            netWorkStatus = ConstantValues.NetWorkStatus.NORMAL;
        } catch (MalformedURLException e) {
//            e.printStackTrace();
            netWorkStatus = ConstantValues.NetWorkStatus.ERROR;
        } catch (IOException e) {
//            e.printStackTrace();
            netWorkStatus = ConstantValues.NetWorkStatus.ERROR;
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d(LOG_TAG, "getNews: " + response.toString());
    }

    public ArrayList<JSONObject> getNewsResp() {
        return newsResp;
    }

    public ConstantValues.NetWorkStatus getNetWorkStatus() {
        return netWorkStatus;
    }
}
