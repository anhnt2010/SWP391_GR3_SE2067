<%-- 
    Document   : approve-open-shift
    Created on : 20 thg 9, 2026, 21:02:59
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Duyệt Đơn Ứng Tuyển Ca Mở - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; }
        .card-custom { border: none; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
    </style>
</head>
<body>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold text-success mb-1"><i class="fa-solid fa-user-check me-2"></i>Duyệt Ca Mở Ứng Tuyển (UC20)</h3>
            <p class="text-muted mb-0">Xem và phê duyệt các nhân viên đăng ký nhận ca mở còn thiếu người.</p>
        </div>
        <a href="dashboard.jsp" class="btn btn-outline-secondary"><i class="fa-solid fa-arrow-left me-1"></i> Dashboard</a>
    </div>

    <!-- Thông báo -->
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show" role="alert">
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% session.removeAttribute("message"); session.removeAttribute("messageType"); %>
    </c:if>

    <!-- Bảng danh sách ứng tuyển -->
    <div class="card card-custom p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-success">
                    <tr>
                        <th>Nhân viên ứng tuyển</th>
                        <th>Ngày làm việc</th>
                        <th>Ca làm việc</th>
                        <th>Thời gian đăng ký</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty applications}">
                            <c:forEach var="app" items="${applications}">
                                <tr>
                                    <td class="fw-bold text-primary">${app.applicantName}</td>
                                    <td><i class="fa-regular fa-calendar me-1"></i>${app.workDate}</td>
                                    <td><span class="badge bg-info text-dark fw-bold">${app.shiftName}</span></td>
                                    <td class="text-muted small">${app.appliedAt}</td>
                                    <td class="text-center">
                                        <form action="approve-open-shift" method="POST" class="d-inline">
                                            <input type="hidden" name="appId" value="${app.id}">
                                            <input type="hidden" name="openShiftId" value="${app.openShiftId}">
                                            <input type="hidden" name="userId" value="${app.userId}">
                                            
                                            <button type="submit" name="action" value="approve" class="btn btn-success btn-sm me-1 fw-bold">
                                                <i class="fa-solid fa-check me-1"></i> Duyệt
                                            </button>
                                            <button type="submit" name="action" value="reject" class="btn btn-outline-danger btn-sm fw-bold" onclick="return confirm('Bạn có chắc muốn từ chối đơn này?');">
                                                <i class="fa-solid fa-xmark me-1"></i> Từ chối
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="text-center py-4 text-muted">
                                    <i class="fa-solid fa-inbox fa-2x mb-2 d-block"></i>
                                    Hiện không có đơn ứng tuyển ca mở nào đang chờ duyệt.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
