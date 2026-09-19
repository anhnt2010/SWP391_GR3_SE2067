package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Store;

/** Thao tác dữ liệu cho danh mục Cơ sở / Chi nhánh. */
public class StoreDAO {

    public List<Store> getAll(boolean onlyActive) throws Exception {
        List<Store> list = new ArrayList<>();
        String sql = "SELECT * FROM store " + (onlyActive ? "WHERE status = 1 " : "") + "ORDER BY store_id";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Store getById(int id) throws Exception {
        String sql = "SELECT * FROM store WHERE store_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean insert(Store s) throws Exception {
        String sql = "INSERT INTO store (store_code, store_name, address, phone, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStoreCode());
            ps.setString(2, s.getStoreName());
            ps.setString(3, s.getAddress());
            ps.setString(4, s.getPhone());
            ps.setBoolean(5, s.isStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Store s) throws Exception {
        String sql = "UPDATE store SET store_code = ?, store_name = ?, address = ?, phone = ?, status = ? WHERE store_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStoreCode());
            ps.setString(2, s.getStoreName());
            ps.setString(3, s.getAddress());
            ps.setString(4, s.getPhone());
            ps.setBoolean(5, s.isStatus());
            ps.setInt(6, s.getStoreId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Ngừng / mở lại hoạt động (không xóa cứng để giữ toàn vẹn dữ liệu lịch sử). */
    public boolean toggleStatus(int id) throws Exception {
        String sql = "UPDATE store SET status = NOT status WHERE store_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Đếm số nhân viên đang thuộc cơ sở này — dùng để cảnh báo trước khi ngừng hoạt động. */
    public int countUsers(int storeId) throws Exception {
        String sql = "SELECT COUNT(*) FROM user WHERE store_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, storeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean isCodeExists(String code, int exceptId) throws Exception {
        String sql = "SELECT 1 FROM store WHERE store_code = ? AND store_id <> ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Store map(ResultSet rs) throws Exception {
        return new Store(
                rs.getInt("store_id"),
                rs.getString("store_code"),
                rs.getString("store_name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getBoolean("status"));
    }
}
