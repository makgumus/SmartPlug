package com.thesis.bmm.smartplug.model;

public class Interrupts {
    private String explain, startingTime, endingTime;

    public Interrupts() {
    }

    public Interrupts(String explain, String startingTime, String endingTime) {
        this.explain = explain;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }
}
