package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

/**
 * Chặn mọi truy cập vào /admin/* nếu người dùng không có vai trò Admin.
 *
 * LƯU Ý: khi bạn CHƯA làm chức năng đăng nhập, hãy để DEV_MODE = true để vẫn
 * vào được màn hình quản trị mà test. Làm xong login thì đổi về false.
 */
@WebFilter(filterName = "AdminAuthFilter", urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {

    private static final boolean DEV_MODE = true;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (DEV_MODE) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (user.getRole() == null || !"Admin".equalsIgnoreCase(user.getRole().getName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Bạn không có quyền truy cập khu vực quản trị hệ thống.");
            return;
        }

        chain.doFilter(req, res);
    }
}
