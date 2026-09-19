<%--
    Document : master_data
    Quản lý danh mục: Cơ sở, Phòng ban, Chức vụ, Tham số hệ thống
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.*" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Master Data | Supermarket HRM</title>
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
    List<Store> storeList = (List<Store>) request.getAttribute("storeList");
    List<Department> departmentList = (List<Department>) request.getAttribute("departmentList");
    List<Position> positionList = (List<Position>) request.getAttribute("positionList");
    List<SystemConfig> configList = (List<SystemConfig>) request.getAttribute("configList");

    String tab = (String) request.getAttribute("activeTab");
    if (tab == null) tab = "store";

    String ctx = request.getContextPath();
    String url = ctx + "/admin/master-data";
%>

<div class="container py-5">

    <div class="d-flex align-items-center mb-4">
        <a href="<%= ctx %>/admin/dashboard" class="btn btn-light border me-3">
            <i class="fa-solid fa-arrow-left"></i>
        </a>
        <i class="fa-solid fa-database fa-2x text-success me-3"></i>
        <div>
            <h4 class="mb-0 fw-bold">Master Data</h4>
            <small class="text-muted">Dữ liệu danh mục dùng chung toàn hệ thống</small>
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

    <ul class="nav nav-pills mb-4">
        <li class="nav-item"><a class="nav-link <%= "store".equals(tab) ? "active" : "" %>"      href="<%= url %>?tab=store">Cơ sở</a></li>
        <li class="nav-item"><a class="nav-link <%= "department".equals(tab) ? "active" : "" %>" href="<%= url %>?tab=department">Phòng ban</a></li>
        <li class="nav-item"><a class="nav-link <%= "position".equals(tab) ? "active" : "" %>"   href="<%= url %>?tab=position">Chức vụ</a></li>
        <li class="nav-item"><a class="nav-link <%= "config".equals(tab) ? "active" : "" %>"     href="<%= url %>?tab=config">Tham số hệ thống</a></li>
    </ul>

<%-- ==================== TAB CƠ SỞ ==================== --%>
<% if ("store".equals(tab)) { %>
    <div class="card card-custom p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0">Danh mục Cơ sở / Chi nhánh</h6>
            <button class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#addStore">
                <i class="fa-solid fa-plus me-1"></i> Thêm cơ sở
            </button>
        </div>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr><th>Mã</th><th>Tên cơ sở</th><th>Địa chỉ</th><th>Điện thoại</th><th>Trạng thái</th><th class="text-end">Thao tác</th></tr>
                </thead>
                <tbody>
                <% if (storeList != null) for (Store s : storeList) { %>
                    <tr>
                        <td><strong><%= s.getStoreCode() %></strong></td>
                        <td><%= s.getStoreName() %></td>
                        <td class="sub"><%= s.getAddress() == null ? "—" : s.getAddress() %></td>
                        <td class="sub"><%= s.getPhone() == null ? "—" : s.getPhone() %></td>
                        <td><span class="badge bg-<%= s.isStatus() ? "success" : "secondary" %>"><%= s.isStatus() ? "Hoạt động" : "Ngừng" %></span></td>
                        <td class="text-end text-nowrap">
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#st_<%= s.getStoreId() %>">
                                <i class="fa-solid fa-pen"></i>
                            </button>
                            <form action="<%= url %>" method="POST" class="d-inline"
                                  onsubmit="return confirm('Đổi trạng thái cơ sở này?');">
                                <input type="hidden" name="entity" value="store">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="id" value="<%= s.getStoreId() %>">
                                <button class="btn btn-sm btn-outline-<%= s.isStatus() ? "danger" : "success" %>">
                                    <i class="fa-solid fa-power-off"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Modal thêm cơ sở -->
    <div class="modal fade" id="addStore" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <form action="<%= url %>" method="POST">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title">Thêm cơ sở mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body">
                        <input type="hidden" name="entity" value="store">
                        <input type="hidden" name="action" value="insert">
                        <div class="mb-3"><label class="form-label fw-semibold">Mã cơ sở <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="code" placeholder="CS04" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Tên cơ sở <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Địa chỉ</label>
                            <input type="text" class="form-control" name="address"></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Điện thoại</label>
                            <input type="tel" class="form-control" name="phone"></div>
                        <div class="form-check"><input class="form-check-input" type="checkbox" name="status" id="ns" checked>
                            <label class="form-check-label" for="ns">Đang hoạt động</label></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-success">Thêm mới</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal sửa cơ sở -->
    <% if (storeList != null) for (Store s : storeList) { %>
    <div class="modal fade" id="st_<%= s.getStoreId() %>" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <form action="<%= url %>" method="POST">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title">Sửa: <%= s.getStoreName() %></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body">
                        <input type="hidden" name="entity" value="store">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="<%= s.getStoreId() %>">
                        <div class="mb-3"><label class="form-label fw-semibold">Mã cơ sở <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="code" value="<%= s.getStoreCode() %>" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Tên cơ sở <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" value="<%= s.getStoreName() %>" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Địa chỉ</label>
                            <input type="text" class="form-control" name="address" value="<%= s.getAddress() == null ? "" : s.getAddress() %>"></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Điện thoại</label>
                            <input type="tel" class="form-control" name="phone" value="<%= s.getPhone() == null ? "" : s.getPhone() %>"></div>
                        <div class="form-check"><input class="form-check-input" type="checkbox" name="status"
                               id="ss<%= s.getStoreId() %>" <%= s.isStatus() ? "checked" : "" %>>
                            <label class="form-check-label" for="ss<%= s.getStoreId() %>">Đang hoạt động</label></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <% } %>
<% } %>

<%-- ==================== TAB PHÒNG BAN ==================== --%>
<% if ("department".equals(tab)) { %>
    <div class="card card-custom p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0">Danh mục Phòng ban / Bộ phận</h6>
            <button class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#addDept">
                <i class="fa-solid fa-plus me-1"></i> Thêm phòng ban
            </button>
        </div>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr><th>Mã</th><th>Tên phòng ban</th><th>Mô tả</th><th>Trạng thái</th><th class="text-end">Thao tác</th></tr>
                </thead>
                <tbody>
                <% if (departmentList != null) for (Department d : departmentList) { %>
                    <tr>
                        <td><strong><%= d.getDepartmentCode() %></strong></td>
                        <td><%= d.getDepartmentName() %></td>
                        <td class="sub"><%= d.getDescription() == null ? "—" : d.getDescription() %></td>
                        <td><span class="badge bg-<%= d.isStatus() ? "success" : "secondary" %>"><%= d.isStatus() ? "Hoạt động" : "Ngừng" %></span></td>
                        <td class="text-end text-nowrap">
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#dp_<%= d.getDepartmentId() %>">
                                <i class="fa-solid fa-pen"></i>
                            </button>
                            <form action="<%= url %>" method="POST" class="d-inline"
                                  onsubmit="return confirm('Đổi trạng thái phòng ban này?');">
                                <input type="hidden" name="entity" value="department">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="id" value="<%= d.getDepartmentId() %>">
                                <button class="btn btn-sm btn-outline-<%= d.isStatus() ? "danger" : "success" %>">
                                    <i class="fa-solid fa-power-off"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <div class="modal fade" id="addDept" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <form action="<%= url %>" method="POST">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title">Thêm phòng ban mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body">
                        <input type="hidden" name="entity" value="department">
                        <input type="hidden" name="action" value="insert">
                        <div class="mb-3"><label class="form-label fw-semibold">Mã phòng ban <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="code" placeholder="PB05" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Tên phòng ban <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Mô tả</label>
                            <textarea class="form-control" name="description" rows="2"></textarea></div>
                        <div class="form-check"><input class="form-check-input" type="checkbox" name="status" id="nd" checked>
                            <label class="form-check-label" for="nd">Đang hoạt động</label></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-success">Thêm mới</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <% if (departmentList != null) for (Department d : departmentList) { %>
    <div class="modal fade" id="dp_<%= d.getDepartmentId() %>" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <form action="<%= url %>" method="POST">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title">Sửa: <%= d.getDepartmentName() %></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body">
                        <input type="hidden" name="entity" value="department">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="<%= d.getDepartmentId() %>">
                        <div class="mb-3"><label class="form-label fw-semibold">Mã phòng ban <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="code" value="<%= d.getDepartmentCode() %>" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Tên phòng ban <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" value="<%= d.getDepartmentName() %>" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Mô tả</label>
                            <textarea class="form-control" name="description" rows="2"><%= d.getDescription() == null ? "" : d.getDescription() %></textarea></div>
                        <div class="form-check"><input class="form-check-input" type="checkbox" name="status"
                               id="ds<%= d.getDepartmentId() %>" <%= d.isStatus() ? "checked" : "" %>>
                            <label class="form-check-label" for="ds<%= d.getDepartmentId() %>">Đang hoạt động</label></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <% } %>
<% } %>

<%-- ==================== TAB CHỨC VỤ ==================== --%>
<% if ("position".equals(tab)) { %>
    <div class="card card-custom p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold mb-0">Danh mục Chức vụ</h6>
            <button class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#addPos">
                <i class="fa-solid fa-plus me-1"></i> Thêm chức vụ
            </button>
        </div>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr><th>Mã</th><th>Tên chức vụ</th><th class="text-end">Lương cơ bản</th><th>Trạng thái</th><th class="text-end">Thao tác</th></tr>
                </thead>
                <tbody>
                <% if (positionList != null) for (Position p : positionList) { %>
                    <tr>
                        <td><strong><%= p.getPositionCode() %></strong></td>
                        <td><%= p.getPositionName() %></td>
                        <td class="text-end"><%= p.getBaseSalaryDisplay() %></td>
                        <td><span class="badge bg-<%= p.isStatus() ? "success" : "secondary" %>"><%= p.isStatus() ? "Hoạt động" : "Ngừng" %></span></td>
                        <td class="text-end text-nowrap">
                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#ps_<%= p.getPositionId() %>">
                                <i class="fa-solid fa-pen"></i>
                            </button>
                            <form action="<%= url %>" method="POST" class="d-inline"
                                  onsubmit="return confirm('Đổi trạng thái chức vụ này?');">
                                <input type="hidden" name="entity" value="position">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="id" value="<%= p.getPositionId() %>">
                                <button class="btn btn-sm btn-outline-<%= p.isStatus() ? "danger" : "success" %>">
                                    <i class="fa-solid fa-power-off"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <div class="modal fade" id="addPos" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <form action="<%= url %>" method="POST">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title">Thêm chức vụ mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body">
                        <input type="hidden" name="entity" value="position">
                        <input type="hidden" name="action" value="insert">
                        <div class="mb-3"><label class="form-label fw-semibold">Mã chức vụ <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="code" placeholder="CV05" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Tên chức vụ <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Lương cơ bản (VNĐ)</label>
                            <input type="number" class="form-control" name="baseSalary" min="0" step="100000" value="0"></div>
                        <div class="form-check"><input class="form-check-input" type="checkbox" name="status" id="np" checked>
                            <label class="form-check-label" for="np">Đang hoạt động</label></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-success">Thêm mới</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <% if (positionList != null) for (Position p : positionList) { %>
    <div class="modal fade" id="ps_<%= p.getPositionId() %>" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <form action="<%= url %>" method="POST">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title">Sửa: <%= p.getPositionName() %></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body">
                        <input type="hidden" name="entity" value="position">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="<%= p.getPositionId() %>">
                        <div class="mb-3"><label class="form-label fw-semibold">Mã chức vụ <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="code" value="<%= p.getPositionCode() %>" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Tên chức vụ <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="name" value="<%= p.getPositionName() %>" required></div>
                        <div class="mb-3"><label class="form-label fw-semibold">Lương cơ bản (VNĐ)</label>
                            <input type="number" class="form-control" name="baseSalary" min="0" step="100000"
                                   value="<%= p.getBaseSalary() == null ? "0" : p.getBaseSalary().toPlainString() %>"></div>
                        <div class="form-check"><input class="form-check-input" type="checkbox" name="status"
                               id="pp<%= p.getPositionId() %>" <%= p.isStatus() ? "checked" : "" %>>
                            <label class="form-check-label" for="pp<%= p.getPositionId() %>">Đang hoạt động</label></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <% } %>
<% } %>

<%-- ==================== TAB THAM SỐ HỆ THỐNG ==================== --%>
<% if ("config".equals(tab)) { %>
    <div class="card card-custom p-4">
        <h6 class="fw-bold mb-3">Tham số cấu hình hệ thống</h6>
        <p class="sub">Các tham số này được toàn hệ thống dùng chung. Thay đổi có hiệu lực ngay.</p>

        <div class="table-responsive">
            <table class="table align-middle">
                <thead class="table-light">
                    <tr><th style="width:25%">Tham số</th><th style="width:30%">Giá trị</th><th>Mô tả</th><th style="width:20%">Cập nhật lần cuối</th><th></th></tr>
                </thead>
                <tbody>
                <% if (configList != null) for (SystemConfig c : configList) { %>
                    <tr>
                        <form action="<%= url %>" method="POST">
                            <input type="hidden" name="entity" value="config">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="configKey" value="<%= c.getConfigKey() %>">
                            <td><code><%= c.getConfigKey() %></code></td>
                            <td><input type="text" class="form-control form-control-sm" name="configValue"
                                       value="<%= c.getConfigValue() %>" required></td>
                            <td class="sub"><%= c.getDescription() == null ? "—" : c.getDescription() %></td>
                            <td class="sub">
                                <%= c.getUpdatedAt() == null ? "Chưa sửa" : c.getUpdatedAt().toString().substring(0, 16) %>
                                <%= c.getUpdatedBy() == null ? "" : " • " + c.getUpdatedBy() %>
                            </td>
                            <td class="text-end">
                                <button type="submit" class="btn btn-sm btn-primary">Lưu</button>
                            </td>
                        </form>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
<% } %>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
