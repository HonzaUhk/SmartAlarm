package cz.uhk.knejpja1.smartalarm.services;

import android.media.MediaRecorder;

import java.util.Timer;
import java.util.TimerTask;

import cz.uhk.knejpja1.smartalarm.util.Consumer;

public class NoiseMeter {
    private long interval;
    private int averageCount;

    private Consumer<? super Integer> callback;

    private MediaRecorder recorder;
    private Timer timer;
    private int[] results;
    private int index;

    private boolean available = true;

    public NoiseMeter(Consumer<? super Integer> callback) {
        this(300L, 3, callback);
    }

    public NoiseMeter(long interval, int averageCount, Consumer<? super Integer> callback) {
        this.interval = interval;
        this.averageCount = averageCount;
        this.callback = callback;

        results = new int[averageCount];
        index = 0;
        timer = new Timer();
    }

    public void start() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");
        try {
            recorder.prepare();
            recorder.start();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    callback.call(getAverageNoiseLevel());
                }
            }, 0L, interval);
        } catch (Exception e) {
            available = false;
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            timer.cancel();

            recorder.stop();
            recorder.release();
            recorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getAverageNoiseLevel() {
        results[index] = recorder.getMaxAmplitude();
        index = ++index % results.length;

        return calculateAverageNoise();
    }

    private int calculateAverageNoise() {
        int sum = 0;
        for (int result : results) {
            sum += result;
        }

        return sum / results.length;
    }

    public boolean isAvailable() {
        return available;
    }
}
