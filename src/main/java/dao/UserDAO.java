package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    
    // 3. Tính năng của Giám đốc: Gắn quản lý cho cơ sở
  public boolean assignManager(String employeeId, int storeId) {
      // Cập nhật store_id và đổi role thành Quản lý cơ sở cho nhân viên được chọn
      String sql = "UPDATE User SET store_id = ?, role = 'Quản lý cơ sở' WHERE employee_id = ?";
      
      try (Connection conn = new DBContext().getConnection();
           PreparedStatement ps = conn.prepareStatement(sql)) {
           
          ps.setInt(1, storeId);
          ps.setString(2, employeeId);
          
          int rowsAffected = ps.executeUpdate();
          return rowsAffected > 0; // Trả về true nếu cập nhật thành công
          
      } catch (Exception e) {
          System.out.println("Lỗi gắn quản lý cơ sở: " + e.getMessage());
      }
      return false;
  }
  
  // Lấy danh sách nhân sự đủ điều kiện làm quản lý
  public List<User> getEligibleManagers() {
      List<User> list = new ArrayList<>();
      // Chỉ lấy những người đang là "Nhân viên"
      String sql = "SELECT employee_id, full_name, role FROM User WHERE role = 'Nhân viên'";
      
      try (Connection conn = new DBContext().getConnection();
           PreparedStatement ps = conn.prepareStatement(sql);
           ResultSet rs = ps.executeQuery()) {
           
          while (rs.next()) {
              User u = new User();
              // Dựa theo code của nhóm bạn, ID nhân viên dùng chuỗi String (employee_id)
              u.setEmployeeId(rs.getString("employee_id")); 
              u.setFullName(rs.getString("full_name"));
              u.setRole(rs.getString("role"));
              list.add(u);
          }
      } catch (Exception e) {
          System.out.println("Lỗi lấy danh sách nhân sự: " + e.getMessage());
      }
      return list;
  }
}