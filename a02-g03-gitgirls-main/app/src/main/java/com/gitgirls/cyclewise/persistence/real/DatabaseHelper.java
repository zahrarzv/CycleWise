package com.gitgirls.cyclewise.persistence.real;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper
 *
 * Responsible only for:
 *   - Opening / creating the database file
 *   - Delegating schema creation to CycleWiseSchema
 *   - Delegating seed data to SeedData
 *   - Handling version upgrades
 *
 * No table names, column names, or SQL strings live here.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String CYCLEWISE_DATABASE = "cyclewise.db";
    public static final int    DB_VERSION = 5; // bumped: added date column to DailyLog // bumped: replaced Symptoms table with SymptomType + LogSymptom //bumped: added notafications to database

    private String dbName;
    private final Context context;



    public DatabaseHelper(Context context) {
        super(context, CYCLEWISE_DATABASE, null, DB_VERSION);
        this.context = context.getApplicationContext();
    }

    /** Integration test constructor - pass "test.db" to get an isolated database. */
    public DatabaseHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
        this.context = context.getApplicationContext();
        this.dbName = dbName;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Enable foreign key support
        db.execSQL("PRAGMA foreign_keys = ON;");

        // Create tables
        db.execSQL(CycleWiseSchema.CREATE_TABLE_USER_ACCOUNT);
        db.execSQL(CycleWiseSchema.CREATE_TABLE_CYCLE);
        db.execSQL(CycleWiseSchema.CREATE_TABLE_DAILY_LOG);
        db.execSQL(CycleWiseSchema.CREATE_TABLE_SYMPTOM_TYPE);
        db.execSQL(CycleWiseSchema.CREATE_TABLE_LOG_SYMPTOM);
        db.execSQL(CycleWiseSchema.CREATE_TABLE_NOTIFICATION_SETTINGS);

        // Insert seed data
        for (String sql : SeedData.SEED_STATEMENTS) {
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables in reverse dependency order
        db.execSQL("DROP TABLE IF EXISTS " + CycleWiseSchema.TABLE_NOTIFICATION_SETTINGS); // references UserAccount
        db.execSQL("DROP TABLE IF EXISTS " + CycleWiseSchema.TABLE_LOG_SYMPTOM);
        db.execSQL("DROP TABLE IF EXISTS " + CycleWiseSchema.TABLE_SYMPTOM_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + CycleWiseSchema.TABLE_DAILY_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + CycleWiseSchema.TABLE_CYCLE);
        db.execSQL("DROP TABLE IF EXISTS " + CycleWiseSchema.TABLE_USER_ACCOUNT);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Android resets PRAGMA foreign_keys on every connection — must re-enable here
        db.execSQL("PRAGMA foreign_keys = ON;");
    }


    /** Deletes the database file. Used by integration tests to reset state between runs. */
    public void deleteDatabase() {
        context.deleteDatabase(dbName);
    }
}