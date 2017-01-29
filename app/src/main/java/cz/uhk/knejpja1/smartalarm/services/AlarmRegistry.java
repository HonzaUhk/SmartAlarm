package cz.uhk.knejpja1.smartalarm.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.knejpja1.smartalarm.db.AlarmsContract;
import cz.uhk.knejpja1.smartalarm.db.AlarmsHelper;
import cz.uhk.knejpja1.smartalarm.model.Alarm;

import static cz.uhk.knejpja1.smartalarm.db.AlarmsContract.AlarmDef;

public class AlarmRegistry {
    private SQLiteDatabase db;
    private List<Alarm> alarms;

    public AlarmRegistry(Context context) {
        db = new AlarmsHelper(context).getWritableDatabase();
        alarms = readAlarmsFromDb();
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void saveAlarm(Alarm alarm) {
        if(alarm.getId() > -1)
            updateAlarm(alarm);
        else
            addNewAlarm(alarm);
    }

    private void addNewAlarm(Alarm alarm) {
        ContentValues vals = getContentValues(alarm);
        long id = db.insert(AlarmDef.TABLE_NAME, null, vals);
        alarm.setId(id);
        System.out.println("Added new alarm: " + alarm.getId());
        alarms.add(alarm);
    }

    @NonNull
    private ContentValues getContentValues(Alarm alarm) {
        ContentValues vals = new ContentValues();
        vals.put(AlarmDef.COLUMN_NAME_HOUR, alarm.getHour());
        vals.put(AlarmDef.COLUMN_NAME_MINUTE, alarm.getMinute());
        vals.put(AlarmDef.COLUMN_NAME_MON, alarm.getRepeat(0));
        vals.put(AlarmDef.COLUMN_NAME_TUE, alarm.getRepeat(1));
        vals.put(AlarmDef.COLUMN_NAME_WED, alarm.getRepeat(2));
        vals.put(AlarmDef.COLUMN_NAME_THU, alarm.getRepeat(3));
        vals.put(AlarmDef.COLUMN_NAME_FRI, alarm.getRepeat(4));
        vals.put(AlarmDef.COLUMN_NAME_SAT, alarm.getRepeat(5));
        vals.put(AlarmDef.COLUMN_NAME_SUN, alarm.getRepeat(6));
        vals.put(AlarmDef.COLUMN_NAME_ACTIVE, alarm.isActive());
        return vals;
    }

    private void updateAlarm(Alarm alarm) {
        System.out.println("Updating alarm: " + alarm.getId());
        ContentValues vals = getContentValues(alarm);
        String selection = AlarmDef._ID + "=?";
        String[] selectionArgs = { Long.toString(alarm.getId()) };

        Alarm oldAlarm = find(alarm.getId());
        alarms.set(alarms.indexOf(oldAlarm), alarm);

        db.update(
                AlarmDef.TABLE_NAME,
                vals,
                selection,
                selectionArgs);
    }

    public Alarm find(long id) {
        for(Alarm a : alarms) {
            if(a.getId() == id)
                return a;
        }

        throw new IllegalArgumentException("Cannot find alarm with id: " + id);
    }

    private List<Alarm> readAlarmsFromDb() {
        String[] projection = {
                AlarmDef.COLUMN_NAME_HOUR,
                AlarmDef.COLUMN_NAME_MINUTE,
                AlarmDef.COLUMN_NAME_ACTIVE,
                AlarmDef.COLUMN_NAME_MON,
                AlarmDef.COLUMN_NAME_TUE,
                AlarmDef.COLUMN_NAME_WED,
                AlarmDef.COLUMN_NAME_THU,
                AlarmDef.COLUMN_NAME_FRI,
                AlarmDef.COLUMN_NAME_SAT,
                AlarmDef.COLUMN_NAME_SUN,
                AlarmDef._ID,
        };

        Cursor cursor = db.query(
                AlarmDef.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Alarm> foundAlarms = new ArrayList<>();
        while(cursor.moveToNext()) {
            foundAlarms.add(parseAlarm(cursor));
        }
        cursor.close();

        return foundAlarms;
    }

    private Alarm parseAlarm(Cursor c) {
        Alarm alarm = new Alarm(
                c.getInt(0),
                c.getInt(1),
                c.getInt(2) == 1
        );

        alarm.setRepeat(0, c.getInt(3) == 1);
        alarm.setRepeat(1, c.getInt(4) == 1);
        alarm.setRepeat(2, c.getInt(5) == 1);
        alarm.setRepeat(3, c.getInt(6) == 1);
        alarm.setRepeat(4, c.getInt(7) == 1);
        alarm.setRepeat(5, c.getInt(8) == 1);
        alarm.setRepeat(6, c.getInt(9) == 1);

        alarm.setId(c.getLong(10));
        return alarm;
    }

    public void deleteAlarm(long id) {
        alarms.remove(find(id));

        String whereClause = "_id=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(AlarmDef.TABLE_NAME, whereClause, whereArgs);
    }
}
