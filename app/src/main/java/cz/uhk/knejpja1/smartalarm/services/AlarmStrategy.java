package cz.uhk.knejpja1.smartalarm.services;

public class AlarmStrategy {
    private int makeAwakeNoiseLevel = 700;
    private int keepAwakeNoiseLevel = 300;
    private long silenceTimeLimit = 5000;

    private long activityTime = 0;
    private long activityTimeLimit = 15_000;

    private long lastActivity = -1;
    private boolean awake = false;
    private float baseLight = -1;
    private boolean lightsOn = false;
    private float startSteps = -1;
    private float currentSteps = -1;

    public void updateNoise(int noiseLevel) {
        if(!awake && noiseLevel > makeAwakeNoiseLevel)
            wakeUp();
        if(awake && noiseLevel > keepAwakeNoiseLevel)
            keepAwake();
        if(awake && noiseLevel < keepAwakeNoiseLevel && (System.currentTimeMillis() - lastActivity) > currentSilenceLimit())
            fallAsleep();
    }

    private long currentSilenceLimit() {
        return lightsOn ? silenceTimeLimit * 4 : silenceTimeLimit;
    }

    public boolean isAwake() {
        return this.awake;
    }

    private void wakeUp() {
        awake = true;
        lastActivity = System.currentTimeMillis();
    }

    private void keepAwake() {
        lastActivity = System.currentTimeMillis();
    }

    private void fallAsleep() {
        awake = false;
        lastActivity = -1;
    }

    public void setMakeAwakeNoiseLevel(int makeAwakeNoiseLevel) {
        this.makeAwakeNoiseLevel = makeAwakeNoiseLevel;
    }

    public void setKeepAwakeNoiseLevel(int keepAwakeNoiseLevel) {
        this.keepAwakeNoiseLevel = keepAwakeNoiseLevel;
    }

    public void addActiveTime(long time) {
        activityTime += time;
        lastActivity = System.currentTimeMillis();
    }

    public boolean isFullyAwake() {
        return activityTime > activityTimeLimit || currentSteps - startSteps > 15;
    }

    public long getActivityTime() {
        return activityTime;
    }

    public void setLight(float light) {
        if(baseLight < 0) {
            baseLight = light;
        } else {
            lightsOn = (light - baseLight) > 100;
        }
    }

    public void setSteps(float steps) {
        if(startSteps < 0)
            startSteps = steps;

        currentSteps = steps;
    }

    public void setActivityTimeLimit(long activityTimeLimit) {
        this.activityTimeLimit = activityTimeLimit;
    }
}
