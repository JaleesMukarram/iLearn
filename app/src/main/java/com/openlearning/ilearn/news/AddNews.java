package com.openlearning.ilearn.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAddNewNewsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.utils.FilePickerUtils;

public class AddNews extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "AddNewNewsTAG";
    private ActivityAddNewNewsBinding mBinding;
    private AddNewsVM viewModel;
    private News editNews;
    private FilePickerUtils filePickerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_news);

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

        Intent intent = getIntent();
        News news = intent.getParcelableExtra(News.PARCELABLE_KEY);

        if (news != null) {

            editNews = news;
            Log.d(TAG, "handleIntent: Edit mode");
        }

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(AddNewsVM.class);

        mBinding.BTNAddNews.setOnClickListener(v -> viewModel.checkNews(this,

                mBinding.ETNewsHeading.getText().toString().trim(),
                mBinding.ETNewsTitle.getText().toString().trim(),
                mBinding.ETNewsBody.getText().toString()
        ));

        mBinding.IVThreeDots.setOnClickListener(v -> showStatusDialogue());

        mBinding.TVSelectImage.setOnClickListener(v -> showFileSelectionDialogue());

        filePickerUtils = new FilePickerUtils(this, FilePickerUtils.MODE_IMAGES_ONLY, (file, type) -> {

            viewModel.addNewsImageToStorage(this, mBinding.LLImageAppender, file);
            Toast.makeText(this, file.getName() + " is uploading...", Toast.LENGTH_SHORT).show();

        });

        filePickerUtils.callHooks();

    }

    @Override
    public void process() {

        if (editNews != null) {

            viewModel.setEditNews(editNews, this, mBinding.LLImageAppender);
            mBinding.setEditNews(editNews);
            viewModel.setActive(editNews.isActive());
            mBinding.BTNAddNews.setText(R.string.edit_news);
        }
    }

    @Override
    public void loaded() {

    }

    private void showStatusDialogue() {

        boolean active = true;

        if (editNews != null) {

            active = editNews.isActive();

        }

        StatusDialogue dialogue = new StatusDialogue(this);
        dialogue.ready(active, editNews != null, new StatusDialogue.StatusListener() {
            @Override
            public void onStatusChanged(boolean active) {

                viewModel.setActive(active);
            }

            @Override
            public void onDelete() {

                viewModel.deleteEditNews(AddNews.this);

            }
        });

        dialogue.show();
    }

    private void showFileSelectionDialogue() {

        filePickerUtils.showNow();
    }
}