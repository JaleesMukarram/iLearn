package com.openlearning.ilearn.chat.view_models;

import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.chat.queries.DocumentChat;
import com.openlearning.ilearn.chat.queries.ImageChat;
import com.openlearning.ilearn.chat.queries.MessageChat;
import com.openlearning.ilearn.chat.repositories.ChatRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.StorageDocument;
import com.openlearning.ilearn.registration.UserRegistration;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SendChatVM extends ViewModel {

    private static final String TAG = "SendChatVMTAG";
    private final UserRegistration userRegistration;
    private ChatRepository chatRepository;
    private MutableLiveData<String> receivingUserName;

    private String SENDING_USER;
    private String RECEIVING_USER;
    private int QUERY_ID;

    private final MutableLiveData<List<Chat>> chatList;

    public SendChatVM() {

        userRegistration = UserRegistration.getInstance();
        receivingUserName = new MutableLiveData<>();
        chatList = new MutableLiveData<>();
        chatList.setValue(new ArrayList<>());
    }

    public String initIDs(String RECEIVING_USER) {

        this.RECEIVING_USER = RECEIVING_USER;
        SENDING_USER = userRegistration.getUserID();
        chatRepository = new ChatRepository(SENDING_USER, RECEIVING_USER);
        return SENDING_USER;

    }

    public void getNameFromDb() {

        userRegistration.getNameOfThisUser(RECEIVING_USER, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                receivingUserName.setValue((String) obj);

            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }

    // Handling Chats
    public void handleNewTextSendMessage(EditText typingView) {

        String message = typingView.getText().toString().trim();

        if (message.length() == 0) {

            Toast.makeText(typingView.getContext(), "Please enter some text", Toast.LENGTH_SHORT).show();


        } else {


            MessageChat messageChat = new MessageChat(SENDING_USER, RECEIVING_USER, message);

            addThisIntoChatList(messageChat);
            chatRepository.setFirstMessageSent(true);

            chatRepository.uploadThisMessageChatToDatabase(messageChat, new FirebaseSuccessListener() {
                @Override
                public void onSuccess(Object obj) {

                    setThisChatAsSent(messageChat.getChatID(), messageChat.getTypedDate());

                }

                @Override
                public void onFailure(Exception ex) {

                    setThisChatAsFailed(messageChat.getChatID());

                }
            });

            typingView.setText("");

        }
    }

    public void handleNewImageChat(Uri uri) {

        ImageChat imageChat = new ImageChat(SENDING_USER, RECEIVING_USER, uri.toString());

        addThisIntoChatList(imageChat);
        chatRepository.setFirstMessageSent(true);

        chatRepository.uploadThisImageChatToDatabase(imageChat, uri, new FirebaseSuccessListener() {

            @Override
            public void onSuccess(Object obj) {

                setThisChatAsSent(imageChat.getChatID(), imageChat.getTypedDate());
            }

            @Override
            public void onFailure(Exception ex) {

                Log.d(TAG, "onFailure: " + ex);

                setThisChatAsFailed(imageChat.getChatID());

            }
        });
    }

    public void handleNewDocumentChat(File file) {

        StorageDocument storageDocument = new StorageDocument(file.getName(), null, null, file.length(), Uri.fromFile(file).toString());

        DocumentChat documentChat = new DocumentChat(SENDING_USER, RECEIVING_USER, storageDocument);
        addThisIntoChatList(documentChat);

        chatRepository.setFirstMessageSent(true);

        chatRepository.uploadThisDocumentChatToDatabase(documentChat, file, new FirebaseSuccessListener() {

            @Override
            public void onSuccess(Object obj) {

                setThisChatAsSent(documentChat.getChatID(), documentChat.getTypedDate());
            }

            @Override
            public void onFailure(Exception ex) {

                Log.d(TAG, "onFailure: " + ex);

                setThisChatAsFailed(documentChat.getChatID());

            }
        });


    }

    // Chat Options
    private void setThisChatAsSent(String chatID, Date typedDate) {

        for (int i = 0; i < Objects.requireNonNull(chatList.getValue()).size(); i++) {

            if (chatList.getValue().get(i).getChatID().equals(chatID)) {

                chatList.getValue().get(i).setSentDate(typedDate);
                Log.d(TAG, "Succeeded one found");
                break;

            }
        }

        chatList.setValue(chatList.getValue());

    }

    private void setThisChatAsFailed(String chatID) {

        for (int i = 0; i < Objects.requireNonNull(chatList.getValue()).size(); i++) {

            if (chatList.getValue().get(i).getChatID().equals(chatID)) {

                chatList.getValue().get(i).setChatSent(false);
                Log.d(TAG, "Failed one found");
                break;
            }
        }

        chatList.setValue(chatList.getValue());

    }

    public void getChatsFromFirebase() {

        chatRepository.getAllChatsFromDatabase(new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                chatList.setValue((List<Chat>) obj);

            }

            @Override
            public void onFailure(Exception ex) {

            }
        });

    }

    public void addThisIntoChatList(Chat chat) {

        List<Chat> tempList = chatList.getValue();
        assert tempList != null;
        tempList.add(chat);
        chatList.setValue(tempList);

    }

    // Mutable
    public MutableLiveData<List<Chat>> getChatList() {
        return chatList;
    }

    public MutableLiveData<String> getReceivingUserName() {

        getNameFromDb();
        return receivingUserName;

    }

    public void destroyForThis() {

        chatRepository.destroyListener();
        chatRepository = null;
    }
}
