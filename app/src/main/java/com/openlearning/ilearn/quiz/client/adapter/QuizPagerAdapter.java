package com.openlearning.ilearn.quiz.client.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.openlearning.ilearn.quiz.client.layers.SectionManager;

public class QuizPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "QuizAdapter";
    private final SectionManager manager;

    public QuizPagerAdapter(@NonNull FragmentActivity fragmentActivity, SectionManager manager) {
        super(fragmentActivity);
        this.manager = manager;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return manager.getQuestionFragmentAt(position);
    }

    @Override
    public int getItemCount() {

        return manager.getTotalQuestionOfActiveSection();
    }

}
