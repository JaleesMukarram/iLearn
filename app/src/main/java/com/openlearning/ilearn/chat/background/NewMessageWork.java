package com.openlearning.ilearn.chat.background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.openlearning.ilearn.chat.repositories.AllChatsRepository;
import com.openlearning.ilearn.utils.BGTasks;

public class NewMessageWork extends Worker {

    private static final String TAG = "NewMessageWorkTAG";
    public static final String UNIQUE_NAME = "NewMessageWork";
    private final Context context;

    public NewMessageWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d(TAG, "doWork: Work Started");

        new BGTasks(context).startBothTasks();

        return Result.retry();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.d(TAG, "onStopped: work stopped");
    }

}


// new Thread() {
//@Override
//public void run() {
//
//        for (int i = 0; i < 100; i++) {
//
//        try {
//        sleep(1000);
//        Log.d(TAG, "run: counter: " + i);
//        } catch (InterruptedException e) {
//        e.printStackTrace();
//        }
//        }
//        }
//        }.start();
