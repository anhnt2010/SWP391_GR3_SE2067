package controller;

import dao.AccountRequestDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import model.AccountRequest;
import model.User;

/**
 * Màn hình của QUẢN LÝ (Manager): gửi đề xuất tạo tài khoản cho nhân viên mới.
 */
@WebServlet(name = "ProposeAccountController", urlPatterns = {"/propose-account"})
public class ProposeAccountController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadMyRequests(request);
        request.getRequestDispatcher("propose_account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            String employeeId = trim(request.getParameter("employeeId"));
            String fullName   = trim(request.getParameter("fullName"));
            String email      = trim(request.getParameter("email"));
            String phone      = trim(request.getParameter("phone"));
            String role       = trim(request.getParameter("role"));
            String reason     = trim(request.getParameter("reason"));
            String storeIdRaw = request.getParameter("storeId");
            String expireRaw  = request.getParameter("expireAt");

            String proposedBy = getCurrentUserId(request);

            // --- Kiểm tra dữ liệu ---
            if (employeeId == null || fullName == null || role == null) {
                request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ các trường bắt buộc (*).");
                loadMyRequests(request);
                request.getRequestDispatcher("propose_account.jsp").forward(request, response);
                return;
            }

            UserDAO userDAO = new UserDAO();
            AccountRequestDAO requestDAO = new AccountRequestDAO();

            if (userDAO.checkEmployeeIdExists(employeeId)) {
                request.setAttribute("errorMessage",
                        "Mã nhân viên '" + employeeId + "' đã tồn tại trong hệ thống!");
                loadMyRequests(request);
                request.getRequestDispatcher("propose_account.jsp").forward(request, response);
                return;
            }

            if (requestDAO.existsPendingByEmployeeId(employeeId)) {
                request.setAttribute("errorMessage",
                        "Đã có một đề xuất cho mã nhân viên '" + employeeId + "' đang chờ HR duyệt.");
                loadMyRequests(request);
                request.getRequestDispatcher("propose_account.jsp").forward(request, response);
                return;
            }

            int storeId = (storeIdRaw != null && !storeIdRaw.trim().isEmpty())
                    ? Integer.parseInt(storeIdRaw) : 1;

            Date expireAt = null;
            if (expireRaw != null && !expireRaw.trim().isEmpty()) {
                expireAt = Date.valueOf(expireRaw);
            }

            AccountRequest ar = new AccountRequest(
                    employeeId, fullName, email, phone, role, storeId, expireAt, reason, proposedBy);

            if (requestDAO.insertRequest(ar)) {
                request.setAttribute("successMessage",
                        "Đã gửi đề xuất tạo tài khoản cho '" + fullName + "'. Vui lòng chờ HR phê duyệt.");
            } else {
                request.setAttribute("errorMessage", "Không thể lưu đề xuất. Vui lòng thử lại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Chi tiết lỗi: " + e.getMessage());
        }

        loadMyRequests(request);
        request.getRequestDispatcher("propose_account.jsp").forward(request, response);
    }

    /** Nạp danh sách đề xuất để hiển thị lịch sử bên dưới form. */
    private void loadMyRequests(HttpServletRequest request) {
        try {
            List<AccountRequest> list = new AccountRequestDAO().getRequests("ALL");
            request.setAttribute("requestList", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy mã nhân viên của người đang đăng nhập từ session.
     * Nếu bạn chưa làm chức năng đăng nhập thì sẽ tạm dùng "NV001".
     */
    private String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object obj = session.getAttribute("user");
            if (obj instanceof User) {
                return ((User) obj).getEmployeeId();
            }
        }
        return "NV001";
    }

    private String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
