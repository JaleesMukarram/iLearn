package com.openlearning.ilearn.article.view_models;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.article.modals.ArticleSection;
import com.openlearning.ilearn.article.repositories.ArticleRepository;
import com.openlearning.ilearn.databinding.ViewSingleArticleSectionAddBinding;
import com.openlearning.ilearn.databinding.ViewSingleImageBinding;
import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.interfaces.ConfirmationListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.news.News;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.registration.User;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.utils.FilePickerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MEDIUM;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;
import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;


public class AddSubjectArticleVM extends ViewModel {

    private static final String TAG = "AddSubArticleTAG";
    private final ArticleRepository articleRepository;
    private final MutableLiveData<List<ArticleSection>> allSections;
    private Article editArticle;
    private Subject subject;
    private boolean active = false;

    public AddSubjectArticleVM() {
        articleRepository = new ArticleRepository();
        allSections = new MutableLiveData<>();
        allSections.setValue(new ArrayList<>());
    }

    public void checkArticleForUpload(Activity activity, String articleName, String articleCategory) {


        String articleNameStatus = validateArticleName(articleName);
        if (!articleNameStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, articleNameStatus);
            return;
        }


        String newsTitleStatus = validateArticleCategory(articleCategory);
        if (!newsTitleStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, newsTitleStatus);
            return;
        }

        if (Objects.requireNonNull(allSections.getValue()).size() == 0) {
            CommonUtils.showWarningDialogue(activity, "Please add atleast one section of Article");
            return;
        }

        for (int i = 0; i < Objects.requireNonNull(allSections.getValue()).size(); i++) {

            ArticleSection as = allSections.getValue().get(i);
            Log.d(TAG, "checkArticleForUpload: " + as.toString());
            if (!as.isValid()) {

                CommonUtils.showWarningDialogue(activity, "Please fill the section " + (i + 1) + "\nname limit: 20");
                return;
            }
        }


        Article article = makeArticle(articleName, articleCategory);
        addArticleToDatabase(activity, article);

    }

    private Article makeArticle(String articleName, String articleCategory) {

        if (editArticle != null) {

            editArticle.setName(articleName);
            editArticle.setCategory(articleCategory);
            editArticle.setActive(active);
            editArticle.setArticleSections(allSections.getValue());
            return editArticle;
        }

        return new Article(articleName, articleCategory, UserRegistration.getInstance().getUserID(), subject.getId(), allSections.getValue(), active);
    }

    private void addArticleToDatabase(Activity activity, Article article) {

        if (editArticle != null) {
            LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we edit your Article");

            articleRepository.updateThisArticleIntoDatabase(article, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    loadingDialogue.cancel();
                    CommonUtils.showSuccessDialogue(activity, "Article updated Successfully");
                }

                @Override
                public void onFailure(Exception ex) {

                    loadingDialogue.cancel();
                    CommonUtils.showDangerDialogue(activity, ex.getLocalizedMessage());

                }
            });

        } else {

            LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we add your new Article");

            articleRepository.insertArticleIntoDatabase(article, new FirebaseSuccessListener() {

                @Override
                public void onSuccess(Object obj) {

                    loadingDialogue.cancel();
                    CommonUtils.showSuccessDialogue(activity, "Article Added Successfully");

                }

                @Override
                public void onFailure(Exception ex) {

                    loadingDialogue.cancel();
                    CommonUtils.showDangerDialogue(activity, ex.getLocalizedMessage());

                }
            });
        }
    }

    // Validations
    public String validateArticleName(String articleName) {

        Log.d(TAG, "Article Name validation started");

        if (articleName == null || articleName.equals(STRING_EMPTY)) {

            String status = "Article Name Empty";
            Log.d(TAG, status);
            return status;

        } else if (articleName.length() < MIN_LENGTH_SMALL) {

            String status = "Article Name less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (articleName.length() > MAX_LENGTH_MEDIUM) {

            String status = "Article Name greater than " + MAX_LENGTH_MEDIUM + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public String validateArticleCategory(String articleCategory) {

        Log.d(TAG, "Article Category validation started");

        if (articleCategory == null || articleCategory.equals(STRING_EMPTY)) {

            String status = "Article Category Empty";
            Log.d(TAG, status);
            return status;

        } else if (articleCategory.length() < MIN_LENGTH_SMALL) {

            String status = "Article Category less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (articleCategory.length() > MAX_LENGTH_MEDIUM) {

            String status = "Article Category greater than " + MAX_LENGTH_MEDIUM + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public ViewSingleArticleSectionAddBinding addNewSection(Activity activity, LinearLayout sectionAppender, FilePickerUtils filePickerUtils, @Nullable ArticleSection articleSection) {

        if (articleSection == null) {
            articleSection = new ArticleSection();
        }

        Objects.requireNonNull(allSections.getValue()).add(articleSection);

        ViewSingleArticleSectionAddBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.view_single_article_section_add, null, false);
        binding.setArticleSection(articleSection);
        sectionAppender.addView(binding.getRoot(), CommonUtils.getLayoutParamWithSideMargins(8, 8, 8, 0));

        final ArticleSection finalArticleSection = articleSection;

        binding.IVCloseIcon.setOnClickListener(v -> CommonUtils.showConfirmationDialogue(activity, "Are you sure to delete this section?", "All the images and data will be deleted for this article section", "Yes", new ConfirmationListener() {
            @Override
            public void onPositive() {
                sectionAppender.removeView(binding.getRoot());
                allSections.getValue().remove(finalArticleSection);
                finalArticleSection.deleteAllMyImages();
            }

            @Override
            public void onNegative() {

            }
        }));
        binding.TVSelectImage.setOnClickListener(v -> {

            filePickerUtils.setFilePickerInterface((file, type) -> addArticleImageToStorage(activity, binding.LLImageAppender, finalArticleSection.getStorageImageList(), file));
            filePickerUtils.showNow();

        });

        return binding;
    }

    public void addArticleImageToStorage(Activity activity, LinearLayout sectionImageAppender, List<StorageImage> storageImageList, File file) {

        articleRepository.addArticleImageToDatabase(file, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                StorageImage storageImage = (StorageImage) obj;
                storageImageList.add(storageImage);
                Log.d(TAG, "onSuccess: new size of storageImage list: " + storageImageList.size());
                appendSingleImageTo(activity, sectionImageAppender, storageImageList, storageImage);
                Log.d(TAG, "onSuccess: added file to storage");
                Toast.makeText(activity, "Image added", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Exception ex) {

                Toast.makeText(activity, "Image: " + file.getName() + " upload failed", Toast.LENGTH_SHORT).show();

            }
        });

        Toast.makeText(activity, "Uploading image: " + file.getName(), Toast.LENGTH_SHORT).show();
    }

    private void appendSingleImageTo(Activity activity, LinearLayout appender, List<StorageImage> storageImageList, StorageImage storageImage) {

        ViewSingleImageBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.view_single_image, appender, false);
        appender.addView(binding.getRoot(), CommonUtils.getLayoutParamWithSideMargins(12, 12, 12, 12));

        Glide.with(activity)
                .load(storageImage.getDownloadURL())
                .into(binding.IVGridRowSingleImage);

        Log.d(TAG, "appendSingleImageTo: url: " + storageImage.getDownloadURL());

        binding.IVCloseIcon.setOnClickListener(v -> {

            storageImageList.remove(storageImage);
            appender.removeView(binding.getRoot());
            CommonUtils.deleteThisStorageImage(storageImage);
        });

    }

    public void initIDs(Subject subject) {
        this.subject = subject;
    }

    public void setEditArticle(Activity activity, Article editArticle, LinearLayout sectionAppender, FilePickerUtils filePickerUtils) {
        this.editArticle = editArticle;

        for (ArticleSection articleSection : editArticle.getArticleSections()) {

            ViewSingleArticleSectionAddBinding binding = addNewSection(activity, sectionAppender, filePickerUtils, articleSection);
            binding.setArticleSection(articleSection);
            for (StorageImage storageImage : articleSection.getStorageImageList()) {

                appendSingleImageTo(activity, binding.LLImageAppender, articleSection.getStorageImageList(), storageImage);

            }
        }
    }

    public boolean isUserInstructor() {

        return UserRegistration.getInstance().getCurrentUserFromDB().getAccountType() == User.TYPE_INSTRUCTOR;

    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void deleteEditArticle(Activity activity) {

        LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we are deleting your Article");
        articleRepository.deleteArticleWithID(editArticle.getId(), new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                loadingDialogue.cancel();
                CommonUtils.showSuccessDialogue(activity, "Article Deleted Successfully");
                activity.onBackPressed();

            }

            @Override
            public void onFailure(Exception ex) {

                loadingDialogue.cancel();
                CommonUtils.showDangerDialogue(activity, "Article Deleted Failed\n" + ex.getLocalizedMessage());

            }
        });

    }


}
