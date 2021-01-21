package com.openlearning.ilearn.article.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MAX;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_MEDIUM;
import static com.openlearning.ilearn.utils.CommonUtils.MAX_LENGTH_SMALL;
import static com.openlearning.ilearn.utils.CommonUtils.MIN_LENGTH_SMALL;

@IgnoreExtraProperties
public class ArticleSection implements Parcelable {

    private String name;
    private String content;
    private List<StorageImage> storageImageList = new ArrayList<>();

    public ArticleSection() {
    }

    public ArticleSection(String name, String content, List<StorageImage> storageImageList) {
        this.name = name;
        this.content = content;
        this.storageImageList = storageImageList;
    }

    protected ArticleSection(Parcel in) {
        name = in.readString();
        content = in.readString();
        storageImageList = in.createTypedArrayList(StorageImage.CREATOR);
    }

    public static final Creator<ArticleSection> CREATOR = new Creator<ArticleSection>() {
        @Override
        public ArticleSection createFromParcel(Parcel in) {
            return new ArticleSection(in);
        }

        @Override
        public ArticleSection[] newArray(int size) {
            return new ArticleSection[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<StorageImage> getStorageImageList() {
        return storageImageList;
    }

    public void setStorageImageList(List<StorageImage> storageImageList) {
        this.storageImageList = storageImageList;
    }

    @Override
    public String toString() {
        return "ArticleSection{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", storageImageList=" + storageImageList +
                '}';
    }

    public void deleteAllMyImages() {

        for (StorageImage storageImage : storageImageList) {

            CommonUtils.deleteThisStorageImage(storageImage);
        }
    }

    @Exclude
    public boolean isValid() {

        if (name == null || name.length() < MIN_LENGTH_SMALL || name.length() > MAX_LENGTH_MEDIUM) {

            return false;

        }

        return content != null && content.length() >= MIN_LENGTH_SMALL && content.length() <= MAX_LENGTH_MAX;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(content);
        dest.writeTypedList(storageImageList);
    }
}
