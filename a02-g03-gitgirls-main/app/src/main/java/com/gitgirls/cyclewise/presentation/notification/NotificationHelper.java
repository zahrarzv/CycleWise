package com.gitgirls.cyclewise.presentation.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.logic.notifications.SchedulerConstants;

public class NotificationHelper {

    private static final String CHANNEL_ID   = SchedulerConstants.CHANNEL_ID;
    private static final String CHANNEL_NAME = SchedulerConstants.CHANNEL_NAME;
    private static final int    NOTIF_ID     = SchedulerConstants.NOTIF_ID;

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void createChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(SchedulerConstants.CHANNEL_DESCRIPTION);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    public void sendPeriodReminder(int days, boolean isLate) {
        String title = isLate ? SchedulerConstants.TITLE_LATE : SchedulerConstants.TITLE_REMINDER;
        String body = isLate
                ? SchedulerConstants.BODY_LATE_PREFIX + days + SchedulerConstants.BODY_LATE_SUFFIX
                : days == 0
                ? SchedulerConstants.BODY_DUE_TODAY
                : SchedulerConstants.BODY_DUE_IN_PREFIX + days + SchedulerConstants.BODY_DAYS_SUFFIX;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(NOTIF_ID, builder.build());
    }
}