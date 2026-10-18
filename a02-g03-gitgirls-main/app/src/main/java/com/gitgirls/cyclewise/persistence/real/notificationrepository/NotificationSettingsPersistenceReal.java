package com.gitgirls.cyclewise.persistence.real.notificationrepository;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gitgirls.cyclewise.models.NotificationSettings;
import com.gitgirls.cyclewise.persistence.NotificationSettingsPersistence;

import java.time.LocalTime;
import com.gitgirls.cyclewise.persistence.real.DatabaseHelper;

import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.*;
public class NotificationSettingsPersistenceReal implements NotificationSettingsPersistence{
    private final DatabaseHelper dbHelper;

    private static final int DEFAULT_DAYS_BEFORE = 3;
    private static final int DEFAULT_HOUR = 9;
    private static final int DEFAULT_MINUTE = 0;

    public NotificationSettingsPersistenceReal(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public NotificationSettings getSettings(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NOTIFICATION_SETTINGS + " WHERE username = ?",
                new String[]{username})) {

            if (!cursor.moveToFirst())  return defaultSettings();

            return new NotificationSettings(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_DAYS_BEFORE_REMINDER)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ENABLED)) == 1,
                    LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(COL_REMINDER_TIME)))
            );
        }
    }

    @Override
    public boolean save(NotificationSettings settings, String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put(COL_DAYS_BEFORE_REMINDER, settings.getDaysBeforeReminder());
        values.put(COL_IS_ENABLED, settings.isEnabled() ? 1 : 0);
        values.put(COL_REMINDER_TIME, settings.getReminderTime().toString());

        long result = db.insertWithOnConflict(TABLE_NOTIFICATION_SETTINGS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }
    private NotificationSettings defaultSettings(){
        return new NotificationSettings(DEFAULT_DAYS_BEFORE, false, LocalTime.of(DEFAULT_HOUR, DEFAULT_MINUTE));
    }
}
