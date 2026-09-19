package controller;

import dao.UserDAO;
import model.User;
import java.io.IOException;
import java.sql.Timestamp;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username"); // Mã nhân viên
        String pass = request.getParameter("password");

        UserDAO dao = new UserDAO();
        User account = dao.checkLogin(username, pass);

        if (account == null) {
            request.setAttribute("errorMessage", "Mã nhân viên hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // 1. Kiểm tra tài khoản bị khóa
        if ("EMERGENCY_LOCKED".equals(account.getStatus()) || "INACTIVE".equals(account.getStatus())) {
            request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // 2. Kiểm tra hạn tài khoản nhân sự thời vụ
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (account.getExpirationDate() != null && account.getExpirationDate().before(now)) {
            request.setAttribute("errorMessage", "Tài khoản thời vụ của bạn đã hết hạn truy cập!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // 3. au khi kiểm tra trSạng thái tài khoản active & chưa hết hạn ...
        HttpSession session = request.getSession();
        session.setAttribute("account", account);

        // Nếu là lần đầu đăng nhập -> Bắt buộc chuyển hướng sang trang đổi mật khẩu (UC03)
        if (account.isFirstLogin()) {
            response.sendRedirect("change-password.jsp?firstLogin=true");
        } else {
            response.sendRedirect("dashboard.jsp");
        }
    }
}
