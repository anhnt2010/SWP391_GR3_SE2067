package dao;

import context.DBContext;
import model.RecruitmentProposal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecruitmentProposalDAO extends DBContext {

    // UC12: Store Manager tạo đề xuất
    public boolean createProposal(RecruitmentProposal proposal) {
    String sql = "INSERT INTO recruitment_proposals (branch_id, position_id, employment_type, quantity, target_date, reason, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, proposal.getBranchId());
        ps.setInt(2, proposal.getPositionId());
        ps.setString(3, proposal.getEmploymentType());
        ps.setInt(4, proposal.getQuantity());
        ps.setDate(5, proposal.getTargetDate());
        ps.setString(6, proposal.getReason());
        ps.setInt(7, proposal.getCreatedBy());
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

    // Lấy danh sách đề xuất theo Branch (Dành cho Store Manager xem local scope)
   public List<RecruitmentProposal> getProposalsByBranch(int branchId) {
    List<RecruitmentProposal> list = new ArrayList<>();
    String sql = "SELECT rp.*, b.name AS branch_name, p.title AS position_name, u1.username AS creator_name, u2.username AS approver_name " +
                 "FROM recruitment_proposals rp " +
                 "JOIN branches b ON rp.branch_id = b.id " +
                 "JOIN positions p ON rp.position_id = p.id " +
                 "JOIN users u1 ON rp.created_by = u1.id " +
                 "LEFT JOIN users u2 ON rp.approved_by = u2.id " +
                 "WHERE rp.branch_id = ? ORDER BY rp.created_at DESC";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, branchId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSetToProposal(rs));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

public List<RecruitmentProposal> getAllProposals() {
    List<RecruitmentProposal> list = new ArrayList<>();
    String sql = "SELECT rp.*, b.name AS branch_name, p.title AS position_name, u1.username AS creator_name, u2.username AS approver_name " +
                 "FROM recruitment_proposals rp " +
                 "JOIN branches b ON rp.branch_id = b.id " +
                 "JOIN positions p ON rp.position_id = p.id " +
                 "JOIN users u1 ON rp.created_by = u1.id " +
                 "LEFT JOIN users u2 ON rp.approved_by = u2.id " +
                 "ORDER BY rp.created_at DESC";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            list.add(mapResultSetToProposal(rs));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}
    // UC13: HR Manager Duyệt hoặc Từ chối đề xuất
    public boolean updateProposalStatus(int proposalId, String status, int hrId, String hrNote) {
        String sql = "UPDATE recruitment_proposals SET status = ?, approved_by = ?, hr_note = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, hrId);
            ps.setString(3, hrNote);
            ps.setInt(4, proposalId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private RecruitmentProposal mapResultSetToProposal(ResultSet rs) throws SQLException {
        RecruitmentProposal p = new RecruitmentProposal();
        p.setId(rs.getInt("id"));
        p.setBranchId(rs.getInt("branch_id"));
        p.setPositionId(rs.getInt("position_id"));
        p.setQuantity(rs.getInt("quantity"));
        p.setReason(rs.getString("reason"));
        p.setStatus(rs.getString("status"));
        p.setCreatedBy(rs.getInt("created_by"));
        p.setApprovedBy((Integer) rs.getObject("approved_by"));
        p.setHrNote(rs.getString("hr_note"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        p.setBranchName(rs.getString("branch_name"));
        p.setPositionName(rs.getString("position_name"));
        p.setCreatorName(rs.getString("creator_name"));
        p.setApproverName(rs.getString("approver_name"));
        return p;
    }
}