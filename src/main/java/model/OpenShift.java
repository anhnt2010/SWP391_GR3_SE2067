package model;

import java.sql.Date;

public class OpenShift {
    private int id;
    private int branchId;
    private int shiftId;
    private Date workDate;
    private int quantityNeeded;
    private String status;

    public OpenShift() {}

    public OpenShift(int id, int branchId, int shiftId, Date workDate, int quantityNeeded, String status) {
        this.id = id;
        this.branchId = branchId;
        this.shiftId = shiftId;
        this.workDate = workDate;
        this.quantityNeeded = quantityNeeded;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }
    public int getShiftId() { return shiftId; }
    public void setShiftId(int shiftId) { this.shiftId = shiftId; }
    public Date getWorkDate() { return workDate; }
    public void setWorkDate(Date workDate) { this.workDate = workDate; }
    public int getQuantityNeeded() { return quantityNeeded; }
    public void setQuantityNeeded(int quantityNeeded) { this.quantityNeeded = quantityNeeded; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}