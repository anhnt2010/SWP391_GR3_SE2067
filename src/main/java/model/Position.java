package model;

import java.math.BigDecimal;

/** Danh mục Chức vụ kèm lương cơ bản. */
public class Position {
    private int positionId;
    private String positionCode;
    private String positionName;
    private BigDecimal baseSalary;
    private boolean status;

    public Position() {}

    public Position(int positionId, String positionCode, String positionName,
                    BigDecimal baseSalary, boolean status) {
        this.positionId = positionId;
        this.positionCode = positionCode;
        this.positionName = positionName;
        this.baseSalary = baseSalary;
        this.status = status;
    }

    public int getPositionId() { return positionId; }
    public void setPositionId(int positionId) { this.positionId = positionId; }

    // Alias cho id (để tương thích nếu các Controller khác gọi getId)
    public int getId() { return positionId; }
    public void setId(int id) { this.positionId = id; }

    public String getPositionCode() { return positionCode; }
    public void setPositionCode(String positionCode) { this.positionCode = positionCode; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    // Alias cho name
    public String getName() { return positionName; }
    public void setName(String name) { this.positionName = name; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    /** Định dạng tiền tệ để hiển thị ra JSP, ví dụ: 6.000.000 đ */
    public String getBaseSalaryDisplay() {
        if (baseSalary == null) return "0 đ";
        return String.format("%,.0f đ", baseSalary).replace(',', '.');
    }
}