package com.example.news;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.news.data.UserConfig;
import com.example.news.ui.main.MainPagerAdapter;
import com.example.news.ui.main.News.NewsListFragment;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.mob.MobSDK;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    private static String LOG_TAG = MainActivity.class.getSimpleName();
    private ArrayAdapter suggestArrayAdapter;
    private ListPopupWindow listPopupWindow;
    private Toolbar toolbar;
    private SearchView searchView;
    private NewsListFragment searchResultFragment;
    private FragmentManager fragmentManager;
    private List<String> searchSuggest;
    private UserConfig userConfig = UserConfig.getInstance();
    private ViewPager mainViewPager;
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;
    private boolean firstCreate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (userConfig.getNightMode()) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        /* Load User Config*/
        searchSuggest = new ArrayList<>();
        searchSuggest.addAll(userConfig.getSearchHistory());

        /* Create Main bottom navigation */
        bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        /* Create Main View Pager */
        mainViewPager = findViewById(R.id.main_viewPager);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager(), 2);
        mainViewPager.setAdapter(mainPagerAdapter);
        mainViewPager.addOnPageChangeListener(this);

        /*科大讯飞语音合成api初始化*/
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5d58fa7c");

        /* Create Tool Bar*/
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        suggestArrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.suggest_item, searchSuggest);

        /* Create Popup Window*/
        listPopupWindow = new ListPopupWindow(MainActivity.this);
        listPopupWindow.setAdapter(suggestArrayAdapter);
        listPopupWindow.setAnchorView(toolbar);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(searchSuggest.get(position), true);
                listPopupWindow.dismiss();
            }
        });

        /* Create Search Result Fragment*/
        fragmentManager = getSupportFragmentManager();
        searchResultFragment = NewsListFragment.newInstance(false);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, searchResultFragment);
        ft.detach(searchResultFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        searchView.setQueryHint(getString(R.string.search_text));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fragmentManager.beginTransaction().attach(searchResultFragment).commit();
                searchResultFragment.setNews(s);
                userConfig.addSearchHistory(s);
                mainViewPager.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
                listPopupWindow.dismiss();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                listPopupWindow.show();
                searchSuggest.clear();
                for (String suggest : userConfig.getSearchHistory()) {
                    if (suggest.toLowerCase().contains(s.toLowerCase())) {
                        searchSuggest.add(suggest);
                    }
                }
                suggestArrayAdapter.notifyDataSetChanged();
                if (searchSuggest.isEmpty()) {
                    listPopupWindow.dismiss();
                }
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    listPopupWindow.show();
                }
                else {
                    listPopupWindow.dismiss();
                }
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                fragmentManager.beginTransaction().hide(searchResultFragment).commit();
                mainViewPager.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        /*科大讯飞语音合成api初始化*/
        try {
            SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5d58fa7c");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        UserConfig.getInstance().setContext(getBaseContext());
        // 分享初始化
        MobSDK.init(this);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.tab_news:
                mainViewPager.setCurrentItem(0);
                break;
            case R.id.tab_mine:
                mainViewPager.setCurrentItem(1);
                break;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        menuItem = bottomNavigationView.getMenu().getItem(position);
        menuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}