package com.example.news.ui.main.Items;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.data.ConstantValues;

public class NewsItemVH extends RecyclerView.ViewHolder {
    private boolean hasRead = false;
    private Context mContext;

    public ConstantValues.ItemViewType layoutType;
    public final TextView title;
    public final TextView author;
    public final TextView time;
    public final ImageView[] images;
    public int mCurrentPosition = -1;


    public NewsItemVH(Context context, View v, ConstantValues.ItemViewType layoutType) {
        super(v);
        this.layoutType = layoutType;
        this.mContext = context;
        title = v.findViewById(R.id.title);
        author = v.findViewById(R.id.author);
        time = v.findViewById(R.id.time);
        int imageNum = ConstantValues.IMAGE_NUM[this.layoutType.ordinal()];
        images = new ImageView[imageNum];
        if (imageNum == 1) {
            images[0] = v.findViewById(R.id.image);
        }
        if (imageNum == 3) {
            images[0] = v.findViewById(R.id.image0);
            images[1] = v.findViewById(R.id.image1);
            images[2] = v.findViewById(R.id.image2);
        }
    }

    public void initImages() {
        for (ImageView image : images) {
            image.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_image));
        }
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setRead(boolean isRead) {
        hasRead = isRead;
        if (isRead) {
            int readColor = ContextCompat.getColor(mContext, R.color.gray);
            title.setTextColor(readColor);
            author.setTextColor(readColor);
            time.setTextColor(readColor);
        }
        else {
            int notReadColor = ContextCompat.getColor(mContext, R.color.text_gray);
            title.setTextColor(notReadColor);
            author.setTextColor(notReadColor);
            time.setTextColor(notReadColor);
        }

    }
}
