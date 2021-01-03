package com.openlearning.ilearn.news;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewErrorDialogueBinding;
import com.openlearning.ilearn.databinding.ViewNewsStatusDialogueBinding;
import com.openlearning.ilearn.interfaces.ConfirmationListener;
import com.openlearning.ilearn.utils.CommonUtils;

public class StatusDialogue {

    private AlertDialog.Builder mBuilder;
    private final Activity homeScreen;

    public StatusDialogue(Activity homeScreen) {
        this.homeScreen = homeScreen;
    }

    public void ready(boolean active, boolean forEdit, StatusListener listener) {

        ViewNewsStatusDialogueBinding mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_news_status_dialogue, null, false);

        String[] statusTypesArray = {"Active", "Hidden"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(homeScreen, android.R.layout.simple_dropdown_item_1line, statusTypesArray);

        mBinding.SPRAllStatus.setAdapter(adapter);
        mBinding.SPRAllStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                listener.onStatusChanged(position == 0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mBinding.SPRAllStatus.setSelection(active ? 0 : 1, true);

        if (forEdit) {

            mBinding.BTNDelete.setOnClickListener(v -> showDeleteConfirmation(listener));

        } else {

            mBinding.BTNDelete.setVisibility(View.GONE);

        }

        mBuilder = new AlertDialog.Builder(homeScreen);
        mBuilder.setView(mBinding.getRoot());
    }

    public void show() {

        Dialog mDialog = mBuilder.create();
        mDialog.show();

    }

    private void showDeleteConfirmation(StatusListener listener) {

        CommonUtils.showConfirmationDialogue(homeScreen, "Delete News", "Are you sure you want to delete this news permanently?", "Delete", new ConfirmationListener() {
            @Override
            public void onPositive() {

                listener.onDelete();

            }

            @Override
            public void onNegative() {

            }
        });
    }

    public interface StatusListener {

        void onStatusChanged(boolean active);

        void onDelete();

    }

}
