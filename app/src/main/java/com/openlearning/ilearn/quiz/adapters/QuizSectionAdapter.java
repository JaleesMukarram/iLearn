package com.openlearning.ilearn.quiz.adapters;

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
import com.openlearning.ilearn.databinding.ViewSingleQuizSectionBinding;
import com.openlearning.ilearn.quiz.activities.AddQuestions;
import com.openlearning.ilearn.quiz.activities.QuizSections;
import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.QuizSection;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class QuizSectionAdapter extends RecyclerView.Adapter<QuizSectionAdapter.NewsViewHolder> {

    private final Context context;
    private final Quiz quiz;
    private final List<QuizSection> quizSectionList;

    public QuizSectionAdapter(Context context, Quiz quiz, List<QuizSection> quizSectionList) {
        this.context = context;
        this.quiz = quiz;
        this.quizSectionList = quizSectionList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleQuizSectionBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_quiz_section, parent, false);
        return new NewsViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        if (quizSectionList  == null){
            return 0;
        }
        return quizSectionList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ViewSingleQuizSectionBinding mBinding;

        public NewsViewHolder(@NonNull ViewSingleQuizSectionBinding itemBinding) {
            super(itemBinding.getRoot());

            mBinding = itemBinding;


        }

        private void setData(final int position) {

            mBinding.setQuizSection(quizSectionList.get(position));
            mBinding.getRoot().setOnClickListener(v -> {

                Intent intent = new Intent(context, AddQuestions.class);
                intent.putExtra(Quiz.PARCELABLE_KEY, (Parcelable) quiz);
                intent.putExtra(QuizSection.PARCELABLE_KEY, (Parcelable) quizSectionList.get(position));
                CommonUtils.changeActivity((Activity) context, intent, false);

            });
        }
    }
}
