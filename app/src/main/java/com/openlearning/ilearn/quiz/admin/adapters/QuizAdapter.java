package com.openlearning.ilearn.quiz.admin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleQuizBinding;
import com.openlearning.ilearn.quiz.admin.activities.AddQuiz;
import com.openlearning.ilearn.quiz.admin.activities.AllQuizSections;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.NewsViewHolder> {

    private final Context context;
    private final List<Quiz> quizList;
    private final Subject quizSubject;

    public QuizAdapter(Context context, Subject quizSubject, List<Quiz> quizList) {
        this.context = context;
        this.quizSubject = quizSubject;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleQuizBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_quiz, parent, false);
        return new NewsViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ViewSingleQuizBinding mBinding;

        public NewsViewHolder(@NonNull ViewSingleQuizBinding itemBinding) {
            super(itemBinding.getRoot());

            mBinding = itemBinding;

            Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.right_animation);
            itemView.setAnimation(animation);

        }

        private void setData(final int position) {

            mBinding.setQuiz(quizList.get(position));

            mBinding.IVThreeDots.setOnClickListener(v -> {

                Intent intent = new Intent(context, AddQuiz.class);
                intent.putExtra(Subject.PARCELABLE_KEY, quizSubject);
                intent.putExtra(Quiz.PARCELABLE_KEY, (Parcelable) quizList.get(position));
                CommonUtils.changeActivity((Activity) context, intent, false);

            });

            mBinding.getRoot().setOnClickListener(v -> {

                Intent intent = new Intent(context, AllQuizSections.class);
                intent.putExtra(Quiz.PARCELABLE_KEY, (Parcelable) quizList.get(position));
                CommonUtils.changeActivity((Activity) context, intent, false);

            });
        }
    }
}
