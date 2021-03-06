package me.echeung.moemoekyun.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import me.echeung.moemoekyun.App;

public class ImageUtil {

    public static void loadImage(ImageView v, Bitmap bitmap) {
        clearImageView(v);

        if (!App.getPreferenceUtil().shouldDownloadImage(v.getContext())) {
            return;
        }

        Glide.with(v.getContext())
                .load(bitmap)
                .apply(new RequestOptions()
                        .placeholder(v.getDrawable())
                        .override(v.getWidth(), v.getHeight())
                        .centerCrop())
                .into(v);
    }

    public static void loadImage(ImageView v, String url) {
        clearImageView(v);

        if (!App.getPreferenceUtil().shouldDownloadImage(v.getContext())) {
            return;
        }

        Glide.with(v.getContext())
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(v.getDrawable())
                        .override(v.getWidth(), v.getHeight())
                        .centerCrop())
                .into(v);
    }

    public static void clearCache(Context context) {
        if (context == null) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Glide.get(context).clearDiskCache();
                return null;
            }
        }.execute();

        Glide.get(context).clearMemory();
    }

    private static void clearImageView(ImageView v) {
        Glide.with(v.getContext())
                .clear(v);
    }

}
