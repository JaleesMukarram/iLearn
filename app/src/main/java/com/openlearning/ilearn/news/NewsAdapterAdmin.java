package com.openlearning.ilearn.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleNewsAdminBinding;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class NewsAdapterAdmin extends RecyclerView.Adapter<NewsAdapterAdmin.NewsViewHolder> {

    private final Context context;
    private final List<News> newsList;

    public NewsAdapterAdmin(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleNewsAdminBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_news_admin, parent, false);
        return new NewsViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ViewSingleNewsAdminBinding mBinding;

        public NewsViewHolder(@NonNull ViewSingleNewsAdminBinding itemBinding) {
            super(itemBinding.getRoot());

            mBinding = itemBinding;

            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.right_animation);
            itemView.setAnimation(animation);

        }

        private void setData(final int position) {

            mBinding.setNews(newsList.get(position));
            mBinding.getRoot().setOnClickListener(v -> {

                Intent intent = new Intent(context, AddNews.class);
                intent.putExtra(News.PARCELABLE_KEY, newsList.get(position));
                CommonUtils.changeActivity((Activity) context, intent, false);

            });

            if (!newsList.get(position).isActive()) {

                mBinding.CLMainContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrayLightMax));

            }
        }
    }
}
