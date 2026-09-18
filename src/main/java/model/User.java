package model;

import java.sql.Timestamp;

public class User {

    private int id;
    private String username; // Mã nhân viên (VD: NV001)
    private String email;    // Có thể null
    private String phone;    // Dùng cho xác thực / reset pass
    private String status;
    private Timestamp expirationDate; // Hạn tài khoản thời vụ
    private Role role;
    private int homeBranchId; // Bắt buộc >= 1
    private String fullName;
    private boolean isFirstLogin;

    public User() {
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getHomeBranchId() {
        return homeBranchId;
    }

    public void setHomeBranchId(int homeBranchId) {
        this.homeBranchId = homeBranchId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }
}
