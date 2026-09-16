<%-- 
    Document   : propose_account
    Màn hình QUẢN LÝ gửi đề xuất tạo tài khoản
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.AccountRequest" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đề Xuất Tạo Tài Khoản | Supermarket HRM</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color:#f4f6f9; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border:none; border-radius:12px; box-shadow:0 4px 20px rgba(0,0,0,.08); }
        .form-label { font-weight:600; color:#495057; }
        .required-star { color:#dc3545; }
    </style>
</head>
<body>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-9">

            <div class="card card-custom p-4 mb-4">

                <div class="d-flex align-items-center mb-4 border-bottom pb-3">
                    <i class="fa-solid fa-paper-plane fa-2x text-warning me-3"></i>
                    <div>
                        <h4 class="mb-0 fw-bold">Đề Xuất Tạo Tài Khoản Nhân Viên</h4>
                        <small class="text-muted">Đề xuất sẽ được gửi tới bộ phận Nhân sự (HR) để phê duyệt</small>
                    </div>
                </div>

                <%
                    String successMessage = (String) request.getAttribute("successMessage");
                    if (successMessage != null) {
                %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> <%= successMessage %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

                <%
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    if (errorMessage != null) {
                %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-triangle-exclamation me-2"></i> <%= errorMessage %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

                <form action="propose-account" method="POST">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="employeeId" class="form-label">Mã Nhân Viên <span class="required-star">*</span></label>
                            <input type="text" class="form-control" id="employeeId" name="employeeId" placeholder="Ví dụ: NV010" required>
                        </div>

                        <div class="col-md-6">
                            <label for="fullName" class="form-label">Họ và Tên <span class="required-star">*</span></label>
                            <input type="text" class="form-control" id="fullName" name="fullName" placeholder="Nguyễn Văn A" required>
                        </div>

                        <div class="col-md-6">
                            <label for="phone" class="form-label">Số Điện Thoại</label>
                            <input type="tel" class="form-control" id="phone" name="phone" placeholder="0987654321">
                        </div>

                        <div class="col-md-6">
                            <label for="email" class="form-label">Email (Tùy chọn)</label>
                            <input type="email" class="form-control" id="email" name="email" placeholder="nguyenvana@gmail.com">
                        </div>

                        <div class="col-md-6">
                            <label for="role" class="form-label">Chức Vụ / Vai Trò <span class="required-star">*</span></label>
                            <select class="form-select" id="role" name="role" required>
                                <option value="Employee" selected>Nhân viên (Employee)</option>
                                <option value="Manager">Quản lý (Manager)</option>
                            </select>
                        </div>

                        <div class="col-md-6">
                            <label for="storeId" class="form-label">Cơ Sở Làm Việc <span class="required-star">*</span></label>
                            <select class="form-select" id="storeId" name="storeId" required>
                                <option value="1" selected>Cơ sở 1</option>
                                <option value="2">Cơ sở 2</option>
                                <option value="3">Cơ sở 3</option>
                            </select>
                        </div>

                        <div class="col-md-6">
                            <label for="expireAt" class="form-label">Ngày Hết Hạn Hợp Đồng</label>
                            <input type="date" class="form-control" id="expireAt" name="expireAt">
                            <div class="form-text">Bỏ trống nếu là nhân viên chính thức.</div>
                        </div>

                        <div class="col-md-6">
                            <label for="reason" class="form-label">Lý Do Đề Xuất <span class="required-star">*</span></label>
                            <textarea class="form-control" id="reason" name="reason" rows="1"
                                      placeholder="Bổ sung nhân sự quầy thu ngân ca tối..." required></textarea>
                        </div>
                    </div>

                    <div class="d-flex justify-content-end gap-2 mt-4">
                        <a href="approval-request" class="btn btn-light border px-4">Xem trang duyệt (HR)</a>
                        <button type="reset" class="btn btn-light border px-4">Làm mới</button>
                        <button type="submit" class="btn btn-warning px-4 text-dark fw-semibold">
                            <i class="fa-solid fa-paper-plane me-1"></i> Gửi Đề Xuất
                        </button>
                    </div>
                </form>
            </div>

            <!-- Lịch sử đề xuất -->
            <div class="card card-custom p-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-clock-rotate-left me-2 text-secondary"></i>Lịch Sử Đề Xuất</h5>

                <%
                    List<AccountRequest> list = (List<AccountRequest>) request.getAttribute("requestList");
                    if (list == null || list.isEmpty()) {
                %>
                    <p class="text-muted mb-0">Chưa có đề xuất nào.</p>
                <%
                    } else {
                %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Mã NV</th>
                                <th>Họ tên</th>
                                <th>Vai trò</th>
                                <th>Ngày gửi</th>
                                <th>Trạng thái</th>
                                <th>Ghi chú của HR</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            for (AccountRequest r : list) {
                                String st = r.getStatus();
                                String badge = "APPROVED".equals(st) ? "success"
                                             : "REJECTED".equals(st) ? "danger" : "warning text-dark";
                                String stText = "APPROVED".equals(st) ? "Đã duyệt"
                                              : "REJECTED".equals(st) ? "Từ chối" : "Chờ duyệt";
                        %>
                            <tr>
                                <td><%= r.getRequestId() %></td>
                                <td><strong><%= r.getEmployeeId() %></strong></td>
                                <td><%= r.getFullName() %></td>
                                <td><%= r.getRole() %></td>
                                <td><%= r.getProposedAt() == null ? "" : r.getProposedAt().toString().substring(0, 16) %></td>
                                <td><span class="badge bg-<%= badge %>"><%= stText %></span></td>
                                <td class="text-muted small"><%= r.getApprovalNote() == null ? "—" : r.getApprovalNote() %></td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
