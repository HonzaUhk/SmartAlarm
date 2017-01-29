package cz.uhk.knejpja1.smartalarm.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import cz.uhk.knejpja1.smartalarm.util.Consumer;
import cz.uhk.knejpja1.smartalarm.util.Vector;

public class ActivityMeter implements SensorEventListener {

    private static double activityThreshold = 1.0f;

    private Consumer<? super Long> callback;
    private final SensorManager sensorManager;
    private final Sensor linearAcceleration;
    private long lastMeasurement = -1;

    public ActivityMeter(Context c, Consumer<? super Long> callback) {
        sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);

        this.callback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Vector acceleration = new Vector(event.values[0], event.values[1], event.values[2]);
        double length = acceleration.length();

        long now = System.currentTimeMillis();

        if(length > activityThreshold) {
            if(lastMeasurement > -1) {
                callback.call(now - lastMeasurement);
            }
        }

        lastMeasurement = now;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stop() {
        sensorManager.unregisterListener(this, linearAcceleration);
        lastMeasurement = -1;
    }
}
