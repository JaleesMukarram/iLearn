package com.openlearning.ilearn.quiz.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleQuestionBinding;
import com.openlearning.ilearn.databinding.ViewSingleQuizSectionBinding;
import com.openlearning.ilearn.quiz.activities.AddQuestions;
import com.openlearning.ilearn.quiz.modals.Quiz;
import com.openlearning.ilearn.quiz.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.modals.QuizSection;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.NewsViewHolder> {

    private final Context context;
    private final Quiz quiz;
    private final QuizSection quizSection;
    private final List<QuizQuestion> quizQuestionList;

    public QuizQuestionAdapter(Context context, Quiz quiz, QuizSection quizSection, List<QuizQuestion> quizQuestionList) {
        this.context = context;
        this.quiz = quiz;
        this.quizSection = quizSection;
        this.quizQuestionList = quizQuestionList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleQuestionBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_question, parent, false);
        return new NewsViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {

        if (quizQuestionList == null) {
            return 0;
        }
        return quizQuestionList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ViewSingleQuestionBinding mBinding;

        public NewsViewHolder(@NonNull ViewSingleQuestionBinding itemBinding) {
            super(itemBinding.getRoot());

            mBinding = itemBinding;


        }

        private void setData(final int position) {

            mBinding.setQuestion(quizQuestionList.get(position));
            mBinding.getRoot().setOnClickListener(v -> {

//                Intent intent = new Intent(context, AddQuestions.class);
//                intent.putExtra(Quiz.PARCELABLE_KEY, (Parcelable) quiz);
//                intent.putExtra(QuizSection.PARCELABLE_KEY, (Parcelable) quizSectionList.get(position));
//                CommonUtils.changeActivity((Activity) context, intent, false);

            });
        }
    }
}
