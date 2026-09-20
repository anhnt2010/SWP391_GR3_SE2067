package model;

import java.sql.Timestamp;

public class OpenShiftApplication {
    private int id;
    private int openShiftId;
    private int userId;
    private Timestamp appliedAt;
    private String status;
    
    // Các trường bổ sung để hiển thị thông tin lên giao diện
    private String applicantName;
    private String workDate;
    private String shiftName;

    public OpenShiftApplication() {}

    public OpenShiftApplication(int id, int openShiftId, int userId, Timestamp appliedAt, String status, String applicantName, String workDate, String shiftName) {
        this.id = id;
        this.openShiftId = openShiftId;
        this.userId = userId;
        this.appliedAt = appliedAt;
        this.status = status;
        this.applicantName = applicantName;
        this.workDate = workDate;
        this.shiftName = shiftName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOpenShiftId() { return openShiftId; }
    public void setOpenShiftId(int openShiftId) { this.openShiftId = openShiftId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Timestamp getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Timestamp appliedAt) { this.appliedAt = appliedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }
    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }
}