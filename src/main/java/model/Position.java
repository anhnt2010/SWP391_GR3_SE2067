package model;

<<<<<<< HEAD
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

    public String getPositionCode() { return positionCode; }
    public void setPositionCode(String positionCode) { this.positionCode = positionCode; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

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
=======
public class Position {
    private int id;
    private String name;

    public Position() {}

    public Position(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
>>>>>>> a0469343f1085105ffc78401283c17527e0302d9
