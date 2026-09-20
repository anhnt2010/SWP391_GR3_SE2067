package controller;

import dao.AvailabilityDAO;
import dao.ScheduleDAO;
import model.EmployeeAvailability;
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
import java.util.ArrayList;
import java.util.List;
import model.OpenShift;

@WebServlet(name = "ManageScheduleServlet", urlPatterns = {"/manage-schedule"})
public class ManageScheduleServlet extends HttpServlet {

    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private AvailabilityDAO availabilityDAO = new AvailabilityDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User manager = (User) session.getAttribute("account");

        if (manager == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Tính ngày đầu tuần (Thứ 2) và cuối tuần (Chủ nhật)
        String weekStartStr = request.getParameter("weekStart");
        LocalDate startOfWeek;
        if (weekStartStr != null && !weekStartStr.isEmpty()) {
            startOfWeek = LocalDate.parse(weekStartStr);
        } else {
            // Mặc định lấy Thứ 2 của tuần tiếp theo
            startOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        }
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Date sqlStartDate = Date.valueOf(startOfWeek);
        Date sqlEndDate = Date.valueOf(endOfWeek);

        // Lấy dữ liệu cho Ma trận xếp lịch
        int branchId = manager.getHomeBranchId() > 0 ? manager.getHomeBranchId() : 1; // Default branch nếu null
        List<User> employees = scheduleDAO.getEmployeesByBranch(branchId);
        List<Shift> shifts = availabilityDAO.getAllShifts();
        List<ShiftSchedule> currentSchedules = scheduleDAO.getSchedulesByDateRange(branchId, sqlStartDate, sqlEndDate);

        // Lấy tất cả lịch rảnh của nhân viên trong cơ sở
        List<EmployeeAvailability> allAvailabilities = new ArrayList<>();
        for (User emp : employees) {
            allAvailabilities.addAll(availabilityDAO.getAvailabilitiesByUserId(emp.getId()));
        }

        request.setAttribute("startOfWeek", startOfWeek);
        request.setAttribute("endOfWeek", endOfWeek);
        request.setAttribute("employees", employees);
        request.setAttribute("shifts", shifts);
        request.setAttribute("currentSchedules", currentSchedules);
        request.setAttribute("allAvailabilities", allAvailabilities);

        // Lấy danh sách ca mở để truyền sang JSP
        List<OpenShift> openShifts = scheduleDAO.getOpenShiftsByDateRange(branchId, sqlStartDate, sqlEndDate);
        request.setAttribute("openShifts", openShifts);

        request.getRequestDispatcher("manage-schedule.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User manager = (User) session.getAttribute("account");

        if (manager == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String weekStartStr = request.getParameter("weekStart");
        LocalDate startOfWeek = LocalDate.parse(weekStartStr);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        int branchId = manager.getHomeBranchId();

        String action = request.getParameter("action"); // "save_draft" hoặc "publish"

        if ("create_open_shift".equals(action)) {
            int shiftId = Integer.parseInt(request.getParameter("shiftId"));
            Date workDate = Date.valueOf(request.getParameter("workDate"));
            int quantityNeeded = Integer.parseInt(request.getParameter("quantityNeeded"));

            OpenShift os = new OpenShift();
            os.setBranchId(branchId);
            os.setShiftId(shiftId);
            os.setWorkDate(workDate);
            os.setQuantityNeeded(quantityNeeded);

            boolean created = scheduleDAO.createOpenShift(os);
            if (created) {
                session.setAttribute("message", "Đã tạo ca mở thành công!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Tạo ca mở thất bại!");
                session.setAttribute("messageType", "danger");
            }
            response.sendRedirect("manage-schedule?weekStart=" + weekStartStr);
            return;
        }

        if ("publish".equals(action)) {
            // Xử lý Xuất bản lịch (UC17)
            boolean published = scheduleDAO.publishSchedule(branchId, Date.valueOf(startOfWeek), Date.valueOf(endOfWeek));

            if (published) {
                session.setAttribute("message", "Đã xuất bản lịch làm việc tuần này thành công! Nhân viên hiện đã có thể xem lịch.");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Chưa có bản nháp nào được tạo để xuất bản. Vui lòng lưu bản nháp trước!");
                session.setAttribute("messageType", "warning");
            }
        } else {
            // Xử lý Lưu bản nháp (UC16)
            String[] assignedShifts = request.getParameterValues("assignedShifts");
            List<ShiftSchedule> schedules = new ArrayList<>();

            if (assignedShifts != null) {
                for (String item : assignedShifts) {
                    String[] parts = item.split("_");
                    if (parts.length == 3) {
                        ShiftSchedule ss = new ShiftSchedule();
                        ss.setBranchId(branchId);
                        ss.setUserId(Integer.parseInt(parts[0]));
                        ss.setWorkDate(Date.valueOf(parts[1]));
                        ss.setShiftId(Integer.parseInt(parts[2]));
                        schedules.add(ss);
                    }
                }
            }

            boolean success = scheduleDAO.saveDraftSchedule(branchId, Date.valueOf(startOfWeek), Date.valueOf(endOfWeek), schedules);

            if (success) {
                session.setAttribute("message", "Lưu bản nháp lịch tuần thành công!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Lưu bản nháp thất bại. Vui lòng thử lại!");
                session.setAttribute("messageType", "danger");
            }
        }

        response.sendRedirect("manage-schedule?weekStart=" + weekStartStr);
    }
}
