package com.openlearning.ilearn.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityNewsDetailsBinding;
import com.openlearning.ilearn.databinding.ViewSingleImageCenteredBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.modals.StorageImage;

public class NewsDetails extends AppCompatActivity implements ActivityHooks {

    private News news;
    private ActivityNewsDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_details);

        callHooks();
    }

    @Override
    public void callHooks() {

        handleIntent();
        init();
        process();

    }

    @Override
    public void handleIntent() {

        news = getIntent().getParcelableExtra(News.PARCELABLE_KEY);
        if (news == null) finish();

    }

    @Override
    public void init() {

        mBinding.setNews(news);
    }

    @Override
    public void process() {

        if (news.getStorageImageList().size() > 0) {

            for (StorageImage storageImage : news.getStorageImageList()) {

                ViewSingleImageCenteredBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_single_image_centered, null, false);
                mBinding.LLNewsImages.addView(binding.getRoot());

                Glide.with(this)
                        .load(storageImage.getDownloadURL())
                        .into(binding.IVGridRowSingleImage);

            }
        }

    }

    @Override
    public void loaded() {

    }
}