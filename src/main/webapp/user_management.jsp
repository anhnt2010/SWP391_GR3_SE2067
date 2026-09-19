<%--
    Document : user_management
    Quản trị người dùng: tìm kiếm, sửa, phân quyền, khóa, reset mật khẩu
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.*" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Trị Người Dùng | Supermarket HRM</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background:#f4f6f9; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border:none; border-radius:12px; box-shadow:0 4px 20px rgba(0,0,0,.08); }
        .sub { font-size:.8rem; color:#6c757d; }
    </style>
</head>
<body>

<%
    List<UserView> userList = (List<UserView>) request.getAttribute("userList");
    List<Store> storeList = (List<Store>) request.getAttribute("storeList");
    List<Department> departmentList = (List<Department>) request.getAttribute("departmentList");
    List<Position> positionList = (List<Position>) request.getAttribute("positionList");
    Map<String, String> roleMap = (Map<String, String>) request.getAttribute("roleMap");
    if (roleMap == null) roleMap = new LinkedHashMap<String, String>();

    String fKeyword = (String) request.getAttribute("fKeyword");
    String fRole    = (String) request.getAttribute("fRole");
    String fStatus  = (String) request.getAttribute("fStatus");
    Integer fStoreId = (Integer) request.getAttribute("fStoreId");
    if (fStoreId == null) fStoreId = 0;

    String ctx = request.getContextPath();
%>

<div class="container-fluid px-4 py-5">

    <div class="d-flex align-items-center mb-4">
        <a href="<%= ctx %>/admin/dashboard" class="btn btn-light border me-3">
            <i class="fa-solid fa-arrow-left"></i>
        </a>
        <i class="fa-solid fa-users-gear fa-2x text-primary me-3"></i>
        <div>
            <h4 class="mb-0 fw-bold">Quản Trị Người Dùng</h4>
            <small class="text-muted">Phân quyền, khóa tài khoản, reset mật khẩu</small>
        </div>
    </div>

    <% String successMessage = (String) request.getAttribute("successMessage");
       if (successMessage != null) { %>
        <div class="alert alert-success alert-dismissible fade show">
            <i class="fa-solid fa-circle-check me-2"></i><%= successMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <% String errorMessage = (String) request.getAttribute("errorMessage");
       if (errorMessage != null) { %>
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="fa-solid fa-triangle-exclamation me-2"></i><%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <!-- Bộ lọc -->
    <div class="card card-custom p-3 mb-4">
        <form action="<%= ctx %>/admin/users" method="GET" class="row g-2 align-items-end">
            <div class="col-md-4">
                <label class="form-label sub mb-1">Từ khóa</label>
                <input type="text" class="form-control" name="keyword"
                       placeholder="Mã NV, họ tên hoặc email"
                       value="<%= fKeyword == null ? "" : fKeyword %>">
            </div>
            <div class="col-md-2">
                <label class="form-label sub mb-1">Vai trò</label>
                <select class="form-select" name="role">
                    <option value="ALL">Tất cả</option>
                    <% for (Map.Entry<String, String> e : roleMap.entrySet()) { %>
                        <option value="<%= e.getKey() %>" <%= e.getKey().equals(fRole) ? "selected" : "" %>>
                            <%= e.getValue() %>
                        </option>
                    <% } %>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label sub mb-1">Cơ sở</label>
                <select class="form-select" name="storeId">
                    <option value="0">Tất cả</option>
                    <% if (storeList != null) for (Store s : storeList) { %>
                        <option value="<%= s.getStoreId() %>" <%= fStoreId == s.getStoreId() ? "selected" : "" %>>
                            <%= s.getStoreName() %>
                        </option>
                    <% } %>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label sub mb-1">Trạng thái</label>
                <select class="form-select" name="status">
                    <option value="ALL">Tất cả</option>
                    <option value="1" <%= "1".equals(fStatus) ? "selected" : "" %>>Đang hoạt động</option>
                    <option value="0" <%= "0".equals(fStatus) ? "selected" : "" %>>Đã khóa</option>
                </select>
            </div>
            <div class="col-md-1">
                <button type="submit" class="btn btn-primary w-100">
                    <i class="fa-solid fa-magnifying-glass"></i>
                </button>
            </div>
        </form>
    </div>

    <!-- Danh sách -->
    <div class="card card-custom p-4">
        <%
            if (userList == null || userList.isEmpty()) {
        %>
            <div class="text-center text-muted py-5">
                <i class="fa-regular fa-folder-open fa-3x mb-3 d-block"></i>
                Không tìm thấy tài khoản nào.
            </div>
        <%
            } else {
        %>
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0">Tìm thấy <%= userList.size() %> tài khoản</h6>
        </div>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Nhân viên</th>
                        <th>Liên hệ</th>
                        <th>Cơ sở / Phòng ban</th>
                        <th>Chức vụ</th>
                        <th>Vai trò</th>
                        <th>Trạng thái</th>
                        <th class="text-end">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    for (UserView u : userList) {
                        String uid = u.getEmployeeId();
                %>
                    <tr>
                        <td>
                            <strong><%= u.getFullName() %></strong><br>
                            <span class="sub"><%= uid %></span>
                        </td>
                        <td class="sub">
                            <%= u.getEmail() == null ? "—" : u.getEmail() %><br>
                            <%= u.getPhone() == null ? "—" : u.getPhone() %>
                        </td>
                        <td>
                            <%= u.getStoreName() == null ? "—" : u.getStoreName() %><br>
                            <span class="sub"><%= u.getDepartmentName() == null ? "—" : u.getDepartmentName() %></span>
                        </td>
                        <td class="sub"><%= u.getPositionName() == null ? "—" : u.getPositionName() %></td>
                        <td>
                            <form action="<%= ctx %>/admin/users" method="POST" class="d-inline">
                                <input type="hidden" name="action" value="role">
                                <input type="hidden" name="employeeId" value="<%= uid %>">
                                <select name="newRole" class="form-select form-select-sm"
                                        style="width:auto; display:inline-block"
                                        onchange="if(confirm('Đổi vai trò của <%= uid %>?')) this.form.submit(); else this.value='<%= u.getRole() %>';">
                                    <% for (Map.Entry<String, String> e : roleMap.entrySet()) { %>
                                        <option value="<%= e.getKey() %>" <%= e.getKey().equals(u.getRole()) ? "selected" : "" %>>
                                            <%= e.getValue() %>
                                        </option>
                                    <% } %>
                                </select>
                            </form>
                        </td>
                        <td>
                            <% if (u.isStatus()) { %>
                                <span class="badge bg-success">Hoạt động</span>
                            <% } else { %>
                                <span class="badge bg-secondary">Đã khóa</span>
                            <% } %>
                            <% if (u.isIsFirstLogin()) { %>
                                <br><span class="badge bg-warning text-dark mt-1">Chưa đổi MK</span>
                            <% } %>
                        </td>
                        <td class="text-end text-nowrap">
                            <button class="btn btn-sm btn-outline-primary"
                                    data-bs-toggle="modal" data-bs-target="#edit_<%= uid %>">
                                <i class="fa-solid fa-pen"></i>
                            </button>

                            <form action="<%= ctx %>/admin/users" method="POST" class="d-inline"
                                  onsubmit="return confirm('Reset mật khẩu của <%= uid %> về mặc định?');">
                                <input type="hidden" name="action" value="reset">
                                <input type="hidden" name="employeeId" value="<%= uid %>">
                                <button type="submit" class="btn btn-sm btn-outline-warning" title="Reset mật khẩu">
                                    <i class="fa-solid fa-key"></i>
                                </button>
                            </form>

                            <form action="<%= ctx %>/admin/users" method="POST" class="d-inline"
                                  onsubmit="return confirm('<%= u.isStatus() ? "KHÓA" : "MỞ KHÓA" %> tài khoản <%= uid %>?');">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="employeeId" value="<%= uid %>">
                                <button type="submit" class="btn btn-sm btn-outline-<%= u.isStatus() ? "danger" : "success" %>">
                                    <i class="fa-solid fa-<%= u.isStatus() ? "lock" : "lock-open" %>"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <!-- ===== Modal sửa hồ sơ ===== -->
        <% for (UserView u : userList) {
               String uid = u.getEmployeeId(); %>
        <div class="modal fade" id="edit_<%= uid %>" tabindex="-1">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <form action="<%= ctx %>/admin/users" method="POST">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Sửa tài khoản: <%= uid %></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="employeeId" value="<%= uid %>">

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" name="fullName"
                                           value="<%= u.getFullName() %>" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Email</label>
                                    <input type="email" class="form-control" name="email"
                                           value="<%= u.getEmail() == null ? "" : u.getEmail() %>">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Số điện thoại</label>
                                    <input type="tel" class="form-control" name="phone"
                                           value="<%= u.getPhone() == null ? "" : u.getPhone() %>">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Vai trò</label>
                                    <select class="form-select" name="role">
                                        <% for (Map.Entry<String, String> e : roleMap.entrySet()) { %>
                                            <option value="<%= e.getKey() %>" <%= e.getKey().equals(u.getRole()) ? "selected" : "" %>>
                                                <%= e.getValue() %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">Cơ sở</label>
                                    <select class="form-select" name="storeId">
                                        <% if (storeList != null) for (Store s : storeList) { %>
                                            <option value="<%= s.getStoreId() %>" <%= s.getStoreId() == u.getStoreId() ? "selected" : "" %>>
                                                <%= s.getStoreName() %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">Phòng ban</label>
                                    <select class="form-select" name="departmentId">
                                        <option value="0">-- Chưa phân --</option>
                                        <% if (departmentList != null) for (Department d : departmentList) { %>
                                            <option value="<%= d.getDepartmentId() %>" <%= d.getDepartmentId() == u.getDepartmentId() ? "selected" : "" %>>
                                                <%= d.getDepartmentName() %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">Chức vụ</label>
                                    <select class="form-select" name="positionId">
                                        <option value="0">-- Chưa phân --</option>
                                        <% if (positionList != null) for (Position p : positionList) { %>
                                            <option value="<%= p.getPositionId() %>" <%= p.getPositionId() == u.getPositionId() ? "selected" : "" %>>
                                                <%= p.getPositionName() %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Ngày hết hạn hợp đồng</label>
                                    <input type="date" class="form-control" name="expireAt"
                                           value="<%= u.getExpireAt() == null ? "" : u.getExpireAt().toString() %>">
                                    <div class="form-text">Bỏ trống nếu là nhân viên chính thức.</div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <% } %>

        <% } %>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
