<%-- 
    Document   : recuitment-proposal
    Created on : 18 thg 9, 2026, 20:39:12
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lý Đề xuất Tuyển dụng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">
        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2>Danh sách Đề xuất Tuyển dụng</h2>
                <c:if test="${sessionScope.account.role.name == 'STORE_MANAGER'}">
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createModal">Tạo đề xuất mới</button>
                </c:if>
            </div>

            <table class="table table-bordered table-hover bg-white">
                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Chi nhánh</th>
                        <th>Vị trí</th>
                        <th>Số lượng</th>
                        <th>Lý do</th>
                        <th>Người tạo</th>
                        <th>Trạng thái</th>
                        <th>Ghi chú HR</th>
                            <c:if test="${sessionScope.account.role.name == 'HR_MANAGER'}">
                            <th>Thao tác</th>
                            </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${proposals}">
                        <tr>
                            <td>${p.id}</td>
                            <td>${p.branchName}</td>
                            <td>${p.positionName}</td>
                            <td>${p.quantity}</td>
                            <td>${p.reason}</td>
                            <td>${p.creatorName}</td>
                            <td>
                                <span class="badge ${p.status == 'APPROVED' ? 'bg-success' : (p.status == 'REJECTED' ? 'bg-danger' : 'bg-warning')}">
                                    ${p.status}
                                </span>
                            </td>
                            <td>${p.hrNote}</td>
                            <c:if test="${sessionScope.account.role.name == 'HR_MANAGER'}">
                                <td>
                                    <c:if test="${p.status == 'PENDING'}">
                                        <button class="btn btn-sm btn-success" onclick="openProcessModal(${p.id}, 'APPROVED')">Duyệt</button>
                                        <button class="btn btn-sm btn-danger" onclick="openProcessModal(${p.id}, 'REJECTED')">Từ chối</button>
                                    </c:if>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Modal Tạo Đề Xuất (Store Manager) -->
        <div class="modal fade" id="createModal" tabindex="-1">
            <div class="modal-dialog">
                <form action="recruitment-proposal" method="POST" class="modal-content">
                    <input type="hidden" name="action" value="create">
                    <div class="modal-header">
                        <h5 class="modal-title">Tạo Đề xuất Tuyển dụng</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Vị trí cần tuyển:</label>
                            <select name="positionId" class="form-select" required>
                                <option value="">-- Chọn vị trí --</option>
                                <c:forEach var="pos" items="${positions}">
                                    <option value="${pos.id}">${pos.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Loại hình nhân sự:</label>
                            <select name="employmentType" class="form-select" required>
                                <option value="FULL_TIME">Chính thức (Full-time)</option>
                                <option value="PART_TIME">Thời vụ / Bán thời gian (Part-time)</option>
                            </select>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label fw-bold">Số lượng cần tuyển:</label>
                                <input type="number" name="quantity" class="form-control" min="1" value="1" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label fw-bold">Thời hạn cần nhân sự:</label>
                                <input type="date" name="targetDate" class="form-control" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Lý do đề xuất & Yêu cầu:</label>
                            <textarea name="reason" class="form-control" rows="3" placeholder="Nhập lý do tuyển dụng..." required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">Gửi đề xuất</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal Xử lý Đề Xuất (HR Manager) -->
        <div class="modal fade" id="processModal" tabindex="-1">
            <div class="modal-dialog">
                <form action="recruitment-proposal" method="POST" class="modal-content">
                    <input type="hidden" name="action" value="process">
                    <input type="hidden" name="proposalId" id="modalProposalId">
                    <input type="hidden" name="status" id="modalStatus">
                    <div class="modal-header">
                        <h5 class="modal-title" id="processModalTitle">Xử lý đề xuất</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Ghi chú / Phản hồi:</label>
                            <textarea name="hrNote" class="form-control" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">Xác nhận</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                            function openProcessModal(id, status) {
                                                document.getElementById('modalProposalId').value = id;
                                                document.getElementById('modalStatus').value = status;
                                                document.getElementById('processModalTitle').innerText = (status === 'APPROVED' ? 'Xác nhận Duyệt' : 'Xác nhận Từ chối') + ' Đề xuất #' + id;
                                                new bootstrap.Modal(document.getElementById('processModal')).show();
                                            }
        </script>
    </body>
</html>