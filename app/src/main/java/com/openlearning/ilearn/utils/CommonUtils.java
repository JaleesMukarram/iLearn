package com.openlearning.ilearn.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.openlearning.ilearn.dialogues.LoadingDialogue;
import com.openlearning.ilearn.dialogues.MessageDialogue;
import com.openlearning.ilearn.interfaces.ConfirmationListener;
import com.openlearning.ilearn.modals.StorageImage;

import java.util.Date;

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
    public static final int MAX_LENGTH_MAX = 102400;


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

    public static boolean checkIfStoragePermissionIsGranted(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        } else {

            return true;
        }
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

    public static void deleteThisStorageImage(StorageImage storageImage) {

        FirebaseStorage.getInstance().getReference(storageImage.getStorageDeletePath()).delete();

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


}
