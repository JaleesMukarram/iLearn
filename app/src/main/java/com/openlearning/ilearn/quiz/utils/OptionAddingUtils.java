package com.openlearning.ilearn.quiz.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleQuestionOptionBinding;
import com.openlearning.ilearn.quiz.modals.QuestionOption;
import com.openlearning.ilearn.quiz.modals.Quiz;

import java.util.ArrayList;
import java.util.List;

public class OptionAddingUtils {

    private final List<ViewSingleQuestionOptionBinding> bindingList;
    private final Quiz quiz;

    public OptionAddingUtils(Quiz quiz) {
        bindingList = new ArrayList<>();
        this.quiz = quiz;
    }

    public void addNewOptionSpace(LinearLayout appender) {

        ViewSingleQuestionOptionBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(appender.getContext()), R.layout.view_single_question_option, appender, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mBinding.IVDelete.setOnClickListener(v -> {

            appender.removeView(mBinding.getRoot());
            bindingList.remove(mBinding);
        });

        mBinding.SWCorrectOption.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                removeAllOtherChecked(mBinding);
            }

        });


        appender.addView(mBinding.getRoot(), params);
        bindingList.add(mBinding);

    }

    private void removeAllOtherChecked(ViewSingleQuestionOptionBinding mBinding) {

        for (ViewSingleQuestionOptionBinding binding : bindingList) {

            if (binding.equals(mBinding)) {
                continue;
            }

            binding.SWCorrectOption.setChecked(false);
        }
    }

    public boolean checkOptions(Context context) {

        if (bindingList.size() <= 1) {

            Toast.makeText(context, "Please add at least 2 options", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean someMarked = false;

        for (ViewSingleQuestionOptionBinding binding : bindingList) {

            if (binding.ETOption.getText().toString().trim().length() <= 3) {
                Toast.makeText(context, "Option text too less", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (binding.SWCorrectOption.isChecked()) {
                someMarked = true;
            }
        }

        if (!someMarked) {

            Toast.makeText(context, "Please mark one option as correct", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public List<QuestionOption> getOptionsList() {

        List<QuestionOption> questionOptionList = new ArrayList<>();

        for (ViewSingleQuestionOptionBinding binding : bindingList) {

            QuestionOption questionOption = new QuestionOption(
                    binding.ETOption.getText().toString().trim(),
                    quiz.getQuizID(),
                    binding.SWCorrectOption.isChecked()
            );
            questionOptionList.add(questionOption);
        }

        return questionOptionList;
    }
}
