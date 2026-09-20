<%-- 
    Document   : employee-availability
    Created on : 19 thg 9, 2026, 15:37:23
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký lịch rảnh - HRMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border: none; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
        .table-schedule th { background-color: #0d6efd; color: white; text-align: center; vertical-align: middle; }
        .table-schedule td { text-align: center; vertical-align: middle; height: 70px; }
        .cell-checkbox { width: 22px; height: 22px; cursor: pointer; }
        .shift-info { font-size: 0.85rem; color: #6c757d; }
    </style>
</head>
<body>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="fw-bold text-primary mb-1"><i class="fa-solid fa-calendar-check me-2"></i>Đăng ký lịch rảnh cá nhân</h3>
            <p class="text-muted mb-0">Tích chọn các ca bạn có thể đi làm trong tuần để Quản lý xếp lịch làm việc.</p>
        </div>
        <a href="dashboard.jsp" class="btn btn-outline-secondary"><i class="fa-solid fa-arrow-left me-1"></i> Dashboard</a>
    </div>

    <!-- Cảnh báo nếu đã quá hạn đăng ký -->
    <c:if test="${!isRegistrationOpen}">
        <div class="alert alert-warning border-warning d-flex align-items-center mb-3" role="alert">
            <i class="fa-solid fa-lock fa-lg me-3 text-warning"></i>
            <div>
                <strong>Hệ thống đang khóa cổng đăng ký!</strong><br>
                Thời hạn đăng ký lịch rảnh cho tuần tới đã kết thúc (Hạn chót: 23:59 Thứ 7). Vui lòng liên hệ Store Manager nếu cần hỗ trợ thay đổi đột xuất.
            </div>
        </div>
    </c:if>

    <!-- Thông báo kết quả thao tác -->
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show" role="alert">
            <i class="fa-solid ${sessionScope.messageType == 'success' ? 'fa-circle-check' : 'fa-triangle-exclamation'} me-2"></i>
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% session.removeAttribute("message"); session.removeAttribute("messageType"); %>
    </c:if>

    <div class="card card-custom p-4">
        <form action="employee-availability" method="POST">
            <div class="table-responsive">
                <table class="table table-bordered table-hover table-schedule align-middle">
                    <thead>
                        <tr>
                            <th style="width: 15%;">Ca làm việc</th>
                            <th>Thứ 2<br><small class="fw-normal">MONDAY</small></th>
                            <th>Thứ 3<br><small class="fw-normal">TUESDAY</small></th>
                            <th>Thứ 4<br><small class="fw-normal">WEDNESDAY</small></th>
                            <th>Thứ 5<br><small class="fw-normal">THURSDAY</small></th>
                            <th>Thứ 6<br><small class="fw-normal">FRIDAY</small></th>
                            <th>Thứ 7<br><small class="fw-normal">SATURDAY</small></th>
                            <th>Chủ Nhật<br><small class="fw-normal">SUNDAY</small></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="shift" items="${shifts}">
                            <tr>
                                <td class="fw-bold bg-light text-start ps-3">
                                    <div>${shift.name}</div>
                                    <div class="shift-info"><i class="fa-regular fa-clock me-1"></i>${shift.startTime} - ${shift.endTime}</div>
                                </td>
                                
                                <c:set var="days" value="MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY" />
                                <c:forEach var="day" items="${days.split(',')}">
                                    <td>
                                        <c:set var="isChecked" value="false" />
                                        <c:forEach var="avail" items="${myAvailabilities}">
                                            <c:if test="${avail.dayOfWeek == day && avail.shiftId == shift.id}">
                                                <c:set var="isChecked" value="true" />
                                            </c:if>
                                        </c:forEach>
                                        
                                        <input class="form-check-input cell-checkbox" type="checkbox" 
                                               name="availabilities" 
                                               value="${day}_${shift.id}" 
                                               ${isChecked ? 'checked' : ''}
                                               ${isRegistrationOpen ? '' : 'disabled'}>
                                    </td>
                                </c:forEach>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="d-flex justify-content-between align-items-center mt-3">
                <span class="text-muted"><i class="fa-solid fa-circle-info me-1"></i> Lịch rảnh sẽ chốt vào 23:59 Thứ 7 hàng tuần.</span>
                <button type="submit" class="btn btn-primary px-4 py-2 fw-semibold" ${isRegistrationOpen ? '' : 'disabled'}>
                    <i class="fa-solid fa-floppy-disk me-1"></i> Lưu thay đổi
                </button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>