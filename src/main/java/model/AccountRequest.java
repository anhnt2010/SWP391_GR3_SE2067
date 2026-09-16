package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Đối tượng đại diện cho 1 ĐỀ XUẤT TẠO TÀI KHOẢN do Quản lý gửi lên chờ HR duyệt.
 */
public class AccountRequest {

    // Các trạng thái của đề xuất
    public static final String STATUS_PENDING  = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    private int requestId;

    // Thông tin tài khoản được đề xuất
    private String employeeId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private int storeId;
    private Date expireAt;

    // Thông tin đề xuất
    private String reason;
    private String proposedBy;
    private Timestamp proposedAt;

    // Thông tin phê duyệt
    private String status;
    private String approvedBy;
    private Timestamp approvedAt;
    private String approvalNote;

    public AccountRequest() {
    }

    /** Constructor dùng khi Quản lý tạo đề xuất mới. */
    public AccountRequest(String employeeId, String fullName, String email, String phone,
                          String role, int storeId, Date expireAt,
                          String reason, String proposedBy) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.storeId = storeId;
        this.expireAt = expireAt;
        this.reason = reason;
        this.proposedBy = proposedBy;
        this.status = STATUS_PENDING;
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getProposedBy() { return proposedBy; }
    public void setProposedBy(String proposedBy) { this.proposedBy = proposedBy; }

    public Timestamp getProposedAt() { return proposedAt; }
    public void setProposedAt(Timestamp proposedAt) { this.proposedAt = proposedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }

    public String getApprovalNote() { return approvalNote; }
    public void setApprovalNote(String approvalNote) { this.approvalNote = approvalNote; }

    /** Tiện dụng cho JSP: kiểm tra đề xuất còn đang chờ duyệt hay không. */
    public boolean isPending() {
        return STATUS_PENDING.equalsIgnoreCase(status);
    }
}
