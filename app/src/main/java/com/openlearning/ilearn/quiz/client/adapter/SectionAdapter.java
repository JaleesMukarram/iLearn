package com.openlearning.ilearn.quiz.client.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewSingleSectionBinding;
import com.openlearning.ilearn.quiz.client.layers.QuizLogic;
import com.openlearning.ilearn.quiz.client.layers.SectionManager;

import java.util.ArrayList;
import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    private static final String TAG = "SectionAdaTAG";
    private final QuizLogic logicLayer;
    private final SectionManager sectionManager;

    private boolean firstDone;

    private final List<View> allSectionsViews;


    public SectionAdapter(QuizLogic logicLayer, SectionManager sectionManager) {
        this.logicLayer = logicLayer;
        this.sectionManager = sectionManager;

        allSectionsViews = new ArrayList<>();
    }

    public void selectThisView(int position) {

        Log.d(TAG, "Selected position: " + position);

        for (int i = 0; i < allSectionsViews.size(); i++) {

            allSectionsViews.get(i).setBackground(ContextCompat.getDrawable(logicLayer.getHomeScreen(), R.drawable.section_un_selected));


            if (position == i) {

                allSectionsViews.get(i).setBackground(ContextCompat.getDrawable(logicLayer.getHomeScreen(), R.drawable.section_selected));

            }
        }
    }


    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewSingleSectionBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_single_section, null, false);

        SectionViewHolder holder = new SectionViewHolder(mBinding);
        allSectionsViews.add(holder.getMyView());

        if (!firstDone) {

            selectThisView(0);
            firstDone = true;
        }

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {

        holder.setData(position);

    }

    @Override
    public int getItemCount() {

        return sectionManager.getTotalSections();

    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {

        private final ViewSingleSectionBinding mBinding;

        public SectionViewHolder(@NonNull ViewSingleSectionBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        void setData(final int position) {

            mBinding.TVNameShowing.setText(logicLayer.getQuiz().getQuizSectionList().get(position).getName());

            mBinding.CLMainContainer.setOnClickListener(view -> logicLayer.confirmThisSectionSubmission(position));


        }

        View getMyView() {

            return mBinding.CLMainContainer;
        }


    }
}
