package com.gitgirls.cyclewise.persistence.real.periodlogrepository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gitgirls.cyclewise.exceptions.InvalidPeriodLogException;
import com.gitgirls.cyclewise.models.PeriodLog;
import com.gitgirls.cyclewise.persistence.PeriodLogPersistence;
import com.gitgirls.cyclewise.persistence.real.DatabaseHelper;
import com.gitgirls.cyclewise.persistence.PersistenceMessages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.*;

public class PeriodLogPersistenceReal implements PeriodLogPersistence {

    private final DatabaseHelper dbHelper;

    private final String ADD = "add";
    private final String UPDATE = "update";

    public PeriodLogPersistenceReal(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<PeriodLog> getAllPeriodLogs(String username) {
        checkNonNullUser(username);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PeriodLog> result = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(
                PeriodLogQueries.allPeriodLogsForUser(),
                new String[]{username})) {
            while (cursor.moveToNext()) {
                result.add(PeriodLogMapper.cursorToPeriodLog(cursor, db));
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public Optional<PeriodLog> getByID(int id, String username) {
        checkNonNullUser(username);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                PeriodLogQueries.periodLogById(),
                new String[]{String.valueOf(id), username})) {
            if (!cursor.moveToFirst()) return Optional.empty();
            return Optional.of(PeriodLogMapper.cursorToPeriodLog(cursor, db));
        }
    }

    @Override
    public boolean add(PeriodLog periodLog, String username) {
        checkNonNullUser(username);
        checkNonNullLog(periodLog, ADD);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int cycleId = CycleMapper.getOrCreateCycleId(
                db, username, periodLog.getStartDate(), periodLog.getEndDate());
        if (cycleId == -1) return false;
        long logId = db.insert(TABLE_DAILY_LOG, null,
                PeriodLogMapper.periodLogToInsertValues(periodLog, cycleId, username));
        if (logId == -1) return false;
        SymptomMapper.insertLogSymptoms(db, (int) logId, periodLog.getSymptoms());
        return true;
    }

    @Override
    public boolean update(PeriodLog periodLog, String username) {
        checkNonNullUser(username);
        checkNonNullLog(periodLog, UPDATE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.update(
                TABLE_DAILY_LOG,
                PeriodLogMapper.updateFlowAndNoteValues(periodLog),
                COL_LOG_ID + "=? AND " + COL_USERNAME + "=?",
                new String[]{String.valueOf(periodLog.getID()), username}
        );
        if (rows == 0) return false;
        SymptomMapper.updateLogSymptoms(db, periodLog.getID(), periodLog.getSymptoms());
        return true;
    }

    @Override
    public boolean delete(int id, String username) {
        checkNonNullUser(username);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                TABLE_DAILY_LOG,
                COL_LOG_ID + "=? AND " + COL_USERNAME + "=?",
                new String[]{String.valueOf(id), username}
        );
        return rows > 0;
    }

    private static void checkNonNullLog(PeriodLog periodLog, String operation) {
        if (periodLog == null) {
            throw new InvalidPeriodLogException(PersistenceMessages.ERROR_NULL_PERIOD_LOG_PREFIX + operation);
        }
    }

    private static void checkNonNullUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException(PersistenceMessages.ERROR_NULL_USERNAME);
        }
    }
}