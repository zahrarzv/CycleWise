package com.gitgirls.cyclewise.presentation.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class NotificationMapper {

    private final Context context;

    public NotificationMapper(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleNotification(LocalDate notifDate, LocalTime notifTime, LocalDate predictedDate, int daysBeforeReminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_PREDICTED_DATE, predictedDate.toString());
        intent.putExtra(NotificationReceiver.EXTRA_DAYS_BEFORE_REMINDER, daysBeforeReminder);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);

        ZonedDateTime triggerTime = notifDate.atTime(notifTime).atZone(ZoneId.systemDefault());
        long triggerMillis = triggerTime.toInstant().toEpochMilli();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("NotificationMapper", "Alarm scheduled for: " + triggerTime.toString());
    }
    public void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);

        pendingIntent.cancel();
    }


}