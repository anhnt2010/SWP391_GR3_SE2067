package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.SystemConfig;

/** Thao tác dữ liệu cho bảng system_config và role. */
public class SystemConfigDAO {

    public List<SystemConfig> getAll() throws Exception {
        List<SystemConfig> list = new ArrayList<>();
        String sql = "SELECT * FROM system_config ORDER BY config_key";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SystemConfig c = new SystemConfig();
                c.setConfigKey(rs.getString("config_key"));
                c.setConfigValue(rs.getString("config_value"));
                c.setDescription(rs.getString("description"));
                c.setUpdatedAt(rs.getTimestamp("updated_at"));
                c.setUpdatedBy(rs.getString("updated_by"));
                list.add(c);
            }
        }
        return list;
    }

    /** Lấy 1 giá trị cấu hình, trả về defaultValue nếu chưa có. */
    public String getValue(String key, String defaultValue) {
        String sql = "SELECT config_value FROM system_config WHERE config_key = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public boolean updateValue(String key, String value, String updatedBy) throws Exception {
        String sql = "UPDATE system_config SET config_value = ?, updated_at = NOW(), updated_by = ? WHERE config_key = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, updatedBy);
            ps.setString(3, key);
            return ps.executeUpdate() > 0;
        }
    }

    /** Lấy danh sách vai trò: key = role_code, value = role_name. */
    public Map<String, String> getRoles() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT role_code, role_name FROM role ORDER BY role_code";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getString(2));
            }
        }
        return map;
    }
}
