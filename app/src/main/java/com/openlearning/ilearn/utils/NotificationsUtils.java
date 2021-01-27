package com.openlearning.ilearn.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;

import androidx.core.app.NotificationCompat;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.activities.HomeScreen;
import com.openlearning.ilearn.chat.activities.AllChats;
import com.openlearning.ilearn.news.activities.NewsDetails;
import com.openlearning.ilearn.news.modals.News;

public class NotificationsUtils {

    public static final int NOTIFICATION_ID_CHAT = 1024;
    public static String NOTIFICATION_CHANNEL_1 = "CHANNEL_1";
    public static final String CHAT_NOTIFICATION_NAME = "Chat Notification";

    public static NotificationsUtils instance;

    public static NotificationsUtils getInstance() {

        if (instance == null) {
            instance = new NotificationsUtils();
        }

        return instance;
    }

    private NotificationsUtils() {
    }

    public void createNotificationChannels(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_1, CHAT_NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Important Messages for chat");

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void createNewsNotification(Context context, News news) {

        Intent intent = new Intent(context, NewsDetails.class);
        intent.putExtra(News.PARCELABLE_KEY, (Parcelable) news);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_1)
                .setSmallIcon(R.drawable.ic_article_24)
                .setContentTitle(news.getHeading())
                .setContentText(news.getTitle())
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_CHAT, notification);


    }

    public void createChatNotification(Context context) {

        Intent intent = new Intent(context, AllChats.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                1, intent, 0);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_1)
                .setSmallIcon(R.drawable.ic_comment_small)
                .setContentTitle("New Message Received")
                .setContentText("Click here to open your new message")
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_CHAT, notification);
    }
}
