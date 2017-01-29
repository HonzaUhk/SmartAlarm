package cz.uhk.knejpja1.smartalarm.db;

import android.provider.BaseColumns;

public class AlarmsContract {

    private AlarmsContract(){
    }

    public static class AlarmDef implements BaseColumns {
        public static final String TABLE_NAME = "Alarm";
        public static final String COLUMN_NAME_HOUR = "Hour";
        public static final String COLUMN_NAME_MINUTE = "Minute";
        public static final String COLUMN_NAME_MON = "Monday";
        public static final String COLUMN_NAME_TUE = "Tuesday";
        public static final String COLUMN_NAME_WED = "Wednesday";
        public static final String COLUMN_NAME_THU = "Thursday";
        public static final String COLUMN_NAME_FRI = "Friday";
        public static final String COLUMN_NAME_SAT = "Saturday";
        public static final String COLUMN_NAME_SUN = "Sunday";
        public static final String COLUMN_NAME_ACTIVE = "Active";
    }


}
