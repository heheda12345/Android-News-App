package com.example.news.ui.main.Mine;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.news.R;
import com.example.news.data.UserConfig;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserConfig.getInstance().getNightMode()) {
            setDark();
        }
        else {
            setBright();
        }

        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        ((ImageView)findViewById(R.id.gyx)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gyx));
    }

    public void setBright() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.alpha = 1.0f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(p);
    }

    public void setDark() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.alpha = 0.3f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(p);
    }
}
