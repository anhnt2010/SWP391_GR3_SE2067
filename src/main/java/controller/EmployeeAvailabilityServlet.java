package controller;
/**
 * Servlet xử lý request GET (hiển thị trang) và POST (lưu đăng ký).
 */
import dao.AvailabilityDAO;
import model.EmployeeAvailability;
import model.Shift;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "EmployeeAvailabilityServlet", urlPatterns = {"/employee-availability"})
public class EmployeeAvailabilityServlet extends HttpServlet {

    private AvailabilityDAO dao = new AvailabilityDAO();

    // Kiểm tra xem hiện tại có trong hạn đăng ký hay không (Mở từ Thứ 2 -> Hết Thứ 7)
    private boolean isRegistrationOpen() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        // Nếu hôm nay là Chủ Nhật (SUNDAY) -> Khóa form đăng ký
        if (today == DayOfWeek.SUNDAY) {
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Shift> shifts = dao.getAllShifts();
        List<EmployeeAvailability> myAvailabilities = dao.getAvailabilitiesByUserId(user.getId());

        // Gửi trạng thái đóng/mở form sang file JSP
        boolean isOpen = isRegistrationOpen();
        request.setAttribute("isRegistrationOpen", isOpen);
        request.setAttribute("shifts", shifts);
        request.setAttribute("myAvailabilities", myAvailabilities);
        
        request.getRequestDispatcher("employee-availability.jsp").forward(request, response);
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

        // Chặn luồng POST nếu cố tình gửi request khi đã hết hạn
        if (!isRegistrationOpen()) {
            session.setAttribute("message", "Đã hết hạn đăng ký lịch rảnh cho tuần tới (Hạn chót: 23:59 Thứ 7)!");
            session.setAttribute("messageType", "warning");
            response.sendRedirect("employee-availability");
            return;
        }

        String[] selectedAvailabilities = request.getParameterValues("availabilities");
        List<String[]> pairs = new ArrayList<>();

        if (selectedAvailabilities != null) {
            for (String val : selectedAvailabilities) {
                String[] parts = val.split("_");
                if (parts.length == 2) {
                    pairs.add(parts);
                }
            }
        }

        boolean success = dao.saveAvailabilities(user.getId(), pairs);

        if (success) {
            session.setAttribute("message", "Cập nhật lịch rảnh thành công!");
            session.setAttribute("messageType", "success");
        } else {
            session.setAttribute("message", "Cập nhật thất bại. Vui lòng thử lại!");
            session.setAttribute("messageType", "danger");
        }

        response.sendRedirect("employee-availability");
    }
}