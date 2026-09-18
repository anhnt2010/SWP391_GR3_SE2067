-- 1. KÍCH HOẠT VÀ TẠO DATABASE
CREATE DATABASE IF NOT EXISTS `swp391_supermarket` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `swp391_supermarket`;

-- Xóa bảng cũ nếu muốn làm sạch (chạy theo thứ tự khóa ngoại)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `audit_logs`, `payslips`, `payroll_periods`, `salary_rules`, 
                    `violations`, `leave_requests`, `shift_change_requests`, 
                    `shift_logs`, `attendances`, `open_shift_applications`, 
                    `open_shifts`, `shift_schedules`, `shifts`, 
                    `employee_availabilities`, `employee_transfers`, 
                    `certificates`, `contracts`, `employee_profiles`, 
                    `branches`, `positions`, `departments`, 
                    `role_permissions`, `permissions`, `users`, `roles`;
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- PHẦN 1: QUẢN TRỊ HỆ THỐNG & PHÂN QUYỀN (RBAC)
-- ==========================================

CREATE TABLE `roles` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT 'ADMIN, DIRECTOR, HR_MANAGER, STORE_MANAGER, EMPLOYEE',
    `description` VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE `permissions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Ví dụ: account:create, shift:approve',
    `description` VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE `role_permissions` (
    `role_id` INT NOT NULL,
    `permission_id` INT NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`),
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `permissions`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) UNIQUE,
    `role_id` INT NOT NULL,
    `status` ENUM('ACTIVE', 'INACTIVE', 'EMERGENCY_LOCKED') DEFAULT 'ACTIVE',
    `expiration_date` DATETIME NULL COMMENT 'Hạn tài khoản nhân sự thời vụ',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `audit_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NULL,
    `action` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `ip_address` VARCHAR(45),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ==========================================
-- PHẦN 2: CHI NHÁNH & MASTER DATA
-- ==========================================

CREATE TABLE `departments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE `positions` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL UNIQUE,
    `department_id` INT,
    FOREIGN KEY (`department_id`) REFERENCES `departments`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `branches` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT 'Mã chi nhánh',
    `name` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `status` ENUM('ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
    `store_manager_id` INT NULL COMMENT 'Chỉ định Store Manager phụ trách',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`store_manager_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ==========================================
-- PHẦN 3: HỒ SƠ NHÂN SỰ
-- ==========================================

CREATE TABLE `employee_profiles` (
    `user_id` INT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20),
    `identity_card` VARCHAR(20) UNIQUE,
    `home_branch_id` INT NOT NULL COMMENT 'Cơ sở quản lý chính (Local Scope)',
    `position_id` INT,
    `department_id` INT,
    `employee_type` ENUM('FULL_TIME', 'PART_TIME', 'SEASONAL') DEFAULT 'FULL_TIME',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`home_branch_id`) REFERENCES `branches`(`id`),
    FOREIGN KEY (`position_id`) REFERENCES `positions`(`id`),
    FOREIGN KEY (`department_id`) REFERENCES `departments`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `contracts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `contract_type` VARCHAR(50) NOT NULL,
    `start_date` DATE NOT NULL,
    `end_date` DATE NULL,
    `status` ENUM('ACTIVE', 'EXPIRED', 'TERMINATED') DEFAULT 'ACTIVE',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `certificates` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `name` VARCHAR(100) NOT NULL COMMENT 'Ví dụ: Chứng chỉ ATVSTP',
    `issued_date` DATE,
    `expiry_date` DATE,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `employee_transfers` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `from_branch_id` INT NOT NULL,
    `to_branch_id` INT NOT NULL,
    `transfer_type` ENUM('PERMANENT', 'TEMPORARY') NOT NULL COMMENT 'Cố định hoặc Mượn tạm thời',
    `start_date` DATE NOT NULL,
    `end_date` DATE NULL,
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`from_branch_id`) REFERENCES `branches`(`id`),
    FOREIGN KEY (`to_branch_id`) REFERENCES `branches`(`id`)
) ENGINE=InnoDB;

-- ==========================================
-- PHẦN 4: VẬN HÀNH CỬA HÀNG & LỊCH CA
-- ==========================================

CREATE TABLE `shifts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL COMMENT 'Ca Sáng, Ca Chiều, Ca Tối',
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL
) ENGINE=InnoDB;

CREATE TABLE `employee_availabilities` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `day_of_week` ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    `shift_id` INT NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`shift_id`) REFERENCES `shifts`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `shift_schedules` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `branch_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `shift_id` INT NOT NULL,
    `work_date` DATE NOT NULL,
    `position_id` INT,
    `status` ENUM('DRAFT', 'PUBLISHED') DEFAULT 'DRAFT',
    FOREIGN KEY (`branch_id`) REFERENCES `branches`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`shift_id`) REFERENCES `shifts`(`id`),
    FOREIGN KEY (`position_id`) REFERENCES `positions`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `open_shifts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `branch_id` INT NOT NULL,
    `shift_id` INT NOT NULL,
    `work_date` DATE NOT NULL,
    `quantity_needed` INT DEFAULT 1,
    `status` ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN',
    FOREIGN KEY (`branch_id`) REFERENCES `branches`(`id`),
    FOREIGN KEY (`shift_id`) REFERENCES `shifts`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `open_shift_applications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `open_shift_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    `applied_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`open_shift_id`) REFERENCES `open_shifts`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `attendances` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `shift_schedule_id` INT NOT NULL,
    `check_in` DATETIME,
    `check_out` DATETIME,
    `method` ENUM('WIFI', 'DYNAMIC_QR') NOT NULL,
    `status` ENUM('VALID', 'INVALID', 'ADJUSTED') DEFAULT 'VALID',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`shift_schedule_id`) REFERENCES `shift_schedules`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `shift_logs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `branch_id` INT NOT NULL,
    `store_manager_id` INT NOT NULL,
    `log_date` DATE NOT NULL,
    `notes` TEXT,
    FOREIGN KEY (`branch_id`) REFERENCES `branches`(`id`),
    FOREIGN KEY (`store_manager_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `leave_requests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `reason` TEXT NOT NULL,
    `start_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `shift_change_requests` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `requester_id` INT NOT NULL,
    `target_user_id` INT NOT NULL,
    `shift_schedule_id` INT NOT NULL,
    `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    FOREIGN KEY (`requester_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`target_user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`shift_schedule_id`) REFERENCES `shift_schedules`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `violations` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `branch_id` INT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `evidence_url` VARCHAR(255),
    `deduction_amount` DECIMAL(12, 2) DEFAULT 0.00,
    `recorded_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`branch_id`) REFERENCES `branches`(`id`)
) ENGINE=InnoDB;

-- ==========================================
-- PHẦN 5: LƯƠNG & PAYROLL
-- ==========================================

CREATE TABLE `salary_rules` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `hourly_rate` DECIMAL(12, 2) NOT NULL,
    `overtime_rate` DECIMAL(12, 2) DEFAULT 1.5,
    `fine_per_violation` DECIMAL(12, 2) DEFAULT 0.00
) ENGINE=InnoDB;

CREATE TABLE `payroll_periods` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `month` INT NOT NULL,
    `year` INT NOT NULL,
    `status` ENUM('DRAFT', 'FINALIZED') DEFAULT 'DRAFT',
    `finalized_by` INT NULL,
    `finalized_at` DATETIME NULL,
    FOREIGN KEY (`finalized_by`) REFERENCES `users`(`id`)
) ENGINE=InnoDB;

CREATE TABLE `payslips` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `payroll_period_id` INT NOT NULL,
    `user_id` INT NOT NULL,
    `total_hours` DECIMAL(6, 2) NOT NULL,
    `base_salary` DECIMAL(12, 2) NOT NULL,
    `bonus` DECIMAL(12, 2) DEFAULT 0.00,
    `deductions` DECIMAL(12, 2) DEFAULT 0.00,
    `net_salary` DECIMAL(12, 2) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`payroll_period_id`) REFERENCES `payroll_periods`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB;

-- ==========================================
-- CHÈN DỮ LIỆU MẪU MẶC ĐỊNH (SEED DATA)
-- ==========================================

INSERT INTO `roles` (`id`, `name`, `description`) VALUES
(1, 'SYSTEM_ADMIN', 'Quản trị hệ thống'),
(2, 'DIRECTOR', 'Giám đốc chuỗi'),
(3, 'HR_MANAGER', 'Quản lý nhân sự chuỗi'),
(4, 'STORE_MANAGER', 'Quản lý chi nhánh'),
(5, 'EMPLOYEE', 'Nhân viên vận hành');