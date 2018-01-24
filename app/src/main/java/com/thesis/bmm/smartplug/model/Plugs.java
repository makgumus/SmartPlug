package com.thesis.bmm.smartplug.model;

/**
 * Created by MUHAMMED on 22.01.2018.
 */

public class Plugs {
    private String plugID;
    private String plugName;
    private String plugRoom;
    private String plugCurrent;
    private String plugStatus;

    public Plugs() {

    }

    public Plugs(String plugID, String plugName, String plugRoom, String plugCurrent, String plugStatus) {

        this.plugID = plugID;
        this.plugName = plugName;
        this.plugRoom = plugRoom;
        this.plugCurrent = plugCurrent;
        this.plugStatus = plugStatus;
    }

    public String getPlugID() {
        return plugID;
    }

    public void setPlugID(String plugID) {
        this.plugID = plugID;
    }

    public String getPlugName() {
        return plugName;
    }

    public void setPlugName(String plugName) {
        this.plugName = plugName;
    }

    public String getPlugRoom() {
        return plugRoom;
    }

    public void setPlugRoom(String plugRoom) {
        this.plugRoom = plugRoom;
    }

    public String getPlugCurrent() {
        return plugCurrent;
    }

    public void setPlugCurrent(String plugCurrent) {
        this.plugCurrent = plugCurrent;
    }

    public String getPlugStatus() {
        return plugStatus;
    }

    public void setPlugStatus(String plugStatus) {
        this.plugStatus = plugStatus;
    }
}
