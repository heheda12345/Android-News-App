package com.example.news.ui.main.News;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.news.collection.CollectionItem;
import com.example.news.collection.CollectionViewModel;
import com.example.news.support.ImageCrawler;
import com.example.news.support.NewsCrawler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.ImageView.ScaleType.FIT_XY;
import static java.lang.Math.max;

import com.example.news.R;

public class NewsDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG =
            NewsCrawler.class.getSimpleName();
    final boolean useImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        parseJson();
        LinearLayout container = findViewById(R.id.container);
        initContainer(container);
        initCollection();
    }

    void initCollection() {
        mCollectionViewModel = ViewModelProviders.of(this).get(CollectionViewModel.class);
        Log.d(LOG_TAG, "collection init end!");
        mCollectionViewModel.getAllItems().observe(this, new Observer<List<CollectionItem>>() {
            @Override
            public void onChanged(@Nullable final List<CollectionItem> items) {
                // Update the cached copy of the words in the adapter.
                for (CollectionItem item: items)
                    Log.d(LOG_TAG, "collection updated!"+item.getNewsID());
                updateCollectionIcon();
            }
        });
    }

    private void updateCollectionIcon() {
        if (mCollectionViewModel.contains(new CollectionItem(newsID))) {
            if (mCollectionIcon != null)
                mCollectionIcon.setIcon(R.drawable.ic_collected);
        } else {
            if (mCollectionIcon != null)
                mCollectionIcon.setIcon(R.drawable.ic_uncollected);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_news, menu);
        mCollectionIcon = menu.findItem(R.id.collecting);
        updateCollectionIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.collecting:
                CollectionItem collectionItem = new CollectionItem(newsID);
                if (mCollectionViewModel.contains(collectionItem)) {
                    mCollectionViewModel.erase(collectionItem);
                } else {
                    mCollectionViewModel.insert(collectionItem);
                }
                return true;
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseJson() {
        Intent intent = getIntent();
        String message = intent.getStringExtra("data");
        try {
            JSONObject jsonNews = new JSONObject(message);
            content.addAll(Arrays.asList(jsonNews.getString("content").split("\n+")));
            title = jsonNews.getString("title");
            newsID = jsonNews.getString("title");
            String url = jsonNews.getString("image");
            if (url.length() > 2) {
                imgUrls.addAll(Arrays.asList(url.substring(1, url.length()-1).split(",")));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void initContainer(LinearLayout container) {
        // 建立所有imageView
        ArrayList<ImageView> imageViews = new ArrayList<>();
        ArrayList<Integer> imageViewCanInsert = new ArrayList<>();
        ArrayList<Boolean> imageViewInserted = new ArrayList<>();
        if (useImage) {
            ArrayList<ImageCrawler> crawlers = new ArrayList<>();
            for (int i=0; i<imgUrls.size(); i++) {
                ImageCrawler imageCrawler = new ImageCrawler(imgUrls.get(i));
                imageCrawler.start();
                crawlers.add(imageCrawler);
            }
            for (int i=0; i<crawlers.size(); i++) {
                try {
                    crawlers.get(i).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                Bitmap bitmap = crawlers.get(i).getBitmap();
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                imageView.setLayoutParams(params);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(FIT_XY);
                imageViews.add(imageView);
                if (bitmap.getHeight() * 1.0 / bitmap.getWidth() < 1)
                    imageViewCanInsert.add(imageViews.size() - 1);
                imageViewInserted.add(false);
            }
        }
        //建立所有的textView
        ArrayList<TextView> textViews = new ArrayList<>();
        for (int i=0; i<content.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(content.get(i));
            textView.setTextSize(18);
            textView.setTextIsSelectable(true);
            textViews.add(textView);
        }
        //标题
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(24);
        titleView.setTextIsSelectable(true);
        container.addView(titleView);
        Log.d(LOG_TAG, "size "+imageViewCanInsert.size() + " " + textViews.size() + " " + imageViews.size());
        // 正文
        if (imageViewCanInsert.size() > 0 && textViews.size() > 0) {
            //认为段数比图数多，多余的图放在最后
            int ratio = max(textViews.size() / imageViewCanInsert.size(), 1); // 每张图放ratio段文字
            int imageToUse = 0;
            for (int i=0; i<textViews.size(); i++) {
                if (i % ratio == 0 && imageToUse < imageViewCanInsert.size()) {
                    container.addView(imageViews.get(imageViewCanInsert.get(imageToUse)));
                    imageViewInserted.set(imageViewCanInsert.get(imageToUse), true);
                    imageToUse++;
                }
                container.addView(textViews.get(i));
            }
            for (int i=0; i<imageViews.size(); i++) {
                if (!imageViewInserted.get(i))
                    container.addView(imageViews.get(i));
            }
        } else {
            for (int i=0; i<textViews.size(); i++) {
                container.addView(textViews.get(i));
            }
            for (int i=0; i<imageViews.size(); i++) {
                container.addView(imageViews.get(i));
            }
        }
        //收到的json
        TextView debugView = new TextView(this);
        debugView.setTextIsSelectable(true);
        debugView.setText(String.format("Debug:\n%s", getIntent().getStringExtra("data")));
        container.addView(debugView);
    }

    private String title = "";
    private ArrayList<String> content = new ArrayList<>();
    private ArrayList<String> imgUrls = new ArrayList<String>();
    private String newsID = "";
    private CollectionViewModel mCollectionViewModel;
    private MenuItem mCollectionIcon;

}
