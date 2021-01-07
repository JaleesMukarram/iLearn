package com.openlearning.ilearn.quiz.client.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleSubjectClientBinding;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.activities.ShowQuiz;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class SubjectAdapterClient extends RecyclerView.Adapter<SubjectAdapterClient.NewsViewHolder> {

    private static final String TAG = "SubjAdrClientTAG";
    private final Context context;
    private final List<Subject> subjectList;

    public SubjectAdapterClient(Context context, List<Subject> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleSubjectClientBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_subject_client, parent, false);
        return new NewsViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ViewSingleSubjectClientBinding mBinding;

        public NewsViewHolder(@NonNull ViewSingleSubjectClientBinding itemBinding) {
            super(itemBinding.getRoot());

            mBinding = itemBinding;

            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.right_animation);
            itemView.setAnimation(animation);

        }

        private void setData(final int position) {

            mBinding.setSubject(subjectList.get(position));
            mBinding.getRoot().setOnClickListener(v -> {

                Intent intent = new Intent(context, ShowQuiz.class);
                intent.putExtra(Subject.PARCELABLE_KEY, subjectList.get(position));
                CommonUtils.changeActivity((Activity) context, intent, false);

            });

        }
    }
}
