package com.gitgirls.cyclewise.persistence.real.userrepository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.*;

import com.gitgirls.cyclewise.models.User; // FIX: Should we access this?
import com.gitgirls.cyclewise.persistence.UserPersistence;
import com.gitgirls.cyclewise.persistence.real.DatabaseHelper;


/**
 * UserPersistenceReal
 * Handles CRUD operations for the UserAccount table.
 */
public class UserPersistenceReal implements UserPersistence {

    private final DatabaseHelper dbHelper;


    public UserPersistenceReal(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public boolean addUser(String username, String firstName, String lastName, String password, String dob) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_FIRST_NAME, firstName);
        values.put(COL_LAST_NAME, lastName);
        values.put(COL_PASSWORD, password);
        values.put(COL_DOB, dob);

        long result = db.insert(TABLE_USER_ACCOUNT, null, values);
        return result != -1;
    }

    @Override
    public User getByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(
                TABLE_USER_ACCOUNT,
                null,
                COL_USERNAME + "=?",
                new String[]{username},
                null,
                null,
                null
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String dob = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOB));

                user = new User(username, password, firstName, lastName, dob);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }
    @Override
    public boolean updateUserInfo(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD, newPassword);

        int rows = db.update(
                TABLE_USER_ACCOUNT,
                values,
                COL_USERNAME + "=?",
                new String[]{username}
        );
        return rows > 0;
    }

    @Override
    public boolean removeUser(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                TABLE_USER_ACCOUNT,
                COL_USERNAME + "=?",
                new String[]{username}
        );
        return rows > 0;
    }


}