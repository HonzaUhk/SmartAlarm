package cz.uhk.knejpja1.smartalarm.model;

import java.io.Serializable;

public class Alarm implements Serializable {
    private int hour;
    private int minute;
    private long id = -1;
    private boolean[] repeat = new boolean[7];
    private boolean active = true;

    public Alarm() {
    }

    public Alarm(int hour, int minute, boolean active) {
        this.hour = hour;
        this.minute = minute;
        this.active = active;
    }

    public void setRepeat(int dayIndex, boolean flag) {
        repeat[dayIndex] = flag;
    }

    public boolean getRepeat(int dayIndex) {
        return repeat[dayIndex];
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        String paddedMinute = Integer.toString(minute);
        if(paddedMinute.length() < 2) {
            paddedMinute = "0" + paddedMinute;
        }
        return hour + ":" + paddedMinute;
    }

    public String repeatString() {
        String repeatString = "";
        repeatString = addDay(repeatString, "Monday", repeat[0]);
        repeatString = addDay(repeatString, "Tuesday", repeat[1]);
        repeatString = addDay(repeatString, "Wednesday", repeat[2]);
        repeatString = addDay(repeatString, "Thursday", repeat[3]);
        repeatString = addDay(repeatString, "Friday", repeat[4]);
        repeatString = addDay(repeatString, "Saturday", repeat[5]);
        repeatString = addDay(repeatString, "Sunday", repeat[6]);

        if(repeatString.length() == 0)
            return "Once";
        else
            return repeatString;
    }

    private String addDay(String original, String dayName, boolean flag) {
        if(!flag)
            return original;
        else if(original.length() > 0)
            return original + ", " + dayName;
        else
            return dayName;

    }

    public boolean isOnce() {
        return !getRepeat(0)
                && !getRepeat(1)
                && !getRepeat(2)
                && !getRepeat(3)
                && !getRepeat(4)
                && !getRepeat(5)
                && !getRepeat(6);
    }
}
