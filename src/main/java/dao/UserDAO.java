package dao;

import context.DBContext;
import model.Role;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User checkLogin(String username, String password) {
        String sql = "SELECT u.id, u.username, u.email, u.status, u.expiration_date, u.is_first_login, " +
                     "r.id AS role_id, r.name AS role_name, " +
                     "ep.home_branch_id, ep.full_name, ep.phone " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.id " +
                     "LEFT JOIN employee_profiles ep ON u.id = ep.user_id " +
                     "WHERE u.username = ? AND u.password_hash = ?";
        try {
            Connection conn = new DBContext().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Role role = new Role(rs.getInt("role_id"), rs.getString("role_name"));
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setStatus(rs.getString("status"));
                user.setExpirationDate(rs.getTimestamp("expiration_date"));
                user.setFirstLogin(rs.getBoolean("is_first_login")); // Đã bổ sung
                user.setRole(role);
                user.setHomeBranchId(rs.getInt("home_branch_id"));
                user.setFullName(rs.getString("full_name") != null ? rs.getString("full_name") : rs.getString("username"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updatePassword(int userId, String newPassword) {
    String sql = "UPDATE users SET password_hash = ?, is_first_login = 0 WHERE id = ?";
    try {
        Connection conn = new DBContext().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, newPassword);
        ps.setInt(2, userId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}