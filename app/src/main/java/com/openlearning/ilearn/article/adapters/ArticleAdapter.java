package com.openlearning.ilearn.article.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.article.activities.AddSubjectArticle;
import com.openlearning.ilearn.article.activities.ArticleDetails;
import com.openlearning.ilearn.article.modals.Article;
import com.openlearning.ilearn.databinding.ViewSingleArticleBinding;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.registration.User;
import com.openlearning.ilearn.registration.UserRegistration;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private static final String TAG = "SubjAdaptTAG";
    private final Context context;
    private final Subject subject;
    private final List<Article> articleList;

    public ArticleAdapter(Context context, Subject subject, List<Article> articleList) {
        this.context = context;
        this.subject = subject;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleArticleBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_article, parent, false);
        return new ArticleViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        private final ViewSingleArticleBinding mBinding;

        public ArticleViewHolder(@NonNull ViewSingleArticleBinding itemBinding) {
            super(itemBinding.getRoot());

            mBinding = itemBinding;

            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.down_animation);
            itemView.setAnimation(animation);

        }

        private void setData(final int position) {

            mBinding.setArticle(articleList.get(position));
            mBinding.getRoot().setOnClickListener(v -> {

                Intent intent = new Intent(context, ArticleDetails.class);
                intent.putExtra(Article.PARCELABLE_KEY, articleList.get(position));
                CommonUtils.changeActivity((Activity) context, intent, false);

            });

            if (UserRegistration.getInstance().getCurrentUserFromDB().getAccountType() == User.TYPE_GENERAL_USER) {

                mBinding.IVThreeDots.setVisibility(View.GONE);
            } else {

                mBinding.IVThreeDots.setOnClickListener(v -> {

                    Intent intent = new Intent(context, AddSubjectArticle.class);
                    intent.putExtra(Article.PARCELABLE_KEY, articleList.get(position));
                    intent.putExtra(Subject.PARCELABLE_KEY, subject);
                    CommonUtils.changeActivity((Activity) context, intent, false);

                });
            }

            if (!articleList.get(position).isActive()) {

                mBinding.CLMainContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrayLightMax));

            }
        }
    }
}
