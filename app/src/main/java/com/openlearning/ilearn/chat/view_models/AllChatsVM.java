package com.openlearning.ilearn.chat.view_models;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.chat.queries.QueryUser;
import com.openlearning.ilearn.chat.repositories.AllChatsRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.registration.UserRegistration;

import java.util.List;

public class AllChatsVM extends ViewModel {

    private final AllChatsRepository allChatsRepository;
    private final MutableLiveData<Boolean> allChatsEmpty;
    private final MutableLiveData<List<QueryUser>> allChatsList;

    public AllChatsVM() {
        allChatsRepository = AllChatsRepository.getInstance();
        allChatsEmpty = new MutableLiveData<>();
        allChatsList = new MutableLiveData<>();
    }

    public void getAllChats() {

        allChatsRepository.setListener(new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {
                    allChatsList.setValue((List<QueryUser>) obj);
                    allChatsEmpty.setValue(false);
                } else {
                    allChatsEmpty.setValue(true);
                }

            }

            @Override
            public void onFailure(Exception ex) {

                allChatsEmpty.setValue(false);

            }
        });

    }

    public MutableLiveData<Boolean> getAllChatsEmpty() {
        return allChatsEmpty;
    }

    public MutableLiveData<List<QueryUser>> getAllChatsList() {
        return allChatsList;
    }

    public void getNameOfThisUser(String userID, FirebaseSuccessListener listener) {

        UserRegistration.getInstance().getNameOfThisUser(userID, listener);
    }
}
