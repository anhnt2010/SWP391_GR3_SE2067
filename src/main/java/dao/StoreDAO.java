/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author nguyn
 */
import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Store;

public class StoreDAO {
    
    // Tính năng 1: Khai báo cơ sở (Thêm store mới vào Database)
    public boolean insertStore(Store store) {
        String sql = "INSERT INTO Store (store_name, address) VALUES (?, ?)";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, store.getStoreName());
            ps.setString(2, store.getAddress());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu thêm thành công
            
        } catch (Exception e) {
            System.out.println("Lỗi Khai báo cơ sở: " + e.getMessage());
        }
        return false;
    }
    
    // Lấy danh sách tất cả các cơ sở để đưa lên giao diện
  public List<Store> getAllStores() {
      List<Store> list = new ArrayList<>();
      String sql = "SELECT * FROM Store";
      
      try (Connection conn = new DBContext().getConnection();
           PreparedStatement ps = conn.prepareStatement(sql);
           ResultSet rs = ps.executeQuery()) {
           
          while (rs.next()) {
              Store s = new Store();
              s.setStoreId(rs.getInt("store_id"));
              s.setStoreName(rs.getString("store_name"));
              s.setAddress(rs.getString("address"));
              list.add(s);
          }
      } catch (Exception e) {
          System.out.println("Lỗi lấy danh sách Store: " + e.getMessage());
      }
      return list;
  }
}
