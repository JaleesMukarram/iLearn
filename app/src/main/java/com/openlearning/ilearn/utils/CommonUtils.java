package com.openlearning.ilearn.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.openlearning.ilearn.BuildConfig;
import com.openlearning.ilearn.R;
import com.openlearning.ilearn.chat.queries.FirebaseGlobals;
import com.openlearning.ilearn.databinding.ViewFullScreenImageShowingBinding;
import com.openlearning.ilearn.databinding.ViewSingleImageBinding;
import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.dialogues.MessageDialogue;
import com.openlearning.ilearn.interfaces.ConfirmationListener;
import com.openlearning.ilearn.interfaces.FirebaseSuccessListener;
import com.openlearning.ilearn.modals.PostReact;
import com.openlearning.ilearn.modals.StorageImage;
import com.openlearning.ilearn.modals.StorageItem;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class CommonUtils {

    public static final String APP_STORAGE = "iLearn/";

    public static final String VALIDATION_SUCCESS = "validated";
    public static final String STRING_EMPTY = "";
    public static final String STRING_SPACE = " ";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 20;

    public static final int MIN_LENGTH_SMALL = 3;
    public static final int MIN_LENGTH_MEDIUM = 8;

    public static final int MAX_LENGTH_SMALL = 20;
    public static final int MAX_LENGTH_MEDIUM = 200;
    public static final int MAX_LENGTH_LARGE = 1000;
    public static final int MAX_LENGTH_MAX = 900000;
    private static final String TAG = "CommUtilsTAG";


    private static void showErrorWarningDialogue(Activity activity, String message, int mode) {

        MessageDialogue dialogue = new MessageDialogue(activity);
        dialogue.ready(message, mode);
        dialogue.show();
    }

    public static void showWarningDialogue(Activity activity, String message) {

        showErrorWarningDialogue(activity, message, MessageDialogue.MODE_WARNING);

    }

    public static void showSuccessDialogue(Activity activity, String message) {

        showErrorWarningDialogue(activity, message, MessageDialogue.MODE_SUCCESS);

    }

    public static void showDangerDialogue(Activity activity, String message) {

        showErrorWarningDialogue(activity, message, MessageDialogue.MODE_DANGER);
    }

    public static LoadingDialogue showLoadingDialogue(Activity activity, String ShortMessage, String longMessage) {

        LoadingDialogue loadingDialogue = new LoadingDialogue(activity);
        loadingDialogue.ready(ShortMessage, longMessage);
        loadingDialogue.show();

        return loadingDialogue;
    }

    private static void startIntent(Activity oldActivity, Intent intent) {

        oldActivity.startActivity(intent);
    }

    public static void changeActivity(Activity oldActivity, Class<?> newActivity, boolean finishOld) {

        Intent intent = new Intent(oldActivity, newActivity);
        startIntent(oldActivity, intent);

        if (finishOld)
            oldActivity.finish();

    }

    public static void changeActivity(Activity oldActivity, Intent intent, boolean finishOld) {

        startIntent(oldActivity, intent);
        if (finishOld)
            oldActivity.finish();

    }


    public static String getRelativeDate(Date date) {

        int second = 1000;
        int minute = second * 60;
        int hour = minute * 60;
        int day = hour * 24;

        Date currentDate = new Date();


        long difference = currentDate.getTime() - date.getTime();

        if (difference > day) {

            return ((int) (Math.floor(1f * difference / day))) + " day(s)";

        } else if (difference > hour) {

            return ((int) (Math.floor(1f * difference / hour))) + " hour(s)";

        } else if (difference > minute) {

            return ((int) (Math.floor(1f * difference / minute))) + " minute(s)";
        } else {

            int diff = ((int) (Math.floor(1f * difference / second)));

            if (diff > 0) {

                return diff + " second(s)";

            } else {

                return "now";
            }
        }

    }

    public static boolean checkIfStoragePermissionIsGranted(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        } else {

            return true;
        }
    }

    public static boolean checkIfCameraPermissionIsGranted(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        } else {

            return true;

        }
    }

    public static boolean checkIfCameraReady(Activity activity) {

        return checkIfCameraPermissionIsGranted(activity) && checkIfStoragePermissionIsGranted(activity);
    }

    public static void getStoragePermission(Activity activity, int requestCode) {

        // If device is on Marshmallow or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // If permission for using camera is not granted
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                activity.requestPermissions(permissions, requestCode);

            }
        }
    }

    public static void getCameraReady(Activity activity, int requestCode) {

        // If device is on Marshmallow or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // If permission for using camera is not granted
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                activity.requestPermissions(permissions, requestCode);

            }
        }
    }


    public static void deleteThisStorageImage(StorageItem storageImage) {

        FirebaseStorage.getInstance().getReference(storageImage.getStorageProcessingPath()).delete();

    }

    public static void showConfirmationDialogue(Activity activity, String title, String message, String positiveText, ConfirmationListener listener) {

        AlertDialog.Builder alertConf = new AlertDialog.Builder(activity);

        alertConf.setTitle(title);
        alertConf.setMessage(message);

        alertConf.setPositiveButton(positiveText, (dialogInterface, i) -> listener.onPositive());
        alertConf.setNegativeButton("Cancel", (dialogInterface, i) -> listener.onNegative());

        AlertDialog dialog = alertConf.create();
        dialog.show();
    }

    public static int dpToPixel(Context context, float dp) {

        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static Drawable getDrawable(Context context, int resource) {
        return ContextCompat.getDrawable(context, resource);

    }

    public static String getSize(long length) {

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

    public static void openThisDocumentViaUri(Activity activity, String documentName) {

        File file = new File(FirebaseGlobals.Directory.LOCAL_DOCUMENT_DIRECTORY + "/" + documentName);

        Uri documentUri;

        //We will need Content providers here
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            documentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);

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
            activity.startActivity(intent);

        } catch (Exception ex) {

            Toast.makeText(activity, "No app to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openThisDocumentViaUri(Activity activity, Uri documentUri) {


        //We will need Content providers here
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            documentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", new File(documentUri.getPath()));

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
            activity.startActivity(intent);

        } catch (Exception ex) {

            Toast.makeText(activity, "No app to open this file", Toast.LENGTH_SHORT).show();
        }
    }


    public static void fullScreenImageShowingDialogue(Context context, String uri) {

        ViewFullScreenImageShowingBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_full_screen_image_showing, null, false);
        mBinding.setImageUriString(uri);

        Dialog fullImageDialogue = new Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        fullImageDialogue.setContentView(mBinding.getRoot());
        fullImageDialogue.show();

    }

    public static boolean validateThisDocumentInStorage(String documentName, long documentSize) {

        File documentFile = new File(FirebaseGlobals.Directory.LOCAL_DOCUMENT_DIRECTORY + "/" + documentName);

        if (documentFile.exists()) {

            return documentFile.length() == documentSize;

        } else {

            return false;

        }
    }

    public static void downloadThisStorageItem(StorageItem storageItem, File savingFile, FirebaseSuccessListener listener) {

        Log.d(TAG, "downloadThisStorageItem: Path: " + storageItem.getStorageProcessingPath());

        FirebaseStorage.getInstance().getReference(storageItem.getStorageProcessingPath())
                .getFile(savingFile)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        listener.onFailure(task.getException());
                        return;
                    }

                    listener.onSuccess(null);

                });
    }

    public static LinearLayout.LayoutParams getLayoutParamWithSideMargins(int left, int top, int right, int bottom) {


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left, top, right, bottom);

        return layoutParams;
    }

    public static boolean isMyReactDone(List<PostReact> postReactList, String myID) {

        for (PostReact postReact : postReactList) {

            if (postReact.getUserID().equals(myID)) return true;
        }
        return false;
    }

    public static void removeMyReact(List<PostReact> postReactList, String myID) {

        for (PostReact postReact : postReactList) {

            if (postReact.getUserID().equals(myID)) {

                postReactList.remove(postReact);
                return;
            }
        }
    }

    public static void addMyReact(List<PostReact> postReactList, String myID, int likeStatus) {

        PostReact postReact = new PostReact(myID, likeStatus);
        postReactList.add(postReact);
    }


}
