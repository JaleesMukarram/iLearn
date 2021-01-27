package com.openlearning.ilearn.utils;

import android.app.Application;
import android.util.Log;

public class App extends Application {

    public static final String TAG = "APPTAG";


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: App created");

        NotificationsUtils.getInstance().createNotificationChannels(this);

    }


}
