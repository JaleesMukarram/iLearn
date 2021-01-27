package com.openlearning.ilearn.utils;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.openlearning.ilearn.chat.repositories.AllChatsRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.news.modals.News;
import com.openlearning.ilearn.news.repositories.NewsRepository;

import java.util.Date;
import java.util.List;

public class BackgroundService extends LifecycleService {

    public static final String TAG = "BGServiceTAG";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service created");
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand: command started");

        new BGTasks(this).startBothTasks();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service destroyed");
    }


}