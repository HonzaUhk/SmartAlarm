package cz.uhk.knejpja1.smartalarm.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import cz.uhk.knejpja1.smartalarm.R;
import cz.uhk.knejpja1.smartalarm.model.Alarm;
import cz.uhk.knejpja1.smartalarm.model.AlarmListAdapter;
import cz.uhk.knejpja1.smartalarm.services.AlarmRegistry;

public class MainActivity extends AppCompatActivity {

    public static boolean DEBUG_MODE = true;
    public static AlarmRegistry alarmRegistry;

    ListView alarmList;
    private AlarmListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.show();


        if(alarmRegistry == null)
            alarmRegistry = new AlarmRegistry(this);

        alarmList = (ListView) findViewById(R.id.alarmList);
        adapter = new AlarmListAdapter(this, alarmRegistry.getAlarms());
        alarmList.setAdapter(adapter);

        alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, SetTimeActivity.class);
                intent.putExtra("alarm", (Alarm) adapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        System.out.println("Data changed");
    }

    public void setAlarm(View v) {
        Intent intent = new Intent(this, SetTimeActivity.class);
        startActivity(intent);
    }
}
