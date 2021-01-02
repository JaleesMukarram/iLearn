package com.openlearning.ilearn.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivitySignUpBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.view_models.SignUpVM;

public class SignUp extends AppCompatActivity implements ActivityHooks {

    private ActivitySignUpBinding mBinding;
    private SignUpVM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
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
    public Intent getIntent() {
        return super.getIntent();
    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(SignUpVM.class);

        mBinding.TVSignInInstead.setOnClickListener(v -> startActivity(new Intent(SignUp.this, SignIn.class)));
        mBinding.BTNSignUp.setOnClickListener(v -> viewModel.validateUser(this, mBinding.ETEmail.getText().toString().trim(), mBinding.ETPassword.getText().toString().trim()));

    }

    @Override
    public void process() {

        viewModel.getSignUpSuccess().observe(this, aBoolean -> {

            if (aBoolean) {
                startActivity(new Intent(SignUp.this, CompleteRegistration.class));
                finish();
            }
        });

    }

    @Override
    public void loaded() {

    }


}