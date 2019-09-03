package com.example.news.support;

import android.util.Log;

import com.example.news.data.ConstantValues;
import com.example.news.data.UserConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NewsCrawler extends Thread {
//    private static final String LOG_TAG = NewsCrawler.class.getSimpleName();
    private ArrayList<JSONObject> newsResp = new ArrayList<>();
    private ConstantValues.NetWorkStatus netWorkStatus = ConstantValues.NetWorkStatus.NORMAL;
    private CrawlerInfo crawlerInfo;

    private void parse(String response) throws JSONException {
        JSONTokener jsonTokener = new JSONTokener(response);
        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            if (!isDuplicate(data.getJSONObject(i))) {
                newsResp.add(data.getJSONObject(i));
            }
        }
    }

    private boolean isDuplicate(JSONObject obj) {
        for (JSONObject newsObj : newsResp) {
            try {
                if (newsObj.getString("newsID").equals(obj.getString("newsID")) ||
                        newsObj.getString("title").equals(obj.getString("title")) ||
                        newsObj.getString("image").equals(obj.getString("image"))
                ) {
                    return true;
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static class CrawlerInfo {
        String keyWords;
        String startTime;
        String endTime;
        String category;
        int size;
        public CrawlerInfo(String keyWords, String startTime, String endTime, String category, int size) {
            this.keyWords = keyWords;
            this.startTime = startTime;
            this.endTime = endTime;
            this.category = category;
            this.size = size;
        }
    }

    public NewsCrawler(CrawlerInfo info) {
        crawlerInfo = info;
    }

    private void getNews(String keyWord, String category, int size) {
        StringBuilder response = new StringBuilder();
        try {
            String urlStr = "https://api2.newsminer.net/svc/news/queryNewsList?" +
                    "words=" + keyWord + "&" +
                    "categories=" + category + "&" +
                    "startDate=" + crawlerInfo.startTime + "&" +
                    "endDate=" + crawlerInfo.endTime + "&" +
                    "size=" + size;
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
            netWorkStatus = ConstantValues.NetWorkStatus.ERROR;
        } catch (IOException e) {
            netWorkStatus = ConstantValues.NetWorkStatus.ERROR;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSuggestNews() {
        int allNum = ConstantValues.DEFAULT_SUGGEST_TOP;
        List<Map.Entry<String, Double>> keyWords = UserConfig.getInstance().getKeyWords(allNum);
        double allScore = 0;
        for (Map.Entry<String, Double> entry : keyWords) {
            allScore += entry.getValue();
        }
        List<Integer> newsNum = new ArrayList<>();
        for (Map.Entry<String, Double> entry : keyWords) {
            int num = (int)((ConstantValues.DEFAULT_NEWS_SIZE + 1) * entry.getValue() / allScore);
            newsNum.add(num);
        }

        for (int i = 0; i < newsNum.size(); ++i) {
            if (newsNum.get(i) > 0) {
                getNews(keyWords.get(i).getKey(), "", newsNum.get(i));
            }
        }
    }

    @Override
    public void run() {
        if (crawlerInfo.category.equals("推荐")) {
            getSuggestNews();
        }
        else {
            getNews(crawlerInfo.keyWords, crawlerInfo.category, crawlerInfo.size);
        }
    }

    public ArrayList<JSONObject> getNewsResp() {
        return newsResp;
    }

    public ConstantValues.NetWorkStatus getNetWorkStatus() {
        return netWorkStatus;
    }
}
