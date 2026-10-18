package com.gitgirls.cyclewise.presentation.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gitgirls.cyclewise.logic.notifications.SchedulerConstants;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String EXTRA_PREDICTED_DATE = "predicted_date";
    public static final String EXTRA_DAYS_BEFORE_REMINDER = "days_before_reminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "onReceive triggered");
        String predictedDateStr = intent.getStringExtra(EXTRA_PREDICTED_DATE);

        if (predictedDateStr == null) {
            Log.e(SchedulerConstants.LOG_TAG_RECEIVER, SchedulerConstants.LOG_MISSING_DATE);
            return;
        }

        LocalDate predictedDate = LocalDate.parse(predictedDateStr);
        int daysUntil = (int) ChronoUnit.DAYS.between(LocalDate.now(), predictedDate);
        boolean isLate = daysUntil < 0;
        int days = Math.abs(daysUntil);

        int daysBeforeReminder = intent.getIntExtra(EXTRA_DAYS_BEFORE_REMINDER, 3);

        if (!isLate && daysUntil > daysBeforeReminder) return; // too early
        if (isLate && days > SchedulerConstants.MAX_DAYS_LATE) return; // too late

        NotificationHelper helper = new NotificationHelper(context);
        helper.sendPeriodReminder(days, isLate);
    }
}