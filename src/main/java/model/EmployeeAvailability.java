/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *Object đại diện cho dữ liệu rảnh (employee_availabilities)
 * @author phong
 */
public class EmployeeAvailability {
    private int id;
    private int userId;
    private String dayOfWeek;
    private int shiftId;

    public EmployeeAvailability() {}

    public EmployeeAvailability(int id, int userId, String dayOfWeek, int shiftId) {
        this.id = id;
        this.userId = userId;
        this.dayOfWeek = dayOfWeek;
        this.shiftId = shiftId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getShiftId() { return shiftId; }
    public void setShiftId(int shiftId) { this.shiftId = shiftId; }
}
