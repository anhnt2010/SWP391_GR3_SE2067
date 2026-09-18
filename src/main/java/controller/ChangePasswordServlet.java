package controller;

import dao.UserDAO;
import model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login");
            return;
        }
        request.getRequestDispatcher("change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("account");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        UserDAO dao = new UserDAO();

        // 1. Kiểm tra mật khẩu hiện tại có đúng không
        if (dao.checkLogin(user.getUsername(), currentPassword) == null) {
            request.setAttribute("errorMessage", "Mật khẩu hiện tại không đúng!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        // 2. Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Xác nhận mật khẩu mới không khớp!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
            return;
        }

        // 3. Tiến hành cập nhật
        boolean isUpdated = dao.updatePassword(user.getId(), newPassword);
        if (isUpdated) {
            // Cập nhật lại trạng thái isFirstLogin trong Session
            user.setFirstLogin(false);
            session.setAttribute("account", user);
            response.sendRedirect("dashboard.jsp");
        } else {
            request.setAttribute("errorMessage", "Đã có lỗi xảy ra, vui lòng thử lại!");
            request.getRequestDispatcher("change-password.jsp").forward(request, response);
        }
    }
}