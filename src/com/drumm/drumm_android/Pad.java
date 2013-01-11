package com.drumm.drumm_android;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class Pad {
    private int id;
    private static final int HISTORY_SIZE = 2;
    private static final int TAP_TIMEOUT = 100;
    private Date lastTapped;
    private boolean isActive;
    private Queue<Boolean> padHistory;

    public Pad(int id) {
        this.id = id;
        padHistory = new LinkedList<Boolean>();
        setValue(false);
        lastTapped = new Date();
    }

    public void setValue(boolean value) {
        isActive = value;
        padHistory.add(value);

        if (padHistory.size() > HISTORY_SIZE) {
            padHistory.remove();
        }
    }

    public boolean justTapped() {
        Date now = new Date();
        long timeSinceTap = now.getTime() - lastTapped.getTime();
        if (isActive && timeSinceTap > TAP_TIMEOUT) {
            lastTapped = new Date();
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

}
