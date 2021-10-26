package com.w3engineers.mesh.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.w3engineers.mesh.R;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;

public class NotificationUtil {
    private static final String CHANNEL_NAME = "viper-client-msg";
    private static final String CHANNEL_ID = "viper-notification_channel";
    private static final int MAX = 100;

    private static NotificationCompat.Builder progressBuilder;

    public static void showAppUpdateProgress(Context context, String appName) {
        HandlerUtil.postForeground(() -> {
            progressBuilder = buildNotification(context);
            prepareProgressNotification(progressBuilder, appName);
            showNotification(context, progressBuilder, appName);
        });
    }

    private static NotificationCompat.Builder buildNotification(Context context) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{2000, 2000});

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    private static void prepareProgressNotification(NotificationCompat.Builder builder, String appName) {
        String title = "Downloading " + appName + " please wait..";
        builder.setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_logo);
        builder.setProgress(MAX, 0, false);
    }


    private static void showNotification(Context context, NotificationCompat.Builder builder, String userId) {
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        int notifyId = Math.abs(userId.hashCode());
        if (notificationManager != null) {
            notificationManager.notify(notifyId, notification);
        }
    }

    public static void updateProgress(Context context, String appName, int progress) {
        HandlerUtil.postForeground(() -> {
            if (progressBuilder == null) return;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService
                    (Context.NOTIFICATION_SERVICE);
            int notifyId = Math.abs(appName.hashCode());
            progressBuilder.setProgress(MAX, progress, false);
            if (notificationManager != null) {
                notificationManager.notify(notifyId, progressBuilder.build());
            }
        });
    }

    public static void removeNotification(Context context, String myUserId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        int notifyId = Math.abs(myUserId.hashCode());
        if (notificationManager != null) {
            notificationManager.cancel(notifyId);
        }
    }

}
