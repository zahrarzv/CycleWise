package com.gitgirls.cyclewise.persistence.real.periodlogrepository;

import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.COL_CYCLE_ID;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.COL_END_DATE;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.COL_START_DATE;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.COL_USERNAME;
import static com.gitgirls.cyclewise.persistence.real.CycleWiseSchema.TABLE_CYCLE;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gitgirls.cyclewise.models.CycleDate;

public final class CycleMapper {
    private CycleMapper() {}
    public static int getOrCreateCycleId(SQLiteDatabase db, String username,
                                         CycleDate start, CycleDate end) {
        int id = -1;

        try (Cursor cursor = db.rawQuery(
                PeriodLogQueries.findCycleByStartDate(),
                new String[]{username, start.getDate().toString()})) {

            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CYCLE_ID));
            }
        }

        if (id == -1) {
            ContentValues values = new ContentValues();
            values.put(COL_USERNAME, username);
            values.put(COL_START_DATE, start.getDate().toString());

            if (end != null) {
                values.put(COL_END_DATE, end.getDate().toString());
            }

            id = (int) db.insert(TABLE_CYCLE, null, values);
        }

        return id;
    }
}
