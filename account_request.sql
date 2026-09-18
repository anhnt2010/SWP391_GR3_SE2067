-- =====================================================================
-- Bảng lưu ĐỀ XUẤT TẠO TÀI KHOẢN do Quản lý (Manager) gửi lên
-- Chạy file này trong database `supermarkethrm`
-- =====================================================================
USE `supermarkethrm`;

DROP TABLE IF EXISTS `account_request`;

CREATE TABLE `account_request` (
  `request_id`    int NOT NULL AUTO_INCREMENT,

  -- Thông tin tài khoản được đề xuất (giống các cột của bảng user)
  `employee_id`   varchar(20)  NOT NULL,
  `full_name`     varchar(100) NOT NULL,
  `email`         varchar(100) DEFAULT NULL,
  `phone`         varchar(20)  DEFAULT NULL,
  `role`          varchar(20)  NOT NULL DEFAULT 'Employee',
  `store_id`      int          NOT NULL DEFAULT '1',
  `expire_at`     date         DEFAULT NULL,

  -- Thông tin đề xuất
  `reason`        varchar(500) DEFAULT NULL,          -- lý do đề xuất
  `proposed_by`   varchar(20)  NOT NULL,              -- mã NV của Quản lý gửi đề xuất
  `proposed_at`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,

  -- Thông tin phê duyệt
  `status`        varchar(10)  NOT NULL DEFAULT 'PENDING',  -- PENDING | APPROVED | REJECTED
  `approved_by`   varchar(20)  DEFAULT NULL,          -- mã NV của HR duyệt
  `approved_at`   datetime     DEFAULT NULL,
  `approval_note` varchar(500) DEFAULT NULL,          -- ghi chú / lý do từ chối

  PRIMARY KEY (`request_id`),
  KEY `idx_status` (`status`),
  KEY `idx_proposed_by` (`proposed_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dữ liệu mẫu để test màn hình phê duyệt
INSERT INTO `account_request`
  (employee_id, full_name, email, phone, role, store_id, expire_at, reason, proposed_by, status)
VALUES
  ('NV010','Nguyễn Thị Lan','lan@gmail.com','0911222333','Employee',1,NULL,
   'Bổ sung nhân sự quầy thu ngân ca tối','NV001','PENDING'),
  ('NV011','Lê Văn Hùng',NULL,'0944555666','Employee',2,'2026-12-31',
   'Nhân viên thời vụ dịp Tết','NV001','PENDING');
