package com.example.news.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.news.data.BitMapCache;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageLoadingTask extends AsyncTask<String, Void, List<Bitmap>> {
    private int sectionPos;
    private ImageView[] imageViews;
    private static final String LOG_TAG = "ImageLoadingTask";


    public ImageLoadingTask(int sectionPos, ImageView[] imageViews) {
        this.sectionPos = sectionPos;
        this.imageViews = imageViews;
    }

    @Override
    protected List<Bitmap> doInBackground(String ... params) {
        List<Bitmap> bitmapList = new ArrayList<>();
        BitMapCache cache = BitMapCache.getInstance();
        for (int i = 0; i < params.length; ++i) {
            String url = params[i];
            if (cache.contains(sectionPos, url)) {
//                Log.d(LOG_TAG, "Get from cache");
                bitmapList.add(cache.get(sectionPos, url));
                continue;
            }
            Bitmap bitmap = null;
            try {
                URL imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imgUrl
                        .openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                cache.add(sectionPos, url, bitmap);
                bitmapList.add(bitmap);
                is.close();
                conn.disconnect();
            } catch (MalformedURLException e) {
//                Log.e(LOG_TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        return bitmapList;
    }

    @Override
    protected void onPostExecute(List<Bitmap> result) {
        if (result != null) {
            for (int i = 0; i < result.size(); ++i) {
                imageViews[i].setImageBitmap(result.get(i));
            }
        }
    }

}
