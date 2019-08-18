package com.example.news.ui.main.News;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.data.UserConfig;

import java.util.ArrayList;

public class SectionsPopWindow extends PopupWindow {
    private static final String TAG = "SectionPopWindow";
    private final View view;
    private Activity context;
    private View.OnClickListener sectionClick;

    public SectionsPopWindow(Activity context, View.OnClickListener itemClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.widget_pop_window, null);
        this.sectionClick = itemClick;
        this.context = context;
        initView();
        initPopWindow();
    }

    private void initView() {
        TextView text = view.findViewById(R.id.section_textView);
        Button button = view.findViewById(R.id.section_button);
        GridView sectionGrid = view.findViewById(R.id.section_grid);
        ArrayList<String> sectionName = new ArrayList<>();
        for (UserConfig.Section section : UserConfig.getInstance().getAllSelectSections()) {
            sectionName.add(section.getSectionName());
        }
        sectionGrid.setAdapter(new SectionsGridAdapter(context, sectionName));

        text.setOnClickListener(sectionClick);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void initPopWindow() {
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.popwin_anim_style);
        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        this.setBackgroundDrawable(dw);
        backGroundAlpha(context,0.5f);
    }

    public void backGroundAlpha(Activity context, float alpha) {
        WindowManager.LayoutParams p = context.getWindow().getAttributes();
        p.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(p);
    }

}
