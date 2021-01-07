package com.openlearning.ilearn.quiz.client.adapter;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleQuestionInSubmitBinding;
import com.openlearning.ilearn.quiz.admin.modals.QuizQuestion;
import com.openlearning.ilearn.quiz.client.layers.SectionManager;

public class QuestionStatusAdapter extends RecyclerView.Adapter<QuestionStatusAdapter.QuestionStatusViewHolder> {

    private final SectionManager sectionManager;
    private final Dialog dialog;

    public QuestionStatusAdapter(SectionManager sectionManager, Dialog dialog) {
        this.sectionManager = sectionManager;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public QuestionStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleQuestionInSubmitBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_question_in_submit, null, false);
        return new QuestionStatusViewHolder(mBinding, dialog);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionStatusViewHolder holder, int position) {

        holder.setData(position);

    }

    @Override
    public int getItemCount() {

        return sectionManager.getTotalQuestionOfActiveSection();
    }

    public class QuestionStatusViewHolder extends RecyclerView.ViewHolder {

        private final Dialog dialog;
        private QuizQuestion modal;
        private final ViewSingleQuestionInSubmitBinding mBinding;

        public QuestionStatusViewHolder(@NonNull ViewSingleQuestionInSubmitBinding mBinding, Dialog dialog) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            this.dialog = dialog;

        }

        void setData(int position) {

            modal = sectionManager.getCurrentSectionQuestionAt(position);
            mBinding.txtQuestion.setText(String.valueOf(position + 1));


            checkedIsMarked();


            if (modal.isVisited() && modal.isAnswered()) {

                mBinding.txtQuestion.setBackground(ContextCompat.getDrawable(sectionManager.getContext(), R.drawable.circular_background_answered));


            } else if (modal.isVisited() && !modal.isAnswered()) {

                mBinding.txtQuestion.setBackground(ContextCompat.getDrawable(sectionManager.getContext(), R.drawable.circular_background_not_answered));

            } else {

                mBinding.txtQuestion.setBackground(ContextCompat.getDrawable(sectionManager.getContext(), R.drawable.circular_background_grey));

            }

            setListener(position);
        }

        private void setListener(final int position) {

            mBinding.txtQuestion.setOnClickListener(view -> {
                sectionManager.moveToThisPosition(position);
                if (dialog != null)
                    dialog.cancel();
            });
        }

        private void checkedIsMarked() {

            if (modal.isMarked())
                mBinding.imgMarked.setVisibility(View.VISIBLE);
            else
                mBinding.imgMarked.setVisibility(View.INVISIBLE);
        }

    }
}
