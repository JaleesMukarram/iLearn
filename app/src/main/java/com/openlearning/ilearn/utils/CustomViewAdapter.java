package com.openlearning.ilearn.utils;

import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

import java.io.File;

public class CustomViewAdapter {

    @BindingAdapter({"android:imgSrcSmall"})
    public static void setImageResource(ImageView view, File imageFile) {

        Picasso.get().load(imageFile)
                .resize(100, 100)
                .into(view);


    }

    @BindingAdapter({"android:imgSrcURI"})
    public static void setImageResource(ImageView view, String imageFile) {

        Picasso.get().load(imageFile)
                .into(view);

    }
}
