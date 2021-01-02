package com.openlearning.ilearn.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.google.firebase.auth.FirebaseAuth;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ActivitySplashBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.registration.User;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.utils.FilePickerUtils;
import com.openlearning.ilearn.view_models.SplashVM;

import java.io.File;

import static com.openlearning.ilearn.registration.User.ACCOUNT_HOLDED;
import static com.openlearning.ilearn.registration.User.STATUS_COMPLETE_REGISTERED;
import static com.openlearning.ilearn.registration.User.STATUS_HALF_REGISTERED;
import static com.openlearning.ilearn.registration.User.STATUS_NOT_REGISTERED;
import static com.openlearning.ilearn.registration.User.STATUS_UNKNOWN_REGISTERED;

public class Splash extends AppCompatActivity implements ActivityHooks {

    ActivitySplashBinding mBinding;
    SplashVM viewModel;
    public static final String TAG = "SplashTAG";
    private boolean isThreadCompleted;
    private boolean pendingChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

//        FirebaseAuth.getInstance().signOut();
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

        viewModel = ViewModelProviders.of(this).get(SplashVM.class);

    }

    @Override
    public void process() {

        startAnimationThread();
        viewModel.checkRegistrationStatus();

        viewModel.getUserStatus().observe(this, integer -> {

            switch (integer) {

                case STATUS_COMPLETE_REGISTERED:

                    Log.d(TAG, "process: STATUS_COMPLETE_REGISTERED");

                    if (viewModel.getUser().getAccountType() == User.TYPE_GENERAL_USER) {
                        CommonUtils.changeActivity(this, HomeScreen.class, true);
                    } else if (viewModel.getUser().getAccountType() == User.TYPE_INSTRUCTOR) {
                        CommonUtils.changeActivity(this, HomeScreenInstructor.class, true);
                    }

                    break;

                case STATUS_NOT_REGISTERED:

                    Log.d(TAG, "process: STATUS_NOT_REGISTERED");

                    if (isThreadCompleted) {
                        CommonUtils.changeActivity(this, SignIn.class, true);
                    } else {
                        pendingChange = true;
                    }

                    break;


                case STATUS_UNKNOWN_REGISTERED:
                    Log.d(TAG, "process: STATUS_UNKNOWN_REGISTERED");
                    CommonUtils.showWarningDialogue(this, "Your account could not be verified, Please try again later");
                    break;

                case STATUS_HALF_REGISTERED:
                    Log.d(TAG, "process: STATUS_UNKNOWN_REGISTERED");
                    CommonUtils.changeActivity(this, CompleteRegistration.class, true);

                    break;

                case ACCOUNT_HOLDED:
                    Log.d(TAG, "process: ACCOUNT_HOLDED");
                    CommonUtils.showDangerDialogue(this, "Your account has been suspended by the administration. Please retry login tomorrow");

                    break;

            }
        });

    }

    @Override
    public void loaded() {

    }

    private void startAnimationThread() {

        new Thread() {

            @Override
            public void run() {

                for (int i = 0; i < 100; i++) {

                    try {

                        sleep(10);
                        mBinding.AppName.setAlpha(i / 100f);
//                        Log.d(TAG, "run: " + (i / 100f));


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

                mBinding.AppName.setAlpha(1f);
                runOnUiThread(() -> {
                    animateAppLogo();
                    mBinding.AppSlogan.setVisibility(View.VISIBLE);
                });

            }
        }.start();
    }

    private void animateAppLogo() {

        mBinding.AppIcon.setVisibility(View.VISIBLE);

        ViewCompat.animate(mBinding.AppIcon).
                translationY(-50f)
                .setStartDelay(50)
                .setDuration(800)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        view.setAlpha(0.7f);
                    }

                    @Override
                    public void onAnimationEnd(View view) {

                        view.setAlpha(1f);
                        isThreadCompleted = true;
                        if (pendingChange) {
                            CommonUtils.changeActivity(Splash.this, SignIn.class, true);
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                    }
                })
                .setInterpolator(new DecelerateInterpolator(1.2f)).start();

    }


}