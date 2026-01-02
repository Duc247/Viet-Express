-- =====================================================
-- LOGISTICS DB - INSERT DATA SCRIPT v2.2 (FULL SUPPLEMENT)
-- Chạy sau khi ứng dụng khởi động (DataInitializer đã tạo dữ liệu cơ bản)
-- Script này bổ sung đầy đủ dữ liệu mẫu cho tất cả các bảng
-- =====================================================

USE logisticsdb;

-- Tắt foreign key check
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. USERS BỔ SUNG (mỗi role thêm 1 tài khoản)
-- Password: 123456 (plain text, KHÔNG hash)
-- =====================================================
INSERT IGNORE INTO users (username, password, full_name, email, phone, role_id, is_active, created_at, updated_at) VALUES
('admin2', '123456', 'Admin Phụ', 'admin2@logistics.vn', '0900000010', 1, true, NOW(), NOW()),
('manager2', '123456', 'Nguyễn Thị Quản Lý', 'manager2@logistics.vn', '0900000020', 2, true, NOW(), NOW()),
('staff2', '123456', 'Hoàng Thị Mai', 'staff2@logistics.vn', '0945678901', 3, true, NOW(), NOW()),
('customer2', '123456', 'Trần Thị Bình', 'customer2@email.com', '0912345678', 5, true, NOW(), NOW()),
('shipper2', '123456', 'Võ Thị Lan', 'shipper2@logistics.vn', '0967890123', 4, true, NOW(), NOW());

-- =====================================================
-- 2. LOCATIONS (Địa điểm/Kho)
-- =====================================================
INSERT IGNORE INTO locations (location_type, warehouse_code, name, address_text, description, is_active, created_at, updated_at) VALUES
('WAREHOUSE', 'WH-HCM-01', 'Kho Tân Bình (Kho C)', '123 Cộng Hòa, P13, Tân Bình, HCM', 'Kho chính tại HCM', true, NOW(), NOW()),
('WAREHOUSE', 'WH-HCM-02', 'Kho Quận 7', '456 Nguyễn Thị Thập, Q7, HCM', 'Kho phụ khu vực phía Nam', true, NOW(), NOW()),
('WAREHOUSE', 'WH-HN-01', 'Kho Hoàng Mai (Kho D)', '789 Giải Phóng, Hoàng Mai, HN', 'Kho chính tại Hà Nội', true, NOW(), NOW()),
('WAREHOUSE', 'WH-DN-01', 'Kho Đà Nẵng', '321 Nguyễn Văn Linh, Hải Châu, ĐN', 'Kho miền Trung', true, NOW(), NOW()),
('SENDER', NULL, 'Điểm A - Kho Bia Sài Gòn', '12 Lý Thường Kiệt, Q10, HCM', 'Tổng kho phân phối Bia', true, NOW(), NOW()),
('SENDER', NULL, 'Địa chỉ gửi 2', '56 Trần Hưng Đạo, Q1, HCM', 'Văn phòng công ty', true, NOW(), NOW()),
('RECEIVER', NULL, 'Điểm B - Đại lý Bia HN', '78 Nguyễn Trãi, Ba Đình, HN', 'Đại lý phân phối bia', true, NOW(), NOW()),
('RECEIVER', NULL, 'Địa chỉ nhận 2', '90 Hai Bà Trưng, Hoàn Kiếm, HN', 'Văn phòng', true, NOW(), NOW());

-- =====================================================
-- 3. SERVICE_TYPES (Loại dịch vụ)
-- =====================================================
INSERT IGNORE INTO service_types (code, name, description, price_per_km, average_speed_kmh, is_active, created_at, updated_at) VALUES
('EXPRESS', 'Giao nhanh', 'Giao 24h nội thành, 48h liên tỉnh', 5000, 60, true, NOW(), NOW()),
('STANDARD', 'Tiêu chuẩn', 'Giao 3-5 ngày', 2000, 40, true, NOW(), NOW()),
('ECONOMY', 'Tiết kiệm', 'Giao 5-7 ngày', 1000, 30, true, NOW(), NOW()),
('SAME_DAY', 'Giao trong ngày', 'Giao nội thành trong ngày', 10000, 80, true, NOW(), NOW());

-- =====================================================
-- 4. VEHICLES (Xe)
-- =====================================================
INSERT IGNORE INTO vehicles (vehicle_type, license_plate, capacity_weight, capacity_volume, description, status, current_location_id, created_at, updated_at) VALUES
('MOTORBIKE', '59A-12345', 30, 0.5, 'Honda SH 150cc', 'AVAILABLE', 1, NOW(), NOW()),
('MOTORBIKE', '59A-67890', 30, 0.5, 'Yamaha Exciter 155', 'AVAILABLE', 1, NOW(), NOW()),
('VAN', '51B-11111', 500, 10, 'Ford Transit 2021', 'AVAILABLE', 1, NOW(), NOW()),
('VAN', '51B-22222', 500, 10, 'Hyundai Solati 2022', 'AVAILABLE', 1, NOW(), NOW()),
('TRUCK', '51C-33333', 2000, 30, 'Hino 300 Series', 'AVAILABLE', 3, NOW(), NOW()),
('TRUCK', '30H-44444', 2000, 30, 'Isuzu QKR77', 'AVAILABLE', 3, NOW(), NOW()),
('CONTAINER', '51D-55555', 10000, 60, 'Container 20ft', 'AVAILABLE', 4, NOW(), NOW());

-- =====================================================
-- 5. ACTION_TYPES (Loại hành động tracking)
-- =====================================================
INSERT IGNORE INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATED', 'Tạo đơn', 'Đơn hàng mới được tạo', NOW(), NOW()),
('CONFIRMED', 'Xác nhận', 'Manager đã xác nhận đơn', NOW(), NOW()),
('PICKUP_ASSIGNED', 'Phân công lấy', 'Đã phân công tài xế lấy hàng', NOW(), NOW()),
('PICKED_UP', 'Đã lấy hàng', 'Tài xế đã lấy hàng', NOW(), NOW()),
('IN_WAREHOUSE', 'Đã nhập kho', 'Hàng đã về kho', NOW(), NOW()),
('IN_TRANSIT', 'Đang vận chuyển', 'Đang vận chuyển liên tỉnh', NOW(), NOW()),
('OUT_FOR_DELIVERY', 'Đang giao', 'Đang trên đường giao hàng', NOW(), NOW()),
('DELIVERED', 'Đã giao', 'Giao hàng thành công', NOW(), NOW()),
('FAILED', 'Thất bại', 'Giao hàng thất bại', NOW(), NOW()),
('RETURNED', 'Hoàn hàng', 'Đã hoàn trả hàng', NOW(), NOW());

-- =====================================================
-- 6. ROUTES (Tuyến đường)
-- =====================================================
INSERT IGNORE INTO routes (from_location_id, to_location_id, distance_km, estimated_time_hours, description, is_active, created_at) VALUES
(5, 1, 10.00, 0.50, 'Nội thành HCM: Điểm A → Kho C', true, NOW()),
(1, 3, 1700.00, 24.00, 'Liên tỉnh: Kho C (HCM) → Kho D (HN)', true, NOW()),
(3, 7, 15.00, 0.75, 'Nội thành HN: Kho D → Điểm B', true, NOW()),
(1, 4, 800.00, 12.00, 'HCM → Đà Nẵng', true, NOW()),
(3, 4, 900.00, 14.00, 'HN → Đà Nẵng', true, NOW());

-- =====================================================
-- 7. CUSTOMERS (Khách hàng bổ sung)
-- =====================================================
INSERT IGNORE INTO customers (user_id, name, full_name, phone, email, address, company_name, gender, description, created_at, updated_at)
SELECT u.user_id, u.full_name, u.full_name, u.phone, u.email, 
       '456 Lê Lợi, Q1, HCM', 'Công ty ABC', 'Nữ', 'Khách hàng thường xuyên', NOW(), NOW()
FROM users u 
WHERE u.username = 'customer2' AND NOT EXISTS (SELECT 1 FROM customers c WHERE c.user_id = u.user_id);

-- =====================================================
-- 8. STAFF (Nhân viên kho bổ sung)
-- =====================================================
INSERT IGNORE INTO staff (user_id, full_name, phone, email, location_id, is_active, created_at, updated_at)
SELECT u.user_id, u.full_name, u.phone, u.email, 3, true, NOW(), NOW()
FROM users u 
WHERE u.username = 'staff2' AND NOT EXISTS (SELECT 1 FROM staff s WHERE s.user_id = u.user_id);

-- =====================================================
-- 9. SHIPPERS (Tài xế bổ sung)
-- =====================================================
INSERT IGNORE INTO shippers (user_id, full_name, phone, working_area, is_available, current_location_id, is_active, created_at, updated_at)
SELECT u.user_id, u.full_name, u.phone, 'Khu vực HCM - Ngoại thành', true, 2, true, NOW(), NOW()
FROM users u 
WHERE u.username = 'shipper2' AND NOT EXISTS (SELECT 1 FROM shippers s WHERE s.user_id = u.user_id);

-- =====================================================
-- 10. CUSTOMER_REQUESTS (Đơn hàng mẫu)
-- Cần lấy customer_id từ bảng customers
-- =====================================================
INSERT IGNORE INTO customer_requests (request_code, sender_id, receiver_id, sender_location_id, receiver_location_id, service_type_id, distance_km, parcel_description, shipping_fee, cod_amount, estimated_delivery_time, status, note, description, created_at, updated_at) VALUES
('REQ-20260102-001', 1, 2, 5, 7, 1, 1725, '10 thùng Bia Heineken', 2000000, 5000000, DATE_ADD(NOW(), INTERVAL 3 DAY), 'PENDING', 'Hàng dễ vỡ', 'Đơn vận chuyển bia từ HCM đến HN', NOW(), NOW()),
('REQ-20260102-002', 1, NULL, 5, 8, 2, 1700, 'Quần áo mùa đông', 3400000, 500000, DATE_ADD(NOW(), INTERVAL 5 DAY), 'PENDING', 'Chờ xác nhận', 'Đơn quần áo mùa đông', NOW(), NOW()),
('REQ-20260102-003', 1, NULL, 5, 7, 4, 15, 'Tài liệu hợp đồng', 150000, 0, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMED', 'Giao gấp trong ngày', 'Đơn tài liệu SAME DAY', NOW(), NOW()),
('REQ-20260102-004', 2, 1, 7, 5, 1, 1700, 'Laptop Dell XPS 15', 8500000, 35000000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CONFIRMED', 'Hàng giá trị cao', 'Đơn laptop từ HN về HCM', NOW(), NOW()),
('REQ-20260102-005', 1, 2, 5, 7, 1, 1725, 'Điện thoại Samsung', 1500000, 15000000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'IN_TRANSIT', 'Cần bảo hiểm', 'Đơn điện thoại Samsung', NOW(), NOW());

-- =====================================================
-- 11. TRACKING_CODES (Mã vận đơn)
-- =====================================================
INSERT IGNORE INTO tracking_codes (request_id, code, created_at) VALUES
(1, 'TRK-20260102-BIA10', NOW()),
(2, 'TRK-20260102-CLOTHES', NOW()),
(3, 'TRK-20260102-DOCS', NOW()),
(4, 'TRK-20260102-LAPTOP', NOW()),
(5, 'TRK-20260102-PHONE', NOW());

-- =====================================================
-- 12. TRIPS (Chuyến xe)
-- =====================================================
INSERT IGNORE INTO trips (trip_type, shipper_id, vehicle_id, start_location_id, end_location_id, started_at, ended_at, cod_amount, status, note, description, created_at, updated_at) VALUES
-- Đơn 3: Tài liệu SAME_DAY
('PICKUP', 1, 1, 5, 1, NOW(), NULL, 0, 'CREATED', 'Lấy tài liệu', 'Lấy tài liệu từ điểm A', NOW(), NOW()),
('DELIVERY', 1, 1, 1, 7, NULL, NULL, 0, 'CREATED', 'Giao tài liệu', 'Giao tài liệu đến điểm B', NOW(), NOW()),
-- Đơn 5: Điện thoại
('PICKUP', 1, 3, 5, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), 0, 'COMPLETED', 'Đã lấy hàng', 'Lấy điện thoại từ kho bia', NOW(), NOW()),
('TRANSFER', 1, 5, 1, 3, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, 0, 'IN_PROGRESS', 'Đang trung chuyển', 'Trung chuyển HCM → HN', NOW(), NOW()),
('DELIVERY', 2, 4, 3, 7, NULL, NULL, 15000000, 'CREATED', 'Chờ hàng về', 'Giao điện thoại và thu COD', NOW(), NOW());

-- =====================================================
-- 13. PARCELS (Kiện hàng)
-- =====================================================
INSERT IGNORE INTO parcels (request_id, parcel_code, description, cod_amount, weight_kg, length_cm, width_cm, height_cm, current_location_id, current_shipper_id, current_trip_id, status, created_at, updated_at) VALUES
-- Đơn 1: Bia (chưa xử lý)
(1, 'PCL-001-01', 'Thùng Bia 01 - 24 lon', 500000, 12, 40, 30, 30, 5, NULL, NULL, 'CREATED', NOW(), NOW()),
(1, 'PCL-001-02', 'Thùng Bia 02 - 24 lon', 500000, 12, 40, 30, 30, 5, NULL, NULL, 'CREATED', NOW(), NOW()),
(1, 'PCL-001-03', 'Thùng Bia 03 - 24 lon', 500000, 12, 40, 30, 30, 5, NULL, NULL, 'CREATED', NOW(), NOW()),
-- Đơn 2: Quần áo
(2, 'PCL-002-01', 'Áo khoác mùa đông', 250000, 1.5, 50, 40, 15, 5, NULL, NULL, 'CREATED', NOW(), NOW()),
(2, 'PCL-002-02', 'Quần jeans nam', 250000, 1.2, 45, 35, 10, 5, NULL, NULL, 'CREATED', NOW(), NOW()),
-- Đơn 3: Tài liệu
(3, 'PCL-003-01', 'Hồ sơ hợp đồng quan trọng', 0, 0.5, 35, 25, 5, 5, NULL, NULL, 'CREATED', NOW(), NOW()),
-- Đơn 4: Laptop
(4, 'PCL-004-01', 'Laptop Dell XPS 15 - 32GB RAM', 30000000, 2.5, 40, 30, 10, 7, NULL, NULL, 'CREATED', NOW(), NOW()),
(4, 'PCL-004-02', 'Dock + Chuột + Túi laptop', 5000000, 1.0, 30, 20, 15, 7, NULL, NULL, 'CREATED', NOW(), NOW()),
-- Đơn 5: Điện thoại (đang vận chuyển)
(5, 'PCL-005-01', 'Samsung Galaxy S24 Ultra', 15000000, 0.5, 20, 10, 8, 1, 1, 4, 'IN_TRANSIT', NOW(), NOW());

-- =====================================================
-- 14. PAYMENTS (Thanh toán)
-- =====================================================
INSERT IGNORE INTO payments (request_id, parcel_id, trip_id, payment_code, payment_type, payer_type, receiver_type, payment_scope, description, expected_amount, paid_amount, status, created_at, updated_at) VALUES
-- Đơn 1: Bia
(1, NULL, NULL, 'PAY-001-SHIP', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'FULL_REQUEST', 'Phí ship 10 thùng bia', 2000000.00, 0.00, 'UNPAID', NOW(), NOW()),
(1, NULL, NULL, 'PAY-001-COD', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu COD tiền bia', 5000000.00, 0.00, 'UNPAID', NOW(), NOW()),
-- Đơn 2: Quần áo
(2, NULL, NULL, 'PAY-002-SHIP', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'FULL_REQUEST', 'Phí ship quần áo', 3400000.00, 0.00, 'UNPAID', NOW(), NOW()),
(2, NULL, NULL, 'PAY-002-COD', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu COD quần áo', 500000.00, 0.00, 'UNPAID', NOW(), NOW()),
-- Đơn 3: Tài liệu
(3, NULL, NULL, 'PAY-003-SHIP', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'FULL_REQUEST', 'Phí giao tài liệu SAME_DAY', 150000.00, 150000.00, 'PAID', NOW(), NOW()),
-- Đơn 4: Laptop
(4, NULL, NULL, 'PAY-004-SHIP', 'SHIPPING_FEE', 'RECEIVER', 'COMPANY', 'FULL_REQUEST', 'Phí ship laptop', 8500000.00, 0.00, 'UNPAID', NOW(), NOW()),
(4, NULL, NULL, 'PAY-004-COD', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu COD laptop', 35000000.00, 0.00, 'UNPAID', NOW(), NOW()),
-- Đơn 5: Điện thoại
(5, NULL, NULL, 'PAY-005-SHIP', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'FULL_REQUEST', 'Phí ship điện thoại', 1500000.00, 1500000.00, 'PAID', NOW(), NOW()),
(5, NULL, NULL, 'PAY-005-COD', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu COD điện thoại', 15000000.00, 0.00, 'UNPAID', NOW(), NOW());

-- =====================================================
-- 15. PAYMENT_TRANSACTIONS (Giao dịch thanh toán)
-- =====================================================
INSERT IGNORE INTO payment_transactions (payment_id, amount, transaction_type, payment_method, transaction_ref, status, performed_by_id, transaction_at, created_at) VALUES
(5, 150000, 'IN', 'VNPAY', 'VNPAY-20260102-001', 'SUCCESS', 1, NOW(), NOW()),
(8, 1500000, 'IN', 'BANK_TRANSFER', 'VCB-20260102-001', 'SUCCESS', 1, NOW(), NOW());

-- =====================================================
-- 16. PARCEL_ACTIONS (Lịch sử tracking)
-- =====================================================
INSERT IGNORE INTO parcel_actions (parcel_id, request_id, action_type_id, from_location_id, to_location_id, actor_user_id, note, created_at) VALUES
-- Đơn 1: Bia (mới tạo)
(1, 1, 1, NULL, NULL, 4, 'Customer tạo đơn 10 thùng bia', NOW()),
-- Đơn 3: Tài liệu
(6, 3, 1, NULL, NULL, 4, 'Tạo đơn tài liệu gấp', NOW()),
(6, 3, 2, NULL, NULL, 2, 'Manager xác nhận đơn', NOW()),
-- Đơn 5: Điện thoại
(9, 5, 1, NULL, NULL, 4, 'Tạo đơn điện thoại Samsung', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(9, 5, 2, NULL, NULL, 2, 'Manager xác nhận', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(9, 5, 4, 5, 1, 5, 'Shipper lấy hàng', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(9, 5, 5, NULL, 1, 3, 'Nhập kho Tân Bình', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(9, 5, 6, 1, 3, 5, 'Đang trung chuyển HCM → HN', DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- =====================================================
-- 17. CASE_STUDIES (Dự án tiêu biểu)
-- =====================================================
INSERT IGNORE INTO case_studies (request_id, service_type_id, title, slug, client_name_display, challenge, solution, result, thumbnail_url, is_featured, is_published, created_at, updated_at) VALUES
(3, 4, 'Giao tài liệu hợp đồng trong 4 giờ', 'giao-tai-lieu-hop-dong-4-gio', 'Công ty Luật ABC', 
 'Khách hàng cần gửi hồ sơ hợp đồng trong ngày để kịp deadline ký kết quan trọng.',
 'Sử dụng dịch vụ SAME_DAY với shipper chuyên dụng, GPS tracking realtime.',
 'Giao thành công trong 4 tiếng, khách hàng ký hợp đồng kịp thời hạn.',
 '/images/case-docs.jpg', true, true, NOW(), NOW()),
(5, 1, 'Vận chuyển điện thoại liên tỉnh an toàn', 'van-chuyen-dien-thoai-lien-tinh', 'Shop Điện Thoại XYZ',
 'Vận chuyển lô hàng điện thoại giá trị cao từ HCM ra Hà Nội, yêu cầu bảo hiểm.',
 'Dịch vụ Express với bảo hiểm 100%, đóng gói chống sốc, tracking realtime.',
 'Giao hàng thành công, không hư hỏng, thu COD đầy đủ.',
 '/images/case-phone.jpg', true, true, NOW(), NOW());

-- =====================================================
-- 18. SYSTEM_LOGS (Nhật ký hệ thống)
-- =====================================================
INSERT IGNORE INTO system_logs (log_level, module_name, action_type, actor_id, target_id, log_details, ip_address, user_agent, created_at) VALUES
('INFO', 'AUTH', 'LOGIN', 1, NULL, 'Admin đăng nhập hệ thống', '192.168.1.1', 'Chrome/120.0', NOW()),
('INFO', 'ORDER', 'CREATE', 4, '1', 'Customer tạo đơn REQ-20260102-001', '192.168.1.100', 'Chrome/120.0', NOW()),
('INFO', 'ORDER', 'CONFIRM', 2, '3', 'Manager xác nhận đơn REQ-20260102-003', '192.168.1.50', 'Chrome/120.0', NOW()),
('INFO', 'PAYMENT', 'SUCCESS', 1, '5', 'Thanh toán phí ship đơn 3 qua VNPAY', '192.168.1.100', 'Chrome/120.0', NOW()),
('INFO', 'TRIP', 'CREATE', 2, '1', 'Tạo chuyến PICKUP cho đơn tài liệu', '192.168.1.50', 'Chrome/120.0', NOW()),
('INFO', 'SHIPPER', 'PICKUP', 5, '9', 'Shipper lấy kiện hàng điện thoại', '10.0.0.5', 'Mobile App', NOW()),
('WARN', 'SYSTEM', 'GPS_FAILED', 5, NULL, 'Lỗi cập nhật GPS shipper', '10.0.0.5', 'Mobile App', NOW());

-- Bật lại foreign key check
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- DONE! Script v2.2 - Full Supplement Data
-- =====================================================
SELECT 'Insert bổ sung ĐẦY ĐỦ hoàn tất!' AS Result;
SELECT '5 đơn hàng, 9 kiện, 5 trips, 9 payments đã được thêm.' AS Details;

-- =====================================================
-- DANH SÁCH TÀI KHOẢN ĐẦY ĐỦ
-- =====================================================
-- Từ DataInitializer:
--   admin / admin123       -> ADMIN
--   manager / manager123   -> MANAGER  
--   staff / staff123       -> STAFF
--   customer / customer123 -> CUSTOMER
--   shipper / shipper123   -> SHIPPER
--
-- Bổ sung từ script (password: 123456):
--   admin2 / 123456    -> ADMIN
--   manager2 / 123456  -> MANAGER
--   staff2 / 123456    -> STAFF
--   customer2 / 123456 -> CUSTOMER
--   shipper2 / 123456  -> SHIPPER
-- =====================================================
