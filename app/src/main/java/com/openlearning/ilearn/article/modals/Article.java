package com.openlearning.ilearn.article.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;
import com.openlearning.ilearn.modals.PostComment;
import com.openlearning.ilearn.modals.PostReact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Article implements Parcelable {

    public static final String PARCELABLE_KEY = "article_key";

    private String id;
    private String name;
    private String category;
    private String articleWriterID;
    private String subjectID;
    private List<ArticleSection> articleSections;
    private List<PostReact> postReactList = new ArrayList<>();
    private List<PostComment> postCommentList = new ArrayList<>();
    private boolean active;
    @ServerTimestamp
    private Date createdDate;

    public Article() {
    }

    public Article(String name, String category, String articleWriterID, String subjectID, List<ArticleSection> articleSections, boolean active) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.articleWriterID = articleWriterID;
        this.subjectID = subjectID;
        this.articleSections = articleSections;
        this.active = active;
    }

    protected Article(Parcel in) {
        id = in.readString();
        name = in.readString();
        category = in.readString();
        articleWriterID = in.readString();
        subjectID = in.readString();
        articleSections = in.createTypedArrayList(ArticleSection.CREATOR);
        postReactList = in.createTypedArrayList(PostReact.CREATOR);
        postCommentList = in.createTypedArrayList(PostComment.CREATOR);
        active = in.readByte() != 0;
        createdDate = (Date) in.readSerializable();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArticleWriterID() {
        return articleWriterID;
    }

    public void setArticleWriterID(String articleWriterID) {
        this.articleWriterID = articleWriterID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public List<ArticleSection> getArticleSections() {
        return articleSections;
    }

    public void setArticleSections(List<ArticleSection> articleSections) {
        this.articleSections = articleSections;
    }

    public List<PostReact> getPostReactList() {
        return postReactList;
    }

    public void setPostReactList(List<PostReact> postReactList) {
        this.postReactList = postReactList;
    }

    public List<PostComment> getPostCommentList() {
        return postCommentList;
    }

    public void setPostCommentList(List<PostComment> postCommentList) {
        this.postCommentList = postCommentList;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", articleWriterID='" + articleWriterID + '\'' +
                ", subjectID='" + subjectID + '\'' +
                ", articleSections=" + articleSections +
                ", active=" + active +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(articleWriterID);
        dest.writeString(subjectID);
        dest.writeTypedList(articleSections);
        dest.writeTypedList(postReactList);
        dest.writeTypedList(postCommentList);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeSerializable(createdDate);
    }
}
