package com.openlearning.ilearn.chat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.chat.adapters.AllChatsAdapter;
import com.openlearning.ilearn.chat.queries.QueryUser;
import com.openlearning.ilearn.chat.view_models.AllChatsVM;
import com.openlearning.ilearn.databinding.ActivityAllChatsBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;

import java.util.List;

public class AllChats extends AppCompatActivity implements ActivityHooks {

    private static final String TAG = "AllChatsTAG";
    private ActivityAllChatsBinding mBinding;
    private AllChatsVM viewModel;
    private AllChatsAdapter allChatsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_chats);

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

        viewModel = ViewModelProviders.of(this).get(AllChatsVM.class);


    }

    @Override
    public void process() {

        viewModel.getAllChatsList().observe(this, queryUsers -> {

            if (allChatsAdapter == null) {

                showAllChatsRecycler(queryUsers);

            } else {

                allChatsAdapter.notifyDataSetChanged();
            }

            loaded();

            getAllNames(queryUsers);

            Log.d(TAG, "process: total users messages: " + queryUsers.size());
        });

        viewModel.getAllChatsEmpty().observe(this, empty -> {

            if (empty) {

                Log.d(TAG, "process: empty messages");
                mBinding.RVAllChatsRecycler.setAdapter(null);
                loaded();
            }
        });

        viewModel.getAllChats();

    }

    @Override
    public void loaded() {

        mBinding.PBRLoading.setVisibility(View.GONE);
        mBinding.RVAllChatsRecycler.setVisibility(View.VISIBLE);
    }

    private void showAllChatsRecycler(List<QueryUser> queryUserList) {

        allChatsAdapter = new AllChatsAdapter(this, queryUserList);
        mBinding.RVAllChatsRecycler.setAdapter(allChatsAdapter);

    }

    private void getAllNames(List<QueryUser> queryUserList) {

        for (QueryUser queryUser : queryUserList) {

            viewModel.getNameOfThisUser(queryUser.getUserID(), new FirebaseSuccessListener() {

                @Override
                public void onSuccess(Object obj) {

                    queryUser.setName((String) obj);
                    allChatsAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Exception ex) {

                }
            });
        }
    }
}