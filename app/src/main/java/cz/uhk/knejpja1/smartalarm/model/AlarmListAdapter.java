package cz.uhk.knejpja1.smartalarm.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import cz.uhk.knejpja1.smartalarm.R;
import cz.uhk.knejpja1.smartalarm.activities.MainActivity;
import cz.uhk.knejpja1.smartalarm.services.AlarmScheduler;

public class AlarmListAdapter extends BaseAdapter {

    private List<Alarm> alarms;
    private Context context;
    private AlarmScheduler alarmScheduler;

    public AlarmListAdapter(Context context, List<Alarm> alarms) {
        this.context = context;
        this.alarms = alarms;
        alarmScheduler = new AlarmScheduler(context);
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return alarms.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.alarm_list_item, parent, false);
        }

        final Alarm alarm = alarms.get(position);
        TextView time = (TextView) convertView.findViewById(R.id.alarmListTime);
        time.setText(alarm.toString());
        TextView repeat = (TextView) convertView.findViewById(R.id.alarmRepeat);
        repeat.setText(alarm.repeatString());
        CheckBox active = (CheckBox) convertView.findViewById(R.id.alarmActive);
        active.setChecked(alarm.isActive());
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(alarms.contains(alarm)) {
                    alarm.setActive(isChecked);
                    if(isChecked) {
                        alarmScheduler.schedule(alarm);
                    } else {
                        alarmScheduler.cancel(alarm);
                    }
                    MainActivity.alarmRegistry.saveAlarm(alarm);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
