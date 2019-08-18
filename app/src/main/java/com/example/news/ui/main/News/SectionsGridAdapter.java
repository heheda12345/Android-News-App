package com.example.news.ui.main.News;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.news.R;

import java.util.List;

public class SectionsGridAdapter extends BaseAdapter {
    private Context context;
    private List<String> listItem;

    public SectionsGridAdapter(Context context, List<String> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public String getItem(int position) {
        return listItem.get(position);
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
        sectionButton.setText(listItem.get(position));
        return convertView;
    }

}
