<%--
    Document : admin_dashboard
    Trang tổng quan khu vực Quản trị hệ thống
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Trị Hệ Thống | Supermarket HRM</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background:#f4f6f9; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border:none; border-radius:12px; box-shadow:0 4px 20px rgba(0,0,0,.08); }
        .stat { border-left:4px solid; }
        .stat .num { font-size:2rem; font-weight:700; line-height:1; }
        .stat .lbl { font-size:.85rem; color:#6c757d; }
        .menu-tile { text-decoration:none; color:inherit; transition:.15s; display:block; height:100%; }
        .menu-tile:hover { transform:translateY(-3px); box-shadow:0 8px 24px rgba(0,0,0,.12); }
    </style>
</head>
<body>

<div class="container py-5">

    <div class="d-flex align-items-center mb-4">
        <i class="fa-solid fa-screwdriver-wrench fa-2x text-dark me-3"></i>
        <div>
            <h3 class="mb-0 fw-bold">Quản Trị Hệ Thống</h3>
            <small class="text-muted"><%= request.getAttribute("companyName") %></small>
        </div>
    </div>

    <% String errorMessage = (String) request.getAttribute("errorMessage");
       if (errorMessage != null) { %>
        <div class="alert alert-danger"><i class="fa-solid fa-triangle-exclamation me-2"></i><%= errorMessage %></div>
    <% } %>

    <!-- Thống kê nhanh -->
    <div class="row g-3 mb-4">
        <div class="col-md-3">
            <div class="card card-custom stat p-3" style="border-left-color:#0d6efd">
                <div class="num text-primary"><%= request.getAttribute("totalUsers") %></div>
                <div class="lbl">Tổng tài khoản</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card card-custom stat p-3" style="border-left-color:#198754">
                <div class="num text-success"><%= request.getAttribute("activeUsers") %></div>
                <div class="lbl">Đang hoạt động</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card card-custom stat p-3" style="border-left-color:#dc3545">
                <div class="num text-danger"><%= request.getAttribute("lockedUsers") %></div>
                <div class="lbl">Đã bị khóa</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card card-custom stat p-3" style="border-left-color:#fd7e14">
                <div class="num text-warning"><%= request.getAttribute("totalStores") %></div>
                <div class="lbl">Cơ sở / Chi nhánh</div>
            </div>
        </div>
    </div>

    <!-- Các chức năng -->
    <div class="row g-3">
        <div class="col-md-4">
            <a href="<%= request.getContextPath() %>/admin/users" class="menu-tile">
                <div class="card card-custom p-4 h-100">
                    <i class="fa-solid fa-users-gear fa-2x text-primary mb-3"></i>
                    <h6 class="fw-bold">Quản trị người dùng</h6>
                    <p class="text-muted small mb-0">
                        Tìm kiếm, sửa hồ sơ, phân quyền, khóa/mở khóa và reset mật khẩu tài khoản.
                    </p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="<%= request.getContextPath() %>/admin/master-data?tab=store" class="menu-tile">
                <div class="card card-custom p-4 h-100">
                    <i class="fa-solid fa-database fa-2x text-success mb-3"></i>
                    <h6 class="fw-bold">Master Data</h6>
                    <p class="text-muted small mb-0">
                        Cơ sở (<%= request.getAttribute("totalStores") %>),
                        Phòng ban (<%= request.getAttribute("totalDepartments") %>),
                        Chức vụ (<%= request.getAttribute("totalPositions") %>).
                    </p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="<%= request.getContextPath() %>/admin/master-data?tab=config" class="menu-tile">
                <div class="card card-custom p-4 h-100">
                    <i class="fa-solid fa-sliders fa-2x text-secondary mb-3"></i>
                    <h6 class="fw-bold">Tham số hệ thống</h6>
                    <p class="text-muted small mb-0">
                        Mật khẩu mặc định, tên đơn vị, giờ công chuẩn, giới hạn đăng nhập sai.
                    </p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="<%= request.getContextPath() %>/create-account-hr" class="menu-tile">
                <div class="card card-custom p-4 h-100">
                    <i class="fa-solid fa-user-plus fa-2x text-info mb-3"></i>
                    <h6 class="fw-bold">Tạo tài khoản</h6>
                    <p class="text-muted small mb-0">Tạo trực tiếp tài khoản nhân viên mới.</p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="<%= request.getContextPath() %>/approval-request" class="menu-tile">
                <div class="card card-custom p-4 h-100">
                    <i class="fa-solid fa-clipboard-check fa-2x text-warning mb-3"></i>
                    <h6 class="fw-bold">Phê duyệt đề xuất</h6>
                    <p class="text-muted small mb-0">Duyệt các đề xuất tạo tài khoản do Quản lý gửi lên.</p>
                </div>
            </a>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
