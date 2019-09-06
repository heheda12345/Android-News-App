package com.example.news.data;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.example.news.collection.CollectionViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserConfig {

    public static class Section {
        private String mSectionName;
        private int mPosition;
        Section(String sectionName, int position) {
            mSectionName = sectionName;
            mPosition = position;
        }

        public String getSectionName() {
            return mSectionName;
        }

        public int getPosition() {
            return mPosition;
        }
    }

    public static class TTS { // 语音合成的配置
        private int voicer = 0;

        int getVoicer() {
            return voicer;
        }

        void setVoicer(int voicer) {
            this.voicer = voicer;
        }
    }

    /* Index of this list <-> Position at the tabs*/
    private List<Integer> selectSectionsIndices;
    private List<Integer> unselectedSectionsIndices;
    private TTS tts;
    private boolean textMode = false;
    private boolean nightMode = false;
    private boolean commentMode = true;

    private List<String> searchHistory;

    private HashMap<String, Double> keyWordsSet;


    private static UserConfig instance = new UserConfig();

    private UserConfig() {
        selectSectionsIndices = new ArrayList<>();
        unselectedSectionsIndices = new ArrayList<>();
        searchHistory = new ArrayList<>();
        /* Hard Code for Debug */
        searchHistory.add("Hello World");
        searchHistory.add("清华大学");
        searchHistory.add("特朗普");
        keyWordsSet = new HashMap<>();
        keyWordsSet.put("新时代", 1.0);
        for (int i = 0; i < ConstantValues.ALL_SECTIONS.length; i += 2) {
            selectSectionsIndices.add(i);
        }
        for (int i = 1; i < ConstantValues.ALL_SECTIONS.length; i += 2) {
            unselectedSectionsIndices.add(i);
        }
        tts = new TTS();
    }

    public static UserConfig getInstance() {
        return instance;
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
        saveTodb();
    }

    public boolean getNightMode() {
        return nightMode;
    }

    /**
     * 搜索的历史纪录
     * */

    public void addSearchHistory(String history) {
        if (!searchHistory.contains(history)) {
            searchHistory.add(history);
            saveTodb();
        }
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }


    /**
     *  Section 获取、添加、删除
     * */

    public Section getSection(int position) {
        int sectionIndex = selectSectionsIndices.get(position);
        return new Section(ConstantValues.ALL_SECTIONS[sectionIndex], position);
    }

    public Section getUnSection(int position) {
        int sectionIndex = unselectedSectionsIndices.get(position);
        return new Section(ConstantValues.ALL_SECTIONS[sectionIndex], position);
    }

    public int getSectionNum() {
        return selectSectionsIndices.size();
    }

    public int getUnSectionNum() { return unselectedSectionsIndices.size(); }

    public void addSection(int pos) {
        int id = unselectedSectionsIndices.get(pos);
        unselectedSectionsIndices.remove(pos);
        selectSectionsIndices.add(id);
        saveTodb();
    }

    public void removeSection(int pos) {
        int id = selectSectionsIndices.get(pos);
        selectSectionsIndices.remove(pos);
        unselectedSectionsIndices.add(id);
        saveTodb();
    }

    public int getTTSVoicer() { return tts.getVoicer(); }
    public void setTTSVoicer(int voicer) {
        tts.setVoicer(voicer);
        saveTodb();
    }

    public boolean isTextMode() {
        return textMode;
    }

    public void setTextMode(boolean textMode) {
        this.textMode = textMode;
        saveTodb();
    }

    public static String getHostName() {
        return "101.6.5.200";
    }
    public static int getHostPort() {return 15565;}

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE));
//        boolean ret = connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private String userName = "";
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        saveTodb();
    }

    public boolean isLogin() {
        return userName.length() > 0;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context, CollectionViewModel database) {
        this.context = context;
        db = database;
        loadFromdb();
    }

    private Context context;

    public List<Section> getAllSelectSections() {
        List<Section> sL = new ArrayList<>();
        for (int position = 0; position < selectSectionsIndices.size(); ++position) {
            sL.add(new Section(ConstantValues.ALL_SECTIONS[selectSectionsIndices.get(position)], position));
        }
        return sL;
    }

    public List<Section> getAllUnselectedSections() {
        List<Section> sL = new ArrayList<>();
        for (int position = 0; position < unselectedSectionsIndices.size(); ++position) {
            sL.add(new Section(ConstantValues.ALL_SECTIONS[unselectedSectionsIndices.get(position)], position));
        }
        return sL;
    }


    /**
     * 关键词操作
     * */
    public void addKeyWords(String keyWord, double score) {
        keyWordsSet.put(keyWord, score);
        saveTodb();
    }

    public List<Map.Entry<String, Double>> getKeyWords(int num) {
        List<Map.Entry<String, Double>>keyWordsLists = new ArrayList<>(keyWordsSet.entrySet());
        Collections.sort(keyWordsLists, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                double q1 = o1.getValue();
                double q2 = o2.getValue();
                double p = q2 - q1;
                if(p > 0){
                    return 1;
                }
                else if(p == 0){
                    return 0;
                }
                else
                    return -1;
            }
        });

        if (num < 0) {
            return keyWordsLists;
        }
        else {
            return keyWordsLists.subList(0, Math.min(num, keyWordsLists.size()));
        }
    }

    public boolean isCommentMode() {
        return commentMode;
    }

    public void setCommentMode(boolean commentMode) {
        this.commentMode = commentMode;
        saveTodb();
    }

    private CollectionViewModel db;

    public String toJson() {
        try {
            JSONObject js = new JSONObject();

            JSONArray selected = new JSONArray();
            for (Integer i: selectSectionsIndices) {
                selected.put(i.intValue());
            }
            js.put("selected", selected);

            JSONArray unselected = new JSONArray();
            for (Integer i: unselectedSectionsIndices) {
                unselected.put(i.intValue());
            }
            js.put("unselected", unselected);

            js.put("tts", tts.getVoicer());
            js.put("textMode", textMode);
            js.put("nightMode", nightMode);
            js.put("commentMode", commentMode);

            JSONArray sh = new JSONArray();
            for (String s: searchHistory)
                sh.put(s);
            js.put("searchHistory", sh);

            JSONObject keywords = new JSONObject();
            for (Map.Entry<String, Double> x: keyWordsSet.entrySet()) {
                keywords.put(x.getKey(), x.getValue());
            }
            js.put("keywords", keywords);

            js.put("userName", userName);
            return js.toString();
        } catch (Exception e) {
            return (new JSONObject()).toString();
        }
    }

    public void parseJson(String jsonString) { // 这里头不要用set*
        Log.d("UserConfig", "loaded: " + jsonString);
        if (jsonString == null)
            return;
        try {
            JSONObject obj = new JSONObject(jsonString);

            List<Integer> selected = new ArrayList<>();
            JSONArray js_selected = obj.getJSONArray("selected");
            for (int i=0; i < js_selected.length(); i++)
                selected.add(js_selected.getInt(i));
            selectSectionsIndices = selected;

            List<Integer> unselected = new ArrayList<>();
            JSONArray js_unselected = obj.getJSONArray("unselected");
            for (int i=0; i<js_unselected.length(); i++)
                unselected.add(js_unselected.getInt(i));
            unselectedSectionsIndices = unselected;

            tts.voicer = obj.getInt("tts");
            textMode = obj.getBoolean("textMode");
            nightMode = obj.getBoolean("nightMode");
            commentMode = obj.getBoolean("commentMode");

            List<String> history = new ArrayList<>();
            JSONArray js_history = obj.getJSONArray("searchHistory");
            for (int i=0; i<js_history.length(); i++) {
                history.add(js_history.getString(i));
            }
            searchHistory = history;

            HashMap<String, Double> keywords = new HashMap<>();
            JSONObject js_keywords = obj.getJSONObject("keywords");
            Iterator iterator = js_keywords.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                Double value = js_keywords.getDouble(key);
                keywords.put(key, value);
            }
            keyWordsSet = keywords;

            userName = obj.getString("userName");
        } catch (Exception e) {
            Log.e("UserConfig", e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveTodb() {
        String saved = toJson();
        Log.d("UserConfig", "saved: "+saved);
        db.updateSetting(toJson());
    }

    public void loadFromdb() {
        parseJson(db.getSetting());
    }

}
