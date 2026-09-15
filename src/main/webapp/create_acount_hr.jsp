<%-- 
    Document   : create_acount_hr
    Created on : 15 thg 9, 2026, 21:57:34
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Tài Khoản Nhân Viên | Supermarket HRM</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f4f6f9;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .card-custom {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }
        .form-label {
            font-weight: 600;
            color: #495057;
        }
        .required-star {
            color: #dc3545;
        }
    </style>
</head>
<body>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card card-custom p-4">
                
                <div class="d-flex align-items-center mb-4 border-bottom pb-3">
                    <i class="fa-solid fa-user-plus fa-2x text-primary me-3"></i>
                    <div>
                        <h4 class="mb-0 font-weight-bold">Tạo Tài Khoản Nhân Viên Mới</h4>
                        <small class="text-muted">Hệ thống Quản lý Nhân sự SupermarketHRM</small>
                    </div>
                </div>

                <!-- Hiển thị thông báo thành công nếu có -->
                <% 
                    String successMessage = (String) request.getAttribute("successMessage");
                    if (successMessage != null) {
                %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> <%= successMessage %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>

                <!-- Hiển thị thông báo lỗi nếu có -->
                <% 
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    if (errorMessage != null) {
                %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-triangle-exclamation me-2"></i> <%= errorMessage %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>

                <!-- Form gửi dữ liệu sang Controller qua phương thức POST -->
                <form action="create-account-hr" method="POST">
                    
                    <div class="row g-3">
                        <!-- Mã Nhân Viên -->
                        <div class="col-md-6">
                            <label for="employeeId" class="form-label">Mã Nhân Viên <span class="required-star">*</span></label>
                            <input type="text" class="form-control" id="employeeId" name="employeeId" placeholder="Ví dụ: NV001" required>
                        </div>

                        <!-- Họ và Tên -->
                        <div class="col-md-6">
                            <label for="fullName" class="form-label">Họ và Tên <span class="required-star">*</span></label>
                            <input type="text" class="form-control" id="fullName" name="fullName" placeholder="Nguyễn Văn A" required>
                        </div>

                        <!-- Số Điện Thoại -->
                        <div class="col-md-6">
                            <label for="phone" class="form-label">Số Điện Thoại</label>
                            <input type="tel" class="form-control" id="phone" name="phone" placeholder="0987654321">
                        </div>

                        <!-- Email -->
                        <div class="col-md-6">
                            <label for="email" class="form-label">Email (Tùy chọn)</label>
                            <input type="email" class="form-control" id="email" name="email" placeholder="nguyenvana@gmail.com">
                        </div>

                        <!-- Vai Trò -->
                        <div class="col-md-6">
                            <label for="role" class="form-label">Chức Vụ / Vai Trò <span class="required-star">*</span></label>
                            <select class="form-select" id="role" name="role" required>
                                <option value="Employee" selected>Nhân viên (Employee)</option>
                                <option value="Manager">Quản lý (Manager)</option>
                                <option value="HR">Nhân sự (HR)</option>
                            </select>
                        </div>

                        <!-- Cơ Sở / Chi Nhánh -->
                        <div class="col-md-6">
                            <label for="storeId" class="form-label">Cơ Sở Làm Việc <span class="required-star">*</span></label>
                            <select class="form-select" id="storeId" name="storeId" required>
                                <option value="1" selected>Cơ sở 1 (Mặc định)</option>
                                <option value="2">Cơ sở 2</option>
                                <option value="3">Cơ sở 3</option>
                            </select>
                        </div>

                        <!-- Ngày Hết Hạn Hợp Đồng (Thời Vụ) -->
                        <div class="col-md-12">
                            <label for="expireAt" class="form-label">Ngày Hết Hạn Hợp Đồng (Nếu là thời vụ)</label>
                            <input type="date" class="form-control" id="expireAt" name="expireAt">
                            <div class="form-text">Bỏ trống nếu là nhân viên chính thức (không giới hạn thời gian).</div>
                        </div>
                    </div>

                    <div class="alert alert-info mt-4 d-flex align-items-center mb-0" role="alert">
                        <i class="fa-solid fa-info-circle me-2 fs-5"></i>
                        <div>
                            Mật khẩu mặc định khởi tạo ban đầu sẽ là <strong>123</strong>. Hệ thống sẽ bắt buộc nhân viên đổi mật khẩu ở lần đăng nhập đầu tiên.
                        </div>
                    </div>

                    <!-- Nút Thao Tác -->
                    <div class="d-flex justify-content-end gap-2 mt-4">
                        <button type="reset" class="btn btn-light border px-4">Làm mới</button>
                        <button type="submit" class="btn btn-primary px-4">
                            <i class="fa-solid fa-user-plus me-1"></i> Tạo Tài Khoản
                        </button>
                    </div>

                </form>

            </div>
        </div>
    </div>
</div>

<!-- Bootstrap 5 JS CDN -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>