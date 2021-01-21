package com.openlearning.ilearn.chat.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.openlearning.ilearn.BuildConfig;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.chat.adapters.ChatAdapter;
import com.openlearning.ilearn.chat.queries.Chat;
import com.openlearning.ilearn.chat.queries.FirebaseGlobals;
import com.openlearning.ilearn.chat.view_models.SendChatVM;
import com.openlearning.ilearn.databinding.ActivitySendChatBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;
import com.openlearning.ilearn.utils.CommonUtils;
import com.openlearning.ilearn.utils.FilePickerUtils;

import java.io.File;


public class SendChat extends AppCompatActivity implements ActivityHooks {

    public static final int CAMERA_AND_STORAGE_BOTH = 1;
    public static final int STORAGE_PERMISSION_FOR_BACKGROUND_LOADING = 2;
    public static final int CAMERA_IMAGE_CAPTURE_ACTIVITY_RESULT = 4;

    public static final String TAG = "SENDCHATTAG";
    private String SENDING_USER;
    private String RECEIVING_USER;

    private ChatAdapter adapter;

    private SoundPool player;
    private int chatReceivedSound;

    private Uri imageUri;

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

        RECEIVING_USER = getIntent().getStringExtra(Chat.RECEIVING_USER_ID_KEY);

        if (RECEIVING_USER == null || RECEIVING_USER.equals("")) {

            Toast.makeText(this, "Unable to start chat", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void init() {

        viewModel = ViewModelProviders.of(this).get(SendChatVM.class);
        SENDING_USER = viewModel.initIDs(RECEIVING_USER);

        initializeSoundPool();

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


        filePickerUtils = new FilePickerUtils(this, FilePickerUtils.MODE_ALL, (file, type) -> {

            if (type == FilePickerUtils.INDEX_FILE_IMAGES) {

                viewModel.handleNewImageChat(imageUri);
            } else {

                viewModel.handleNewDocumentChat(file);

            }

        });

        if (CommonUtils.checkIfStoragePermissionIsGranted(this)) {

            filePickerUtils.callHooks();
        }

        viewModel.getReceivingUserName().observe(this, name -> mBinding.TVNameShowing.setText(name));


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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_CAPTURE_ACTIVITY_RESULT) {
            if (resultCode == RESULT_OK) {

                viewModel.handleNewImageChat(imageUri);

            }
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

                break;

            case CAMERA_AND_STORAGE_BOTH:

                Log.d(TAG, "onRequestPermissionsResult: permissions size: " + permissions.length);
                Log.d(TAG, "onRequestPermissionsResult: grantResults size: " + grantResults.length);

                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    openCameraForImage();

                } else {

                    Toast.makeText(this, "Camera or Storage Permission denied", Toast.LENGTH_SHORT).show();
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

        playSound();

    }

    private void playSound() {

        if (player != null) {

            player.play(chatReceivedSound, 0.5f, 0.5f, 1, 0, 1);
        }
    }

    private void requestCameraCall() {

        if (CommonUtils.checkIfCameraReady(this)) {

            openCameraForImage();

        } else {

            CommonUtils.getCameraReady(this, CAMERA_AND_STORAGE_BOTH);
//            Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this, "Error while making Image directory", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAttachmentSelectionDialogue() {

        filePickerUtils.showNow();
    }


}