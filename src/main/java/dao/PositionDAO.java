package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Position;

/** Thao tác dữ liệu cho danh mục Chức vụ. */
public class PositionDAO {

    public List<Position> getAll(boolean onlyActive) throws Exception {
        List<Position> list = new ArrayList<>();
        String sql = "SELECT * FROM position " + (onlyActive ? "WHERE status = 1 " : "") + "ORDER BY position_id";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Position getById(int id) throws Exception {
        String sql = "SELECT * FROM position WHERE position_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean insert(Position p) throws Exception {
        String sql = "INSERT INTO position (position_code, position_name, base_salary, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPositionCode());
            ps.setString(2, p.getPositionName());
            ps.setBigDecimal(3, p.getBaseSalary());
            ps.setBoolean(4, p.isStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Position p) throws Exception {
        String sql = "UPDATE position SET position_code = ?, position_name = ?, base_salary = ?, status = ? WHERE position_id = ?";
        try (Connection conn = DBContext.getConnection();
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
        String sql = "UPDATE position SET status = NOT status WHERE position_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countUsers(int positionId) throws Exception {
        String sql = "SELECT COUNT(*) FROM user WHERE position_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, positionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean isCodeExists(String code, int exceptId) throws Exception {
        String sql = "SELECT 1 FROM position WHERE position_code = ? AND position_id <> ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Position map(ResultSet rs) throws Exception {
        return new Position(
                rs.getInt("position_id"),
                rs.getString("position_code"),
                rs.getString("position_name"),
                rs.getBigDecimal("base_salary"),
                rs.getBoolean("status"));
    }
}
