package cz.uhk.knejpja1.smartalarm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static cz.uhk.knejpja1.smartalarm.db.AlarmsContract.AlarmDef;

public class AlarmsHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "Alarms.db";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + AlarmDef.TABLE_NAME + " (" +
                    AlarmDef._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AlarmDef.COLUMN_NAME_HOUR + " INTEGER," +
                    AlarmDef.COLUMN_NAME_MINUTE + " INTEGER," +
                    AlarmDef.COLUMN_NAME_MON + " INTEGER," +
                    AlarmDef.COLUMN_NAME_TUE + " INTEGER," +
                    AlarmDef.COLUMN_NAME_WED + " INTEGER," +
                    AlarmDef.COLUMN_NAME_THU + " INTEGER," +
                    AlarmDef.COLUMN_NAME_FRI + " INTEGER," +
                    AlarmDef.COLUMN_NAME_SAT + " INTEGER," +
                    AlarmDef.COLUMN_NAME_SUN + " INTEGER," +
                    AlarmDef.COLUMN_NAME_ACTIVE + " INTEGER)";
    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + AlarmDef.TABLE_NAME;


    public AlarmsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}
