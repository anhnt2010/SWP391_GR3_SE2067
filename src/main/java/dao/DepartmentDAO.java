package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Department;

/** Thao tác dữ liệu cho danh mục Phòng ban / Bộ phận. */
public class DepartmentDAO {

    public List<Department> getAll(boolean onlyActive) throws Exception {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM department " + (onlyActive ? "WHERE status = 1 " : "") + "ORDER BY department_id";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Department getById(int id) throws Exception {
        String sql = "SELECT * FROM department WHERE department_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean insert(Department d) throws Exception {
        String sql = "INSERT INTO department (department_code, department_name, description, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getDepartmentCode());
            ps.setString(2, d.getDepartmentName());
            ps.setString(3, d.getDescription());
            ps.setBoolean(4, d.isStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Department d) throws Exception {
        String sql = "UPDATE department SET department_code = ?, department_name = ?, description = ?, status = ? WHERE department_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getDepartmentCode());
            ps.setString(2, d.getDepartmentName());
            ps.setString(3, d.getDescription());
            ps.setBoolean(4, d.isStatus());
            ps.setInt(5, d.getDepartmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleStatus(int id) throws Exception {
        String sql = "UPDATE department SET status = NOT status WHERE department_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countUsers(int departmentId) throws Exception {
        String sql = "SELECT COUNT(*) FROM user WHERE department_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean isCodeExists(String code, int exceptId) throws Exception {
        String sql = "SELECT 1 FROM department WHERE department_code = ? AND department_id <> ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Department map(ResultSet rs) throws Exception {
        return new Department(
                rs.getInt("department_id"),
                rs.getString("department_code"),
                rs.getString("department_name"),
                rs.getString("description"),
                rs.getBoolean("status"));
    }
}
