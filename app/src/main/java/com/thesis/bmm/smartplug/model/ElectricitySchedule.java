package com.thesis.bmm.smartplug.model;

public class ElectricitySchedule {
    private String T1, T2, T3;

    public ElectricitySchedule() {
    }

    public ElectricitySchedule(String t1, String t2, String t3) {
        T1 = t1;
        T2 = t2;
        T3 = t3;
    }

    public String getT1() {
        return T1;
    }

    public void setT1(String t1) {
        T1 = t1;
    }

    public String getT2() {
        return T2;
    }

    public void setT2(String t2) {
        T2 = t2;
    }

    public String getT3() {
        return T3;
    }

    public void setT3(String t3) {
        T3 = t3;
    }
}
