<%-- 
    Document   : change-password
    Created on : 18 thg 9, 2026, 16:44:30
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    User user = (User) session.getAttribute("account");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đổi Mật Khẩu Lần Đầu</title>
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
        .custom-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 16px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 450px;
            padding: 40px 30px;
        }
        .icon-box {
            width: 60px;
            height: 60px;
            background: #f59e0b;
            color: #ffffff;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            margin: 0 auto 20px auto;
        }
        .form-control {
            border-radius: 10px;
            padding: 12px 16px;
        }
        .btn-warning {
            border-radius: 10px;
            padding: 12px;
            font-weight: 600;
            background-color: #f59e0b;
            border: none;
            color: #fff;
        }
        .btn-warning:hover {
            background-color: #d97706;
            color: #fff;
        }
    </style>
</head>
<body>

<div class="custom-card">
    <div class="icon-box">
        <i class="fa-solid fa-key"></i>
    </div>
    <h4 class="text-center fw-bold text-dark mb-1">Yêu Cầu Đổi Mật Khẩu</h4>
    <p class="text-center text-muted small mb-4">
        Xin chào <strong><%= user.getFullName() %></strong> (<%= user.getUsername() %>). Đây là lần đầu bạn đăng nhập, vui lòng tạo mật khẩu mới để bảo mật tài khoản.
    </p>

    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <small><i class="fa-solid fa-circle-exclamation me-1"></i> <%= request.getAttribute("errorMessage") %></small>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <form action="change-password" method="POST" autocomplete="off">
        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold">Mật khẩu hiện tại</label>
            <input type="password" name="currentPassword" class="form-control" placeholder="••••••••" required>
        </div>

        <div class="mb-3">
            <label class="form-label text-secondary small fw-bold">Mật khẩu mới</label>
            <input type="password" name="newPassword" class="form-control" placeholder="Mật khẩu mới (tối thiểu 6 ký tự)" required minlength="6">
        </div>

        <div class="mb-4">
            <label class="form-label text-secondary small fw-bold">Xác nhận mật khẩu mới</label>
            <input type="password" name="confirmPassword" class="form-control" placeholder="Nhập lại mật khẩu mới" required minlength="6">
        </div>

        <button type="submit" class="btn btn-warning w-100">
            Cập Nhật Mật Khẩu <i class="fa-solid fa-check ms-1"></i>
        </button>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
