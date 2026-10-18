package com.gitgirls.cyclewise.persistence.real;

/**
 * CycleWiseSchema
 * Single source of truth for all table names, column names,
 * and CREATE TABLE statements.
 */
public final class CycleWiseSchema {

    private CycleWiseSchema() {}

    
    // TABLE NAMES
    

    public static final String TABLE_USER_ACCOUNT = "UserAccount";
    public static final String TABLE_CYCLE        = "Cycle";
    public static final String TABLE_DAILY_LOG    = "DailyLog";
    public static final String TABLE_SYMPTOM_TYPE = "SymptomType";  // lookup - pre-seeded
    public static final String TABLE_LOG_SYMPTOM  = "LogSymptom";   // join table

    
    // COLUMN NAMES - UserAccount
    

    public static final String COL_USERNAME   = "username";
    public static final String COL_FIRST_NAME = "first_name";
    public static final String COL_LAST_NAME  = "last_name";
    public static final String COL_PASSWORD   = "password";
    public static final String COL_DOB        = "dob";

    
    // COLUMN NAMES - Cycle
    

    public static final String COL_CYCLE_ID   = "cycle_id";
    public static final String COL_START_DATE = "start_date";
    public static final String COL_END_DATE   = "end_date";

    
    // COLUMN NAMES - DailyLog
    

    public static final String COL_LOG_ID     = "log_id";
    public static final String COL_FLOW_LEVEL = "flow_level";
    public static final String COL_NOTES      = "notes";

    
    // COLUMN NAMES - SymptomType
    

    public static final String COL_SYMPTOM_ID   = "symptom_id";
    public static final String COL_SYMPTOM_NAME = "name";

    // CREATE TABLE - UserAccount

    public static final String CREATE_TABLE_USER_ACCOUNT =
            "CREATE TABLE " + TABLE_USER_ACCOUNT + " (" +
                    COL_USERNAME   + " TEXT PRIMARY KEY NOT NULL, " +
                    COL_FIRST_NAME + " TEXT NOT NULL, " +
                    COL_LAST_NAME  + " TEXT NOT NULL, " +
                    COL_PASSWORD   + " TEXT NOT NULL, " +
                    COL_DOB        + " TEXT NOT NULL" +
                    ");";


    // CREATE TABLE - Cycle
    // start_date / end_date = first and last day of the period


    public static final String CREATE_TABLE_CYCLE =
            "CREATE TABLE " + TABLE_CYCLE + " (" +
                    COL_CYCLE_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_USERNAME   + " TEXT NOT NULL, " +
                    COL_START_DATE + " TEXT NOT NULL, " +
                    COL_END_DATE   + " TEXT, " +
                    "FOREIGN KEY (" + COL_USERNAME + ") REFERENCES " + TABLE_USER_ACCOUNT + "(" + COL_USERNAME + ") ON DELETE CASCADE" +
                    ");";

    
    // CREATE TABLE - DailyLog
    

    public static final String COL_DATE = "date";

    public static final String CREATE_TABLE_DAILY_LOG =
            "CREATE TABLE " + TABLE_DAILY_LOG + " (" +
                    COL_LOG_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CYCLE_ID   + " INTEGER NOT NULL, " +
                    COL_USERNAME   + " TEXT NOT NULL, " +
                    COL_DATE       + " TEXT NOT NULL, " +
                    COL_FLOW_LEVEL + " TEXT NOT NULL, " +
                    COL_NOTES      + " TEXT, " +
                    "FOREIGN KEY (" + COL_CYCLE_ID + ") REFERENCES " + TABLE_CYCLE        + "(" + COL_CYCLE_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + COL_USERNAME + ") REFERENCES " + TABLE_USER_ACCOUNT + "(" + COL_USERNAME + ") ON DELETE CASCADE" +
                    ");";

    
    // CREATE TABLE - SymptomType (lookup - rows seeded once at DB creation)
    

    public static final String CREATE_TABLE_SYMPTOM_TYPE =
            "CREATE TABLE " + TABLE_SYMPTOM_TYPE + " (" +
                    COL_SYMPTOM_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SYMPTOM_NAME + " TEXT NOT NULL UNIQUE" +
                    ");";


    // CREATE TABLE - LogSymptom (join table - links a log to its symptoms)
    // Deleting a DailyLog row automatically removes its LogSymptom rows via CASCADE.

    public static final String CREATE_TABLE_LOG_SYMPTOM =
            "CREATE TABLE " + TABLE_LOG_SYMPTOM + " (" +
                    COL_LOG_ID     + " INTEGER NOT NULL, " +
                    COL_SYMPTOM_ID + " INTEGER NOT NULL, " +
                    COL_NOTES      + " TEXT, " +
                    "PRIMARY KEY (" + COL_LOG_ID + ", " + COL_SYMPTOM_ID + "), " +
                    "FOREIGN KEY (" + COL_LOG_ID     + ") REFERENCES " + TABLE_DAILY_LOG    + "(" + COL_LOG_ID     + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + COL_SYMPTOM_ID + ") REFERENCES " + TABLE_SYMPTOM_TYPE + "(" + COL_SYMPTOM_ID + ")" +
                    ");";




    //NOTAFICATIONS
    public static final String TABLE_NOTIFICATION_SETTINGS = "NotificationSettings";
    public static final String COL_DAYS_BEFORE_REMINDER = "days_before_reminder";
    public static final String COL_IS_ENABLED = "is_enabled";
    public static final String COL_REMINDER_TIME = "reminder_time";

    public static final String CREATE_TABLE_NOTIFICATION_SETTINGS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_SETTINGS + " (" +
                    "username TEXT PRIMARY KEY," +
                    "days_before_reminder INTEGER," +
                    "is_enabled INTEGER," +
                    "reminder_time TEXT," +
                    "FOREIGN KEY (username) REFERENCES " + TABLE_USER_ACCOUNT + "(" + COL_USERNAME + ") ON DELETE CASCADE)";
}