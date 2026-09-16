<%-- 
    Document   : approval_request
    Màn hình HR phê duyệt các đề xuất tạo tài khoản do Quản lý gửi lên
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.AccountRequest" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phê Duyệt Đề Xuất | Supermarket HRM</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color:#f4f6f9; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border:none; border-radius:12px; box-shadow:0 4px 20px rgba(0,0,0,.08); }
        .detail-label { font-size:.8rem; color:#6c757d; }
    </style>
</head>
<body>

<%
    List<AccountRequest> list = (List<AccountRequest>) request.getAttribute("requestList");
    String currentStatus = (String) request.getAttribute("currentStatus");
    if (currentStatus == null) currentStatus = "PENDING";
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");
%>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-11">

            <div class="card card-custom p-4">

                <div class="d-flex align-items-center mb-4 border-bottom pb-3">
                    <i class="fa-solid fa-clipboard-check fa-2x text-success me-3"></i>
                    <div class="flex-grow-1">
                        <h4 class="mb-0 fw-bold">Phê Duyệt Đề Xuất Tạo Tài Khoản</h4>
                        <small class="text-muted">Các đề xuất do Quản lý (Manager) gửi lên</small>
                    </div>
                    <% if (pendingCount != null && pendingCount > 0) { %>
                        <span class="badge bg-warning text-dark fs-6">
                            <i class="fa-solid fa-bell me-1"></i> <%= pendingCount %> chờ duyệt
                        </span>
                    <% } %>
                </div>

                <%
                    String successMessage = (String) request.getAttribute("successMessage");
                    if (successMessage != null) {
                %>
                    <div class="alert alert-success alert-dismissible fade show">
                        <i class="fa-solid fa-circle-check me-2"></i> <%= successMessage %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

                <%
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    if (errorMessage != null) {
                %>
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fa-solid fa-triangle-exclamation me-2"></i> <%= errorMessage %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

                <!-- Bộ lọc trạng thái -->
                <ul class="nav nav-pills mb-4">
                    <li class="nav-item">
                        <a class="nav-link <%= "PENDING".equals(currentStatus) ? "active" : "" %>"
                           href="approval-request?status=PENDING">Chờ duyệt</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= "APPROVED".equals(currentStatus) ? "active" : "" %>"
                           href="approval-request?status=APPROVED">Đã duyệt</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= "REJECTED".equals(currentStatus) ? "active" : "" %>"
                           href="approval-request?status=REJECTED">Đã từ chối</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= "ALL".equals(currentStatus) ? "active" : "" %>"
                           href="approval-request?status=ALL">Tất cả</a>
                    </li>
                </ul>

                <%
                    if (list == null || list.isEmpty()) {
                %>
                    <div class="text-center text-muted py-5">
                        <i class="fa-regular fa-folder-open fa-3x mb-3 d-block"></i>
                        Không có đề xuất nào trong mục này.
                    </div>
                <%
                    } else {
                %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Nhân viên đề xuất</th>
                                <th>Vai trò / Cơ sở</th>
                                <th>Người gửi</th>
                                <th>Ngày gửi</th>
                                <th>Trạng thái</th>
                                <th class="text-end">Thao tác</th>
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
                                <td>
                                    <strong><%= r.getFullName() %></strong><br>
                                    <span class="detail-label">
                                        <%= r.getEmployeeId() %>
                                        <%= r.getPhone() != null ? " • " + r.getPhone() : "" %>
                                    </span>
                                </td>
                                <td>
                                    <%= r.getRole() %><br>
                                    <span class="detail-label">Cơ sở <%= r.getStoreId() %></span>
                                </td>
                                <td><%= r.getProposedBy() %></td>
                                <td><%= r.getProposedAt() == null ? "" : r.getProposedAt().toString().substring(0, 16) %></td>
                                <td><span class="badge bg-<%= badge %>"><%= stText %></span></td>
                                <td class="text-end">
                                    <button class="btn btn-sm btn-outline-secondary"
                                            data-bs-toggle="modal" data-bs-target="#detail<%= r.getRequestId() %>">
                                        <i class="fa-solid fa-eye"></i> Chi tiết
                                    </button>

                                    <% if (r.isPending()) { %>
                                        <form action="approval-request" method="POST" class="d-inline"
                                              onsubmit="return confirm('Xác nhận PHÊ DUYỆT và tạo tài khoản <%= r.getEmployeeId() %>?');">
                                            <input type="hidden" name="requestId" value="<%= r.getRequestId() %>">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="status" value="<%= currentStatus %>">
                                            <button type="submit" class="btn btn-sm btn-success">
                                                <i class="fa-solid fa-check"></i> Duyệt
                                            </button>
                                        </form>

                                        <button class="btn btn-sm btn-danger"
                                                data-bs-toggle="modal" data-bs-target="#reject<%= r.getRequestId() %>">
                                            <i class="fa-solid fa-xmark"></i> Từ chối
                                        </button>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>

                <!-- ============ CÁC MODAL ============ -->
                <%
                    for (AccountRequest r : list) {
                %>
                <!-- Modal chi tiết -->
                <div class="modal fade" id="detail<%= r.getRequestId() %>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Chi tiết đề xuất #<%= r.getRequestId() %></h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <div class="row g-3">
                                    <div class="col-6"><div class="detail-label">Mã nhân viên</div><%= r.getEmployeeId() %></div>
                                    <div class="col-6"><div class="detail-label">Họ và tên</div><%= r.getFullName() %></div>
                                    <div class="col-6"><div class="detail-label">Email</div><%= r.getEmail() == null ? "—" : r.getEmail() %></div>
                                    <div class="col-6"><div class="detail-label">Điện thoại</div><%= r.getPhone() == null ? "—" : r.getPhone() %></div>
                                    <div class="col-6"><div class="detail-label">Vai trò</div><%= r.getRole() %></div>
                                    <div class="col-6"><div class="detail-label">Cơ sở</div>Cơ sở <%= r.getStoreId() %></div>
                                    <div class="col-6"><div class="detail-label">Hết hạn hợp đồng</div><%= r.getExpireAt() == null ? "Không giới hạn" : r.getExpireAt() %></div>
                                    <div class="col-6"><div class="detail-label">Người đề xuất</div><%= r.getProposedBy() %></div>
                                    <div class="col-12"><div class="detail-label">Lý do đề xuất</div><%= r.getReason() == null ? "—" : r.getReason() %></div>
                                    <% if (!r.isPending()) { %>
                                    <div class="col-6"><div class="detail-label">Người xử lý</div><%= r.getApprovedBy() == null ? "—" : r.getApprovedBy() %></div>
                                    <div class="col-6"><div class="detail-label">Thời gian xử lý</div><%= r.getApprovedAt() == null ? "—" : r.getApprovedAt() %></div>
                                    <div class="col-12"><div class="detail-label">Ghi chú</div><%= r.getApprovalNote() == null ? "—" : r.getApprovalNote() %></div>
                                    <% } %>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Đóng</button>
                            </div>
                        </div>
                    </div>
                </div>

                <% if (r.isPending()) { %>
                <!-- Modal từ chối -->
                <div class="modal fade" id="reject<%= r.getRequestId() %>" tabindex="-1">
                    <div class="modal-dialog modal-dialog-centered">
                        <form action="approval-request" method="POST">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Từ chối đề xuất #<%= r.getRequestId() %></h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <input type="hidden" name="requestId" value="<%= r.getRequestId() %>">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="status" value="<%= currentStatus %>">
                                    <label class="form-label fw-semibold">Lý do từ chối <span class="text-danger">*</span></label>
                                    <textarea class="form-control" name="note" rows="3" required
                                              placeholder="Ví dụ: Chưa đủ định biên nhân sự cho cơ sở này."></textarea>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Hủy</button>
                                    <button type="submit" class="btn btn-danger">Xác nhận từ chối</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                <% } %>

                <% } // end for modal %>
                <% } // end else %>

            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
