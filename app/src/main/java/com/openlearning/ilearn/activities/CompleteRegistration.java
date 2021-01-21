package com.openlearning.ilearn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivityCompleteRegistrationBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.view_models.CompleteRegistrationVM;

import static com.openlearning.ilearn.registration.User.TYPE_ARTICLE_WRITER;
import static com.openlearning.ilearn.registration.User.TYPE_GENERAL_USER;
import static com.openlearning.ilearn.registration.User.TYPE_INSTRUCTOR;

public class CompleteRegistration extends AppCompatActivity implements ActivityHooks {

    ActivityCompleteRegistrationBinding mBinding;
    CompleteRegistrationVM viewModel;
    private int currentTypeIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_complete_registration);
        viewModel = ViewModelProviders.of(this).get(CompleteRegistrationVM.class);

        firstUserTypeSelected();

        mBinding.IVLeft.setOnClickListener(v -> onTypeIndexChanged(false));

        mBinding.IVRight.setOnClickListener(v -> onTypeIndexChanged(true));

        mBinding.BTNFinish.setOnClickListener(v -> viewModel.validateName(this, mBinding.ETName.getText().toString().trim()));

    }

    @Override
    public void process() {

        mBinding.setEmail(viewModel.getUserEmail());

    }

    @Override
    public void loaded() {

    }

    private void onTypeIndexChanged(boolean next) {

        currentTypeIndex = next ? currentTypeIndex + 1 : currentTypeIndex - 1;

        if (currentTypeIndex == 0) {

            firstUserTypeSelected();
        } else if (currentTypeIndex == 1) {

            secondUserTypeSelected();
        } else if (currentTypeIndex == 2) {

            thirdUserTypeSelected();
        }
    }

    private void firstUserTypeSelected() {

        mBinding.IVLeft.setVisibility(View.GONE);
        mBinding.IVRight.setVisibility(View.VISIBLE);

        mBinding.IVAccountType.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_reading));
        mBinding.TVAccountType.setText(R.string.student);

        viewModel.setUserType(TYPE_GENERAL_USER);


    }

    private void secondUserTypeSelected() {

        mBinding.IVLeft.setVisibility(View.VISIBLE);
        mBinding.IVRight.setVisibility(View.VISIBLE);

        mBinding.IVAccountType.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_instructor));
        mBinding.TVAccountType.setText(R.string.instructor);

        viewModel.setUserType(TYPE_INSTRUCTOR);


    }

    private void thirdUserTypeSelected() {

        mBinding.IVLeft.setVisibility(View.VISIBLE);
        mBinding.IVRight.setVisibility(View.GONE);

        mBinding.IVAccountType.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_content_writing));
        mBinding.TVAccountType.setText(R.string.article_writer);

        viewModel.setUserType(TYPE_ARTICLE_WRITER);

    }

}