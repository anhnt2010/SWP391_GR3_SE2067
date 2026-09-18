<%-- 
    Document   : assign_manager
    Created on : 16 Sept 2026, 21:10:10
    Author     : nguyn
--%>

<%@page import="java.util.List"%>
<%@page import="model.Store"%>
<%@page import="model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Phân công Quản lý - Giám đốc</title>
    <!-- Nhúng Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5" style="max-width: 650px;">
        <h2 class="mb-4 text-success">Phân công Quản lý Cơ sở</h2>

        <!-- Khối hiển thị thông báo trả về từ Controller -->
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

        <div class="card shadow-sm">
            <div class="card-body">
                <!-- Giả định Controller xử lý là AssignManagerController -->
                <form action="AssignManagerController" method="POST">
                    
                    <!-- Danh sách Nhân sự thả xuống -->
                    <div class="mb-4">
                        <label class="form-label fw-bold">Chọn nhân sự (Chỉ hiển thị Nhân viên):</label>
                        <!-- Thuộc tính name="employeeId" sẽ được dùng trong hàm doPost -->
                        <select class="form-select" name="employeeId" required>
                            <option value="" disabled selected>-- Vui lòng chọn nhân sự --</option>
                            <% 
                                // Lấy danh sách từ request do Controller đẩy sang
                                List<User> listUsers = (List<User>) request.getAttribute("listUsers");
                                if (listUsers != null) {
                                    for (User u : listUsers) {
                            %>
                                <!-- Thẻ option: value là ID ngầm để gửi đi, phần hiển thị là Tên -->
                                <option value="<%= u.getEmployeeId() %>">
                                    <%= u.getFullName() %> (Mã NV: <%= u.getEmployeeId() %>)
                                </option>
                            <%      } 
                                } 
                            %>
                        </select>
                    </div>

                    <!-- Danh sách Cơ sở thả xuống -->
                    <div class="mb-4">
                        <label class="form-label fw-bold">Chọn chi nhánh / cơ sở:</label>
                        <!-- Thuộc tính name="storeId" sẽ được dùng trong hàm doPost -->
                        <select class="form-select" name="storeId" required>
                            <option value="" disabled selected>-- Vui lòng chọn cơ sở --</option>
                            <% 
                                // Lấy danh sách từ request do Controller đẩy sang
                                List<Store> listStores = (List<Store>) request.getAttribute("listStores");
                                if (listStores != null) {
                                    for (Store s : listStores) {
                            %>
                                <option value="<%= s.getStoreId() %>">
                                    <%= s.getStoreName() %> - <%= s.getAddress() %>
                                </option>
                            <%      } 
                                } 
                            %>
                        </select>
                    </div>
                    
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-success btn-lg">Xác nhận Phân công</button>
                    </div>
                    
                </form>
            </div>
        </div>
    </div>
</body>
</html>