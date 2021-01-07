package com.openlearning.ilearn.quiz.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityShowSubjectsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.quiz.admin.adapters.SubjectAdapter;
import com.openlearning.ilearn.quiz.admin.modals.Subject;
import com.openlearning.ilearn.quiz.client.adapters.SubjectAdapterClient;
import com.openlearning.ilearn.quiz.client.view_models.ShowSubjectsVM;
import com.openlearning.ilearn.utils.CommonUtils;

import java.util.List;

public class ShowSubjects extends AppCompatActivity implements ActivityHooks {

    private ActivityShowSubjectsBinding mBinding;
    private ShowSubjectsVM viewModel;
    private List<Subject> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_subjects);

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

        viewModel = ViewModelProviders.of(this).get(ShowSubjectsVM.class);
        mBinding.SRLNewsRefresh.setOnRefreshListener(() -> viewModel.getSubjects(this, true));

    }

    @Override
    public void process() {

        viewModel.getSubjectsList().observe(this, subjects -> {

            subjectList = subjects;
            loaded();
            showSubjectRecycler();
            mBinding.SRLNewsRefresh.setRefreshing(false);

        });

        viewModel.getSubjectsEmpty().observe(this, aBoolean -> {

            if (aBoolean) {

                loaded();
                CommonUtils.showWarningDialogue(this, "No Subjects are being offered right now!\n Come back later");
                mBinding.SRLNewsRefresh.setRefreshing(false);

            }
        });

        viewModel.getSubjects(this, false);
    }

    @Override
    public void loaded() {

        mBinding.PBRLoading.setVisibility(View.GONE);
        mBinding.SRLNewsRefresh.setVisibility(View.VISIBLE);

    }

    private void showSubjectRecycler() {

        SubjectAdapterClient subjectAdapter = new SubjectAdapterClient(this, subjectList);
        mBinding.RVAllSubjectsRecycler.setAdapter(subjectAdapter);

    }
}