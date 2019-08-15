package com.example.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.news.support.ImageCrawler;
import com.example.news.support.NewsCrawler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.ImageView.ScaleType.FIT_XY;
import static java.lang.Math.max;

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

        //解析json
        Intent intent = getIntent();
        String message = intent.getStringExtra("data");
        String title = "";
        ArrayList<String> content = new ArrayList<>();
        ArrayList<String> imgUrls = new ArrayList<String>();
        try {
            JSONObject jsonNews = new JSONObject(message);
            content.addAll(Arrays.asList(jsonNews.getString("content").split("\n+")));
            title = jsonNews.getString("title");
            String url = jsonNews.getString("image");
            if (url.length() > 2) {
                imgUrls.addAll(Arrays.asList(url.substring(1, url.length()-1).split(",")));
            }
//            JSONArray imageJson = jsonNews.getJSONArray("image");
//            for (int i=0; i<imageJson.length(); i++) {
//                imgUrls.add(imageJson.getString(i));
//            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

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
//            ViewGroup.MarginLayoutParams textViewParam = (ViewGroup.MarginLayoutParams)textView.getLayoutParams();
//            textViewParam.setMargins(0, 10, 0, 10);
            textViews.add(textView);
        }

        LinearLayout container = findViewById(R.id.container);
        //标题
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(24);
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
        debugView.setText("Debug:\n" + message);
        container.addView(debugView);
    }

}
