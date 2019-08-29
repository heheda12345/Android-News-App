package com.example.news.data;

public class ConstantValues {
    final static public String[] ALL_SECTIONS = {
            "推荐",
            "娱乐",
            "军事",
            "教育",
            "文化",
            "健康",
            "财经",
            "体育",
            "汽车",
            "科技",
            "社会"
    };

    final static public int[] IMAGE_NUM = {0, 1, 1, 3};

    public enum ItemViewType {
        NONE, ONE_BIG, ONE_SMALL, THREE, FOOTER
    }

    public enum NetWorkStatus {
        NORMAL, ERROR
    }

    public static int DEFAULT_NEWS_SIZE = 15;
}
