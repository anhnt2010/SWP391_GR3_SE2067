package controller;

import dao.AvailabilityDAO;
import dao.ScheduleDAO;
import model.OpenShift;
import model.Shift;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ViewOpenShiftsServlet", urlPatterns = {"/view-open-shifts"})
public class ViewOpenShiftsServlet extends HttpServlet {

    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private AvailabilityDAO availabilityDAO = new AvailabilityDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy danh sách tất cả ca mở đang OPEN của chi nhánh
        int branchId = user.getHomeBranchId();
        List<OpenShift> openShifts = scheduleDAO.getOpenShiftsByBranch(branchId);
        List<Shift> shifts = availabilityDAO.getAllShifts();
        List<Integer> appliedIds = scheduleDAO.getAppliedOpenShiftIds(user.getId());

        request.setAttribute("openShifts", openShifts);
        request.setAttribute("shifts", shifts);
        request.setAttribute("appliedIds", appliedIds);

        request.getRequestDispatcher("view-open-shifts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int openShiftId = Integer.parseInt(request.getParameter("openShiftId"));
        boolean success = scheduleDAO.applyOpenShift(openShiftId, user.getId());

        if (success) {
            session.setAttribute("message", "Đăng ký nhận ca mở thành công! Vui lòng chờ Quản lý duyệt.");
            session.setAttribute("messageType", "success");
        } else {
            session.setAttribute("message", "Bạn đã đăng ký ca này trước đó hoặc có lỗi xảy ra.");
            session.setAttribute("messageType", "warning");
        }

        response.sendRedirect("view-open-shifts");
    }
}