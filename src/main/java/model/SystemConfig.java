package model;

import java.sql.Timestamp;

/** Tham số cấu hình hệ thống (key - value). */
public class SystemConfig {
    private String configKey;
    private String configValue;
    private String description;
    private Timestamp updatedAt;
    private String updatedBy;

    public SystemConfig() {}

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
