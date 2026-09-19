package model;

/**
 * Mở rộng của User dùng cho màn hình Quản trị người dùng:
 * bổ sung phòng ban, chức vụ và tên hiển thị của các danh mục (JOIN từ master data).
 * Nhờ kế thừa nên không phải sửa file User.java gốc.
 */
public class UserView extends User {

    private int departmentId;
    private int positionId;

    private String storeName;
    private String departmentName;
    private String positionName;

    public UserView() {
        super();
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public int getPositionId() { return positionId; }
    public void setPositionId(int positionId) { this.positionId = positionId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
}
