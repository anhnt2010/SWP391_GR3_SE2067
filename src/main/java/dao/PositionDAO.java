package dao;

import context.DBContext;
import model.Position;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO extends DBContext {

    public List<Position> getAllPositions() {
        List<Position> list = new ArrayList<>();
        // Lấy cột title và đổi tên giả (alias) thành name để khớp với Model Position
        String sql = "SELECT id, title AS name FROM positions";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Position(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Xem log lỗi dưới Output Console của NetBeans nếu có
        }
        return list;
    }
}
