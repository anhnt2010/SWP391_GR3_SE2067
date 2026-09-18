<%-- 
    Document   : login
    Created on : 18 thg 9, 2026, 16:01:44
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Hệ Thống Quản Lý Chuỗi Siêu Thị</title>
    <!-- Bootstrap 5 CSS & FontAwesome Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .login-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 16px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 420px;
            padding: 40px 30px;
        }
        .brand-icon {
            width: 60px;
            height: 60px;
            background: #2563eb;
            color: #ffffff;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            margin: 0 auto 20px auto;
            box-shadow: 0 8px 16px rgba(37, 99, 235, 0.3);
        }
        .form-control {
            border-radius: 10px;
            padding: 12px 16px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }
        .form-control:focus {
            box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.15);
            border-color: #2563eb;
        }
        .btn-primary {
            background: #2563eb;
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-weight: 600;
            font-size: 15px;
            transition: all 0.2s ease;
        }
        .btn-primary:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }
    </style>
</head>
<body>

<div class="login-card">
    <div class="brand-icon">
        <i class="fa-solid fa-store"></i>
    </div>
    <h4 class="text-center fw-bold text-dark mb-1">Siêu Thị Vận Hành</h4>
    <p class="text-center text-muted small mb-4">Hệ thống quản trị & phân quyền nhân sự</p>

    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center" role="alert">
            <i class="fa-solid fa-circle-exclamation me-2"></i>
            <small><%= request.getAttribute("errorMessage") %></small>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Bổ sung autocomplete="off" vào thẻ form -->
<form action="login" method="POST" autocomplete="off">
    <!-- Mẹo: Thêm 2 input ẩn này để "lừa" tính năng autofill của trình duyệt -->
    <input type="text" style="display:none">
    <input type="password" style="display:none">

    <div class="mb-3">
        <label class="form-label text-secondary small fw-bold">Mã nhân viên</label>
        <div class="input-group">
            <span class="input-group-text bg-light border-end-0 rounded-start-3">
                <i class="fa-regular fa-user text-muted"></i>
            </span>
            <!-- Bổ sung autocomplete="new-username" -->
            <input type="text" name="username" class="form-control border-start-0" 
                   placeholder="Nhập mã nhân viên (VD: NV001)" required autocomplete="new-username">
        </div>
    </div>

    <div class="mb-4">
        <label class="form-label text-secondary small fw-bold">Mật khẩu</label>
        <div class="input-group">
            <span class="input-group-text bg-light border-end-0 rounded-start-3">
                <i class="fa-solid fa-lock text-muted"></i>
            </span>
            <!-- Bổ sung autocomplete="new-password" -->
            <input type="password" name="password" class="form-control border-start-0" 
                   placeholder="••••••••" required autocomplete="new-password">
        </div>
    </div>

    <button type="submit" class="btn btn-primary w-100 mb-3">
        Đăng Nhập <i class="fa-solid fa-arrow-right ms-2"></i>
    </button>
</form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>