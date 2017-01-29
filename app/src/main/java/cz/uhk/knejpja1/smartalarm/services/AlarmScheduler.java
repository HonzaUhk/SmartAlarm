package cz.uhk.knejpja1.smartalarm.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import cz.uhk.knejpja1.smartalarm.activities.AlarmRingActivity;
import cz.uhk.knejpja1.smartalarm.activities.MainActivity;
import cz.uhk.knejpja1.smartalarm.model.Alarm;

public class AlarmScheduler {

    private static long WEEK_IN_MILIS = 7L * 24L * 60L * 60L * 1000L;

    private AlarmManager alarmMng;
    private Context context;

    public AlarmScheduler(Context c) {
        context = c;
        alarmMng = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(Alarm alarm) {
        int hour = alarm.getHour();
        int minute = alarm.getMinute();

        boolean once = true;
        for(int i = 0; i < 7; ++i) {
            boolean repeat = alarm.getRepeat(i);
            if(repeat) {
                once = false;
                Calendar calendar = getNextAlarmTime(hour, minute, i);
                scheduleAlarmTime((int) alarm.getId(), calendar.getTimeInMillis(), true, i);
            }
        }
        if(once) {
            Calendar calendar = getNextAlarmTime(hour, minute);

            System.out.println("++++++" + new Date(calendar.getTimeInMillis()) + "++++++++");
            scheduleAlarmTime((int) alarm.getId(), calendar.getTimeInMillis(), false, 0);
        }
    }

    public void cancel(Alarm alarm) {
        boolean once = true;
        for(int i = 0; i < 7; ++i) {
            boolean repeat = alarm.getRepeat(i);
            if(repeat) {
                once = false;
                alarmMng.cancel(getPendingIntent((int)alarm.getId(), i));
            }
        }
        if(once) {
            alarmMng.cancel(getPendingIntent((int)alarm.getId(), 0));
        }
    }

    private void scheduleAlarmTime(int id, long timeInMilis, boolean repeating, int day) {
        PendingIntent pendingIntent = getPendingIntent(id, day);
        if(repeating)
            alarmMng.setRepeating(AlarmManager.RTC_WAKEUP, timeInMilis, WEEK_IN_MILIS, pendingIntent);
        else
            alarmMng.set(AlarmManager.RTC_WAKEUP, timeInMilis, pendingIntent);
    }

    private PendingIntent getPendingIntent(int id, int day) {
        Intent intent = new Intent(context, AlarmRingActivity.class);
        intent.putExtra("alarmId", id);
        int requestCode = id * 10 + day;
        return PendingIntent.getActivity(context, requestCode, intent, 0);
    }

    @NonNull
    private Calendar getNextAlarmTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if(MainActivity.DEBUG_MODE)
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 1);

        if(calendar.getTimeInMillis() < System.currentTimeMillis())
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar;
    }

    @NonNull
    private Calendar getNextAlarmTime(int hour, int minute, int standardDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        int dayOfWeek = getStandardDayOfWeek(calendar);
        calendar.add(Calendar.DAY_OF_YEAR, (7 + standardDayOfWeek - dayOfWeek) % 7);
        if(calendar.getTimeInMillis() < System.currentTimeMillis())
            calendar.add(Calendar.WEEK_OF_MONTH, 1);

        System.out.println("++++++" + new Date(calendar.getTimeInMillis()) + "++++++++");
        return calendar;
    }

    private int getStandardDayOfWeek(Calendar calendar) {
        return (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
    }
}
