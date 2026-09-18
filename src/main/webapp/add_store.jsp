<%-- 
    Document   : add_store
    Created on : 16 Sept 2026, 20:03:29
    Author     : nguyn
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Khai báo cơ sở mới - Giám đốc</title>
    <!-- Nhúng Bootstrap để giao diện tự động căn chỉnh đẹp mắt -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5" style="max-width: 600px;">
        <h2 class="mb-4 text-primary">Khai báo Cơ sở / Chi nhánh mới</h2>

        <!-- Khối hiển thị thông báo trả về từ Controller (Thành công hoặc Thất bại) -->
        <% if(request.getAttribute("message") != null) { %>
            <div class="alert alert-success">
                <strong>Thành công!</strong> <%= request.getAttribute("message") %>
            </div>
        <% } %>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <strong>Lỗi!</strong> <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <!-- Khối Form nhập liệu -->
        <div class="card shadow-sm">
            <div class="card-body">
                <!-- action trỏ đúng tên Servlet, method trỏ đúng hàm doPost -->
                <form action="StoreController" method="POST">
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">Tên cơ sở:</label>
                        <!-- Thuộc tính name="txtStoreName" bắt buộc phải khớp với request.getParameter trong Controller -->
                        <input type="text" class="form-control" name="txtStoreName" required placeholder="Ví dụ: Siêu thị Cầu Giấy">
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">Địa chỉ chi tiết:</label>
                        <!-- Thuộc tính name="txtAddress" bắt buộc phải khớp với Controller -->
                        <input type="text" class="form-control" name="txtAddress" required placeholder="Nhập địa chỉ của chi nhánh...">
                    </div>
                    
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">Lưu Cơ Sở</button>
                    </div>
                    
                </form>
            </div>
        </div>
        
    </div>
</body>
</html>
