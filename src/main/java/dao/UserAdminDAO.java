package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.UserView;
import model.Role;

/**
 * Các nghiệp vụ QUẢN TRỊ tài khoản người dùng: tìm kiếm, lọc, cập nhật, khóa/mở
 * khóa, reset mật khẩu, phân quyền.
 */
public class UserAdminDAO {

    /**
     * Tìm kiếm và lọc danh sách tài khoản.
     *
     * @param keyword tìm theo mã NV / họ tên / email (có thể null)
     * @param role lọc theo vai trò, null hoặc "ALL" = tất cả
     * @param storeId lọc theo cơ sở, 0 = tất cả
     * @param status "1" đang hoạt động, "0" đã khóa, null/"ALL" = tất cả
     */
    public List<UserView> search(String keyword, String role, int storeId, String status) throws Exception {
        List<UserView> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT u.*, s.store_name, d.department_name, p.position_name "
                + "FROM user u "
                + "LEFT JOIN store s      ON u.store_id = s.store_id "
                + "LEFT JOIN department d ON u.department_id = d.department_id "
                + "LEFT JOIN position p   ON u.position_id = p.position_id "
                + "WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.employee_id LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (role != null && !role.trim().isEmpty() && !"ALL".equalsIgnoreCase(role)) {
            sql.append("AND u.role = ? ");
            params.add(role);
        }
        if (storeId > 0) {
            sql.append("AND u.store_id = ? ");
            params.add(storeId);
        }
        if ("1".equals(status) || "0".equals(status)) {
            sql.append("AND u.status = ? ");
            params.add(Integer.parseInt(status));
        }

        sql.append("ORDER BY u.employee_id");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public UserView getByEmployeeId(String employeeId) throws Exception {
        String sql = "SELECT u.*, s.store_name, d.department_name, p.position_name "
                + "FROM user u "
                + "LEFT JOIN store s      ON u.store_id = s.store_id "
                + "LEFT JOIN department d ON u.department_id = d.department_id "
                + "LEFT JOIN position p   ON u.position_id = p.position_id "
                + "WHERE u.employee_id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /**
     * Cập nhật hồ sơ + phân công (không đụng tới mật khẩu).
     */
    public boolean updateUser(UserView u) throws Exception {
        String sql = "UPDATE user SET full_name = ?, email = ?, phone = ?, role = ?, "
                + "store_id = ?, department_id = ?, position_id = ?, expire_at = ? "
                + "WHERE employee_id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getRole() != null ? u.getRole().getName() : null);
            ps.setInt(5, u.getHomeBranchId());

            if (u.getDepartmentId() > 0) {
                ps.setInt(6, u.getDepartmentId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            if (u.getPositionId() > 0) {
                ps.setInt(7, u.getPositionId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }

            if (u.getExpirationDate() != null) {
                ps.setTimestamp(8, u.getExpirationDate());
            } else {
                ps.setNull(8, java.sql.Types.TIMESTAMP);
            }

            ps.setString(9, u.getUsername());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Khóa / mở khóa tài khoản.
     */
    public boolean toggleStatus(String employeeId) throws Exception {
        String sql = "UPDATE user SET status = NOT status WHERE employee_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Reset mật khẩu về mặc định và bắt buộc đổi ở lần đăng nhập kế tiếp.
     */
    public boolean resetPassword(String employeeId, String defaultPassword) throws Exception {
        String sql = "UPDATE user SET password = ?, is_first_login = 1 WHERE employee_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, defaultPassword);
            ps.setString(2, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Đổi vai trò (phân quyền nhanh ngay trên danh sách).
     */
    public boolean changeRole(String employeeId, String newRole) throws Exception {
        String sql = "UPDATE user SET role = ? WHERE employee_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setString(2, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Đếm số tài khoản Admin đang hoạt động — chặn việc tự khóa/hạ quyền Admin
     * cuối cùng.
     */
    public int countActiveAdmins() throws Exception {
        String sql = "SELECT COUNT(*) FROM user WHERE role = 'Admin' AND status = 1";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Thống kê nhanh cho trang Dashboard: tổng, đang hoạt động, đã khóa.
     */
    public int[] getUserStats() throws Exception {
        String sql = "SELECT COUNT(*), SUM(status = 1), SUM(status = 0) FROM user";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)};
            }
        }
        return new int[]{0, 0, 0};
    }

    private UserView map(ResultSet rs) throws Exception {
        UserView u = new UserView();
        
        u.setUsername(rs.getString("employee_id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            u.setRole(new Role(0, roleStr));
        }
        
        u.setHomeBranchId(rs.getInt("store_id"));
        u.setDepartmentId(rs.getInt("department_id"));
        u.setPositionId(rs.getInt("position_id"));
        
        try { u.setFirstLogin(rs.getBoolean("is_first_login")); } catch (Exception ignored) {}
        try { u.setStatus(rs.getString("status")); } catch (Exception ignored) {}
        try { u.setExpirationDate(rs.getTimestamp("expire_at")); } catch (Exception ignored) {}
        
        u.setStoreName(rs.getString("store_name"));
        u.setDepartmentName(rs.getString("department_name"));
        u.setPositionName(rs.getString("position_name"));
        
        return u;
    }
}
