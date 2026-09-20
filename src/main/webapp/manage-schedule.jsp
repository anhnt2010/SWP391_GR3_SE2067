<%-- 
    Document   : manage-schedule
    Created on : 19 thg 9, 2026, 18:32:41
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tạo & Quản lý Lịch Tuần - HRMS</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f4f6f9;
            }
            .card-custom {
                border: none;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.05);
            }
            .table-schedule th {
                background-color: #198754;
                color: white;
                text-align: center;
            }
            .table-schedule td {
                text-align: center;
                vertical-align: top;
            }
            .avail-badge {
                font-size: 0.75rem;
                display: block;
                margin-bottom: 4px;
            }
        </style>
    </head>
    <body>

        <div class="container-fluid py-4 px-4">
            <!-- Header trang -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold text-success mb-1"><i class="fa-solid fa-calendar-days me-2"></i>Lập Lịch Làm Việc Tuần (UC16/17)</h3>
                    <p class="text-muted mb-0">Phân công ca làm cho nhân viên thuộc cơ sở. Đăng ca mở khi thiếu nhân sự (UC18).</p>
                </div>
                <div>
                    <!-- Nút Tạo Ca Mở (UC18) đặt lên đây cho thuận tiện thao tác -->
                    <button type="button" class="btn btn-warning fw-semibold me-2" data-bs-toggle="modal" data-bs-target="#openShiftModal">
                        <i class="fa-solid fa-plus-circle me-1"></i> Tạo Ca Mở (UC18)
                    </button>
                    <a href="dashboard.jsp" class="btn btn-outline-secondary"><i class="fa-solid fa-arrow-left me-1"></i> Dashboard</a>
                </div>
            </div>

            <!-- Thông báo -->
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show" role="alert">
                    ${sessionScope.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% session.removeAttribute("message"); session.removeAttribute("messageType"); %>
            </c:if>

            <!-- Bộ chọn tuần -->
            <div class="card card-custom p-3 mb-4">
                <form action="manage-schedule" method="GET" class="row align-items-center g-3">
                    <div class="col-auto">
                        <label class="fw-bold me-2">Chọn ngày bắt đầu tuần (Thứ 2):</label>
                    </div>
                    <div class="col-auto">
                        <input type="date" name="weekStart" class="form-control" value="${startOfWeek}" onchange="this.form.submit()">
                    </div>
                    <div class="col-auto text-muted">
                        (Tuần từ <strong>${startOfWeek}</strong> đến <strong>${endOfWeek}</strong>)
                    </div>
                </form>
            </div>

            <!-- Ma trận xếp lịch -->
            <div class="card card-custom p-4">
                <form action="manage-schedule" method="POST">
                    <input type="hidden" name="weekStart" value="${startOfWeek}">

                    <div class="table-responsive">
                        <table class="table table-bordered align-middle table-schedule">
                            <thead>
                                <tr>
                                    <th style="width: 15%;">Nhân viên</th>
                                    <c:set var="dayNames" value="Thứ 2,Thứ 3,Thứ 4,Thứ 5,Thứ 6,Thứ 7,Chủ Nhật" />
                                    <c:forEach var="i" begin="0" end="6">
                                        <th>
                                            ${dayNames.split(',')[i]}<br>
                                            <small class="fw-normal">${startOfWeek.plusDays(i)}</small>
                                        </th>
                                    </c:forEach>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="emp" items="${employees}">
                                    <tr>
                                        <td class="fw-bold text-start bg-light ps-3">
                                            <div>${emp.fullName}</div>
                                            <small class="text-muted">ID: ${emp.id}</small>
                                        </td>

                                        <c:forEach var="i" begin="0" end="6">
                                            <c:set var="currentDate" value="${startOfWeek.plusDays(i)}" />
                                            <c:set var="dayEnum" value="${currentDate.getDayOfWeek().toString()}" />
                                            <td>
                                                <c:forEach var="shift" items="${shifts}">
                                                    <!-- Check xem nhân viên có rảnh ca này không -->
                                                    <c:set var="isAvailable" value="false" />
                                                    <c:forEach var="avail" items="${allAvailabilities}">
                                                        <c:if test="${avail.userId == emp.id && avail.dayOfWeek == dayEnum && avail.shiftId == shift.id}">
                                                            <c:set var="isAvailable" value="true" />
                                                        </c:if>
                                                    </c:forEach>

                                                    <!-- Check xem đã được xếp ca này chưa -->
                                                    <c:set var="isAssigned" value="false" />
                                                    <c:forEach var="sch" items="${currentSchedules}">
                                                        <c:if test="${sch.userId == emp.id && sch.workDate.toString() == currentDate.toString() && sch.shiftId == shift.id}">
                                                            <c:set var="isAssigned" value="true" />
                                                        </c:if>
                                                    </c:forEach>

                                                    <div class="form-check text-start p-1 mb-1 border rounded ${isAvailable ? 'bg-success-subtle' : ''}">
                                                        <input class="form-check-input ms-1" type="checkbox" 
                                                               name="assignedShifts" 
                                                               value="${emp.id}_${currentDate}_${shift.id}" 
                                                               ${isAssigned ? 'checked' : ''}>
                                                        <label class="form-check-label small ms-1">
                                                            ${shift.name}
                                                            <c:if test="${isAvailable}">
                                                                <span class="badge bg-success" title="Nhân viên rảnh ca này"><i class="fa-solid fa-check"></i> Rảnh</span>
                                                            </c:if>
                                                        </label>
                                                    </div>
                                                </c:forEach>
                                            </td>
                                        </c:forEach>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <span class="text-muted">
                            <i class="fa-solid fa-circle-info me-1"></i> Các ô màu xanh lá là ca làm nhân viên đã đăng ký rảnh ở UC15.
                        </span>
                        <div>
                            <!-- Nút 1: Lưu bản nháp (UC16) -->
                            <button type="submit" name="action" value="save_draft" class="btn btn-outline-success px-4 py-2 fw-semibold me-2">
                                <i class="fa-solid fa-floppy-disk me-1"></i> Lưu Bản Nháp (Draft)
                            </button>

                            <!-- Nút 2: Xuất bản lịch (UC17) -->
                            <button type="submit" name="action" value="publish" class="btn btn-success px-4 py-2 fw-semibold" 
                                    onclick="return confirm('Bạn có chắc chắn muốn xuất bản lịch làm việc tuần này không? Nhân viên sẽ thấy lịch chính thức ngay sau khi xuất bản.');">
                                <i class="fa-solid fa-paper-plane me-1"></i> Xuất Bản Lịch (Publish)
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal Đăng Ca Mở (UC18) -->
        <div class="modal fade" id="openShiftModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="manage-schedule" method="POST">
                        <input type="hidden" name="action" value="create_open_shift">
                        <input type="hidden" name="weekStart" value="${startOfWeek}">

                        <div class="modal-header bg-warning text-dark">
                            <h5 class="modal-title fw-bold"><i class="fa-solid fa-bullhorn me-2"></i>Đăng Tuyển Ca Mở (UC18)</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>

                        <div class="modal-body">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Chọn Ngày Cần Tuyển:</label>
                                <input type="date" name="workDate" class="form-control" min="${startOfWeek}" max="${endOfWeek}" value="${startOfWeek}" required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold">Chọn Ca Làm Việc:</label>
                                <select name="shiftId" class="form-select" required>
                                    <c:forEach var="shift" items="${shifts}">
                                        <option value="${shift.id}">${shift.name} (${shift.startTime} - ${shift.endTime})</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold">Số lượng nhân viên cần thêm:</label>
                                <input type="number" name="quantityNeeded" class="form-control" min="1" max="10" value="1" required>
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-warning fw-bold">Đăng Ca Mở</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>