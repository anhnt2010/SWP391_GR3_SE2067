package dao;

import context.DBContext;
import model.EmployeeAvailability;
import model.ShiftSchedule;
import model.User;
import model.OpenShift;
import model.OpenShiftApplication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO extends DBContext {

    // 1. Lấy danh sách Nhân viên thuộc cùng Chi nhánh với Manager
    public List<User> getEmployeesByBranch(int branchId) {
        List<User> list = new ArrayList<>();

        // Sử dụng ep.home_branch_id đúng theo thiết kế database
        String sql = "SELECT u.id, u.email, ep.full_name, ep.home_branch_id "
                + "FROM users u "
                + "JOIN employee_profiles ep ON u.id = ep.user_id "
                + "JOIN roles r ON u.role_id = r.id "
                + "WHERE r.name = 'EMPLOYEE' AND ep.home_branch_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setEmail(rs.getString("email"));
                    u.setFullName(rs.getString("full_name"));
                    u.setHomeBranchId(rs.getInt("home_branch_id")); // Ánh xạ vào model User
                    list.add(u);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] ScheduleDAO.getEmployeesByBranch ex: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // 2. Lấy lịch làm việc đã xếp trong khoảng từ startDate đến endDate
    public List<ShiftSchedule> getSchedulesByDateRange(int branchId, Date startDate, Date endDate) {
        List<ShiftSchedule> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_schedules WHERE branch_id = ? AND work_date BETWEEN ? AND ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ShiftSchedule(
                            rs.getInt("id"),
                            rs.getInt("branch_id"),
                            rs.getInt("user_id"),
                            rs.getInt("shift_id"),
                            rs.getDate("work_date"),
                            rs.getInt("position_id"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Lưu/Cập nhật bản nháp lịch tuần (DRAFT)
    public boolean saveDraftSchedule(int branchId, Date startDate, Date endDate, List<ShiftSchedule> schedules) {
        String deleteSql = "DELETE FROM shift_schedules WHERE branch_id = ? AND work_date BETWEEN ? AND ? AND status = 'DRAFT'";
        String insertSql = "INSERT INTO shift_schedules (branch_id, user_id, shift_id, work_date, position_id, status) VALUES (?, ?, ?, ?, ?, 'DRAFT')";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Xóa các bản ghi nháp cũ trong tuần này để ghi đè
            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                psDel.setInt(1, branchId);
                psDel.setDate(2, startDate);
                psDel.setDate(3, endDate);
                psDel.executeUpdate();
            }

            // Thêm danh sách phân công mới
            if (schedules != null && !schedules.isEmpty()) {
                try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                    for (ShiftSchedule ss : schedules) {
                        psIns.setInt(1, branchId);
                        psIns.setInt(2, ss.getUserId());
                        psIns.setInt(3, ss.getShiftId());
                        psIns.setDate(4, ss.getWorkDate());
                        psIns.setInt(5, ss.getPositionId() > 0 ? ss.getPositionId() : 1); // Default position
                        psIns.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Cập nhật tất cả bản ghi DRAFT trong tuần thành PUBLISHED
    public boolean publishSchedule(int branchId, Date startDate, Date endDate) {
        String sql = "UPDATE shift_schedules SET status = 'PUBLISHED' "
                + "WHERE branch_id = ? AND work_date BETWEEN ? AND ? AND status = 'DRAFT'";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, branchId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            int rowsUpdated = ps.executeUpdate();
            System.out.println("=== DEBUG UC17 ===");
            System.out.println("-> Published rows: " + rowsUpdated);

            return rowsUpdated > 0;
        } catch (Exception e) {
            System.err.println("[ERROR] ScheduleDAO.publishSchedule ex: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lấy lịch làm việc cá nhân đã được xuất bản (PUBLISHED) của 1 nhân viên
    public List<ShiftSchedule> getMyPublishedSchedules(int userId, Date startDate, Date endDate) {
        List<ShiftSchedule> list = new ArrayList<>();
        String sql = "SELECT * FROM shift_schedules "
                + "WHERE user_id = ? AND work_date BETWEEN ? AND ? AND status = 'PUBLISHED'";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ShiftSchedule(
                            rs.getInt("id"),
                            rs.getInt("branch_id"),
                            rs.getInt("user_id"),
                            rs.getInt("shift_id"),
                            rs.getDate("work_date"),
                            rs.getInt("position_id"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm Ca Mở mới (UC18)
    public boolean createOpenShift(OpenShift os) {
        String sql = "INSERT INTO open_shifts (branch_id, shift_id, work_date, quantity_needed, status) VALUES (?, ?, ?, ?, 'OPEN')";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, os.getBranchId());
            ps.setInt(2, os.getShiftId());
            ps.setDate(3, os.getWorkDate());
            ps.setInt(4, os.getQuantityNeeded());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

// Lấy danh sách Ca Mở theo khoảng thời gian tuần
    public List<OpenShift> getOpenShiftsByDateRange(int branchId, Date startDate, Date endDate) {
        List<OpenShift> list = new ArrayList<>();
        String sql = "SELECT * FROM open_shifts WHERE branch_id = ? AND work_date BETWEEN ? AND ? AND status = 'OPEN'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OpenShift(
                            rs.getInt("id"),
                            rs.getInt("branch_id"),
                            rs.getInt("shift_id"),
                            rs.getDate("work_date"),
                            rs.getInt("quantity_needed"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đăng ký nhận ca mở (UC19)
    public boolean applyOpenShift(int openShiftId, int userId) {
        String sql = "INSERT INTO open_shift_applications (open_shift_id, user_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, openShiftId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[ERROR] ScheduleDAO.applyOpenShift ex: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// Lấy danh sách ID các ca mở mà nhân viên này ĐÃ đăng ký (để ẩn nút hoặc đổi trạng thái nút "Đã đăng ký")
    public List<Integer> getAppliedOpenShiftIds(int userId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT open_shift_id FROM open_shift_applications WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("open_shift_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //Lấy tất cả ca mở status = 'OPEN' của chi nhánh
    public List<OpenShift> getOpenShiftsByBranch(int branchId) {
        List<OpenShift> list = new ArrayList<>();
        String sql = "SELECT * FROM open_shifts WHERE branch_id = ? AND status = 'OPEN' ORDER BY work_date ASC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OpenShift(
                            rs.getInt("id"), rs.getInt("branch_id"), rs.getInt("shift_id"),
                            rs.getDate("work_date"), rs.getInt("quantity_needed"), rs.getString("status")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Lấy danh sách các đơn ứng tuyển ca mở thuộc chi nhánh (dành cho Store Manager)
public List<OpenShiftApplication> getApplicationsByBranch(int branchId) {
    List<OpenShiftApplication> list = new ArrayList<>();
    String sql = "SELECT app.id, app.open_shift_id, app.user_id, app.applied_at, app.status, " +
                 "ep.full_name AS applicant_name, os.work_date, s.name AS shift_name " +
                 "FROM open_shift_applications app " +
                 "JOIN open_shifts os ON app.open_shift_id = os.id " +
                 "JOIN employee_profiles ep ON app.user_id = ep.user_id " +
                 "JOIN shifts s ON os.shift_id = s.id " +
                 "WHERE os.branch_id = ? AND app.status = 'PENDING' " +
                 "ORDER BY app.applied_at DESC";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, branchId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new OpenShiftApplication(
                    rs.getInt("id"),
                    rs.getInt("open_shift_id"),
                    rs.getInt("user_id"),
                    rs.getTimestamp("applied_at"),
                    rs.getString("status"),
                    rs.getString("applicant_name"),
                    rs.getDate("work_date").toString(),
                    rs.getString("shift_name")
                ));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

// Duyệt đơn ứng tuyển (Approve Application)
public boolean approveOpenShiftApplication(int appId, int openShiftId, int userId, int branchId) {
    Connection conn = null;
    try {
        conn = getConnection();
        conn.setAutoCommit(false); // Bật Transaction

        // 1. Cập nhật đơn ứng tuyển thành APPROVED
        String sqlApprove = "UPDATE open_shift_applications SET status = 'APPROVED' WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlApprove)) {
            ps.setInt(1, appId);
            ps.executeUpdate();
        }

        // 2. Lấy thông tin ca mở (work_date, shift_id) để đẩy vào shift_schedules
        String sqlGetShift = "SELECT shift_id, work_date FROM open_shifts WHERE id = ?";
        int shiftId = 0;
        java.sql.Date workDate = null;
        try (PreparedStatement ps = conn.prepareStatement(sqlGetShift)) {
            ps.setInt(1, openShiftId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    shiftId = rs.getInt("shift_id");
                    workDate = rs.getDate("work_date");
                }
            }
        }

        // 3. Thêm nhân viên vào lịch chính thức (shift_schedules)
        String sqlInsertSchedule = "INSERT INTO shift_schedules (branch_id, user_id, shift_id, work_date, status) VALUES (?, ?, ?, ?, 'PUBLISHED')";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsertSchedule)) {
            ps.setInt(1, branchId);
            ps.setInt(2, userId);
            ps.setInt(3, shiftId);
            ps.setDate(4, workDate);
            ps.executeUpdate();
        }

        // 4. Giảm quantity_needed của ca mở
        String sqlUpdateQty = "UPDATE open_shifts SET quantity_needed = quantity_needed - 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdateQty)) {
            ps.setInt(1, openShiftId);
            ps.executeUpdate();
        }

        // 5. Nếu quantity_needed <= 0 thì tự động CLOSE ca mở
        String sqlCloseOpenShift = "UPDATE open_shifts SET status = 'CLOSED' WHERE id = ? AND quantity_needed <= 0";
        try (PreparedStatement ps = conn.prepareStatement(sqlCloseOpenShift)) {
            ps.setInt(1, openShiftId);
            ps.executeUpdate();
        }

        conn.commit(); // Commit thành công
        return true;
    } catch (Exception e) {
        if (conn != null) {
            try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
        }
        e.printStackTrace();
        return false;
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}

// Từ chối đơn ứng tuyển (Reject Application)
public boolean rejectOpenShiftApplication(int appId) {
    String sql = "UPDATE open_shift_applications SET status = 'REJECTED' WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, appId);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}
