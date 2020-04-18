package com.example.sethu.myapplication.DTO;

public class UserActivityDTO {

    private String userName;
    private String userId;
    private String targetDevice;
    private String activity;
    private String time;

    public UserActivityDTO(String userName, String userId, String targetDevice, String activity, String time) {
        this.userName = userName;
        this.userId = userId;
        this.targetDevice = targetDevice;
        this.activity = activity;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice) {
        this.targetDevice = targetDevice;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
