package com.example.news;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.news.collection.CollectionItem;
import com.example.news.collection.CollectionRepository;
import com.example.news.collection.CollectionViewModel;
import com.example.news.collection.NewsListItem;
import com.example.news.collection.NewsSavedItem;
import com.example.news.data.UserConfig;
import com.example.news.support.NewsItem;
import com.example.news.ui.main.MainPagerAdapter;
import com.example.news.ui.main.News.NewsListFragment;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
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

        /*初始化db*/
        CollectionRepository.setApp(getApplication());
        db = new CollectionViewModel(getApplication());
        db.getAllNews().observe(this, new Observer<List<NewsSavedItem>>() {
            @Override
            public void onChanged(@Nullable List<NewsSavedItem> newsSavedItems) {
                for (NewsSavedItem item: newsSavedItems) {
                    Log.d(LOG_TAG, "saved news: " + item.id);
                }
            }
        });
        db.getAllList().observe(this, new Observer<List<NewsListItem>>() {
            @Override
            public void onChanged(@Nullable List<NewsListItem> newsListItems) {
                for (NewsListItem item: newsListItems) {
                    Log.d(LOG_TAG, "saved list: " + item.cid + " " + item.ids);
                }
                ArrayList<NewsItem> collected = db.getNewsList("CCC");
                for (NewsItem item: collected) {
                    Log.d(LOG_TAG, "collection: " + item.getTitle());
                }
            }
        });
        db.getAllItems().observe(this, new Observer<List<CollectionItem>>() {
            @Override
            public void onChanged(@Nullable List<CollectionItem> collectionItems) {
                db.updateCollection();
            }
        });

        UserConfig.getInstance().setContext(getBaseContext(), db);

        if (userConfig.getNightMode()) {
            setDark();
        }
        else {
            setBright();
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
        listPopupWindow.setHorizontalOffset(1000);
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
                searchResultFragment.setInit();
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
                fragmentManager.beginTransaction().detach(searchResultFragment).commit();
                mainViewPager.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                return false;
            }
        });

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

    public void setBright() {
        Log.d(LOG_TAG, "set bright");
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.screenBrightness = -1.0f;
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(p);
    }

    public void setDark() {
        Log.d(LOG_TAG, "set dark");
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.screenBrightness = 0.1f;
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(p);
    }

    CollectionViewModel db;
    boolean permissinDenied;

}