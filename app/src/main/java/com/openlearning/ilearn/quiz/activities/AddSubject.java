package com.openlearning.ilearn.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAddSubjectBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.modals.Subject;
import com.openlearning.ilearn.quiz.view_models.AddSubjectVM;

public class AddSubject extends AppCompatActivity implements ActivityHooks {

    private ActivityAddSubjectBinding mBinding;
    private AddSubjectVM viewModel;
    private Subject editSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_subject);
        callHooks();
    }

    @Override
    public void callHooks() {

        handleIntent();
        init();
        process();

    }

    @Override
    public void handleIntent() {

        editSubject = getIntent().getParcelableExtra(Subject.PARCELABLE_KEY);

    }

    @Override
    public void init() {

        viewModel = ViewModelProviders
                .of(this)
                .get(AddSubjectVM.class);

        if (editSubject != null) {

            viewModel.setEditSubject(editSubject);
            mBinding.setSubject(editSubject);
            mBinding.BTNAddSubject.setText(R.string.edit_subject);
        }

        mBinding.BTNAddSubject.setOnClickListener(v -> viewModel.checkSubject(
                this,
                mBinding.ETSubjectName.getText().toString().trim(),
                mBinding.ETSubjectDescription.getText().toString()
        ));

        mBinding.IVThreeDots.setOnClickListener(v -> {


        });


    }

    @Override
    public void process() {


    }

    @Override
    public void loaded() {

    }
}