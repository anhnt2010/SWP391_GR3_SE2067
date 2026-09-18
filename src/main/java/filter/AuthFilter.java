package filter;

import model.User;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Bỏ qua kiểm tra đối với trang login và các tài nguyên tĩnh (CSS, JS, Images)
        boolean isLoginRoute = path.endsWith("login") || path.endsWith("login.jsp");
        boolean isStaticResource = path.contains("/assets/") || path.endsWith(".css") || path.endsWith(".js");

        boolean isLoggedIn = (session != null && session.getAttribute("account") != null);

        if (isLoggedIn || isLoginRoute || isStaticResource) {
            chain.doFilter(request, response); // Cho phép đi tiếp
        } else {
            res.sendRedirect(req.getContextPath() + "/login"); // Bắt quay về đăng nhập
        }
    }
}