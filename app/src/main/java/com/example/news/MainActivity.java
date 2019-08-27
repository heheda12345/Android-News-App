package com.example.news;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.widget.ArrayAdapter;

import com.example.news.data.UserConfig;
import com.example.news.ui.main.MainPagerAdapter;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter suggestArrayAdapter;
    private ListPopupWindow listPopupWindow;
    private Toolbar toolbar;
    private List<String> searchSuggest;
    private UserConfig userConfig = UserConfig.getInstance();
    private String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Load User Config*/
        searchSuggest = new ArrayList<>();
        searchSuggest.addAll(userConfig.getSearchHistory());

        /* Create Main Tab Layout */
        TabLayout mainTabLayout = findViewById(R.id.main_tabLayout);
        mainTabLayout.addTab(mainTabLayout.newTab().setText(R.string.mainTab_news));
        mainTabLayout.addTab(mainTabLayout.newTab().setText(R.string.mainTab_mine));
        /* Create Main View Pager */
        final ViewPager mainViewPager = findViewById(R.id.main_viewPager);
        final MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager(), 2);
        mainViewPager.setAdapter(mainPagerAdapter);
        /* Set listener for clicks*/
        mainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTabLayout));
        mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
//        ViewPager viewPager = findViewById(R.id.view_pager);
//        viewPager.setAdapter(sectionsPagerAdapter);
//        TabLayout tabs = findViewById(R.id.tabs);
//        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        searchView.setQueryHint(getString(R.string.search_text));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
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
        return true;
    }
}