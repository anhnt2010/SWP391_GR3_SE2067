package controller;

import dao.RecruitmentProposalDAO;
import model.RecruitmentProposal;
import model.User;
import dao.PositionDAO;
import model.Position;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RecruitmentProposalServlet", urlPatterns = {"/recruitment-proposal"})
public class RecruitmentProposalServlet extends HttpServlet {

    private RecruitmentProposalDAO proposalDAO = new RecruitmentProposalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        PositionDAO positionDAO = new PositionDAO();
        List<Position> positionList = positionDAO.getAllPositions();
        request.setAttribute("positions", positionList); // Đẩy danh sách vị trí sang JSP
        
        List<RecruitmentProposal> list;
        // Kiểm tra Role để phân tách Scope dữ liệu
        if ("STORE_MANAGER".equals(user.getRole())) {
            list = proposalDAO.getProposalsByBranch(user.getHomeBranchId());
        } else {
            list = proposalDAO.getAllProposals();
        }

        request.setAttribute("proposals", list);
        request.getRequestDispatcher("/recruitment-proposal.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("create".equals(action)) { // UC12: Store Manager tạo đề xuất
            int positionId = Integer.parseInt(request.getParameter("positionId"));
            String employmentType = request.getParameter("employmentType");
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String targetDateStr = request.getParameter("targetDate");
            String reason = request.getParameter("reason");

            RecruitmentProposal proposal = new RecruitmentProposal();
            proposal.setBranchId(user.getHomeBranchId());
            proposal.setPositionId(positionId);
            proposal.setEmploymentType(employmentType);
            proposal.setQuantity(quantity);
            if (targetDateStr != null && !targetDateStr.isEmpty()) {
                proposal.setTargetDate(java.sql.Date.valueOf(targetDateStr));
            }
            proposal.setReason(reason);
            proposal.setCreatedBy(user.getId());

            proposalDAO.createProposal(proposal);
        } else if ("process".equals(action)) { // UC13: HR Manager Duyệt/Từ chối
            int proposalId = Integer.parseInt(request.getParameter("proposalId"));
            String status = request.getParameter("status"); // APPROVED hoặc REJECTED
            String hrNote = request.getParameter("hrNote");

            proposalDAO.updateProposalStatus(proposalId, status, user.getId(), hrNote);
        }

        response.sendRedirect("recruitment-proposal");
    }
}
