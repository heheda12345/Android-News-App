package com.example.news.ui.main.News;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.news.R;
import com.example.news.data.UserConfig;

import java.util.List;

public class SectionsGridAdapter extends BaseAdapter {
    private Context context;
    boolean type;
//    private List<UserConfig.Section> listItem;
    private View.OnClickListener sectionClick;

    public SectionsGridAdapter(Context context, boolean type, View.OnClickListener sectionClick) {
        this.context = context;
//        this.listItem = listItem;
        this.type = type;
        this.sectionClick = sectionClick;
    }

    @Override
    public int getCount() {
        if (type) {
            return UserConfig.getInstance().getSectionNum();
        }
        else {
            return UserConfig.getInstance().getUnSectionNum();
        }
    }

    @Override
    public String getItem(int position) {
        if (type) {
            return UserConfig.getInstance().getSection(position).getSectionName();
        }
        else {
            return UserConfig.getInstance().getUnSetion(position).getSectionName();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_view_sections, null);
        }
        Button sectionButton = convertView.findViewById(R.id.section_button);
        sectionButton.setTag(position);
        if (type) {
            sectionButton.setText(UserConfig.getInstance().getSection(position).getSectionName());
        }
        else {
            sectionButton.setText(UserConfig.getInstance().getUnSetion(position).getSectionName());
        }
        sectionButton.setOnClickListener(sectionClick);
        return convertView;
    }

}