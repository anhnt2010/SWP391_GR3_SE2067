<%-- 
    Document   : view-open-shifts
    Created on : 20 thg 9, 2026, 19:57:35
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh Sách Ca Mở - HRMS</title>
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
            <h3 class="fw-bold text-warning mb-1"><i class="fa-solid fa-bullhorn me-2"></i>Danh Sách Ca Mở (UC19)</h3>
            <p class="text-muted mb-0">Các ca làm việc đang thiếu nhân sự. Đăng ký nhận ca để tăng thêm thu nhập.</p>
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

    <!-- Danh sách Ca Mở dưới dạng thẻ Card -->
    <div class="row g-3">
        <c:choose>
            <c:when test="${not empty openShifts}">
                <c:forEach var="os" items="${openShifts}">
                    <!-- Tìm tên ca làm việc tương ứng -->
                    <c:set var="shiftName" value="" />
                    <c:set var="shiftTime" value="" />
                    <c:forEach var="s" items="${shifts}">
                        <c:if test="${s.id == os.shiftId}">
                            <c:set var="shiftName" value="${s.name}" />
                            <c:set var="shiftTime" value="${s.startTime} - ${s.endTime}" />
                        </c:if>
                    </c:forEach>

                    <div class="col-md-4">
                        <div class="card card-custom p-3 border-start border-warning border-4">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-warning text-dark fw-bold">${shiftName}</span>
                                <small class="text-muted"><i class="fa-regular fa-calendar me-1"></i>${os.workDate}</small>
                            </div>
                            <h5 class="fw-bold mb-1"><i class="fa-regular fa-clock me-1 text-primary"></i>${shiftTime}</h5>
                            <p class="text-muted small mb-3">Cần tuyển thêm: <strong>${os.quantityNeeded}</strong> nhân viên</p>

                            <!-- Check xem nhân viên đã đăng ký ca này chưa -->
                            <c:set var="isApplied" value="false" />
                            <c:forEach var="appId" items="${appliedIds}">
                                <c:if test="${appId == os.id}">
                                    <c:set var="isApplied" value="true" />
                                </c:if>
                            </c:forEach>

                            <form action="view-open-shifts" method="POST">
                                <input type="hidden" name="openShiftId" value="${os.id}">
                                <c:choose>
                                    <c:when test="${isApplied}">
                                        <button type="button" class="btn btn-secondary w-100 fw-bold" disabled>
                                            <i class="fa-solid fa-check-circle me-1"></i> Đã Đăng Ký (Chờ Duyệt)
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="submit" class="btn btn-warning w-100 fw-bold text-dark">
                                            <i class="fa-solid fa-hand-pointer me-1"></i> Đăng Ký Nhận Ca
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </c:when>

            <c:otherwise>
                <div class="col-12 text-center py-5 card card-custom">
                    <i class="fa-solid fa-folder-open text-muted fa-3x mb-3"></i>
                    <h5 class="text-muted">Hiện tại không có ca mở nào cần bổ sung nhân sự!</h5>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>