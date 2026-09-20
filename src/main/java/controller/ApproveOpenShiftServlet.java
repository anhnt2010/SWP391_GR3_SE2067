package controller;

import dao.ScheduleDAO;
import model.OpenShiftApplication;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ApproveOpenShiftServlet", urlPatterns = {"/approve-open-shift"})
public class ApproveOpenShiftServlet extends HttpServlet {

    private ScheduleDAO scheduleDAO = new ScheduleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null || !"STORE_MANAGER".equals(user.getRole().getName())) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<OpenShiftApplication> applications = scheduleDAO.getApplicationsByBranch(user.getHomeBranchId());
        request.setAttribute("applications", applications);

        request.getRequestDispatcher("approve-open-shift.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null || !"STORE_MANAGER".equals(user.getRole().getName())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        int appId = Integer.parseInt(request.getParameter("appId"));

        if ("approve".equals(action)) {
            int openShiftId = Integer.parseInt(request.getParameter("openShiftId"));
            int applicantUserId = Integer.parseInt(request.getParameter("userId"));

            boolean success = scheduleDAO.approveOpenShiftApplication(appId, openShiftId, applicantUserId, user.getHomeBranchId());
            if (success) {
                session.setAttribute("message", "Đã duyệt đơn ứng tuyển và bổ sung nhân viên vào lịch làm việc!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Duyệt đơn thất bại!");
                session.setAttribute("messageType", "danger");
            }
        } else if ("reject".equals(action)) {
            boolean success = scheduleDAO.rejectOpenShiftApplication(appId);
            if (success) {
                session.setAttribute("message", "Đã từ chối đơn ứng tuyển!");
                session.setAttribute("messageType", "warning");
            } else {
                session.setAttribute("message", "Thao tác thất bại!");
                session.setAttribute("messageType", "danger");
            }
        }

        response.sendRedirect("approve-open-shift");
    }
}