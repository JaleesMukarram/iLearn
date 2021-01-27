package com.openlearning.ilearn.chat.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.chat.queries.DocumentChat;
import com.openlearning.ilearn.chat.queries.FirebaseGlobals;
import com.openlearning.ilearn.chat.queries.ImageChat;
import com.openlearning.ilearn.chat.queries.MessageChat;
import com.openlearning.ilearn.chat.queries.QueryUser;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.registration.UserRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AllChatsRepository {

    public static final String TAG = "AllChatsRepoTAG";
    public static AllChatsRepository instance;
    private final FirebaseDatabase db;
    private final List<QueryUser> queryUserList;
    private final List<String> chatIDsList;
    private final MutableLiveData<Boolean> chatUpdated;

    private final List<FireStoreObjectGetListener> listenerList;

    private AllChatsRepository() {

        db = FirebaseDatabase.getInstance();
        chatUpdated = new MutableLiveData<>(Boolean.FALSE);
        queryUserList = new ArrayList<>();
        chatIDsList = new ArrayList<>();
        listenerList = new ArrayList<>();
        getAllChatUsers();

    }

    public void setListener(FireStoreObjectGetListener listener) {
        this.listenerList.add(listener);

        if (Objects.requireNonNull(queryUserList).size() > 0) {

            listener.onSuccess(queryUserList);
        }
    }

    public static AllChatsRepository getInstance() {
        if (instance == null) {
            instance = new AllChatsRepository();
        }
        return instance;
    }

    private void getAllChatUsers() {

        Query query = db.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE)
                .orderByChild("receivingUserID")
                .equalTo(UserRegistration.getInstance().getUserID());

        query.keepSynced(false);

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d(TAG, "onDataChange: size: " + snapshot.getChildrenCount());

                Objects.requireNonNull(queryUserList).clear();
                chatIDsList.clear();

                if (snapshot.exists()) {

                    for (DataSnapshot chat : snapshot.getChildren()) {

                        ThisChatSnapshotIsForMe(chat, getChatType(chat));

                    }
                }

                sortAllChats();
            }

            private void ThisChatSnapshotIsForMe(DataSnapshot snapshot, int chatType) {

                if (chatType == Chat.CHAT_TYPE_MESSAGE) {

                    MessageChat messageChat = snapshot.getValue(MessageChat.class);
                    assert messageChat != null;
                    if (checkIfChatAvailableInList(messageChat.getChatID())) {
                        return;
                    }

                    chatIDsList.add(messageChat.getChatID());

                    int index = getIndexOfThisUserInList(messageChat.getSendingUserID());

                    if (index != -1) {
                        thisUserChatIsAdditional(messageChat, index);
                    } else {
                        thisUserChatIsNew(messageChat);
                    }


                } else if (chatType == Chat.CHAT_TYPE_IMAGE) {

                    ImageChat imageChat = snapshot.getValue(ImageChat.class);
                    assert imageChat != null;

                    if (checkIfChatAvailableInList(imageChat.getChatID())) {
                        return;
                    }

                    chatIDsList.add(imageChat.getChatID());

                    int index = getIndexOfThisUserInList(imageChat.getSendingUserID());
                    if (index != -1) {
                        thisUserChatIsAdditional(imageChat, index);
                    } else {
                        thisUserChatIsNew(imageChat);
                    }

                } else if (chatType == Chat.CHAT_TYPE_DOCUMENTS) {

                    DocumentChat documentChat = snapshot.getValue(DocumentChat.class);
                    assert documentChat != null;

                    if (checkIfChatAvailableInList(documentChat.getChatID())) {
                        return;
                    }

                    chatIDsList.add(documentChat.getChatID());

                    int index = getIndexOfThisUserInList(documentChat.getSendingUserID());
                    if (index != -1) {
                        thisUserChatIsAdditional(documentChat, index);
                    } else {
                        thisUserChatIsNew(documentChat);
                    }

                }

            }

            private void thisUserChatIsAdditional(Chat chat, int index) {

                QueryUser queryUser = queryUserList.get(index);
                queryUser.incrementTotalMessages();

                // If this message is newer than
                if (queryUser.getLastMessageDate().before(chat.getTypedDate())) {

                    queryUser.setLastMessageDate(chat.getTypedDate());

                }

                if (checkIfChatIsUnread(chat)) {

                    queryUser.incrementUnReadMessages();
                }

            }

            // If the news user message is new
            private void thisUserChatIsNew(Chat chat) {

                QueryUser queryUser = new QueryUser(chat.getSendingUserID(), chat.getTypedDate());
                if (checkIfChatIsUnread(chat)) {
                    queryUser.incrementUnReadMessages();
                }
                queryUserList.add(queryUser);

            }

            // checking if this user message is in list
            private int getIndexOfThisUserInList(String sendingUserID) {

                for (int i = 0; i < queryUserList.size(); i++) {

                    if (queryUserList.get(i).getUserID().equals(sendingUserID)) {

                        return i;
                    }
                }

                return -1;
            }

            private boolean checkIfChatIsUnread(Chat chat) {

                Date readDate = chat.getReadDate();
                return readDate == null;

            }

            private boolean checkIfChatAvailableInList(String chatID) {

                for (String id : chatIDsList) {
                    if (id.equals(chatID)) {
                        return true;
                    }
                }
                return false;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                sortAllChats();
            }
        });
    }

    private void sortAllChats() {

        Collections.sort(Objects.requireNonNull(queryUserList), (queryUser, t1) -> t1.getLastMessageDate().compareTo(queryUser.getLastMessageDate()));
        for (FireStoreObjectGetListener listener : listenerList) {

            listener.onSuccess(queryUserList);
        }

        chatUpdated.setValue(!Objects.requireNonNull(chatUpdated.getValue()));
    }

    private int getChatType(DataSnapshot chat) {

        int chatType = 0;

        // Getting the attributes of the user
        for (DataSnapshot attribute : chat.getChildren()) {

            if ("chatType".equals(attribute.getKey())) {

                chatType = (int) ((long) attribute.getValue());
            }
        }

        return chatType;
    }

    public int getTotalUnReadMessages() {

        int total = 0;
        for (QueryUser queryUser : Objects.requireNonNull(queryUserList)) {

            total += queryUser.getUnreadMessagesCount();
        }

        return total;

    }

    public List<QueryUser> getQueryUserList() {
        return queryUserList;
    }

    public MutableLiveData<Boolean> getChatUpdated() {
        return chatUpdated;
    }

    public void destroy() {
        instance = null;
    }

}
