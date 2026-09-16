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
import java.util.List;
import model.AccountRequest;
import model.User;

/**
 * Màn hình của HR: xem và PHÊ DUYỆT / TỪ CHỐI các đề xuất do Quản lý gửi lên.
 *
 *  GET  /approval-request?status=PENDING  -> hiển thị danh sách
 *  POST /approval-request (action=approve|reject) -> xử lý phê duyệt
 */
@WebServlet(name = "ApprovalController", urlPatterns = {"/approval-request"})
public class ApprovalController extends HttpServlet {

    /** Mật khẩu mặc định khi tài khoản được duyệt và tạo mới. */
    private static final String DEFAULT_PASSWORD = "123";

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
        String idRaw  = request.getParameter("requestId");
        String note   = request.getParameter("note");
        String approver = getCurrentUserId(request);

        try {
            int requestId = Integer.parseInt(idRaw);
            AccountRequestDAO requestDAO = new AccountRequestDAO();
            AccountRequest ar = requestDAO.getById(requestId);

            if (ar == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đề xuất #" + requestId);
            } else if (!ar.isPending()) {
                request.setAttribute("errorMessage",
                        "Đề xuất #" + requestId + " đã được xử lý trước đó (trạng thái: " + ar.getStatus() + ").");
            } else if ("approve".equalsIgnoreCase(action)) {
                approve(request, ar, approver, note, requestDAO);
            } else if ("reject".equalsIgnoreCase(action)) {
                reject(request, ar, approver, note, requestDAO);
            } else {
                request.setAttribute("errorMessage", "Hành động không hợp lệ.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Mã đề xuất không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Chi tiết lỗi: " + e.getMessage());
        }

        showList(request, response);
    }

    /** PHÊ DUYỆT: tạo tài khoản thật trong bảng user rồi cập nhật trạng thái đề xuất. */
    private void approve(HttpServletRequest request, AccountRequest ar, String approver,
                         String note, AccountRequestDAO requestDAO) throws Exception {

        UserDAO userDAO = new UserDAO();

        // Kiểm tra lại: có thể HR đã tạo tay tài khoản này trong lúc chờ duyệt
        if (userDAO.checkEmployeeIdExists(ar.getEmployeeId())) {
            request.setAttribute("errorMessage",
                    "Không thể duyệt: mã nhân viên '" + ar.getEmployeeId() + "' đã tồn tại trong bảng User.");
            return;
        }

        User newUser = new User(
                ar.getEmployeeId(),
                ar.getFullName(),
                ar.getEmail(),
                ar.getPhone(),
                DEFAULT_PASSWORD,
                ar.getRole(),
                ar.getStoreId(),
                true,   // isFirstLogin -> bắt buộc đổi mật khẩu lần đầu
                true,   // status -> đang hoạt động
                ar.getExpireAt()
        );

        if (!userDAO.insertUser(newUser)) {
            request.setAttribute("errorMessage", "Duyệt thất bại: không tạo được tài khoản trong bảng User.");
            return;
        }

        boolean updated = requestDAO.updateStatus(
                ar.getRequestId(), AccountRequest.STATUS_APPROVED, approver, note);

        if (updated) {
            request.setAttribute("successMessage",
                    "Đã phê duyệt đề xuất #" + ar.getRequestId() + ". Tài khoản '" + ar.getEmployeeId()
                    + "' đã được tạo với mật khẩu mặc định: " + DEFAULT_PASSWORD);
        } else {
            request.setAttribute("errorMessage",
                    "Tài khoản đã được tạo nhưng không cập nhật được trạng thái đề xuất. "
                    + "Có thể đề xuất vừa được người khác xử lý.");
        }
    }

    /** TỪ CHỐI: chỉ cập nhật trạng thái, không tạo tài khoản. */
    private void reject(HttpServletRequest request, AccountRequest ar, String approver,
                        String note, AccountRequestDAO requestDAO) throws Exception {

        if (note == null || note.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập lý do khi từ chối đề xuất.");
            return;
        }

        boolean updated = requestDAO.updateStatus(
                ar.getRequestId(), AccountRequest.STATUS_REJECTED, approver, note.trim());

        if (updated) {
            request.setAttribute("successMessage", "Đã từ chối đề xuất #" + ar.getRequestId() + ".");
        } else {
            request.setAttribute("errorMessage", "Không cập nhật được trạng thái đề xuất.");
        }
    }

    /** Nạp danh sách theo bộ lọc trạng thái và chuyển sang JSP. */
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String status = request.getParameter("status");
        if (status == null || status.trim().isEmpty()) {
            status = "PENDING";
        }

        try {
            AccountRequestDAO dao = new AccountRequestDAO();
            List<AccountRequest> list = dao.getRequests(status);
            request.setAttribute("requestList", list);
            request.setAttribute("currentStatus", status);
            request.setAttribute("pendingCount", dao.countPending());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải danh sách: " + e.getMessage());
        }

        request.getRequestDispatcher("approval_request.jsp").forward(request, response);
    }

    private String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object obj = session.getAttribute("user");
            if (obj instanceof User) {
                return ((User) obj).getEmployeeId();
            }
        }
        return "HR001";
    }
}
