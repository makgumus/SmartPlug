package com.thesis.bmm.smartplug.model;

/**
 * Created by MUHAMMED on 13.03.2018.
 */

public class Locations {
    private String locationID;
    private String province;
    private String district;
    private String region;
    private Boolean notificationStatus;

    public Locations() {
    }

    public Locations(String locationID, String province, String district, String region, Boolean notificationStatus) {
        this.locationID = locationID;
        this.province = province;
        this.district = district;
        this.region = region;
        this.notificationStatus = notificationStatus;
    }

    public Boolean getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(Boolean notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
