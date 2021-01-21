package com.openlearning.ilearn.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityNewsDetailsBinding;
import com.openlearning.ilearn.databinding.ViewSingleImageCenteredBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.modals.StorageItem;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.utils.ImageSliderUtils;

public class NewsDetails extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "NewsDetailsTAG";
    private News news;
    private ActivityNewsDetailsBinding mBinding;
    private NewsDetailsVM viewModel;

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
//        if (news == null) finish();

//        news = NewsRepository.getInstance().getNewsFromLocalListWithId(news.getId());
//        Log.d(TAG, "handleIntent: " + news);

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(NewsDetailsVM.class);
        viewModel.initIDs(news);
        mBinding.setNews(news);

        mBinding.MCVLikeContainer.setOnClickListener(v -> viewModel.manageLike());
    }

    @Override
    public void process() {

        if (news.getStorageImageList().size() > 0) {

            ImageSliderUtils imageSliderUtils = new ImageSliderUtils(this, news.getStorageImageList(), mBinding.LLNewsImages);
            imageSliderUtils.callHooks();

        }

        viewModel.getLikeStatusChanged().observe(this, likeStatusChanged -> {

            if (likeStatusChanged) {

                updateLikeStatus();
            }
        });
        updateLikeStatus();
    }

    @Override
    public void loaded() {

    }

    private void updateLikeStatus() {

        Log.d(TAG, "updateLikeStatus: Like status updated");

        if (CommonUtils.isMyReactDone(news.getPostReactList(), viewModel.getUserID())) {

            mBinding.IVLikedIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like_done));
            mBinding.TVLikeCounts.setTextColor(getColor(R.color.colorPrimary));

        } else {

            mBinding.IVLikedIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like));
            mBinding.TVLikeCounts.setTextColor(getColor(R.color.colorGrayTextLight));

        }

        mBinding.TVLikeCounts.setText(String.valueOf(news.getPostReactList().size()));
    }
}