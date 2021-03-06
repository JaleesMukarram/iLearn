package com.openlearning.ilearn.quiz.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleQuestionBinding;
import com.openlearning.ilearn.interfaces.EditRequestListener;
import com.openlearning.ilearn.quiz.admin.modals.Quiz;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.admin.modals.QuizSection;

import java.util.List;

public class QuizQuestionAdapter extends RecyclerView.Adapter<QuizQuestionAdapter.NewsViewHolder> {

    private final Context context;
    private final Quiz quiz;
    private final QuizSection quizSection;
    private final List<QuizQuestion> quizQuestionList;
    private final EditRequestListener editRequestListener;


    public QuizQuestionAdapter(Context context, Quiz quiz, QuizSection quizSection, List<QuizQuestion> quizQuestionList, EditRequestListener editRequestListener) {
        this.context = context;
        this.quiz = quiz;
        this.quizSection = quizSection;
        this.quizQuestionList = quizQuestionList;
        this.editRequestListener = editRequestListener;
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


            });

            mBinding.IVThreeDots.setOnClickListener(v -> editRequestListener.onEditRequest(quizQuestionList.get(position)));
        }
    }

}
