package com.openlearning.ilearn.news;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleImageBinding;
import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MAX;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MEDIUM;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.STRING_EMPTY;
import static com.openlearning.ilearn.utils.CommonUtils.VALIDATION_SUCCESS;

public class AddNewsVM extends ViewModel {

    private static final String TAG = "AddNewsVMTAG";
    private final NewsRepository newsRepository;
    private final UserRegistration userRegistration;
    private boolean active = true;
    private News editNews;

    private final MutableLiveData<List<StorageImage>> storageImageList;

    public AddNewsVM() {

        newsRepository = NewsRepository.getInstance();
        userRegistration = UserRegistration.getInstance();
        storageImageList = new MutableLiveData<>();
        storageImageList.setValue(new ArrayList<>());

    }

    public void checkNews(Activity activity, String newsHeading, String newsTitle, String newsBody) {

        String newsHeadingStatus = validateNewsHeading(newsHeading);
        if (!newsHeadingStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, newsHeadingStatus);
            return;
        }


        String newsTitleStatus = validateNewsTitle(newsTitle);
        if (!newsTitleStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, newsTitleStatus);
            return;
        }

        String newsBodyStatus = validateNewsBody(newsBody);
        if (!newsBodyStatus.equals(VALIDATION_SUCCESS)) {
            CommonUtils.showWarningDialogue(activity, newsBodyStatus);
            return;
        }

        News news = makeNews(newsHeading, newsTitle, newsBody);
        Log.d(TAG, "checkNews: News made");

        addNewsToDatabase(activity, news);
    }

    private void addNewsToDatabase(Activity activity, News news) {


        if (editNews != null) {
            LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we add your breaking News");

            newsRepository.updateThisNewsIntoDatabase(news, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    loadingDialogue.cancel();
                    String message = "News Added Successfully";
                    if (editNews != null) {
                        message = "News Edited Successfully";
                    }
                    CommonUtils.showSuccessDialogue(activity, message);
                }

                @Override
                public void onFailure(Exception ex) {

                    loadingDialogue.cancel();
                    CommonUtils.showDangerDialogue(activity, ex.getLocalizedMessage());

                }
            });

        } else {
            LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we edit your breaking News");

            newsRepository.insertNewsIntoDatabase(news, new FirebaseSuccessListener() {

                @Override
                public void onSuccess(Object obj) {

                    loadingDialogue.cancel();
                    CommonUtils.showSuccessDialogue(activity, "News Added Successfully");

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
    public String validateNewsHeading(String newsHeading) {

        Log.d(TAG, "New Heading validation started");

        if (newsHeading == null || newsHeading.equals(STRING_EMPTY)) {

            String status = "New Heading Empty";
            Log.d(TAG, status);
            return status;

        } else if (newsHeading.length() < MIN_LENGTH_SMALL) {

            String status = "New Heading less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (newsHeading.length() > MAX_LENGTH_MEDIUM) {

            String status = "New Heading greater than " + MAX_LENGTH_MEDIUM + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public String validateNewsTitle(String newsTitle) {

        Log.d(TAG, "News Title validation started");

        if (newsTitle == null || newsTitle.equals(STRING_EMPTY)) {

            String status = "News Title Empty";
            Log.d(TAG, status);
            return status;

        } else if (newsTitle.length() < MIN_LENGTH_SMALL) {

            String status = "News Title less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (newsTitle.length() > MAX_LENGTH_MEDIUM) {

            String status = "News Title greater than " + MAX_LENGTH_MEDIUM + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public String validateNewsBody(String newsBody) {

        Log.d(TAG, "News Body validation started");

        if (newsBody == null || newsBody.equals(STRING_EMPTY)) {

            String status = "News Body Empty";
            Log.d(TAG, status);
            return status;

        } else if (newsBody.length() < MIN_LENGTH_SMALL) {

            String status = "News Body less than " + MIN_LENGTH_SMALL + " characters";
            Log.d(TAG, status);
            return status;

        } else if (newsBody.length() > MAX_LENGTH_MAX) {

            String status = "News Body greater than " + MAX_LENGTH_MAX + " characters";
            Log.d(TAG, status);
            return status;

        }

        return VALIDATION_SUCCESS;

    }

    public News makeNews(String newsHeading, String newsTitle, String newsBody) {

        if (editNews != null) {

            editNews.setHeading(newsHeading);
            editNews.setTitle(newsTitle);
            editNews.setBody(newsBody);
            editNews.setStorageImageList(storageImageList.getValue());
            editNews.setActive(active);
            return editNews;
        }

        return new News(newsHeading, newsTitle, newsBody, userRegistration.getUserID(), active, storageImageList.getValue());

    }

    public void setEditNews(News editNews) {

        this.editNews = editNews;
        this.storageImageList.setValue(editNews.getStorageImageList());
        Log.d(TAG, "setEditNews: News Added for editing");

    }

    public void addNewsImageToStorage(Activity activity, File file) {

        newsRepository.addNewsImageToDatabase(file, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                List<StorageImage> oldList = storageImageList.getValue();
                assert oldList != null;
                oldList.add((StorageImage) obj);
                storageImageList.setValue(oldList);
                Log.d(TAG, "onSuccess: added file to storage");

            }

            @Override
            public void onFailure(Exception ex) {

                Toast.makeText(activity, "Image: " + file.getName() + " upload failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void updateAllImages(Activity activity, LinearLayout appender) {

        appender.removeAllViews();

        for (StorageImage storageImage : Objects.requireNonNull(storageImageList.getValue())) {

            ViewSingleImageBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.view_single_image, null, false);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(12, 12, 12, 12);
            appender.addView(binding.getRoot(), layoutParams);

            Glide.with(activity)
                    .load(storageImage.getDownloadURL())
                    .into(binding.IVGridRowSingleImage);

            binding.IVCloseIcon.setOnClickListener(v -> {

                storageImageList.getValue().remove(storageImage);
                appender.removeView(binding.getRoot());
                CommonUtils.deleteThisStorageImage(storageImage);
            });

        }
    }

    public void deleteEditNews(Activity activity) {

        LoadingDialogue loadingDialogue = CommonUtils.showLoadingDialogue(activity, "Please wait...", "Please wait while we are deleting your News");
        newsRepository.deleteNewsWithID(editNews.getId(), new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                loadingDialogue.cancel();
                CommonUtils.showSuccessDialogue(activity, "News Deleted Successfully");

            }

            @Override
            public void onFailure(Exception ex) {

                loadingDialogue.cancel();
                CommonUtils.showDangerDialogue(activity, "News Deleted Failed\n" + ex.getLocalizedMessage());

            }
        });

    }

    public MutableLiveData<List<StorageImage>> getStorageImageList() {
        return storageImageList;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
