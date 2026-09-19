<%-- 
    Document   : dashboard
    Created on : 18 thg 9, 2026, 16:02:27
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
    <title>Dashboard - Hệ Thống Siêu Thị</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark px-4">
    <a class="navbar-brand fw-bold" href="#">SUPERMARKET ERP</a>
    <div class="ms-auto d-flex align-items-center text-white">
        <span class="me-3"><i class="fa-solid fa-circle-user me-1"></i> <%= user.getFullName() %> (<strong class="text-warning"><%= user.getRole().getName() %></strong>)</span>
        <a href="logout" class="btn btn-outline-light btn-sm"><i class="fa-solid fa-right-from-bracket me-1"></i> Đăng xuất</a>
    </div>
</nav>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
    <div class="card p-3 mb-3">
        <h5 class="mb-3">Chức năng khả dụng</h5>
        <div class="d-flex gap-2">
            <!-- Chức năng cho Store Manager (UC12) -->
            <c:if test="${sessionScope.account.role.name == 'STORE_MANAGER'}">
                <a href="${pageContext.request.contextPath}/recruitment-proposal" class="btn btn-outline-primary">
                    <i class="fa-solid fa-file-signature me-1"></i> Đề xuất tuyển dụng (UC12)
                </a>
            </c:if>

            <!-- Chức năng cho HR Manager (UC13) -->
            <c:if test="${sessionScope.account.role.name == 'HR_MANAGER'}">
                <a href="${pageContext.request.contextPath}/recruitment-proposal" class="btn btn-outline-success">
                    <i class="fa-solid fa-tasks me-1"></i> Duyệt đề xuất tuyển dụng (UC13)
                </a>
            </c:if>
        </div>
    </div>
</div>
<div class="container mt-5">
    <div class="card shadow-sm border-0 p-4">
        <h3>Xin chào, <%= user.getFullName() %>!</h3>
        <p class="text-muted">Bạn đã đăng nhập thành công vào hệ thống.</p>
        <hr>
        <div class="row">
            <div class="col-md-4">
                <div class="p-3 bg-primary text-white rounded">
                    <h5>Role hiện tại</h5>
                    <h4><%= user.getRole().getName() %></h4>
                </div>
            </div>
            <div class="col-md-4">
                <div class="p-3 bg-success text-white rounded">
                    <h5>Chi nhánh phụ trách (Scope)</h5>
                    <h4><%= user.getHomeBranchId() > 0 ? "Chi nhánh ID: " + user.getHomeBranchId() : "Toàn hệ thống (Global)" %></h4>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
