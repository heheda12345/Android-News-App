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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.news.support.ImageCrawler;
import com.example.news.support.NewsCrawler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class NewsDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG =
            NewsCrawler.class.getSimpleName();
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

        Intent intent = getIntent();
        String message = intent.getStringExtra("data");
        String title = "";
        String content = "";
        try {
            JSONObject jsonNews = new JSONObject(message);
            content = jsonNews.getString("content");
            title = jsonNews.getString("title");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        ImageCrawler imageCrawler = new ImageCrawler("http://5b0988e595225.cdn.sohucs.com/images/20190702/0c9bd3240e934475b027b8d5d1b9146e.jpeg");
        imageCrawler.start();
        try {
            imageCrawler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LinearLayout container = findViewById(R.id.container);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(30);
        titleView.setTypeface(null, Typeface.BOLD);
        container.addView(titleView);

        Bitmap bitmap = imageCrawler.getBitmap();
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        container.addView(imageView);

        TextView textView = new TextView(this);
        textView.setText(content);
        container.addView(textView);

        TextView debugView = new TextView(this);
        debugView.setText("Debug:\n" + message);
        container.addView(debugView);
    }

}
