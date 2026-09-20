package controller;

import dao.DepartmentDAO;
import dao.PositionDAO;
import dao.StoreDAO;
import dao.SystemConfigDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import model.Department;
import model.Position;
import model.Store;
import model.User;

/**
 * QUẢN LÝ MASTER DATA: Cơ sở, Phòng ban, Chức vụ, Tham số hệ thống.
 *
 *  GET  /admin/master-data?tab=store|department|position|config
 *  POST /admin/master-data  (entity + action=insert|update|toggle)
 */
@WebServlet(name = "MasterDataController", urlPatterns = {"/admin/master-data"})
public class MasterDataController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String entity = request.getParameter("entity");
        String action = request.getParameter("action");

        try {
            if ("store".equals(entity)) {
                handleStore(request, action);
            } else if ("department".equals(entity)) {
                handleDepartment(request, action);
            } else if ("position".equals(entity)) {
                handlePosition(request, action);
            } else if ("config".equals(entity)) {
                handleConfig(request);
            } else {
                request.setAttribute("errorMessage", "Danh mục không hợp lệ.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Chi tiết lỗi: " + e.getMessage());
        }

        showPage(request, response);
    }

    // ---------------- CƠ SỞ ----------------
    private void handleStore(HttpServletRequest request, String action) throws Exception {
        StoreDAO dao = new StoreDAO();
        int id = parseInt(request.getParameter("id"), 0);

        if ("toggle".equals(action)) {
            Store s = dao.getById(id);
            if (s != null && s.isStatus()) {
                int used = dao.countUsers(id);
                if (used > 0) {
                    request.setAttribute("errorMessage",
                            "Không thể ngừng hoạt động cơ sở này: đang có " + used + " nhân viên trực thuộc.");
                    return;
                }
            }
            dao.toggleStatus(id);
            request.setAttribute("successMessage", "Đã đổi trạng thái cơ sở.");
            return;
        }

        Store s = new Store();
        s.setStoreId(id);
        s.setStoreCode(trim(request.getParameter("code")));
        s.setStoreName(trim(request.getParameter("name")));
        s.setAddress(trim(request.getParameter("address")));
        s.setPhone(trim(request.getParameter("phone")));
        s.setStatus(request.getParameter("status") != null);

        if (dao.isCodeExists(s.getStoreCode(), id)) {
            request.setAttribute("errorMessage", "Mã cơ sở '" + s.getStoreCode() + "' đã tồn tại.");
            return;
        }

        boolean ok = "insert".equals(action) ? dao.insert(s) : dao.update(s);
        request.setAttribute(ok ? "successMessage" : "errorMessage",
                ok ? ("Đã " + ("insert".equals(action) ? "thêm" : "cập nhật") + " cơ sở '" + s.getStoreName() + "'.")
                   : "Thao tác thất bại.");
    }

    // ---------------- PHÒNG BAN ----------------
    private void handleDepartment(HttpServletRequest request, String action) throws Exception {
        DepartmentDAO dao = new DepartmentDAO();
        int id = parseInt(request.getParameter("id"), 0);

        if ("toggle".equals(action)) {
            Department d = dao.getById(id);
            if (d != null && d.isStatus()) {
                int used = dao.countUsers(id);
                if (used > 0) {
                    request.setAttribute("errorMessage",
                            "Không thể ngừng hoạt động phòng ban này: đang có " + used + " nhân viên trực thuộc.");
                    return;
                }
            }
            dao.toggleStatus(id);
            request.setAttribute("successMessage", "Đã đổi trạng thái phòng ban.");
            return;
        }

        Department d = new Department();
        d.setDepartmentId(id);
        d.setDepartmentCode(trim(request.getParameter("code")));
        d.setDepartmentName(trim(request.getParameter("name")));
        d.setDescription(trim(request.getParameter("description")));
        d.setStatus(request.getParameter("status") != null);

        if (dao.isCodeExists(d.getDepartmentCode(), id)) {
            request.setAttribute("errorMessage", "Mã phòng ban '" + d.getDepartmentCode() + "' đã tồn tại.");
            return;
        }

        boolean ok = "insert".equals(action) ? dao.insert(d) : dao.update(d);
        request.setAttribute(ok ? "successMessage" : "errorMessage",
                ok ? ("Đã " + ("insert".equals(action) ? "thêm" : "cập nhật") + " phòng ban '" + d.getDepartmentName() + "'.")
                   : "Thao tác thất bại.");
    }

    // ---------------- CHỨC VỤ ----------------
    private void handlePosition(HttpServletRequest request, String action) throws Exception {
        PositionDAO dao = new PositionDAO();
        int id = parseInt(request.getParameter("id"), 0);

        if ("toggle".equals(action)) {
            Position p = dao.getById(id);
            if (p != null && p.isStatus()) {
                int used = dao.countUsers(id);
                if (used > 0) {
                    request.setAttribute("errorMessage",
                            "Không thể ngừng hoạt động chức vụ này: đang có " + used + " nhân viên đang giữ.");
                    return;
                }
            }
            dao.toggleStatus(id);
            request.setAttribute("successMessage", "Đã đổi trạng thái chức vụ.");
            return;
        }

        Position p = new Position();
        p.setPositionId(id);
        p.setPositionCode(trim(request.getParameter("code")));
        p.setPositionName(trim(request.getParameter("name")));
        p.setStatus(request.getParameter("status") != null);

        String salaryRaw = request.getParameter("baseSalary");
        try {
            p.setBaseSalary(new BigDecimal(
                    (salaryRaw == null || salaryRaw.trim().isEmpty()) ? "0" : salaryRaw.trim()));
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Lương cơ bản phải là một số hợp lệ.");
            return;
        }

        if (p.getBaseSalary().signum() < 0) {
            request.setAttribute("errorMessage", "Lương cơ bản không được là số âm.");
            return;
        }

        if (dao.isCodeExists(p.getPositionCode(), id)) {
            request.setAttribute("errorMessage", "Mã chức vụ '" + p.getPositionCode() + "' đã tồn tại.");
            return;
        }

        boolean ok = "insert".equals(action) ? dao.insert(p) : dao.update(p);
        request.setAttribute(ok ? "successMessage" : "errorMessage",
                ok ? ("Đã " + ("insert".equals(action) ? "thêm" : "cập nhật") + " chức vụ '" + p.getPositionName() + "'.")
                   : "Thao tác thất bại.");
    }

    // ---------------- THAM SỐ HỆ THỐNG ----------------
    private void handleConfig(HttpServletRequest request) throws Exception {
        String key   = request.getParameter("configKey");
        String value = trim(request.getParameter("configValue"));

        if (key == null || value == null) {
            request.setAttribute("errorMessage", "Giá trị cấu hình không được để trống.");
            return;
        }

        boolean ok = new SystemConfigDAO().updateValue(key, value, getCurrentUserId(request));
        request.setAttribute(ok ? "successMessage" : "errorMessage",
                ok ? ("Đã cập nhật tham số " + key + ".") : "Không cập nhật được tham số.");
    }

    // ---------------- Hiển thị ----------------
    private void showPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tab = request.getParameter("tab");
        if (tab == null || tab.trim().isEmpty()) {
            tab = request.getParameter("entity");
        }
        if (tab == null || tab.trim().isEmpty()) {
            tab = "store";
        }

        try {
            request.setAttribute("storeList",      new StoreDAO().getAll(false));
            request.setAttribute("departmentList", new DepartmentDAO().getAll(false));
            request.setAttribute("positionList",   new PositionDAO().getAll(false));
            request.setAttribute("configList",     new SystemConfigDAO().getAll());
            request.setAttribute("activeTab",      tab);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải dữ liệu: " + e.getMessage());
        }

        request.getRequestDispatcher("/master_data.jsp").forward(request, response);
    }

    private String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") instanceof User) {
            return ((User) session.getAttribute("user")).getUsername();
        }
        return "AD001";
    }

    private int parseInt(String s, int def) {
        try {
            return (s == null || s.trim().isEmpty()) ? def : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
