package com.example.news.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

    /* Index of this list <-> Position at the tabs*/
    private List<Integer> selectSectionsIndices;
    private List<Integer> unselectedSectionsIndices;

    private List<String> searchHistory;

    private HashMap<String, Double> keyWordsSet;


    private static UserConfig instance = new UserConfig();

    private UserConfig() {
        selectSectionsIndices = new ArrayList<>();
        unselectedSectionsIndices = new ArrayList<>();
        searchHistory = new ArrayList<>();
        searchHistory.add("Hello World");
        searchHistory.add("清华大学");
        searchHistory.add("特朗普");
        keyWordsSet = new HashMap<String, Double>();
        keyWordsSet.put("新时代", 1.0);
        for (int i = 0; i < ConstantValues.ALL_SECTIONS.length; i += 2) {
            selectSectionsIndices.add(i);
        }
        for (int i = 1; i < ConstantValues.ALL_SECTIONS.length; i += 2) {
            unselectedSectionsIndices.add(i);
        }
    }

    public static UserConfig getInstance() {
        return instance;
    }

    /**
     * 搜索的历史纪录
     * */

    public void addSearchHistory(String history) {
        if (!searchHistory.contains(history)) {
            searchHistory.add(history);
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

    }

    public void removeSection(int pos) {
        int id = selectSectionsIndices.get(pos);
        selectSectionsIndices.remove(pos);
        unselectedSectionsIndices.add(id);
    }

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
}
