USE `swp391_supermarket`;

-- 1. Tạo Phòng ban & Chức danh mẫu
INSERT INTO `departments` (`id`, `name`) VALUES (1, 'Bộ phận Vận hành Cửa hàng'), (2, 'Bộ phận Nhân sự');
INSERT INTO `positions` (`id`, `title`, `department_id`) VALUES (1, 'Cửa hàng trưởng', 1), (2, 'Nhân viên Bán hàng', 1), (3, 'HR Specialist', 2);

-- 2. Tạo Các Tài khoản mẫu (Mật khẩu giả định: 123456)
INSERT INTO `users` (`id`, `username`, `password_hash`, `email`, `role_id`, `status`) VALUES
(1, 'admin_user', 'hashed_pwd_123', 'admin@supermarket.com', 1, 'ACTIVE'),
(2, 'director_user', 'hashed_pwd_123', 'director@supermarket.com', 2, 'ACTIVE'),
(3, 'hr_user', 'hashed_pwd_123', 'hr@supermarket.com', 3, 'ACTIVE'),
(4, 'manager_store1', 'hashed_pwd_123', 'manager1@supermarket.com', 4, 'ACTIVE'),
(5, 'employee_01', 'hashed_pwd_123', 'employee01@supermarket.com', 5, 'ACTIVE');

-- 3. Tạo Chi nhánh mẫu và gán Store Manager (manager_store1) phụ trách
INSERT INTO `branches` (`id`, `code`, `name`, `address`, `status`, `store_manager_id`) VALUES
(1, 'CN01', 'Chi nhánh Cầu Giấy', '123 Xuân Thủy, Cầu Giấy, Hà Nội', 'ACTIVE', 4);

-- 4. Tạo Hồ sơ Nhân viên (Profile) và gán Phạm vi (Scope) Chi nhánh
INSERT INTO `employee_profiles` (`user_id`, `full_name`, `phone`, `identity_card`, `home_branch_id`, `position_id`, `department_id`, `employee_type`) VALUES
(4, 'Nguyễn Văn Quản Lý', '0987654321', '001099000001', 1, 1, 1, 'FULL_TIME'),
(5, 'Trần Thị Nhân Viên', '0912345678', '001099000002', 1, 2, 1, 'PART_TIME');