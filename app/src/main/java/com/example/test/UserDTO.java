package com.example.test;

public class UserDTO {
    private String fcnToken;
    private int locationX;
    private int locationY;

    public UserDTO(String fcnToken, int locationX, int locationY) {
        this.fcnToken = fcnToken;
        this.locationX = locationX;
        this.locationY = locationY;
    }
    public UserDTO()
    {

    }
    public String getFcnToken() {
        return fcnToken;
    }

    public void setFcnToken(String fcnToken) {
        this.fcnToken = fcnToken;
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }
}