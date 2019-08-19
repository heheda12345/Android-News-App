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
    private View.OnClickListener removeSectionClick;
    private View.OnClickListener addSectionClick;
    private SectionsGridAdapter unSelectedGridAdapter;
    private SectionsGridAdapter selectedGridAdapter;

    public SectionsPopWindow(Activity context, View.OnClickListener removeSectionClick, View.OnClickListener addSectionClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.widget_pop_window, null);
        this.removeSectionClick = removeSectionClick;
        this.addSectionClick = addSectionClick;
        this.context = context;
        initView();
        initPopWindow();
    }

    private void initView() {
        TextView text = view.findViewById(R.id.section_textView);
        Button button = view.findViewById(R.id.section_button);
        GridView sectionGrid = view.findViewById(R.id.section_grid);
        GridView unSectionGrid = view.findViewById(R.id.unsection_grid);


        selectedGridAdapter = new SectionsGridAdapter(context, UserConfig.getInstance().getAllSelectSections(), removeSectionClick);
        sectionGrid.setAdapter(selectedGridAdapter);

        unSelectedGridAdapter = new SectionsGridAdapter(context, UserConfig.getInstance().getAllUnselectedSections(), addSectionClick);
        unSectionGrid.setAdapter(unSelectedGridAdapter);

//        text.setOnClickListener(sectionClick);

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

    public void updatePage() {
        unSelectedGridAdapter.notifyDataSetChanged();
        selectedGridAdapter.notifyDataSetChanged();
    }

}
