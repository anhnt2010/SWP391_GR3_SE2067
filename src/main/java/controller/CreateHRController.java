package controller;

import dao.UserDAO;
import java.io.IOException;
import java.sql.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

@WebServlet(name = "CreateHRController", urlPatterns = {"/create-account-hr"})
public class CreateHRController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("create_acount_hr.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

        try {
            // 1. Lấy dữ liệu từ Form
            String employeeId = request.getParameter("employeeId");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String role = request.getParameter("role");
            String storeIdRaw = request.getParameter("storeId");
            String expireAtRaw = request.getParameter("expireAt");

            UserDAO userDAO = new UserDAO();

            // 2. Kiểm tra trùng Mã nhân viên
            if (userDAO.checkEmployeeIdExists(employeeId)) {
                request.setAttribute("errorMessage", "Mã nhân viên '" + employeeId + "' đã tồn tại trong hệ thống!");
                request.getRequestDispatcher("create_acount_hr.jsp").forward(request, response);
                return;
            }

            // 3. Xử lý dữ liệu
            String defaultPassword = "123"; 
            int storeId = (storeIdRaw != null && !storeIdRaw.trim().isEmpty()) ? Integer.parseInt(storeIdRaw) : 1;

            Date expireAt = null;
            if (expireAtRaw != null && !expireAtRaw.trim().isEmpty()) {
                expireAt = Date.valueOf(expireAtRaw);
            }

            // 4. Khởi tạo đối tượng User
            User newUser = new User(
                    employeeId,
                    fullName,
                    (email != null && !email.trim().isEmpty()) ? email : null,
                    (phone != null && !phone.trim().isEmpty()) ? phone : null,
                    defaultPassword,
                    role,
                    storeId,
                    true,
                    true,
                    expireAt
            );

            // 5. Thêm vào Database
            boolean isSuccess = userDAO.insertUser(newUser);

            if (isSuccess) {
                request.setAttribute("successMessage", "Tạo tài khoản thành công! Mật khẩu mặc định: " + defaultPassword);
            } else {
                request.setAttribute("errorMessage", "Lỗi: Không thể chèn dữ liệu vào bảng (UserDAO trả về false). Hãy kiểm tra lại kết nối DB!");
            }

        } catch (Exception e) {
            // Bắt toàn bộ ngoại lệ và đẩy thông điệp lỗi chi tiết lên giao diện
            e.printStackTrace();
            request.setAttribute("errorMessage", "Chi tiết lỗi: " + e.getMessage());
        }

        request.getRequestDispatcher("create_acount_hr.jsp").forward(request, response);
    }
}