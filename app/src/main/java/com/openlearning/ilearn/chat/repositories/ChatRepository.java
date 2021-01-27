package com.openlearning.ilearn.chat.repositories;

import android.net.Uri;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.chat.queries.DocumentChat;
import com.openlearning.ilearn.chat.queries.FirebaseGlobals;
import com.openlearning.ilearn.chat.queries.ImageChat;
import com.openlearning.ilearn.chat.queries.MessageChat;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.StorageDocument;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.utils.StorageUploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.openlearning.ilearn.chat.queries.FirebaseGlobals.Storage.DOCUMENT_STORAGE_REFERENCE;
import static com.openlearning.ilearn.chat.queries.FirebaseGlobals.Storage.IMAGE_STORAGE_REFERENCE;

public class ChatRepository {

    private static final String TAG = "ChatRepoTAG";
    private final String SENDING_USER;
    private final String RECEIVING_USER;

    private final FirebaseDatabase db;
    private boolean firstMessageSent;
    private final List<Chat> chatList;

    private ConstantChildEventListenerForChats listenerForChats;

    private FireStoreObjectGetListener listener;

    public ChatRepository(String sendingUserID, String receivingUserID) {
        this.SENDING_USER = sendingUserID;
        this.RECEIVING_USER = receivingUserID;

        db = FirebaseDatabase.getInstance();
        chatList = new ArrayList<>();

    }

    public void uploadThisMessageChatToDatabase(final MessageChat chat, FirebaseSuccessListener listener) {

        DatabaseReference databaseReference = db.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

        Log.d(TAG, "uploadThisMessageChatToDatabase: started uploading chat: " + chat.getMessage());

        databaseReference.child(String.valueOf(chat.getChatID())).setValue(chat)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        listener.onFailure(task.getException());
                        Log.d(TAG, "uploadThisMessageChatToDatabase: failed: " + task.getException());
                        return;
                    }

                    Log.d(TAG, "uploadThisMessageChatToDatabase: uploaded: " + chat.getMessage());
                    listener.onSuccess(chat);

                });
    }

    public void uploadThisImageChatToDatabase(final ImageChat imageChat, Uri uri, FirebaseSuccessListener listener) {

        Log.d(TAG, "uploadThisImageChatToDatabase: started upload image to storage: " + imageChat.getChatID());

        addNewItemToDatabase(uri, UUID.randomUUID().toString(), StorageImage.class, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                imageChat.setStorageImage((StorageImage) obj);

                DatabaseReference databaseReference = db.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

                Log.d(TAG, "uploadThisImageChatToDatabase: started uploading chat to database: " + imageChat.getChatID());

                databaseReference.child(String.valueOf(imageChat.getChatID())).setValue(imageChat)
                        .addOnCompleteListener(task -> {

                            if (!task.isSuccessful()) {

                                listener.onFailure(task.getException());
                                Log.d(TAG, "uploadThisImageChatToDatabase: failed: " + task.getException());
                                return;
                            }

                            Log.d(TAG, "uploadThisImageChatToDatabase: uploaded: " + imageChat.getChatID());
                            listener.onSuccess(imageChat);

                        });

            }

            @Override
            public void onFailure(Exception ex) {

                listener.onFailure(ex);
            }
        });

    }

    public void uploadThisDocumentChatToDatabase(final DocumentChat documentChat, File file, FirebaseSuccessListener listener) {

        Log.d(TAG, "uploadThisDocumentChatToDatabase: started upload image to storage: " + documentChat.getChatID());

        addNewItemToDatabase(Uri.fromFile(file), documentChat.getStorageDocument().getName(), StorageDocument.class, new FirebaseSuccessListener() {
            @Override
            public void onSuccess(Object obj) {

                StorageDocument newDocument = (StorageDocument) obj;
                documentChat.getStorageDocument().saveDatabaseValues(newDocument);

                DatabaseReference databaseReference = db.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

                Log.d(TAG, "uploadThisDocumentChatToDatabase: started uploading chat to database: " + documentChat.getChatID());

                databaseReference.child(String.valueOf(documentChat.getChatID())).setValue(documentChat)
                        .addOnCompleteListener(task -> {

                            if (!task.isSuccessful()) {

                                listener.onFailure(task.getException());
                                Log.d(TAG, "uploadThisDocumentChatToDatabase: failed: " + task.getException());
                                return;
                            }

                            Log.d(TAG, "uploadThisDocumentChatToDatabase: uploaded: " + documentChat.getChatID());
                            listener.onSuccess(documentChat);

                        });

            }

            @Override
            public void onFailure(Exception ex) {

                listener.onFailure(ex);
            }
        });

    }


    public void getAllChatsFromDatabase(FireStoreObjectGetListener listener) {

        this.listener = listener;

        Query query = db.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

        query.keepSynced(false);

        listenerForChats = new ConstantChildEventListenerForChats();

        query.addChildEventListener(listenerForChats);

    }

    public void destroyListener() {
        db.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE).removeEventListener(listenerForChats);
    }


    // Addition Snapshots
    private void thisAddedChatSnapshotIsForMe(DataSnapshot chat) {

        int chatType = 0;

        for (DataSnapshot attribute : chat.getChildren()) {

            if ("chatType".equals(attribute.getKey())) {

                chatType = (int) ((long) attribute.getValue());
                break;
            }
        }

        if (chatType == Chat.CHAT_TYPE_MESSAGE) {

            addThisMessageChatFromDatabaseToLocal(chat);

        } else if (chatType == Chat.CHAT_TYPE_IMAGE) {

            addThisImageChatFromDatabaseToLocal(chat);

        } else if (chatType == Chat.CHAT_TYPE_DOCUMENTS) {

            addThisDocumentChatFromDatabaseToLocal(chat);

        }
    }

    private void addThisMessageChatFromDatabaseToLocal(DataSnapshot chat) {

        MessageChat messageChat = chat.getValue(MessageChat.class);

        try {

            assert messageChat != null;
            messageChat.setSentDate(messageChat.getTypedDate());
            messageChat.setChatSent(true);
            chatList.add(messageChat);

        } catch (Exception ignored) {
        }

        sortAllChats();
    }

    private void addThisImageChatFromDatabaseToLocal(DataSnapshot chat) {

        ImageChat imageChat = chat.getValue(ImageChat.class);

        try {

            assert imageChat != null;
            imageChat.setSentDate(imageChat.getTypedDate());
            chatList.add(imageChat);
//            mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(chatList.size());
//
//            setAllMessagesAsRead();


        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Problem while");
        }

        sortAllChats();
    }

    private void addThisDocumentChatFromDatabaseToLocal(DataSnapshot chat) {

        DocumentChat documentChat = chat.getValue(DocumentChat.class);

        try {

            assert documentChat != null;
            documentChat.setSentDate(documentChat.getTypedDate());

            chatList.add(documentChat);
//            mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(chatList.size());
//            setAllMessagesAsRead();


        } catch (Exception ignored) {

        }

        sortAllChats();

    }


    // Update Snapshots
    private void thisUpdatedSnapshotIsForMe(DataSnapshot chat) {

        int chatType = 0;

        for (DataSnapshot attribute : chat.getChildren()) {

            if ("chatType".equals(attribute.getKey())) {

                chatType = (int) ((long) attribute.getValue());
                break;
            }
        }

        if (chatType == Chat.CHAT_TYPE_MESSAGE) {

            updateThisMessageChatFromDatabaseToLocal(chat);

        } else if (chatType == Chat.CHAT_TYPE_IMAGE) {

            updateThisImageChatFromDatabaseToLocal(chat);

        } else if (chatType == Chat.CHAT_TYPE_DOCUMENTS) {

            updateThisDocumentChatFromDatabaseToLocal(chat);

        }
    }

    private void updateThisMessageChatFromDatabaseToLocal(DataSnapshot chat) {

        MessageChat messageChat = chat.getValue(MessageChat.class);
        assert messageChat != null;
        messageChat.setSentDate(messageChat.getTypedDate());

        String chatID = messageChat.getChatID();

        for (int i = 0; i < chatList.size(); i++) {

            if (chatID.equals(chatList.get(i).getChatID())) {

                chatList.set(i, messageChat);
                Log.d(TAG, messageChat.getMessage() + " is updated successfully");
                break;
            }
        }

        sortAllChats();
    }

    private void updateThisImageChatFromDatabaseToLocal(DataSnapshot chat) {

        ImageChat imageChat = chat.getValue(ImageChat.class);
        assert imageChat != null;
        imageChat.setSentDate(imageChat.getTypedDate());

        String chatID = imageChat.getChatID();

        for (int i = 0; i < chatList.size(); i++) {

            if (chatID.equals(chatList.get(i).getChatID())) {

                chatList.set(i, imageChat);
                Log.d(TAG, imageChat.getStorageImage().getName() + " is updated successfully");
                break;
            }
        }

    }

    private void updateThisDocumentChatFromDatabaseToLocal(DataSnapshot chat) {

        DocumentChat documentChat = chat.getValue(DocumentChat.class);
        assert documentChat != null;
        documentChat.setSentDate(documentChat.getTypedDate());

        String chatID = documentChat.getChatID();

        for (int i = 0; i < chatList.size(); i++) {

            if (chatID.equals(chatList.get(i).getChatID())) {

                chatList.set(i, documentChat);
                Log.d(TAG, documentChat.getStorageDocument().getName() + " is updated successfully");
                break;
            }
        }
    }

    private void thisDeletedSnapShotIsForMe(DataSnapshot chat) {

        Chat chat1 = chat.getValue(Chat.class);

        if (chat1 != null) {

            for (int i = 0; i < chatList.size(); i++) {

                if (chat1.getChatID().equals(chatList.get(i).getChatID())) {

                    chatList.remove(i);
                    Log.d(TAG, "LOCAL COPY REMOVED");
                    break;
                }
            }
        }
    }

    private void sortAllChats() {

        Collections.sort(chatList, (chat, t1) -> chat.getTypedDate().compareTo(t1.getTypedDate()));

        if (listener != null) {

            listener.onSuccess(chatList);
            Log.d(TAG, "sortAllChats: success sent with size: " + chatList.size());
        }

        setAllMessagesAsRead();
    }

    private void setAllMessagesAsRead() {

        for (Chat c : chatList) {

            if (c.getSendingUserID().equals(RECEIVING_USER)) {

                if (c.getReadDate() == null) {

                    setThisChatSeenStatusAsTrue(c);
                }
            }
        }
    }

    private void setThisChatSeenStatusAsTrue(final Chat c) {

        DatabaseReference reference = db.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

        final Date readDate = new Date();
        Map<String, Object> dateMap = new HashMap<>();
        dateMap.put("readDate", readDate);
        reference.child(c.getChatID()).updateChildren(dateMap, (databaseError, databaseReference) -> {

            if (databaseError == null) {

                c.setReadDate(readDate);
            }
        });
    }

    public void setFirstMessageSent(boolean firstMessageSent) {
        this.firstMessageSent = firstMessageSent;
    }


    // Storage
    private void addNewItemToDatabase(Uri uri, String fileName, Class<?> returningClass, FirebaseSuccessListener listener) {

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(returningClass == StorageImage.class ? IMAGE_STORAGE_REFERENCE : DOCUMENT_STORAGE_REFERENCE);

        StorageUploadTask storageUploadTask = new StorageUploadTask(storageReference, fileName, uri, returningClass);
        storageUploadTask.setListener(listener);

    }

    //Listeners
    private class ConstantChildEventListenerForChats implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot chat, @Nullable String s) {

            Log.d(TAG, "CHAT SNAPSHOT CHILD ADDED: " + chat);

            if (chat.exists()) {

                String sendingUserID = "";
                String receivingUserID = "";

//              Getting who sent to whom
                for (DataSnapshot attribute : chat.getChildren()) {

                    if ("sendingUserID".equals(attribute.getKey())) {

                        sendingUserID = (String) (attribute.getValue());

                    } else if ("receivingUserID".equals(attribute.getKey())) {

                        receivingUserID = (String) (attribute.getValue());
                    }
                }

//              If I sent to him
                if (!firstMessageSent && sendingUserID.equals(SENDING_USER) && receivingUserID.equals(RECEIVING_USER)) {

                    thisAddedChatSnapshotIsForMe(chat);
                    Log.d(TAG, "I sent");

                }

//              If he sent to me
                else if (sendingUserID.equals(RECEIVING_USER) && receivingUserID.equals(SENDING_USER)) {

                    thisAddedChatSnapshotIsForMe(chat);
                    Log.d(TAG, "I received");
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot chat, @Nullable String s) {

            if (chat.exists()) {

                String sendingUserID = "";
                String receivingUserID = "";

//              Getting who sent to whom
                for (DataSnapshot attribute : chat.getChildren()) {

                    if ("sendingUserID".equals(attribute.getKey())) {

                        sendingUserID = (String) (attribute.getValue());

                    } else if ("receivingUserID".equals(attribute.getKey())) {

                        receivingUserID = (String) (attribute.getValue());
                    }
                }

//              If change is made on my chat things
                if (sendingUserID.equals(SENDING_USER) && receivingUserID.equals(RECEIVING_USER)) {

                    thisUpdatedSnapshotIsForMe(chat);
                    Log.d(TAG, "my chat things got updated");

                }

//              If change is made on his chat things
                else if (sendingUserID.equals(RECEIVING_USER) && receivingUserID.equals(SENDING_USER)) {

                    thisUpdatedSnapshotIsForMe(chat);
                    Log.d(TAG, "his chat things got updated");
                }
            }

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot chat) {

            Log.d(TAG, "CHAT DELETED " + chat);


            if (chat.exists()) {


                String sendingUserID = "";
                String receivingUserID = "";

//              Getting who sent to whom
                for (DataSnapshot attribute : chat.getChildren()) {

                    if ("sendingUserID".equals(attribute.getKey())) {

                        sendingUserID = (String) (attribute.getValue());

                    } else if ("receivingUserID".equals(attribute.getKey())) {

                        receivingUserID = (String) (attribute.getValue());
                    }
                }

//              If I deleted for him
                if (sendingUserID.equals(SENDING_USER) && receivingUserID.equals(RECEIVING_USER)) {

                    thisDeletedSnapShotIsForMe(chat);
                    Log.d(TAG, "I deleted");

                }

//              If he deletes for me
                else if (sendingUserID.equals(RECEIVING_USER) && receivingUserID.equals(SENDING_USER)) {

                    thisDeletedSnapShotIsForMe(chat);
                    Log.d(TAG, "he deleted");
                }

            } else {

                Log.d(TAG, "Not existing");
            }


        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
