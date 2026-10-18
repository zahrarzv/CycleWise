package com.gitgirls.cyclewise.application;

import android.app.Application;
import android.content.Context;

import com.gitgirls.cyclewise.logic.notifications.SchedulerConstants;
import com.gitgirls.cyclewise.logic.notifications.SchedulerService;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.User;
import com.gitgirls.cyclewise.logic.periodlogging.PeriodLogService;
import com.gitgirls.cyclewise.persistence.NotificationSettingsPersistence;
import com.gitgirls.cyclewise.persistence.real.notificationrepository.NotificationSettingsPersistenceReal;
import com.gitgirls.cyclewise.persistence.real.DatabaseHelper;
import com.gitgirls.cyclewise.persistence.real.userrepository.UserPersistenceReal;

import com.gitgirls.cyclewise.persistence.real.periodlogrepository.PeriodLogPersistenceReal;
import com.gitgirls.cyclewise.presentation.notification.NotificationHelper;
import com.gitgirls.cyclewise.presentation.notification.NotificationMapper;

public class PeriodTracker extends Application {

    private DatabaseHelper dbHelper;
    private AuthService authService;
    private PeriodLogService periodLogService;
    private User loggedInUser;  // store currently logged-in user

    private static NotificationSettingsPersistence notificationSettingsPersistence;

    @Override
    public void onCreate() {
        super.onCreate();

        // Single DatabaseHelper instance for the app
        dbHelper = new DatabaseHelper(getApplicationContext());
        UserPersistenceReal userPersistence = new UserPersistenceReal(dbHelper);
        authService = new AuthService(userPersistence);
        NotificationHelper.createChannel(getApplicationContext());
        // periodLogService will be created after login
        periodLogService = null;
        loggedInUser = null;
    }



    // Call this after a successful login
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        if (user != null) {
            periodLogService = new PeriodLogService(new PeriodLogPersistenceReal(dbHelper), user.getUsername());
            notificationSettingsPersistence = new NotificationSettingsPersistenceReal(dbHelper);
        } else {
            periodLogService = null;
            notificationSettingsPersistence = null; // ADD THIS
        }
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public PeriodLogService getPeriodLogService() {
        if (periodLogService == null) {
            throw new IllegalStateException(
                    ApplicationMessages.ERROR_PERIOD_LOG_SERVICE_NOT_INITIALIZED
            );
        }
        return periodLogService;
    }
    public static NotificationSettingsPersistence getNotificationSettingsPersistence() {
        if (notificationSettingsPersistence == null) {
            throw new IllegalStateException(SchedulerConstants.PERSISTENCE_UNINITIALIZED);
        }
        return notificationSettingsPersistence;
    }

    /** For testing only — swaps the database to an isolated test DB. */
    public void setTestDatabase(String dbName) {
        dbHelper = new DatabaseHelper(getApplicationContext(), dbName);
        UserPersistenceReal userPersistence = new UserPersistenceReal(dbHelper);
        authService = new AuthService(userPersistence);
        periodLogService = null;
        loggedInUser = null;
        notificationSettingsPersistence = null;
    }

    public DatabaseHelper getDatabaseHelper() {
        return dbHelper;
    }

    //factory method
    public static SchedulerService getSchedulerService(Context context) {
        return new SchedulerService(
                getNotificationSettingsPersistence(),
                new NotificationMapper(context)
        );
    }
}