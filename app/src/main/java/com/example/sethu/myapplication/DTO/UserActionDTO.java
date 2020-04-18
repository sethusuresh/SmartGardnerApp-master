package com.example.sethu.myapplication.DTO;

public class UserActionDTO {

    private UserActivityDTO userActivity;
    private DeviceDTO device;

    public UserActionDTO(UserActivityDTO userActivity, DeviceDTO device) {
        this.userActivity = userActivity;
        this.device = device;
    }

    public UserActivityDTO getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(UserActivityDTO userActivity) {
        this.userActivity = userActivity;
    }

    public DeviceDTO getDevice() {
        return device;
    }

    public void setDevice(DeviceDTO device) {
        this.device = device;
    }
}
