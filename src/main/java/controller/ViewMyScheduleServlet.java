package controller;

import dao.AvailabilityDAO;
import dao.ScheduleDAO;
import model.Shift;
import model.ShiftSchedule;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@WebServlet(name = "ViewMyScheduleServlet", urlPatterns = {"/view-schedule"})
public class ViewMyScheduleServlet extends HttpServlet {

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

        // Chọn ngày bắt đầu tuần (mặc định lấy Thứ 2 tuần hiện tại)
        String weekStartStr = request.getParameter("weekStart");
        LocalDate startOfWeek;
        if (weekStartStr != null && !weekStartStr.isEmpty()) {
            startOfWeek = LocalDate.parse(weekStartStr);
        } else {
            startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Truy vấn danh sách ca và lịch phân công chính thức
        List<Shift> shifts = availabilityDAO.getAllShifts();
        List<ShiftSchedule> mySchedules = scheduleDAO.getMyPublishedSchedules(
                user.getId(), Date.valueOf(startOfWeek), Date.valueOf(endOfWeek));

        request.setAttribute("startOfWeek", startOfWeek);
        request.setAttribute("endOfWeek", endOfWeek);
        request.setAttribute("shifts", shifts);
        request.setAttribute("mySchedules", mySchedules);

        request.getRequestDispatcher("view-schedule.jsp").forward(request, response);
    }
}