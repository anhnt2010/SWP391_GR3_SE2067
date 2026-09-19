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
import java.io.IOException;

/**
 * Trang chủ khu vực Quản trị hệ thống: hiển thị các chỉ số tổng quan
 * và lối vào các chức năng con.
 */
@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int[] userStats = new UserAdminDAO().getUserStats();
            request.setAttribute("totalUsers",    userStats[0]);
            request.setAttribute("activeUsers",   userStats[1]);
            request.setAttribute("lockedUsers",   userStats[2]);

            request.setAttribute("totalStores",      new StoreDAO().getAll(false).size());
            request.setAttribute("totalDepartments", new DepartmentDAO().getAll(false).size());
            request.setAttribute("totalPositions",   new PositionDAO().getAll(false).size());
            request.setAttribute("companyName",
                    new SystemConfigDAO().getValue("COMPANY_NAME", "SupermarketHRM"));

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải dữ liệu: " + e.getMessage());
        }

        request.getRequestDispatcher("/admin_dashboard.jsp").forward(request, response);
    }
}
