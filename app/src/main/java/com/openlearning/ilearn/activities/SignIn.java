package com.openlearning.ilearn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivitySignInBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.view_models.SignInVM;
import com.openlearning.ilearn.view_models.SplashVM;

public class SignIn extends AppCompatActivity implements ActivityHooks {

    private ActivitySignInBinding mBinding;
    private SignInVM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

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

        viewModel = ViewModelProviders.of(this).get(SignInVM.class);

        mBinding.TVSignUpInstead.setOnClickListener(v -> startActivity(new Intent(SignIn.this, SignUp.class)));
        mBinding.BTNSignIn.setOnClickListener(v -> viewModel.validateUser(this, mBinding.ETEmail.getText().toString().trim(), mBinding.ETPassword.getText().toString().trim()));

    }

    @Override
    public void process() {

    }

    @Override
    public void loaded() {

    }
}