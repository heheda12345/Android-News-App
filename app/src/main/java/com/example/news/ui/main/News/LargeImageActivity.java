package com.example.news.ui.main.News;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.transition.Fade;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.news.R;
import com.example.news.support.ImageCrawler;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class LargeImageActivity extends Activity {
    private ViewPager viewPager;
    private ArrayList<View> imageViews;
    private LargeImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        viewPager = findViewById(R.id.viewPager);
        imageViews = new ArrayList<>();

        initImageViews();
        mAdapter = new LargeImageAdapter(imageViews);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("click", 0));
    }

    void initImageViews() {
        Intent intent = getIntent();
        ArrayList<String> imgUrls = intent.getStringArrayListExtra("url");
        ArrayList<ImageCrawler> crawlers = new ArrayList<>();
        for (int i = 0; i < imgUrls.size(); i++) {
            ImageCrawler imageCrawler = new ImageCrawler(imgUrls.get(i));
            imageCrawler.start();
            crawlers.add(imageCrawler);
        }
        for (int i = 0; i < crawlers.size(); i++) {
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
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageViews.add(imageView);
        }
    }
}
