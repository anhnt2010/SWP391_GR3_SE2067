package controller;

import dao.DepartmentDAO;
import dao.PositionDAO;
import dao.StoreDAO;
import dao.SystemConfigDAO;
import dao.UserAdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import model.User;
import model.UserView;

/**
 * QUẢN TRỊ NGƯỜI DÙNG: tìm kiếm, sửa hồ sơ, phân quyền,
 * khóa/mở khóa tài khoản, reset mật khẩu.
 *
 *  GET  /admin/users                       -> danh sách + bộ lọc
 *  POST /admin/users (action=update|toggle|reset|role)
 */
@WebServlet(name = "UserManagementController", urlPatterns = {"/admin/users"})
public class UserManagementController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String employeeId = request.getParameter("employeeId");

        try {
            UserAdminDAO dao = new UserAdminDAO();

            if ("update".equals(action)) {
                doUpdate(request, dao, employeeId);

            } else if ("toggle".equals(action)) {
                doToggle(request, dao, employeeId);

            } else if ("reset".equals(action)) {
                String defaultPwd = new SystemConfigDAO().getValue("DEFAULT_PASSWORD", "123");
                if (dao.resetPassword(employeeId, defaultPwd)) {
                    request.setAttribute("successMessage",
                            "Đã reset mật khẩu của '" + employeeId + "' về: " + defaultPwd
                            + ". Người dùng phải đổi mật khẩu ở lần đăng nhập kế tiếp.");
                } else {
                    request.setAttribute("errorMessage", "Không reset được mật khẩu.");
                }

            } else if ("role".equals(action)) {
                doChangeRole(request, dao, employeeId);

            } else {
                request.setAttribute("errorMessage", "Hành động không hợp lệ.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Chi tiết lỗi: " + e.getMessage());
        }

        showList(request, response);
    }

    private void doUpdate(HttpServletRequest request, UserAdminDAO dao, String employeeId) throws Exception {
        UserView u = new UserView();
        u.setEmployeeId(employeeId);
        u.setFullName(request.getParameter("fullName"));
        u.setEmail(blankToNull(request.getParameter("email")));
        u.setPhone(blankToNull(request.getParameter("phone")));
        u.setRole(request.getParameter("role"));
        u.setStoreId(parseInt(request.getParameter("storeId"), 1));
        u.setDepartmentId(parseInt(request.getParameter("departmentId"), 0));
        u.setPositionId(parseInt(request.getParameter("positionId"), 0));

        String expireRaw = request.getParameter("expireAt");
        if (expireRaw != null && !expireRaw.trim().isEmpty()) {
            u.setExpireAt(Date.valueOf(expireRaw));
        }

        // Không cho hạ quyền Admin cuối cùng
        UserView old = dao.getByEmployeeId(employeeId);
        if (old != null && "Admin".equalsIgnoreCase(old.getRole())
                && !"Admin".equalsIgnoreCase(u.getRole())
                && dao.countActiveAdmins() <= 1) {
            request.setAttribute("errorMessage",
                    "Không thể hạ quyền tài khoản Admin cuối cùng của hệ thống.");
            return;
        }

        if (dao.updateUser(u)) {
            request.setAttribute("successMessage", "Đã cập nhật tài khoản '" + employeeId + "'.");
        } else {
            request.setAttribute("errorMessage", "Không cập nhật được tài khoản.");
        }
    }

    private void doToggle(HttpServletRequest request, UserAdminDAO dao, String employeeId) throws Exception {
        UserView u = dao.getByEmployeeId(employeeId);
        if (u == null) {
            request.setAttribute("errorMessage", "Không tìm thấy tài khoản.");
            return;
        }

        // Không cho tự khóa chính mình
        if (employeeId.equals(getCurrentUserId(request))) {
            request.setAttribute("errorMessage", "Bạn không thể tự khóa tài khoản của chính mình.");
            return;
        }

        if (u.isStatus() && "Admin".equalsIgnoreCase(u.getRole()) && dao.countActiveAdmins() <= 1) {
            request.setAttribute("errorMessage", "Không thể khóa tài khoản Admin cuối cùng của hệ thống.");
            return;
        }

        if (dao.toggleStatus(employeeId)) {
            request.setAttribute("successMessage",
                    "Đã " + (u.isStatus() ? "KHÓA" : "MỞ KHÓA") + " tài khoản '" + employeeId + "'.");
        } else {
            request.setAttribute("errorMessage", "Không đổi được trạng thái tài khoản.");
        }
    }

    private void doChangeRole(HttpServletRequest request, UserAdminDAO dao, String employeeId) throws Exception {
        String newRole = request.getParameter("newRole");
        UserView u = dao.getByEmployeeId(employeeId);

        if (u != null && "Admin".equalsIgnoreCase(u.getRole())
                && !"Admin".equalsIgnoreCase(newRole)
                && dao.countActiveAdmins() <= 1) {
            request.setAttribute("errorMessage", "Không thể hạ quyền tài khoản Admin cuối cùng.");
            return;
        }

        if (dao.changeRole(employeeId, newRole)) {
            request.setAttribute("successMessage",
                    "Đã đổi vai trò của '" + employeeId + "' thành " + newRole + ".");
        } else {
            request.setAttribute("errorMessage", "Không đổi được vai trò.");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String role    = request.getParameter("role");
        String status  = request.getParameter("status");
        int storeId    = parseInt(request.getParameter("storeId"), 0);

        try {
            request.setAttribute("userList",
                    new UserAdminDAO().search(keyword, role, storeId, status));

            // dữ liệu cho các dropdown
            request.setAttribute("storeList",      new StoreDAO().getAll(true));
            request.setAttribute("departmentList", new DepartmentDAO().getAll(true));
            request.setAttribute("positionList",   new PositionDAO().getAll(true));
            request.setAttribute("roleMap",        new SystemConfigDAO().getRoles());

            // giữ lại giá trị bộ lọc trên form
            request.setAttribute("fKeyword", keyword);
            request.setAttribute("fRole",    role);
            request.setAttribute("fStatus",  status);
            request.setAttribute("fStoreId", storeId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải danh sách: " + e.getMessage());
        }

        request.getRequestDispatcher("/user_management.jsp").forward(request, response);
    }

    private String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") instanceof User) {
            return ((User) session.getAttribute("user")).getEmployeeId();
        }
        return null;
    }

    private int parseInt(String s, int def) {
        try {
            return (s == null || s.trim().isEmpty()) ? def : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String blankToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
