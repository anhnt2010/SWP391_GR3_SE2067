package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Position;

/** Thao tác dữ liệu cho danh mục Chức vụ. */
public class PositionDAO extends DBContext {

    public List<Position> getAll(boolean onlyActive) throws Exception {
        List<Position> list = new ArrayList<>();
        String sql = "SELECT * FROM positions " + (onlyActive ? "WHERE status = 1 " : "") + "ORDER BY id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Position getById(int id) throws Exception {
        String sql = "SELECT * FROM positions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean insert(Position p) throws Exception {
        String sql = "INSERT INTO positions (position_code, position_name, base_salary, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPositionCode());
            ps.setString(2, p.getPositionName());
            ps.setBigDecimal(3, p.getBaseSalary());
            ps.setBoolean(4, p.isStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Position p) throws Exception {
        String sql = "UPDATE positions SET position_code = ?, position_name = ?, base_salary = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPositionCode());
            ps.setString(2, p.getPositionName());
            ps.setBigDecimal(3, p.getBaseSalary());
            ps.setBoolean(4, p.isStatus());
            ps.setInt(5, p.getPositionId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleStatus(int id) throws Exception {
        String sql = "UPDATE positions SET status = NOT status WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countUsers(int positionId) throws Exception {
        String sql = "SELECT COUNT(*) FROM employee_profiles WHERE position_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, positionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean isCodeExists(String code, int exceptId) throws Exception {
        String sql = "SELECT 1 FROM positions WHERE position_code = ? AND id <> ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Hàm phục vụ lấy danh sách đơn giản dành cho dropdown list
    public List<Position> getAllPositions() {
        List<Position> list = new ArrayList<>();
        String sql = "SELECT id, title FROM positions";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Position p = new Position();
                p.setPositionId(rs.getInt("id"));
                p.setPositionName(rs.getString("title"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Position map(ResultSet rs) throws Exception {
        Position p = new Position();
        p.setPositionId(rs.getInt("id"));
        
        // Kiểm tra an toàn xem bảng có các cột mở rộng không
        try { p.setPositionCode(rs.getString("position_code")); } catch (Exception ignored) {}
        try { p.setPositionName(rs.getString("title")); } catch (Exception ignored) {}
        try { p.setBaseSalary(rs.getBigDecimal("base_salary")); } catch (Exception ignored) {}
        try { p.setStatus(rs.getBoolean("status")); } catch (Exception ignored) {}
        
        return p;
    }
}