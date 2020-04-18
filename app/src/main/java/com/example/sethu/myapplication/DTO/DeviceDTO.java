package com.example.sethu.myapplication.DTO;

public class DeviceDTO {

    private String name;
    private String id;
    private WaterConfigDTO waterConfig;

    public DeviceDTO(String name, String id, WaterConfigDTO waterConfig) {
        this.name = name;
        this.id = id;
        this.waterConfig = waterConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WaterConfigDTO getWaterConfig() {
        return waterConfig;
    }

    public void setWaterConfig(WaterConfigDTO waterConfig) {
        this.waterConfig = waterConfig;
    }
}
