package com.openlearning.ilearn.chat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.BuildConfig;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.chat.adapters.ChatAdapter;
import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.chat.queries.DocumentChat;
import com.openlearning.ilearn.chat.queries.FirebaseGlobals;
import com.openlearning.ilearn.chat.queries.FirebaseUploadListener;
import com.openlearning.ilearn.chat.queries.ImageChat;
import com.openlearning.ilearn.chat.queries.MessageChat;
import com.openlearning.ilearn.chat.queries.ObjectChatUploadInterface;
import com.openlearning.ilearn.chat.view_models.SendChatVM;
import com.openlearning.ilearn.databinding.ActivitySendChatBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.utils.FilePickerUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SendChat extends AppCompatActivity implements ActivityHooks {

    public static final int CAMERA_PERMISSION = 128;
    public static final int CAMERA_AND_IMAGE_BOTH = 2;
    public static final int STORAGE_PERMISSION = 4;
    public static final int STORAGE_PERMISSION_FOR_BACKGROUND_LOADING = 8;
    public static final int CAMERA_IMAGE_CAPTURE_ACTIVITY_RESULT = 256;
    public static final int POSITION_FILE_IMAGES = 0;

    public static final int POSITION_FILE_PDF = 1;
    public static final int POSITION_FILE_DOCUMENTS = 2;
    public static final String TAG = "SENDCHATTAG";
    private String SENDING_USER;
    private String RECEIVING_USER;
    private String TITLE;

    private AlertDialog attachmentSelectDialogue;


    private ChatAdapter adapter;

    private SoundPool player;
    private int chatReceivedSound;

    private List<Chat> chatList;

    private Uri imageUri;

    private List<List<File>> filesList;

    private FirebaseDatabase firebaseDatabase;

    private boolean filesLoaded;
    private boolean firstMessageSent;

    // Later

    private ActivitySendChatBinding mBinding;
    private SendChatVM viewModel;
    private FilePickerUtils filePickerUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_chat);

        callHooks();

    }

    @Override
    public void callHooks() {

        handleIntent();
        init();
        process();

    }

    @Override
    public void handleIntent() {

        if (true) {

            SENDING_USER = "abc";
            RECEIVING_USER = "xyz";

        } else {

            SENDING_USER = "xyz";
            RECEIVING_USER = "abc";
        }
    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(SendChatVM.class);
        viewModel.initIDs(SENDING_USER, RECEIVING_USER);

        initializeSoundPool();


        firebaseDatabase = FirebaseDatabase.getInstance();

        chatList = new ArrayList<>();
        filesList = new ArrayList<>();

        mBinding.IVSendMessage.setOnClickListener(view -> viewModel.handleNewTextSendMessage(mBinding.TVEnteredMessage));
        mBinding.IVBackButton.setOnClickListener(view -> onBackPressed());
        mBinding.IVStartCamera.setOnClickListener(view -> requestCameraCall());
        mBinding.IVSendAttachment.setOnClickListener(view -> {

            if (CommonUtils.checkIfStoragePermissionIsGranted(this)) {

                showAttachmentSelectionDialogue();

            } else {

                CommonUtils.getStoragePermission(this, STORAGE_PERMISSION_FOR_BACKGROUND_LOADING);
            }

        });

        mBinding.TVSelectQueryShowing.setText(RECEIVING_USER);


        filePickerUtils = new FilePickerUtils(this, FilePickerUtils.MODE_ALL, (file, type) -> {

            if (type == FilePickerUtils.INDEX_FILE_IMAGES) {

                viewModel.handleNewImageChat(file);
            }

        });

        if (CommonUtils.checkIfStoragePermissionIsGranted(this)) {

            filePickerUtils.callHooks();
        }
    }

    @Override
    public void process() {

        viewModel.getChatList().observe(this, newChatList -> {

            Log.d(TAG, "process: chat value changed");

            if (adapter != null) {

                adapter.setChatList(newChatList);
                adapter.notifyDataSetChanged();
                mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(newChatList.size());


            } else {

                adapter = new ChatAdapter(this, newChatList, SENDING_USER, RECEIVING_USER);
                mBinding.RVAllChatsRecyclerShowing.setAdapter(adapter);
            }
        });

        viewModel.getChatsFromFirebase();
    }

    @Override
    public void loaded() {


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (filesLoaded || checkIfStoragePermissionIsGranted()) {
            new BackgroundLoading(false).execute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        chatList.clear();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case CAMERA_IMAGE_CAPTURE_ACTIVITY_RESULT:

                if (resultCode == RESULT_OK) {

                    ImageChat imageChat = new ImageChat(SENDING_USER, RECEIVING_USER, imageUri.toString());
                    chatList.add(imageChat);
                    adapter.notifyDataSetChanged();
                    mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(chatList.size());

                    uploadImageChatToDatabase(imageChat);

                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {


            case STORAGE_PERMISSION_FOR_BACKGROUND_LOADING:

                Log.d(TAG, "onRequestPermissionsResult: permissions size: " + permissions.length);
                Log.d(TAG, "onRequestPermissionsResult: grantResults size: " + grantResults.length);

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    filePickerUtils.callHooks();
                    filePickerUtils.showNow();
                    Toast.makeText(this, "Files loading. Please wait a moment", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "File Permission denied", Toast.LENGTH_SHORT).show();
                }

            case CAMERA_AND_IMAGE_BOTH:

                Log.d(TAG, "onRequestPermissionsResult: permissions size: " + permissions.length);
                Log.d(TAG, "onRequestPermissionsResult: grantResults size: " + grantResults.length);

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    filePickerUtils.callHooks();
                    filePickerUtils.showNow();
                    Toast.makeText(this, "Files loading. Please wait a moment", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "File Permission denied", Toast.LENGTH_SHORT).show();
                }


//            case CAMERA_AND_IMAGE_BOTH:
//
//                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//
//                    openCameraForImage();
//                    new BackgroundLoading(false).execute();
//
//                } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//
//                    new BackgroundLoading(false).execute();
//                    Toast.makeText(this, "Some Permission denied", Toast.LENGTH_SHORT).show();
//
//
//                } else {
//
//                    Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
//                }
//
//                break;
//
//
//            case STORAGE_PERMISSION_FOR_BACKGROUND_LOADING:
//
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Getting files ready...", Toast.LENGTH_SHORT).show();
//                    new BackgroundLoading(true).execute();
//                } else {
//
//                    Toast.makeText(this, "Storage Permission denied", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//
//            case CAMERA_PERMISSION:
//                Log.d(TAG, "Camera permission granted");
//                break;
//
//            case STORAGE_PERMISSION:
//                Log.d(TAG, "Storage permission granted");
//                break;

        }
    }

    private void initializeSoundPool() {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        player = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();

        chatReceivedSound = player.load(this, R.raw.chat_sound, 1);

    }

    private void playSound() {

        if (player != null) {

            player.play(chatReceivedSound, 0.5f, 0.5f, 1, 0, 1);
        }
    }

    // Sending Messages Chat
    private void handleNewTextSendMessage() {

        String message = mBinding.TVEnteredMessage.getText().toString().trim();

        if (message.length() == 0) {

            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();

        } else {

            firstMessageSent = true;

            MessageChat messageChat = new MessageChat(SENDING_USER, RECEIVING_USER, message);

            chatList.add(messageChat);

            uploadThisMessageChatToDatabase(messageChat);

            adapter.notifyDataSetChanged();
            mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(chatList.size());

            mBinding.TVEnteredMessage.setText("");

        }
    }

    private void uploadThisMessageChatToDatabase(final MessageChat chat) {


        DatabaseReference databaseReference = firebaseDatabase.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

        databaseReference.child(String.valueOf(chat.getChatID())).setValue(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        setThisChatAsSent(chat.getChatID(), chat.getTypedDate());

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        setThisChatAsFailed(chat.getChatID());

                    }
                })

                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                        setThisChatAsFailed(chat.getChatID());

                    }
                });

    }

    private void setThisChatAsSent(String chatID, Date typedDate) {

        for (int i = 0; i < chatList.size(); i++) {

            if (chatList.get(i).getChatID() == chatID) {

                chatList.get(i).setSentDate(typedDate);
                Log.d(TAG, "Succeeded one found");
                break;

            }
        }

        adapter.notifyDataSetChanged();
    }

    private void setThisChatAsFailed(String chatID) {

        for (int i = 0; i < chatList.size(); i++) {

            if (chatList.get(i).getChatID() == chatID) {

                chatList.get(i).setChatSent(false);
                Log.d(TAG, "Failed one found");
                break;

            }
        }

        adapter.notifyDataSetChanged();
    }

    // Sending Image Chat
    private void uploadImageChatToDatabase(final ImageChat imageChat) {

        firstMessageSent = true;


        try {

            ImageChat imageChaToSend = (ImageChat) imageChat.clone();

            UploadImageTaskClass imageTaskClass = new UploadImageTaskClass(imageChaToSend);
            imageTaskClass.setListener(new FirebaseUploadListener() {
                @Override
                public void onTaskUploadSuccess() {

                    adapter.notifyDataSetChanged();
                    setThisChatAsSent(imageChat.getChatID(), imageChat.getTypedDate());

                }

                @Override
                public void onTaskUploadFailed() {

                    adapter.notifyDataSetChanged();
                    setThisChatAsFailed(imageChat.getChatID());

                }

                @Override
                public void progress(int progress) {

                    adapter.notifyDataSetChanged();
                }
            });

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }

    //Sending Document Chat
    private void uploadDocumentChatToDatabase(final DocumentChat documentChat) {

        firstMessageSent = true;

        try {
            DocumentChat documentChatToSend = (DocumentChat) documentChat.clone();
            documentChat.setProgress(0);

            UploadDocumentTaskClass documentTaskClass = new UploadDocumentTaskClass(documentChatToSend);

            documentTaskClass.setListener(new FirebaseUploadListener() {
                @Override
                public void onTaskUploadSuccess() {

                    adapter.notifyDataSetChanged();
                    setThisChatAsSent(documentChat.getChatID(), documentChat.getTypedDate());

                }

                @Override
                public void onTaskUploadFailed() {

                    adapter.notifyDataSetChanged();
                    setThisChatAsFailed(documentChat.getChatID());

                }

                @Override
                public void progress(int progress) {

                    adapter.notifyDataSetChanged();
                    documentChat.setProgress(progress);

                }
            });


        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


    }

    // Receiving Messages
    private void getChatsFromFirebase() {


        Query query = firebaseDatabase.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

        query.keepSynced(false);

        query.orderByChild("queryID")
                .equalTo("")
                .addChildEventListener(new ConstantChildEventListenerForChats());

    }

    private void sortAllChats() {

        Collections.sort(chatList, new Comparator<Chat>() {
            @Override
            public int compare(Chat chat, Chat t1) {

                return chat.getTypedDate().compareTo(t1.getTypedDate());
            }
        });

        adapter.notifyDataSetChanged();


    }

    private void thisAddedChatSnapshotIsForMe(DataSnapshot chat) {

        int chatType = 0;

        for (DataSnapshot attribute : chat.getChildren()) {

            if ("chatType".equals(attribute.getKey())) {

                chatType = (int) ((long) attribute.getValue());
                break;
            }
        }

        if (firstMessageSent) {

            playSound();
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

            chatList.add(messageChat);
            mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(chatList.size());
            setAllMessagesAsRead();


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
            mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(chatList.size());

            setAllMessagesAsRead();


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
            mBinding.RVAllChatsRecyclerShowing.smoothScrollToPosition(chatList.size());
            setAllMessagesAsRead();


        } catch (Exception ignored) {

        }

        sortAllChats();

    }


    //Deleted
    private void thisDeletedSnapShotIsForMe(DataSnapshot chat) {

        Chat chat1 = chat.getValue(Chat.class);

        if (chat1 != null) {

            for (int i = 0; i < chatList.size(); i++) {

                if (chat1.getChatID() == chatList.get(i).getChatID()) {

                    chatList.remove(i);
                    Log.d(TAG, "LOCAL COPY REMOVED");
                    break;
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    // Status read
    private void setAllMessagesAsRead() {

        for (Chat c : chatList) {

            if (c.getSendingUserID() == RECEIVING_USER) {

                if (c.getReadDate() == null) {

                    setThisChatSeenStatusAsTrue(c);
                }
            }
        }


    }

    private void setThisChatSeenStatusAsTrue(final Chat c) {

        DatabaseReference reference = firebaseDatabase.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);

        final Date readDate = new Date();
        Map<String, Object> dateMap = new HashMap<>();
        dateMap.put("readDate", readDate);
        reference.child(c.getChatID() + "").updateChildren(dateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null) {

                    c.setReadDate(readDate);
                }
            }
        });


    }

    // Updated
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

            if (chatID == chatList.get(i).getChatID()) {

                chatList.set(i, messageChat);
                Log.d(TAG, messageChat.getMessage() + " is updated successfully");
                break;
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void updateThisImageChatFromDatabaseToLocal(DataSnapshot chat) {

        ImageChat imageChat = chat.getValue(ImageChat.class);
        assert imageChat != null;
        imageChat.setSentDate(imageChat.getTypedDate());

        String chatID = imageChat.getChatID();

        for (int i = 0; i < chatList.size(); i++) {

            if (chatID == chatList.get(i).getChatID()) {

                chatList.set(i, imageChat);
                Log.d(TAG, imageChat.getImageUri() + " is updated successfully");
                break;
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void updateThisDocumentChatFromDatabaseToLocal(DataSnapshot chat) {

        DocumentChat documentChat = chat.getValue(DocumentChat.class);
        assert documentChat != null;
        documentChat.setSentDate(documentChat.getTypedDate());

        String chatID = documentChat.getChatID();

        for (int i = 0; i < chatList.size(); i++) {

            if (chatID == chatList.get(i).getChatID()) {

                chatList.set(i, documentChat);
                Log.d(TAG, documentChat.getDocumentName() + " is updated successfully");
                break;
            }
        }

        adapter.notifyDataSetChanged();
    }


    // Others
    private String getRelativeDate(Date date) {

        int second = 1000;
        int minute = second * 60;
        int hour = minute * 60;
        int day = hour * 24;

        Date currentDate = new Date();


        long difference = currentDate.getTime() - date.getTime();

        if (difference > day) {

            return ((int) (Math.floor(1f * difference / day))) + "d";

        } else if (difference > hour) {

            return ((int) (Math.floor(1f * difference / hour))) + "h";

        } else if (difference > minute) {

            return ((int) (Math.floor(1f * difference / minute))) + "m";
        } else {

            int diff = ((int) (Math.floor(1f * difference / second)));

            if (diff > 0) {

                return diff + "s";

            } else {

                return "now";
            }
        }

    }

    private void requestCameraCall() {


        if (CommonUtils.checkIfCameraReady(this)) {

            openCameraForImage();

        } else {

            CommonUtils.getCameraReady(this, CAMERA_AND_IMAGE_BOTH);
        }

    }

    private void openCameraForImage() {

        if (FirebaseGlobals.makeImageDirectory()) {

            File capturedImageFile = new File(FirebaseGlobals.Directory.LOCAL_IMAGE_DIRECTORY + "/" + System.currentTimeMillis() + ".jpg");

            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", capturedImageFile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            startActivityForResult(cameraIntent, CAMERA_IMAGE_CAPTURE_ACTIVITY_RESULT);

        } else {

            Toast.makeText(this, "Error while making Image diirectory", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAttachmentSelectionDialogue() {

        filePickerUtils.showNow();
    }

    private String getSize(long length) {

        int kb = 1024;
        int mb = kb * 1000;

        if (length > mb) {

            return new DecimalFormat("##.##").format(1f * length / mb) + "MB";
        } else if (length > kb) {

            return new DecimalFormat("##.##").format(1f * length / kb) + "KB";
        } else {

            return length + "B";
        }

    }

    private void deleteThisChatFromDatabase(String chatID) {

        DatabaseReference reference = firebaseDatabase.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);
        reference.child(String.valueOf(chatID)).removeValue();

    }

    @SuppressLint("InflateParams")
    private void fullScreenImageShowingDialogue(String uri) {

//        View view = getLayoutInflater().inflate(R.layout.view_full_screen_image_showing, null);
//        ImageView imageView = view.findViewById(R.id.IVFullScreenImageShowing);
//
//        Picasso.get().load(uri).into(imageView);
//
//        Dialog fullImageDialogue = new Dialog(this, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
//        fullImageDialogue.setContentView(view);
//        fullImageDialogue.show();

    }

    private boolean validateThisDocumentInStorage(String documentName, long documentSize) {

        getStoragePermission(STORAGE_PERMISSION);

        File documentFile = new File(FirebaseGlobals.Directory.LOCAL_DOCUMENT_DIRECTORY + "/" + documentName);

        if (documentFile.exists()) {

            return documentFile.length() == documentSize;

        } else {

            Log.d(TAG, "File doesn't exists");
            return false;

        }
    }

    private void openThisDocumentViaUri(String documentName) {

        File file = new File(FirebaseGlobals.Directory.LOCAL_DOCUMENT_DIRECTORY + "/" + documentName);

        Uri documentUri;

        //We will need Content providers here
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            documentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);

        }

        // Else simple uri from file is enough
        else {

            documentUri = Uri.fromFile(file);

        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (documentUri.toString().contains(".doc") || documentUri.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(documentUri, "application/msword");
        } else if (documentUri.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(documentUri, "application/pdf");
        } else if (documentUri.toString().contains(".ppt") || documentUri.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(documentUri, "application/vnd.ms-powerpoint");
        } else if (documentUri.toString().contains(".xls") || documentUri.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(documentUri, "application/vnd.ms-excel");
        } else if (documentUri.toString().contains(".zip")) {
            // ZIP file
            intent.setDataAndType(documentUri, "application/zip");
        } else if (documentUri.toString().contains(".rar")) {
            // RAR file
            intent.setDataAndType(documentUri, "application/x-rar-compressed");
        } else if (documentUri.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(documentUri, "application/rtf");
        } else if (documentUri.toString().contains(".wav") || documentUri.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(documentUri, "audio/x-wav");
        } else if (documentUri.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(documentUri, "image/gif");
        } else if (documentUri.toString().contains(".jpg") || documentUri.toString().contains(".jpeg") || documentUri.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(documentUri, "image/jpeg");
        } else if (documentUri.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(documentUri, "text/plain");
        } else if (documentUri.toString().contains(".3gp") || documentUri.toString().contains(".mpg") ||
                documentUri.toString().contains(".mpeg") || documentUri.toString().contains(".mpe") || documentUri.toString().contains(".mp4") || documentUri.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(documentUri, "video/*");
        } else {
            intent.setDataAndType(documentUri, "*/*");
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            startActivity(intent);

        } catch (Exception ex) {

            Toast.makeText(this, "No app to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private void getStoragePermission(int mode) {

        // If device is on Marshmallow or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // If permission for using camera is not granted
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, mode);

            }
        }
    }

    private boolean checkIfStoragePermissionIsGranted() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        } else {

            return true;
        }
    }


    //Upload Image Chat
    private class UploadImageTaskClass implements ObjectChatUploadInterface {

        private final DatabaseReference databaseReference = firebaseDatabase.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);
        private final StorageReference storageReference = FirebaseStorage.getInstance().getReference(FirebaseGlobals.Storage.IMAGE_STORAGE_REFERENCE);
        private FirebaseUploadListener listener;
        private ImageChat imageChat;


        public UploadImageTaskClass(ImageChat imageChat) {

            this.imageChat = imageChat;
        }

        public void setListener(FirebaseUploadListener listener) {
            this.listener = listener;
            uploadObjectToStorage();
        }

        @Override
        public void uploadObjectToStorage() {

            storageReference.child(imageChat.getChatID() + ".jpg")
                    .putFile(Uri.parse(imageChat.getImageUri()))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            getObjectURI();
                            Log.d(TAG, "Image uploaded successfully");

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            onObjectUploadFailed();
                            Log.d(TAG, "Exception: " + e);

                        }
                    })

                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            onObjectUploadFailed();
                            Log.d(TAG, "Canceled");

                        }
                    });

        }

        @Override
        public void getObjectURI() {

            listener.progress(30);

            storageReference.child(String.valueOf(imageChat.getChatID()) + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Log.d(TAG, "URI got " + uri);
                            imageChat.setImageUri(uri.toString());
                            onObjectUploadSuccess();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "URI failed");
                            onObjectUploadFailed();

                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            Log.d(TAG, "URI failed");
                            onObjectUploadFailed();

                        }
                    });


        }

        @Override
        public void onObjectUploadSuccess() {

            uploadObjectChatToDatabase();
            listener.progress(60);

        }

        @Override
        public void onObjectUploadFailed() {

            listener.onTaskUploadFailed();

        }

        @Override
        public void uploadObjectChatToDatabase() {

            databaseReference.child(String.valueOf(imageChat.getChatID()))
                    .setValue(imageChat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(TAG, "Image Chat uploaded completey");
                            onObjectChatUploadSuccess();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Image chat uploaded failed");
                            onObjectChatUploadFailed();

                        }
                    })

                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            Log.d(TAG, "Image chat uploaded failed");
                            onObjectChatUploadFailed();

                        }
                    });

        }

        @Override
        public void onObjectChatUploadSuccess() {

            listener.progress(100);
            listener.onTaskUploadSuccess();
        }

        @Override
        public void onObjectChatUploadFailed() {

            listener.onTaskUploadFailed();

        }
    }

    //Upload Document Chat
    private class UploadDocumentTaskClass implements ObjectChatUploadInterface {

        private final DatabaseReference databaseReference = firebaseDatabase.getReference(FirebaseGlobals.Database.QUERY_CHAT_REFERENCE);
        private final StorageReference storageReference = FirebaseStorage.getInstance().getReference(FirebaseGlobals.Storage.DOCUMENT_STORAGE_REFERENCE);
        private FirebaseUploadListener listener;
        private DocumentChat documentChat;


        public UploadDocumentTaskClass(DocumentChat documentChat) {

            this.documentChat = documentChat;
            this.documentChat.setProgress(100);
        }

        public void setListener(FirebaseUploadListener listener) {
            this.listener = listener;
            uploadObjectToStorage();
        }

        @Override
        public void uploadObjectToStorage() {

            storageReference.child(documentChat.getChatID() + documentChat.getDocumentName())
                    .putFile(Uri.parse(documentChat.getDocumentUri()))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            getObjectURI();
                            Log.d(TAG, "Document uploaded successfully");

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            onObjectUploadFailed();
                            Log.d(TAG, "Exception: " + e);

                        }
                    })

                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            onObjectUploadFailed();
                            Log.d(TAG, "Canceled");

                        }
                    });

        }

        @Override
        public void getObjectURI() {

            listener.progress(30);

            storageReference.child(documentChat.getChatID() + documentChat.getDocumentName())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Log.d(TAG, "URI got " + uri);
                            documentChat.setDocumentUri(uri.toString());
                            onObjectUploadSuccess();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "URI failed");
                            onObjectUploadFailed();

                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            Log.d(TAG, "URI failed");
                            onObjectUploadFailed();

                        }
                    });

        }

        @Override
        public void onObjectUploadSuccess() {

            uploadObjectChatToDatabase();
            listener.progress(60);

        }

        @Override
        public void onObjectUploadFailed() {

            listener.onTaskUploadFailed();

        }

        @Override
        public void uploadObjectChatToDatabase() {

            databaseReference.child(String.valueOf(documentChat.getChatID()))
                    .setValue(documentChat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(TAG, "Image Chat uploaded completey");
                            onObjectChatUploadSuccess();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Image chat uploaded failed");
                            onObjectChatUploadFailed();

                        }
                    })

                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                            Log.d(TAG, "Image chat uploaded failed");
                            onObjectChatUploadFailed();

                        }
                    });

        }

        @Override
        public void onObjectChatUploadSuccess() {

            listener.progress(100);
            listener.onTaskUploadSuccess();
        }

        @Override
        public void onObjectChatUploadFailed() {

            listener.onTaskUploadFailed();

        }
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
                else if (sendingUserID == RECEIVING_USER && receivingUserID == SENDING_USER) {

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


    @SuppressLint("StaticFieldLeak")
    private class BackgroundLoading extends AsyncTask<Void, Void, Void> {

        private List<File> imageFiles;
        private List<File> pdfFiles;
        private List<File> documentsFiles;

        private boolean pending;

        private BackgroundLoading(boolean pending) {

            this.pending = pending;

            imageFiles = new ArrayList<>();
            pdfFiles = new ArrayList<>();
            documentsFiles = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            getAllImageFiles(Environment.getExternalStorageDirectory());
            sortFiles();
            return null;
        }


        private void getAllImageFiles(File externalStorageDirectory) {

            File[] files = externalStorageDirectory.listFiles();

            if (files != null) {

                for (File file : files) {

                    if (file.isDirectory()) {

                        if (file.getName().contains(".")) {
                            continue;
                        }

                        getAllImageFiles(file);

                    } else {

                        if (file.length() < 3072000) {

                            if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg")) {

                                imageFiles.add(file);

                            } else if (file.getName().endsWith(".pdf")) {

                                pdfFiles.add(file);

                            } else {

                                if (file.getName().endsWith(".doc") ||
                                        file.getName().endsWith(".docx") ||
                                        file.getName().endsWith(".ppt") ||
                                        file.getName().endsWith(".pptx") ||
                                        file.getName().endsWith(".xls") ||
                                        file.getName().endsWith(".xlss") ||
                                        file.getName().endsWith(".zip") ||
                                        file.getName().endsWith(".rar") ||
                                        file.getName().endsWith(".wav") ||
                                        file.getName().endsWith(".mp3") ||
                                        file.getName().endsWith(".txt") ||
                                        file.getName().endsWith(".3gp") ||
                                        file.getName().endsWith(".mpg") ||
                                        file.getName().endsWith(".mp4") ||
                                        file.getName().endsWith(".avi") ||
                                        file.getName().endsWith(".mpeg")) {

                                    documentsFiles.add(file);

                                }

                            }
                        }
                    }
                }
            }
        }

        private void sortFiles() {

            Collections.sort(imageFiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o2.lastModified(), o1.lastModified());
                }
            });

            Collections.sort(pdfFiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o2.lastModified(), o1.lastModified());
                }
            });

            Collections.sort(documentsFiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o2.lastModified(), o1.lastModified());
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            filesList.add(imageFiles);
            filesList.add(pdfFiles);
            filesList.add(documentsFiles);
            filesLoaded = true;

            if (pending) {

                showAttachmentSelectionDialogue();
            }
        }


    }


}