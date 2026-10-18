package com.gitgirls.cyclewise.logic.notifications;

public class SchedulerConstants {

    private SchedulerConstants(){};
    public static final String NULL_PERIOD_CALC = "PeriodCalculator cannot be null";
    public static final String NULL_NOTI_SETTING = "NotificationSettings cannot be null";
    public static final String NOTI_NOT_ENABLED = "Notifications are not enabled";
    public static final String NO_NOTIF = "Could not schedule notification";
    public static final String INVALID_DATES = "Dates are wrong somewhere:";
    public static final String BAD_STATE = "Something went wrong!";
    public static final String NULL_NOTI_VALIDATOR = "NotificationValidator cannot be null";
    public static final String PERSISTENCE_UNINITIALIZED = "NotificationPersistence Not Initialized. User isn't logged in";
    public static final int MAX_DAYS_LATE = 7;


    // UI messages
    public static final String ERROR_DAYS_REQUIRED      = "Please enter a number";
    public static final String SETTINGS_SAVED           = "Settings saved!";
    public static final String NO_LOGS                  = "Cannot enable notifications. Not enough data.";
    // receiver stuff
    public static final String LOG_TAG_RECEIVER = "NotificationReceiver";
    public static final String LOG_MISSING_DATE = "Missing predicted date in intent";

    // Notification channel
    public static final String CHANNEL_ID          = "ch_period_reminder";
    public static final String CHANNEL_NAME        = "Period Reminders";
    public static final String CHANNEL_DESCRIPTION = "Reminders about your upcoming period";
    public static final int    NOTIF_ID            = 1001;

    // Notification titles
    public static final String TITLE_LATE          = "Period Late";
    public static final String TITLE_REMINDER      = "Period Reminder";

    // Notification body
    public static final String BODY_LATE_PREFIX    = "Your period is ";
    public static final String BODY_DUE_TODAY      = "Your period is due today!";
    public static final String BODY_DUE_IN_PREFIX  = "Your period is due in ";
    public static final String BODY_DAYS_SUFFIX    = " day(s).";
    public static final String BODY_LATE_SUFFIX    = " day(s) late.";
}
