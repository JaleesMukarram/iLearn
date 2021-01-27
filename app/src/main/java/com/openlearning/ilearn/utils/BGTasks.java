package com.openlearning.ilearn.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.openlearning.ilearn.chat.repositories.AllChatsRepository;
import com.openlearning.ilearn.interfaces.FireStoreObjectGetListener;
import com.openlearning.ilearn.news.modals.News;
import com.openlearning.ilearn.news.repositories.NewsRepository;

import java.util.Date;
import java.util.List;

public class BGTasks {

    public static final String TAG = "BGTasksTAG";
    private final AllChatsRepository allChatsRepository;
    private int previousUnreadCount = 0;
    private final Context context;

    public BGTasks(Context context) {
        this.context = context;
        allChatsRepository = AllChatsRepository.getInstance();
    }

    public void startBothTasks() {

        checkNewsThings();
        checkChatThings();
    }

    private void checkNewsThings() {

        NewsRepository.getInstance().getNewsFromDatabase(new FireStoreObjectGetListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(@Nullable Object obj) {

                if (obj != null) {

                    List<News> newsList = (List<News>) obj;
                    News latestNews = newsList.get(0);
                    if (checkIfNewNewsAvailable(latestNews)) {

                        NotificationsUtils.getInstance().createNewsNotification(context, latestNews);

                    }
                }
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });

    }

    private boolean checkIfNewNewsAvailable(News news) {

        try {
            Date currentDate = new Date();
            long diff = currentDate.getTime() - news.getCreatedDate().getTime();
            Log.d(TAG, "checkIfNewNewsAvailable: diff: " + diff);
            return diff < 1000 * 10;

        } catch (Exception ex) {
            return false;
        }

    }

    private void checkChatThings() {

        allChatsRepository.getChatUpdated().observe((LifecycleOwner) context, updated -> {

            Log.d(TAG, "checkChatThings: New List Observed for chat with user size: " + allChatsRepository.getQueryUserList().size());

            int currentUnread = allChatsRepository.getTotalUnReadMessages();

            if (currentUnread != previousUnreadCount) {

                if (currentUnread > previousUnreadCount) {
                    Log.d(TAG, "checkChatThings: Unread incremented previous: " + previousUnreadCount + " new: " + currentUnread);
                    previousUnreadCount = currentUnread;
                    NotificationsUtils.getInstance().createChatNotification(context);
                }
                previousUnreadCount = currentUnread;

            } else {
                Log.d(TAG, "checkChatThings: no new message from previous notified point");
            }

        });
    }


}
