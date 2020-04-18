package com.example.sethu.myapplication.DTO;

public class WaterConfigDTO {

    private String dayOfWeek;
    private String morningTime;
    private String eveningTime;
    private boolean waterNow;

    public WaterConfigDTO(String dayOfWeek, String morningTime, String eveningTime, boolean waterNow) {
        this.dayOfWeek = dayOfWeek;
        this.morningTime = morningTime;
        this.eveningTime = eveningTime;
        this.waterNow = waterNow;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getMorningTime() {
        return morningTime;
    }

    public void setMorningTime(String morningTime) {
        this.morningTime = morningTime;
    }

    public String getEveningTime() {
        return eveningTime;
    }

    public void setEveningTime(String eveningTime) {
        this.eveningTime = eveningTime;
    }

    public boolean isWaterNow() {
        return waterNow;
    }

    public void setWaterNow(boolean waterNow) {
        this.waterNow = waterNow;
    }
}
