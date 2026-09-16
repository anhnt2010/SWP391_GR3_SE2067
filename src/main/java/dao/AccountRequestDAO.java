package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.AccountRequest;

/**
 * Xử lý dữ liệu cho bảng account_request (đề xuất tạo tài khoản).
 */
public class AccountRequestDAO {

    /** 1. Quản lý gửi 1 đề xuất mới (trạng thái mặc định PENDING). */
    public boolean insertRequest(AccountRequest r) throws Exception {
        String sql = "INSERT INTO account_request "
                + "(employee_id, full_name, email, phone, role, store_id, expire_at, reason, proposed_by, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getEmployeeId());
            ps.setString(2, r.getFullName());
            ps.setString(3, r.getEmail());
            ps.setString(4, r.getPhone());
            ps.setString(5, r.getRole());
            ps.setInt(6, r.getStoreId());

            if (r.getExpireAt() != null) {
                ps.setDate(7, r.getExpireAt());
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }

            ps.setString(8, r.getReason());
            ps.setString(9, r.getProposedBy());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 2. Lấy danh sách đề xuất.
     * @param status PENDING / APPROVED / REJECTED, truyền null hoặc "ALL" để lấy tất cả.
     */
    public List<AccountRequest> getRequests(String status) throws Exception {
        List<AccountRequest> list = new ArrayList<>();

        boolean filter = status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status);
        String sql = "SELECT * FROM account_request "
                + (filter ? "WHERE status = ? " : "")
                + "ORDER BY (status = 'PENDING') DESC, proposed_at DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (filter) {
                ps.setString(1, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /** 3. Lấy 1 đề xuất theo id. */
    public AccountRequest getById(int requestId) throws Exception {
        String sql = "SELECT * FROM account_request WHERE request_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * 4. Cập nhật kết quả phê duyệt.
     * Điều kiện status = 'PENDING' để tránh 2 người duyệt cùng lúc 1 đề xuất.
     */
    public boolean updateStatus(int requestId, String newStatus, String approvedBy, String note) throws Exception {
        String sql = "UPDATE account_request "
                + "SET status = ?, approved_by = ?, approved_at = NOW(), approval_note = ? "
                + "WHERE request_id = ? AND status = 'PENDING'";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, approvedBy);
            ps.setString(3, note);
            ps.setInt(4, requestId);

            return ps.executeUpdate() > 0;
        }
    }

    /** 5. Đếm số đề xuất đang chờ duyệt (dùng cho badge thông báo). */
    public int countPending() throws Exception {
        String sql = "SELECT COUNT(*) FROM account_request WHERE status = 'PENDING'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /** 6. Kiểm tra đã có đề xuất PENDING cho mã nhân viên này chưa (tránh gửi trùng). */
    public boolean existsPendingByEmployeeId(String employeeId) throws Exception {
        String sql = "SELECT 1 FROM account_request WHERE employee_id = ? AND status = 'PENDING'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Chuyển 1 dòng ResultSet thành đối tượng AccountRequest. */
    private AccountRequest mapRow(ResultSet rs) throws Exception {
        AccountRequest r = new AccountRequest();
        r.setRequestId(rs.getInt("request_id"));
        r.setEmployeeId(rs.getString("employee_id"));
        r.setFullName(rs.getString("full_name"));
        r.setEmail(rs.getString("email"));
        r.setPhone(rs.getString("phone"));
        r.setRole(rs.getString("role"));
        r.setStoreId(rs.getInt("store_id"));
        r.setExpireAt(rs.getDate("expire_at"));
        r.setReason(rs.getString("reason"));
        r.setProposedBy(rs.getString("proposed_by"));
        r.setProposedAt(rs.getTimestamp("proposed_at"));
        r.setStatus(rs.getString("status"));
        r.setApprovedBy(rs.getString("approved_by"));
        r.setApprovedAt(rs.getTimestamp("approved_at"));
        r.setApprovalNote(rs.getString("approval_note"));
        return r;
    }
}
