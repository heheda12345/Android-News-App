package com.example.news.ui.main.News;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.news.R;
import com.example.news.collection.CollectionItem;
import com.example.news.collection.CollectionViewModel;
import com.example.news.data.UserConfig;
import com.example.news.support.ImageCrawler;
import com.example.news.support.NewsCrawler;
import com.example.news.support.ServerInteraction;
import com.r0adkll.slidr.Slidr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import am.widget.smoothinputlayout.SmoothInputLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.ImageView.ScaleType.FIT_XY;
import static java.lang.Math.max;


public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener{
    private static final String LOG_TAG =
            NewsCrawler.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        parseJson();
        container = findViewById(R.id.container);
        initContainer(container);
        initBottom();
        initCollection();
        initTTS();
        loadComment();
        Slidr.attach(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void initTTS() {
        btnVoice.setTexts(text);
    }

    void initBottom() {
        Toolbar mToolbar = findViewById(R.id.smoothinputlayout_toolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        lytContent = findViewById(R.id.sil_lyt_content);
        edtInput = findViewById(R.id.sil_edt_input);
        btnCollect = findViewById(R.id.sil_ibtn_collect);
        btnVoice = findViewById(R.id.sil_ibtn_tts);
        btnShare = findViewById(R.id.sil_ibtn_share);
        btnSend = findViewById(R.id.sil_ibtn_send);
        vList = findViewById(R.id.sil_v_list);

        btnCollect.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        edtInput.setOnTouchListener(this);
        vList.setOnTouchListener(this);
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
        updateCollectionIcon();
    }

    private void updateCollectionIcon() {
        if (mCollectionViewModel.contains(new CollectionItem(newsID))) {
            btnCollect.setBackgroundResource(R.drawable.ic_collected);
        } else {
            btnCollect.setBackgroundResource(R.drawable.ic_uncollected);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(LOG_TAG, "home clicked");
                finish();
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
            text = jsonNews.getString("content");
            content.addAll(Arrays.asList(text.split("\n+")));
            title = jsonNews.getString("title");
            newsID = jsonNews.getString("title");
            newsSource = jsonNews.getString("publisher");
            newsTime  = jsonNews.getString("publishTime");
            String url = jsonNews.getString("image");
            if (url.length() > 5) {
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
        if (!UserConfig.getInstance().isTextMode()) {
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
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NewsDetailActivity.this, LargeImageActivity.class);
                        intent.putStringArrayListExtra("url", imgUrls);
                        startActivity(intent);
                    }
                });
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
        SpannableStringBuilder spanTitle = new SpannableStringBuilder(title + "\n\n");
        spanTitle.setSpan(new AbsoluteSizeSpan(24, true), 0, title.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spanSource = new SpannableStringBuilder( newsSource + "  ");
        spanSource.setSpan(new ForegroundColorSpan(0xff5c78b9), 0, newsSource.length()+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanSource.setSpan(new AbsoluteSizeSpan(18, true), 0, newsSource.length()+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spanTime = new SpannableStringBuilder(newsTime);
        spanTime.setSpan(new AbsoluteSizeSpan(18, true), 0, newsTime.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanTime.setSpan(new ForegroundColorSpan(0xffb7b7b7), 0, newsTime.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        titleView.setText(spanTitle.append(spanSource).append(spanTime));
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
        commentDivider = new TextView(this);
        commentDivider.setText("评论区");
        commentDivider.setTextSize(20);
        commentDivider.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        commentDivider.setVisibility(View.GONE);
        container.addView(commentDivider);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((TtsButton)findViewById(R.id.sil_ibtn_tts)).destroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sil_ibtn_collect:
                CollectionItem collectionItem = new CollectionItem(newsID);
                if (mCollectionViewModel.contains(collectionItem)) {
                    mCollectionViewModel.erase(collectionItem);
                } else {
                    mCollectionViewModel.insert(collectionItem);
                }
                break;
            case R.id.sil_ibtn_share:
                Toast.makeText(getApplicationContext(), "share, unimplemented",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.sil_ibtn_send:
                sendMessage();
                break;
        }
    }

    private void setToolsVisibility(int visibilityCode) {
        btnCollect.setVisibility(visibilityCode);
        btnShare.setVisibility(visibilityCode);
        btnVoice.setVisibility(visibilityCode);
    }

    private void loadComment() {
        JSONArray comments = ServerInteraction.getInstance().getComment(newsID);
        Log.d(LOG_TAG, comments.toString());
        for (View v: commentViews)
            container.removeView(v);
        commentViews.clear();
        for (int i=0; i<comments.length(); i++) {
            try {
                JSONObject comment = comments.getJSONObject(i);
                String name = comment.getString("name");
                String text = comment.getString("text");
                Log.d(LOG_TAG, name+text);
                SpannableStringBuilder spanName = new SpannableStringBuilder(name + "\n");
                spanName.setSpan(new ForegroundColorSpan(0xff5c78b9), 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spanName.setSpan(new AbsoluteSizeSpan(18, true), 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                SpannableStringBuilder spanText = new SpannableStringBuilder(text);
                spanText.setSpan(new AbsoluteSizeSpan(24, true), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.comment_item, null, false);
                v.setId(View.generateViewId());
                TextView tv = v.findViewById(R.id.commentView);
                tv.setText(spanName.append(spanText));
                commentViews.add(v);
                container.addView(v);
                ImageView iv = v.findViewById(R.id.iconView);
                if (UserConfig.getInstance().isTextMode()) {
                    iv.setVisibility(View.GONE);
                } else {
                    File f1 = ServerInteraction.getInstance().getIcon(name,false, getBaseContext());
                    if (f1 != null)
                        Glide.with(getBaseContext()).load(Uri.fromFile(f1)).into(iv);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        commentDivider.setVisibility(commentViews.size() > 0 ? View.VISIBLE : View.GONE);
    }

    private void sendMessage() {
        if (!UserConfig.getInstance().isLogin()) {
            Toast.makeText(getApplicationContext(), "登录后才能发表评论哦",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String text = edtInput.getText().toString().trim();
        if (text.length() == 0) {
            Toast.makeText(getApplicationContext(), "评论不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ServerInteraction.ResultCode resultCode = ServerInteraction.getInstance().postComment(
                UserConfig.getInstance().getUserName(),
                newsID, text
        );
        if (resultCode == ServerInteraction.ResultCode.success) {
            Toast.makeText(getApplicationContext(), "评论成功",
                    Toast.LENGTH_SHORT).show();
            edtInput.setText(null);
            loadComment();
            onTouch(vList, null);
        } else {
            Toast.makeText(getApplicationContext(), "评论失败，请稍后重试",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.sil_v_list:
                lytContent.closeKeyboard(true);
                lytContent.closeInputPane();
                if (edtInput.getText().toString().trim().length() > 0) {
                    setToolsVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    setToolsVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                }
                break;
            case R.id.sil_edt_input:
                setToolsVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
                break;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (lytContent.isInputPaneOpen()) {
            lytContent.closeInputPane();
            return;
        }
        super.onBackPressed();
    }

    private String title = "";
    private String text = "";
    private ArrayList<String> content = new ArrayList<>();
    private ArrayList<String> imgUrls = new ArrayList<>();
    private String newsID = "";
    private String newsSource = "";
    private String newsTime = "";
    private CollectionViewModel mCollectionViewModel;

    private SmoothInputLayout lytContent;
    private EditText edtInput;
    private View btnCollect, btnShare, btnSend, vList;
    private TtsButton btnVoice;
    private TextView commentDivider;
    private LinearLayout container;
    private ArrayList<View> commentViews = new ArrayList<>();
}
