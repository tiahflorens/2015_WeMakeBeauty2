package com.jnu.dns.tiah.wemakebeauty.items;

import java.util.ArrayList;

/**
 * Created by Peter on 2015-03-31.
 */
public class EventItem {

    private String brand , memo,due;
    private long due2;
    private double lat,lng;

    public EventItem(String brand, String memo, long due) {
        this.brand = brand;
        this.memo = memo;
        this.due2 = due;
    }

    public EventItem(String brand, String memo, String due, String lat, String lng) {
        this.brand = brand;
        this.memo = memo;
        this.due = due;
        this.lat = Double.parseDouble(lat);
        this.lng = Double.parseDouble(lng);
    }


    public long getDueLong(){
        return due2;
    }
    public String getDue() {
        return due;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getBrand() {
        return brand;
    }

    public String getMemo() {
        return memo;
    }


}
