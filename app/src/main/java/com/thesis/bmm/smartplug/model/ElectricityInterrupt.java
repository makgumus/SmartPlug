package com.thesis.bmm.smartplug.model;

import java.util.Date;

/**
 * Created by MUHAMMED on 5.03.2018.
 */

public class ElectricityInterrupt {
    private String province;
    private String district;
    private Date date;
    private String explain;
    private String regions;
    private String contentLink;

    public ElectricityInterrupt() {

    }

    public String getContentLink() {
        return contentLink;
    }

    public void setContentLink(String contentLink) {
        this.contentLink = contentLink;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }


}
