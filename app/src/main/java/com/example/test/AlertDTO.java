package com.example.test;

public class AlertDTO {
    private String type;
    private String time;
    private String locationX;
    private String locationY;

    public AlertDTO(String type, String time, String locationX, String locationY) {
        this.type = type;
        this.time = time;
        this.locationX = locationX;
        this.locationY = locationY;
    }
    public AlertDTO() {

    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

}
