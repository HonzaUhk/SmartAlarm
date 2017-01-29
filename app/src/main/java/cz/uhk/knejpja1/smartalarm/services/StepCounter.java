package cz.uhk.knejpja1.smartalarm.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import cz.uhk.knejpja1.smartalarm.util.Consumer;

public class StepCounter implements SensorEventListener {

    private Consumer<? super Float> callback;
    private final SensorManager sensorManager;
    private final Sensor steps;

    public StepCounter(Context c, Consumer<? super Float> callback) {
        sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        steps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, steps, SensorManager.SENSOR_DELAY_NORMAL);

        this.callback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        callback.call(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stop() {
        sensorManager.unregisterListener(this, steps);
    }

    public boolean isAvailable() {
        return steps != null;
    }
}
