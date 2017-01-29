package cz.uhk.knejpja1.smartalarm.activities;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cz.uhk.knejpja1.smartalarm.R;
import cz.uhk.knejpja1.smartalarm.model.Alarm;
import cz.uhk.knejpja1.smartalarm.services.ActivityMeter;
import cz.uhk.knejpja1.smartalarm.services.AlarmStrategy;
import cz.uhk.knejpja1.smartalarm.services.LightMeter;
import cz.uhk.knejpja1.smartalarm.services.NoiseMeter;
import cz.uhk.knejpja1.smartalarm.services.StepCounter;
import cz.uhk.knejpja1.smartalarm.util.Consumer;

public class AlarmRingActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private NoiseMeter noiseMeter;
    private ActivityMeter activityMeter;
    private LightMeter lightMeter;
    private StepCounter stepCounter;
    private AlarmStrategy strategy;

    private TextView debugNoiseLevel;
    private TextView debugActivity;
    private TextView debugLight;
    private TextView micAvailiable;

    long alarmStart = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        prepareAlarm();

        debugNoiseLevel = new TextView(this);
        debugActivity = new TextView(this);
        debugLight = new TextView(this);
        strategy = new AlarmStrategy();

        micAvailiable = (TextView) findViewById(R.id.micUnavailable);

        if (MainActivity.DEBUG_MODE)
            addDebugElements();

        setupAlarmSound();
        startAlarmSound();
        startMeasuringNoise();
        startCountingSteps();
        if(!stepCounter.isAvailable()) {
            System.out.println("----Measuring activity-----");
            startMeasuringActivity();
        } else {
            System.out.println("-----Counting steps------");
        }
        startMeasuringLight();

        turnScreenOn();
        alarmStart = System.currentTimeMillis();
    }

    private void startCountingSteps() {
        stepCounter = new StepCounter(this, new Consumer<Float>() {
            @Override
            public void call(final Float param) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        strategy.setSteps(param);
                        if(strategy.isFullyAwake())
                            stop();
                    }
                });
            }
        });
    }

    private void startMeasuringLight() {
        lightMeter = new LightMeter(this, new Consumer<Float>() {
            @Override
            public void call(final Float param) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        strategy.setLight(param);
                        debugLight.setText("Light: " + param);
                    }
                });
            }
        });
    }

    private void startMeasuringActivity() {
        activityMeter = new ActivityMeter(this, new Consumer<Long>() {
            @Override
            public void call(Long param) {
                strategy.addActiveTime(param);
                debugActivity.setText("Activity: " + strategy.getActivityTime());
                if(strategy.isFullyAwake()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stop();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int noiseLimit = sharedPref.getInt("noiseLevel", 300);
        System.out.println("Noise limit: " + noiseLimit);
        int activityTime = sharedPref.getInt("activityTime", 15);

        strategy.setMakeAwakeNoiseLevel(400 + noiseLimit);
        strategy.setKeepAwakeNoiseLevel(noiseLimit);
        strategy.setActivityTimeLimit(activityTime * 1000);

    }

    private void prepareAlarm() {
        int id = getIntent().getIntExtra("alarmId", -1);
        Alarm alarm = MainActivity.alarmRegistry.find(id);
        if(alarm.isOnce()) {
            alarm.setActive(false);
        }
        MainActivity.alarmRegistry.saveAlarm(alarm);
    }

    private void startMeasuringNoise() {
        noiseMeter = new NoiseMeter(new Consumer<Integer>() {
            @Override
            public void call(final Integer param) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processNoise(param);
                    }
                });
            }
        });
        noiseMeter.start();
        if(!noiseMeter.isAvailable()) {
            micAvailiable.setVisibility(View.VISIBLE);
        }
    }

    private void addDebugElements() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_alarm_ring);

        relativeLayout.addView(debugNoiseLevel);

        RelativeLayout.LayoutParams distanceLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        distanceLayoutParams.setMargins(0, 20, 0, 0);
        debugActivity.setLayoutParams(distanceLayoutParams);
        relativeLayout.addView(debugActivity);

        RelativeLayout.LayoutParams lightLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lightLayoutParams.setMargins(0, 40, 0, 0);
        debugLight.setLayoutParams(lightLayoutParams);
        relativeLayout.addView(debugLight);
    }

    private void setupAlarmSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.setLooping(true);
        mp.setVolume(1.0f, 1.0f);
    }

    private void processNoise(int noiseLevel) {
        debugNoiseLevel.setText("Noise: " + noiseLevel);
        strategy.updateNoise(noiseLevel);

        if(strategy.isAwake() && soundIsPlaying())
            pauseAlarmSound();
        else if(!strategy.isAwake() && !soundIsPlaying())
            startAlarmSound();

    }

    private void startAlarmSound() {
        mp.start();
    }

    private boolean soundIsPlaying() {
        return mp != null && mp.isPlaying();
    }

    private void pauseAlarmSound() {
        mp.pause();
    }

    public void stop(View v) {
        stop();
    }

    private void stop() {
        if(MainActivity.DEBUG_MODE)
            Toast.makeText(this, "Alarm took " + (System.currentTimeMillis() - alarmStart) + "ms", Toast.LENGTH_LONG).show();

        if(mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }

        noiseMeter.stop();
        if(activityMeter != null)
            activityMeter.stop();
        stepCounter.stop();
        lightMeter.stop();
        onBackPressed();
    }

    private void turnScreenOn() {
        int flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().addFlags(flags);
    }
}
