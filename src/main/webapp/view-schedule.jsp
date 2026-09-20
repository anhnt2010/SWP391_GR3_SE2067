<%-- 
    Document   : view-schedule
    Created on : 21 thg 9, 2026, 09:11:50
    Author     : phong
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch Làm Việc Cá Nhân - HRMS</title>
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
                background-color: #0d6efd;
                color: white;
                text-align: center;
            }
            .table-schedule td {
                text-align: center;
                vertical-align: middle;
                height: 90px;
            }
            .assigned-badge {
                font-size: 0.9rem;
                padding: 8px 12px;
                border-radius: 8px;
            }
        </style>
    </head>
    <body>

        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold text-primary mb-1"><i class="fa-solid fa-calendar-check me-2"></i>Lịch Làm Việc Chính Thức</h3>
                    <p class="text-muted mb-0">Xem danh sách các ca làm việc đã được Quản lý cửa hàng xuất bản.</p>
                </div>
                <a href="dashboard.jsp" class="btn btn-outline-secondary"><i class="fa-solid fa-arrow-left me-1"></i> Dashboard</a>
            </div>

            <!-- Chọn tuần xem lịch -->
            <div class="card card-custom p-3 mb-4">
                <form action="view-schedule" method="GET" class="row align-items-center g-3">
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

            <!-- Bảng thời khóa biểu lịch cá nhân -->
            <div class="card card-custom p-4">
                <div class="table-responsive">
                    <table class="table table-bordered table-schedule align-middle">
                        <thead>
                            <tr>
                                <th style="width: 15%;">Ca làm việc</th>
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
                            <c:forEach var="shift" items="${shifts}">
                                <tr>
                                    <td class="fw-bold bg-light text-start ps-3">
                                        <div>${shift.name}</div>
                                        <small class="text-muted"><i class="fa-regular fa-clock me-1"></i>${shift.startTime} - ${shift.endTime}</small>
                                    </td>

                                    <c:forEach var="i" begin="0" end="6">
                                        <c:set var="currentDate" value="${startOfWeek.plusDays(i)}" />

                                        <c:set var="isWorking" value="false" />
                                        <c:forEach var="sch" items="${mySchedules}">
                                            <!-- Chuyển cả 2 về String để so sánh chuỗi YYYY-MM-DD -->
                                            <c:if test="${sch.workDate.toString() == currentDate.toString() && sch.shiftId == shift.id}">
                                                <c:set var="isWorking" value="true" />
                                            </c:if>
                                        </c:forEach>

                                        <td>
                                            <c:choose>
                                                <c:when test="${isWorking}">
                                                    <span class="badge bg-primary assigned-badge">
                                                        <i class="fa-solid fa-briefcase me-1"></i> Làm việc
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted small">-</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:forEach>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
