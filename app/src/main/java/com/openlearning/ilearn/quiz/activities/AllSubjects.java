package com.openlearning.ilearn.quiz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAllSubjectsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.news.NewsAdapterAdmin;
import com.openlearning.ilearn.quiz.adapters.SubjectAdapter;
import com.openlearning.ilearn.quiz.modals.Subject;
import com.openlearning.ilearn.quiz.view_models.AllSubjectsVM;

import java.util.ArrayList;
import java.util.List;

public class AllSubjects extends AppCompatActivity implements ActivityHooks {

    private ActivityAllSubjectsBinding mBinding;
    private AllSubjectsVM viewModel;
    private List<Subject> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_subjects);

        callHooks();
    }

    @Override
    public void callHooks() {

        init();
        process();

    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void init() {

        subjectList = new ArrayList<>();

        viewModel = ViewModelProviders.of(this).get(AllSubjectsVM.class);


    }

    @Override
    public void process() {

        viewModel.getSubjectList().observe(this, subjects -> {

            subjectList = subjects;
            showNewsRecycler();


        });
        viewModel.getSubjects();

    }

    @Override
    public void loaded() {

    }

    private void showNewsRecycler() {

        SubjectAdapter subjectAdapter = new SubjectAdapter(this, subjectList);
        mBinding.RVAllSubjectsRecycler.setAdapter(subjectAdapter);

    }
}