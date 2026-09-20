package model;
/**
 * Object ánh xạ bảng shift_schedules
 */
import java.sql.Date;

public class ShiftSchedule {
    private int id;
    private int branchId;
    private int userId;
    private int shiftId;
    private Date workDate;
    private int positionId;
    private String status;

    public ShiftSchedule() {}

    public ShiftSchedule(int id, int branchId, int userId, int shiftId, Date workDate, int positionId, String status) {
        this.id = id;
        this.branchId = branchId;
        this.userId = userId;
        this.shiftId = shiftId;
        this.workDate = workDate;
        this.positionId = positionId;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getShiftId() { return shiftId; }
    public void setShiftId(int shiftId) { this.shiftId = shiftId; }

    public Date getWorkDate() { return workDate; }
    public void setWorkDate(Date workDate) { this.workDate = workDate; }

    public int getPositionId() { return positionId; }
    public void setPositionId(int positionId) { this.positionId = positionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}