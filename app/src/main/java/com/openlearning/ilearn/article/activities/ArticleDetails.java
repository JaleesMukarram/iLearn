package com.openlearning.ilearn.article.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.modals.ArticleSection;
import com.openlearning.ilearn.article.view_models.ArticleDetailsVM;
import com.openlearning.ilearn.databinding.ActivityArticleDetailsBinding;
import com.openlearning.ilearn.databinding.ViewSingleArticleSectionShowingBinding;
import com.openlearning.ilearn.databinding.ViewSingleCommentShowingBinding;
import com.openlearning.ilearn.databinding.ViewSingleImageCenteredBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.PostComment;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.utils.CommonUtils;

import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;

public class ArticleDetails extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "ArtDetailsTAG";
    private ActivityArticleDetailsBinding mBinding;
    private ArticleDetailsVM viewModel;
    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_article_details);

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

        article = getIntent().getParcelableExtra(Article.PARCELABLE_KEY);

        if (article == null) finish();

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(ArticleDetailsVM.class);
        viewModel.initIDs(article);
        mBinding.setArticle(article);

        mBinding.MCVLikeContainer.setOnClickListener(v -> viewModel.manageLike());
        mBinding.IVSendMessage.setOnClickListener(v -> {

            if (mBinding.ETEnteredMessage.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter some comment to send", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.manageNewComment(mBinding.ETEnteredMessage.getText().toString());
            mBinding.ETEnteredMessage.setText(STRING_EMPTY);
            Toast.makeText(this, "Commenting...Please wait!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void process() {

        showAllSections();

        viewModel.getLikeStatusChanged().observe(this, likeStatusChanged -> {

            if (likeStatusChanged) {

                updateLikeStatus();
            }
        });
        viewModel.getCommentStatusChanged().observe(this, commentStatusChanged -> {

            if (commentStatusChanged) {

                updateCommentStatus();
            }
        });
    }

    @Override
    public void loaded() {

    }

    private void showAllSections() {

        for (ArticleSection articleSection : article.getArticleSections()) {

            populateThisSingleArticle(articleSection);
        }
    }

    private void populateThisSingleArticle(ArticleSection articleSection) {

        ViewSingleArticleSectionShowingBinding binding = DataBindingUtil.inflate(this.getLayoutInflater(), R.layout.view_single_article_section_showing, null, false);
        binding.setArticleSection(articleSection);
        mBinding.LLArticleSectionAppender.addView(binding.getRoot(), CommonUtils.getLayoutParamWithSideMargins(0, 0, 0, 0));

        for (StorageImage storageImage : articleSection.getStorageImageList()) {

            addCenteredImageToAppender(storageImage, binding.LLSectionImageAppender);
        }
    }

    private void addCenteredImageToAppender(StorageImage storageImage, LinearLayout llSectionImageAppender) {

        ViewSingleImageCenteredBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_single_image_centered, llSectionImageAppender, false);
        llSectionImageAppender.addView(binding.getRoot(), CommonUtils.getLayoutParamWithSideMargins(0, 8, 0, 8));

        binding.IVGridRowSingleImage.setOnClickListener(v -> {

            CommonUtils.fullScreenImageShowingDialogue(this, storageImage.getDownloadURL());

        });

        binding.setImageURI(storageImage.getDownloadURL());
    }

    private void updateLikeStatus() {

        Log.d(TAG, "updateLikeStatus: Like status updated");

        if (CommonUtils.isMyReactDone(article.getPostReactList(), viewModel.getUserID())) {

            mBinding.IVLikedIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like_done));
            mBinding.TVLikeCounts.setTextColor(getColor(R.color.colorPrimary));

        } else {

            mBinding.IVLikedIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like));
            mBinding.TVLikeCounts.setTextColor(getColor(R.color.colorGrayTextLight));

        }

        mBinding.TVLikeCounts.setText(String.valueOf(article.getPostReactList().size()));
    }

    private void updateCommentStatus() {

        viewModel.sortAllComments(article.getPostCommentList());
        mBinding.LLCommentsAppender.removeAllViews();

        Log.d(TAG, "updateCommentStatus: Comment status updated");

        for (PostComment postComment : article.getPostCommentList()) {

            ViewSingleCommentShowingBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_single_comment_showing, mBinding.LLCommentsAppender, false);
            binding.setPostComment(postComment);
            mBinding.LLCommentsAppender.addView(binding.getRoot(), CommonUtils.getLayoutParamWithSideMargins(0, 8, 0, 8));

            viewModel.getNameOfThisUser(postComment.getUserID(), new FirebaseSuccessListener() {

                @Override
                public void onSuccess(Object obj) {

                    binding.TVUserName.setText((String) obj);
                }

                @Override
                public void onFailure(Exception ex) {

                }
            });

            if (viewModel.getUserID().equals(postComment.getUserID())) {

                binding.IVRemoveComment.setVisibility(View.VISIBLE);
                binding.IVRemoveComment.setOnClickListener(v -> viewModel.deleteThisComment(postComment));

            }
        }
    }

}