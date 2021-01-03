package com.openlearning.ilearn.quiz.admin.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleQuestionOptionBinding;
import com.openlearning.ilearn.quiz.admin.modals.QuestionOption;

import java.util.ArrayList;
import java.util.List;

public class OptionAddingUtils {

    private final List<ViewSingleQuestionOptionBinding> bindingList;

    public OptionAddingUtils() {
        bindingList = new ArrayList<>();
    }

    public void addNewOptionSpace(LinearLayout appender, QuestionOption questionOption) {

        ViewSingleQuestionOptionBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(appender.getContext()), R.layout.view_single_question_option, appender, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mBinding.IVDelete.setOnClickListener(v -> {

            appender.removeView(mBinding.getRoot());
            bindingList.remove(mBinding);

        });

        mBinding.SWCorrectOption.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                unMarkAllOther(mBinding);
            }

        });

        if (questionOption != null) {

            mBinding.setOption(questionOption);
        }


        appender.addView(mBinding.getRoot(), params);
        bindingList.add(mBinding);

    }

    private void unMarkAllOther(ViewSingleQuestionOptionBinding mBinding) {

        for (ViewSingleQuestionOptionBinding binding : bindingList) {

            if (binding.equals(mBinding)) {
                continue;
            }

            binding.SWCorrectOption.setChecked(false);
        }
    }

    public boolean validateAllOptions(Context context) {

        if (bindingList.size() <= 1) {

            Toast.makeText(context, "Please add at least 2 options", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (bindingList.size() > 6) {

            Toast.makeText(context, "Please add at most 6 options", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean someMarked = false;

        for (ViewSingleQuestionOptionBinding binding : bindingList) {

            if (binding.ETOption.getText().toString().trim().length() <= 0) {
                Toast.makeText(context, "Option empty", Toast.LENGTH_SHORT).show();
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

    public List<QuestionOption> getOptionsList(String questionID) {

        List<QuestionOption> questionOptionList = new ArrayList<>();

        for (ViewSingleQuestionOptionBinding binding : bindingList) {

            QuestionOption questionOption = new QuestionOption(
                    binding.ETOption.getText().toString().trim(),
                    questionID,
                    binding.SWCorrectOption.isChecked()
            );
            questionOptionList.add(questionOption);
        }

        return questionOptionList;
    }
}
