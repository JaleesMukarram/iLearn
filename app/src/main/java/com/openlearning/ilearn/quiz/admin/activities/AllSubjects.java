package com.openlearning.ilearn.quiz.admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityAllSubjectsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.admin.adapters.SubjectAdapter;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.admin.view_models.AllSubjectsVM;
import com.openlearning.ilearn.utils.CommonUtils;

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

        mBinding.SRLSubjectRefresh.setOnRefreshListener(() -> viewModel.getSubjects(this, true));
        mBinding.BTNAddSubject.setOnClickListener(v -> CommonUtils.changeActivity(this, AddSubject.class, false));

    }

    @Override
    public void process() {

        viewModel.getSubjectList().observe(this, subjects -> {

            subjectList = subjects;
            loaded();
            showSubjectRecycler();
            mBinding.SRLSubjectRefresh.setRefreshing(false);


        });
        viewModel.getSubjectEmpty().observe(this, aBoolean -> {

            loaded();
            CommonUtils.showWarningDialogue(this, "No Subject Found. Your added Subjects will be displayed here");
            mBinding.SRLSubjectRefresh.setRefreshing(false);

        });


        viewModel.getSubjects(this, false);
    }

    @Override
    public void loaded() {

        mBinding.PBRLoading.setVisibility(View.GONE);
        mBinding.SRLSubjectRefresh.setVisibility(View.VISIBLE);

    }

    private void showSubjectRecycler() {

        SubjectAdapter subjectAdapter = new SubjectAdapter(this, subjectList);
        mBinding.RVAllSubjectsRecycler.setAdapter(subjectAdapter);

    }
}