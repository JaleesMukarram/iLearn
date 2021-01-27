package com.openlearning.ilearn.article.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.view_models.AddSubjectArticleVM;
import com.openlearning.ilearn.databinding.ActivityAddSubjectArticleBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.news.dialogues.StatusDialogue;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.utils.FilePickerUtils;

public class AddSubjectArticle extends AppCompatActivity implements ActivityHooks {

    private ActivityAddSubjectArticleBinding mBinding;
    private AddSubjectArticleVM viewModel;
    private FilePickerUtils filePickerUtils;

    private Subject subject;
    private Article editArticle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_subject_article);

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

        subject = getIntent().getParcelableExtra(Subject.PARCELABLE_KEY);
        editArticle = getIntent().getParcelableExtra(Article.PARCELABLE_KEY);
        if (subject == null)
            finish();

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(AddSubjectArticleVM.class);
        viewModel.initIDs(subject);


        mBinding.MCVAddSection.setOnClickListener(v -> viewModel.addNewSection(this, mBinding.LLArticleSectionAppender, filePickerUtils, null));
        mBinding.BTNAddArticle.setOnClickListener(v -> viewModel.checkArticleForUpload(
                this,
                mBinding.ETArticleName.getText().toString(),
                mBinding.ETArticleCategory.getText().toString()
        ));

        filePickerUtils = new FilePickerUtils(this, FilePickerUtils.MODE_IMAGES_ONLY);

        if (CommonUtils.checkIfStoragePermissionIsGranted(this)) {

            filePickerUtils.callHooks();
        }

        if (editArticle != null) {

            viewModel.setEditArticle(this, editArticle, mBinding.LLArticleSectionAppender, filePickerUtils);
            mBinding.setEditArticle(editArticle);
            mBinding.BTNAddArticle.setText(R.string.edit_article);
        }

        if (viewModel.isUserInstructor()) {

            mBinding.IVThreeDots.setOnClickListener(v -> showStatusDialogue());

        }
    }

    @Override
    public void process() {


    }

    @Override
    public void loaded() {

    }

    private void showStatusDialogue() {

        boolean active = true;

        if (editArticle != null) {

            active = editArticle.isActive();

        }

        StatusDialogue dialogue = new StatusDialogue(this);
        dialogue.ready(active, editArticle != null, new StatusDialogue.StatusListener() {
            @Override
            public void onStatusChanged(boolean active) {

                viewModel.setActive(active);
            }

            @Override
            public void onDelete() {

                viewModel.deleteEditArticle(AddSubjectArticle.this);

            }
        });

        dialogue.show();
    }

}