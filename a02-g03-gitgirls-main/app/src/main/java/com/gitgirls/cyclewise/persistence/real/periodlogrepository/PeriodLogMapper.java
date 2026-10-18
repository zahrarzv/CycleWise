//------------------------------------------------------
// PeriodLogMapper
//
// PURPOSE:    Maps PeriodLog objects to and from database
//             representations. Converts Cursor data into
//             PeriodLog models and prepares ContentValues
//             for inserting or updating logs, cycles,
//             and symptoms in the SQLite database.
//
// NOTES:      Also handles retrieving, inserting, and
//             updating symptoms associated with a
//             specific period log.
//------------------------------------------------------

package com.gitgirls.cyclewise.persistence.real.periodlogrepository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gitgirls.cyclewise.models.*;

import java.time.LocalDate;

import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.*;

public final class PeriodLogMapper {

    private PeriodLogMapper() {}


    public static PeriodLog cursorToPeriodLog(Cursor cursor, SQLiteDatabase db) {
        int periodLogID = cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_ID));
        String flowStr  = cursor.getString(cursor.getColumnIndexOrThrow(COL_FLOW_LEVEL));
        String notes    = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES));
        String startStr = cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE));
        String endStr   = cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE));

        CycleDate startDate = new CycleDate(LocalDate.parse(startStr));
        CycleDate endDate   = endStr != null ? new CycleDate(LocalDate.parse(endStr)) : null;
        Flow flow           = Flow.valueOf(flowStr.toUpperCase());
        Symptoms symptoms   = SymptomMapper.getSymptomsForLog(periodLogID, db);

        return new PeriodLog(periodLogID, startDate, endDate, flow, symptoms, notes);
    }


    public static ContentValues periodLogToInsertValues(PeriodLog periodLog, int cycleId, String username) {
        ContentValues values = updateFlowAndNoteValues(periodLog);
        values.put(COL_CYCLE_ID, cycleId);
        values.put(COL_USERNAME, username);
        values.put(COL_DATE, LocalDate.now().toString());

        return values;
    }

    public static ContentValues updateFlowAndNoteValues(PeriodLog periodLog) {
        ContentValues values = new ContentValues();

        Flow flow = periodLog.getFlow();
        if (flow != null) {
            values.put(COL_FLOW_LEVEL, flow.name());
        }

        values.put(COL_NOTES, periodLog.getNotes());

        return values;
    }
}