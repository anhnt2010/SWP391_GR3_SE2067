package dao;
/**
 * Chứa các hàm truy vấn database (lấy ca làm, lấy lịch rảnh hiện tại, cập nhật lịch rảnh)
 */
import context.DBContext;
import model.EmployeeAvailability;
import model.Shift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAO extends DBContext {

    // Lấy danh sách tất cả các ca mẫu
    public List<Shift> getAllShifts() {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT * FROM shifts ORDER BY start_time";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Shift(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách lịch rảnh hiện tại của 1 nhân viên
    public List<EmployeeAvailability> getAvailabilitiesByUserId(int userId) {
        List<EmployeeAvailability> list = new ArrayList<>();
        String sql = "SELECT * FROM employee_availabilities WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EmployeeAvailability(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("day_of_week"),
                        rs.getInt("shift_id")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Cập nhật lại toàn bộ lịch rảnh của nhân viên (Dùng Transaction để đảm bảo an toàn)
    public boolean saveAvailabilities(int userId, List<String[]> selectedPairs) {
        String deleteSql = "DELETE FROM employee_availabilities WHERE user_id = ?";
        String insertSql = "INSERT INTO employee_availabilities (user_id, day_of_week, shift_id) VALUES (?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Xóa toàn bộ lựa chọn cũ
            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                psDel.setInt(1, userId);
                psDel.executeUpdate();
            }

            // 2. Chèn danh sách lựa chọn mới
            if (selectedPairs != null && !selectedPairs.isEmpty()) {
                try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                    for (String[] pair : selectedPairs) {
                        // pair[0] = DAY (MONDAY, TUESDAY...), pair[1] = shift_id
                        psIns.setInt(1, userId);
                        psIns.setString(2, pair[0]);
                        psIns.setInt(3, Integer.parseInt(pair[1]));
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }
            }

            conn.commit(); // Commit Transaction
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}