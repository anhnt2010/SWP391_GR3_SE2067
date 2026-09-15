package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

public class UserDAO {

    // 1. Phương thức thêm tài khoản mới vào Database
   public boolean insertUser(User user) throws Exception {
    String sql = "INSERT INTO User (employee_id, full_name, email, phone, password, role, store_id, is_first_login, status, expire_at) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, user.getEmployeeId());
        ps.setString(2, user.getFullName());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getPhone());
        ps.setString(5, user.getPassword());
        ps.setString(6, user.getRole());
        ps.setInt(7, user.getStoreId());
        ps.setBoolean(8, user.isIsFirstLogin());
        ps.setBoolean(9, user.isStatus());
        
        if (user.getExpireAt() != null) {
            ps.setDate(10, user.getExpireAt());
        } else {
            ps.setNull(10, java.sql.Types.DATE);
        }

        return ps.executeUpdate() > 0;
    }
}

    // 2. Phương thức kiểm tra xem mã nhân viên (employee_id) đã tồn tại chưa
    public boolean checkEmployeeIdExists(String employeeId) {
        String sql = "SELECT employee_id FROM User WHERE employee_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true; // Đã tồn tại
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Chưa tồn tại
    }
}