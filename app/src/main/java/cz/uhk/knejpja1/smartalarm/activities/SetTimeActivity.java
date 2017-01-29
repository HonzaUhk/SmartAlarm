package cz.uhk.knejpja1.smartalarm.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import cz.uhk.knejpja1.smartalarm.R;
import cz.uhk.knejpja1.smartalarm.model.Alarm;
import cz.uhk.knejpja1.smartalarm.services.AlarmScheduler;

public class SetTimeActivity extends AppCompatActivity {


    private TimePicker timePicker;
    private Button deleteBtn;
    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thursday;
    private CheckBox friday;
    private CheckBox saturday;
    private CheckBox sunday;

    private AlarmScheduler alarmScheduler;
    private Alarm alarm;
    private boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        alarm = (Alarm) getIntent().getSerializableExtra("alarm");
        if(alarm == null) {
            alarm = new Alarm();
            isNew = true;
        }

        alarmScheduler = new AlarmScheduler(this);

        timePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        deleteBtn = (Button) findViewById(R.id.alarmDelete);
        timePicker.setIs24HourView(true);
        if(!isNew){
            timePicker.setCurrentHour(alarm.getHour());
            timePicker.setCurrentMinute(alarm.getMinute());
            deleteBtn.setVisibility(View.VISIBLE);
        }

        monday = (CheckBox) findViewById(R.id.mon);
        tuesday = (CheckBox) findViewById(R.id.tue);
        wednesday = (CheckBox) findViewById(R.id.wed);
        thursday = (CheckBox) findViewById(R.id.thu);
        friday = (CheckBox) findViewById(R.id.fri);
        saturday = (CheckBox) findViewById(R.id.sat);
        sunday = (CheckBox) findViewById(R.id.sun);

        monday.setChecked(alarm.getRepeat(0));
        tuesday.setChecked(alarm.getRepeat(1));
        wednesday.setChecked(alarm.getRepeat(2));
        thursday.setChecked(alarm.getRepeat(3));
        friday.setChecked(alarm.getRepeat(4));
        saturday.setChecked(alarm.getRepeat(5));
        sunday.setChecked(alarm.getRepeat(6));
    }

    public void set(View v) {
        setAlarm();
        onBackPressed();
    }

    private void setAlarm() {
        int h = timePicker.getCurrentHour();
        int m = timePicker.getCurrentMinute();

        alarm.setHour(h);
        alarm.setMinute(m);
        alarm.setRepeat(0, monday.isChecked());
        alarm.setRepeat(1, tuesday.isChecked());
        alarm.setRepeat(2, wednesday.isChecked());
        alarm.setRepeat(3, thursday.isChecked());
        alarm.setRepeat(4, friday.isChecked());
        alarm.setRepeat(5, saturday.isChecked());
        alarm.setRepeat(6, sunday.isChecked());
        alarm.setActive(true);
        MainActivity.alarmRegistry.saveAlarm(alarm);

        alarmScheduler.cancel(alarm);
        alarmScheduler.schedule(alarm);
    }



    public void delete(View v) {
        MainActivity.alarmRegistry.deleteAlarm(alarm.getId());
        alarmScheduler.cancel(alarm);
        onBackPressed();
    }
}
