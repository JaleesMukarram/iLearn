package com.openlearning.ilearn.utils;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSliderUtilsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.modals.StorageImage;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ImageSliderUtils implements ActivityHooks {

    private static final String TAG = "ImageSTAG";
    private ViewSliderUtilsBinding mBinding;
    private final Activity homeScreen;
    private final List<StorageImage> storageImageList;
    private final LinearLayout container;
    private Timer timer;


    private int currentIndex = -1;

    public ImageSliderUtils(Activity homeScreen, List<StorageImage> storageImageList, LinearLayout container) {
        this.homeScreen = homeScreen;
        this.storageImageList = storageImageList;
        this.container = container;
        Log.d(TAG, "ImageSliderUtils: total sizr: " + storageImageList.size());
    }

    @Override
    public void callHooks() {

        init();
        process();
        setTimer();

    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void init() {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(homeScreen), R.layout.view_slider_utils, null, false);

        mBinding.IVSlideLeft.setOnClickListener(v -> simulateImageSliding(false));
        mBinding.IVSlideRight.setOnClickListener(v -> simulateImageSliding(true));

    }

    @Override
    public void process() {

        container.addView(mBinding.getRoot());
        simulateImageSliding(true);

    }

    @Override
    public void loaded() {

    }

    private void simulateImageSliding(boolean next) {

        if (homeScreen == null || homeScreen.isDestroyed()) {
            endTimer();
            return;
        }

        if (next) {
            currentIndex++;
            if (currentIndex == storageImageList.size()) {
                currentIndex = 0;
            }


            Animation leftAnimation = AnimationUtils.loadAnimation(homeScreen, R.anim.left_animation);
            mBinding.IVMainImage.setAnimation(leftAnimation);
        } else {

            currentIndex--;
            if (currentIndex < 0) {
                currentIndex = storageImageList.size() - 1;
            }


            Animation leftAnimation = AnimationUtils.loadAnimation(homeScreen, R.anim.right_animation);
            mBinding.IVMainImage.setAnimation(leftAnimation);

        }

        Glide.with(homeScreen)
                .load(storageImageList.get(currentIndex).getDownloadURL())
                .into(mBinding.IVMainImage);


        validateArrows();
        Log.d(TAG, "simulateImageSliding: current image: " + currentIndex);

    }

    private void validateArrows() {


        if (currentIndex == 0) {
            mBinding.IVSlideLeft.setVisibility(View.GONE);
            mBinding.IVSlideRight.setVisibility(View.VISIBLE);

        } else if (currentIndex == storageImageList.size() - 1) {
            mBinding.IVSlideLeft.setVisibility(View.VISIBLE);
            mBinding.IVSlideRight.setVisibility(View.GONE);
        } else {

            mBinding.IVSlideLeft.setVisibility(View.VISIBLE);
            mBinding.IVSlideRight.setVisibility(View.VISIBLE);

        }

    }

    private void setTimer() {


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                homeScreen.runOnUiThread(() -> simulateImageSliding(true));
            }
        }, 5000, 5000);
        Log.d(TAG, "setTimer: Timer started");

    }

    private void endTimer() {

        timer.purge();
        timer.cancel();
        Log.d(TAG, "endTimer: Timer ended");
    }

}
