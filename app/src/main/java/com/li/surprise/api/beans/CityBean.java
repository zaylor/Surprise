package com.li.surprise.api.beans;

/**
 * Created by zaylor on 16/6/2.
 */
public class CityBean {
    private int Id;
    private int stateProvincedId;
    private String name;
    private String abbreviation;
    private double longitude;    //经度
    private double latitude;    //纬度
    private int mapLevel;
    private int displayOrder;

    public CityBean(int id, String name) {
        Id = id;
        this.name = name;
    }

    public CityBean(int id, String name, double longitude, double latitude, int mapLevel) {
        Id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.mapLevel = mapLevel;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getStateProvincedId() {
        return stateProvincedId;
    }

    public void setStateProvincedId(int stateProvincedId) {
        this.stateProvincedId = stateProvincedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMapLevel() {
        return mapLevel;
    }

    public void setMapLevel(int mapLevel) {
        this.mapLevel = mapLevel;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
