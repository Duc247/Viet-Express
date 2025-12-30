USE logisticsdb;
-- =====================================================
-- LOGISTICS DB - INSERT DATA SCRIPT (v3.0 - Simplified)
-- Chạy sau khi ứng dụng tạo bảng (ddl-auto=update)
-- =====================================================

-- 1. ROLES
INSERT INTO roles (role_name, description, is_active, created_at, updated_at) VALUES
('ADMIN', 'Quản trị viên', true, NOW(), NOW()),
('MANAGER', 'Quản lý kho', true, NOW(), NOW()),
('STAFF', 'Nhân viên kho', true, NOW(), NOW()),
('SHIPPER', 'Tài xế', true, NOW(), NOW()),
('CUSTOMER', 'Khách hàng', true, NOW(), NOW());

-- 2. USERS
INSERT INTO users (username, password, full_name, email, phone, role_id, is_active, created_at, updated_at) VALUES
('admin', '123456', 'Administrator', 'admin@logistics.vn', '0900000000', 1, true, NOW(), NOW()),
('manager1', '123456', 'Trần Văn Quản', 'manager1@logistics.vn', '0900000001', 2, true, NOW(), NOW()),
('staff1', '123456', 'Phạm Văn Nhân', 'staff1@logistics.vn', '0934567890', 3, true, NOW(), NOW()),
('staff2', '123456', 'Hoàng Thị Mai', 'staff2@logistics.vn', '0945678901', 3, true, NOW(), NOW()),
('shipper1', '123456', 'Đinh Văn Tài', 'shipper1@logistics.vn', '0956789012', 4, true, NOW(), NOW()),
('shipper2', '123456', 'Võ Thị Lan', 'shipper2@logistics.vn', '0967890123', 4, true, NOW(), NOW()),
('shipper3', '123456', 'Bùi Văn Hùng', 'shipper3@logistics.vn', '0978901234', 4, true, NOW(), NOW()),
('customer1', '123456', 'Nguyễn Văn Khách', 'khach1@email.com', '0901234567', 5, true, NOW(), NOW()),
('customer2', '123456', 'Trần Thị Hà', 'khach2@email.com', '0912345678', 5, true, NOW(), NOW()),
('customer3', '123456', 'Lê Minh Tâm', 'khach3@email.com', '0923456789', 5, true, NOW(), NOW());

-- 3. LOCATIONS
INSERT INTO locations (location_type, warehouse_code, name, address_text, is_active, created_at, updated_at) VALUES
('WAREHOUSE', 'WH-HCM-01', 'Kho Tân Bình', '123 Cộng Hòa, P13, Tân Bình, HCM', true, NOW(), NOW()),
('WAREHOUSE', 'WH-HCM-02', 'Kho Quận 7', '456 Nguyễn Thị Thập, Q7, HCM', true, NOW(), NOW()),
('WAREHOUSE', 'WH-HN-01', 'Kho Hoàng Mai', '789 Giải Phóng, Hoàng Mai, HN', true, NOW(), NOW()),
('WAREHOUSE', 'WH-DN-01', 'Kho Đà Nẵng', '321 Nguyễn Văn Linh, Hải Châu, ĐN', true, NOW(), NOW()),
('SENDER', NULL, 'Địa chỉ gửi 1', '12 Lý Thường Kiệt, Q10, HCM', true, NOW(), NOW()),
('SENDER', NULL, 'Địa chỉ gửi 2', '56 Trần Hưng Đạo, Q1, HCM', true, NOW(), NOW()),
('RECEIVER', NULL, 'Địa chỉ nhận 1', '78 Nguyễn Trãi, Ba Đình, HN', true, NOW(), NOW()),
('RECEIVER', NULL, 'Địa chỉ nhận 2', '90 Hai Bà Trưng, Hoàn Kiếm, HN', true, NOW(), NOW());

-- 4. ROUTES
INSERT INTO routes (from_location_id, to_location_id, distance_km, estimated_time_hours, is_active, created_at) VALUES
(1, 3, 1700.00, 24.00, true, NOW()), (1, 4, 800.00, 12.00, true, NOW()),
(3, 4, 900.00, 14.00, true, NOW()), (1, 2, 15.00, 0.50, true, NOW()), (3, 1, 1700.00, 24.00, true, NOW());

-- 5. SERVICE_TYPES
INSERT INTO service_types (code, name, description, price_per_km, average_speed_kmh, is_active, created_at, updated_at) VALUES
('EXPRESS', 'Giao nhanh', 'Giao 24h nội thành, 48h liên tỉnh', 5000, 60, true, NOW(), NOW()),
('STANDARD', 'Tiêu chuẩn', 'Giao 3-5 ngày', 2000, 40, true, NOW(), NOW()),
('ECONOMY', 'Tiết kiệm', 'Giao 5-7 ngày', 1000, 30, true, NOW(), NOW()),
('SAME_DAY', 'Giao trong ngày', 'Giao nội thành trong ngày', 10000, 80, true, NOW(), NOW());

-- 6. VEHICLES
INSERT INTO vehicles (vehicle_type, license_plate, capacity_weight, capacity_volume, current_location_id, status, created_at, updated_at) VALUES
('MOTORBIKE', '59A-12345', 30, 0.5, 1, 'AVAILABLE', NOW(), NOW()),
('MOTORBIKE', '59A-67890', 30, 0.5, 1, 'AVAILABLE', NOW(), NOW()),
('VAN', '51B-11111', 500, 10, 1, 'AVAILABLE', NOW(), NOW()),
('TRUCK', '51C-22222', 2000, 30, 3, 'AVAILABLE', NOW(), NOW()),
('CONTAINER', '51D-33333', 10000, 60, 4, 'AVAILABLE', NOW(), NOW());

-- 7. CUSTOMERS (Mã: CUST-YYYYMMDD-XXX)
INSERT INTO customers (customer_code, user_id, name, full_name, phone, email, address, company_name, created_at, updated_at) VALUES
('CUST-20251230-001', 8, 'Nguyễn Văn Khách', 'Nguyễn Văn Khách', '0901234567', 'khach1@email.com', '123 Lê Lợi, Q1, HCM', NULL, NOW(), NOW()),
('CUST-20251230-002', 9, 'Trần Thị Hà', 'Trần Thị Hà', '0912345678', 'khach2@email.com', '456 Nguyễn Huệ, Q1, HCM', 'Công ty ABC', NOW(), NOW()),
('CUST-20251230-003', 10, 'Lê Minh Tâm', 'Lê Minh Tâm', '0923456789', 'khach3@email.com', '789 Hai Bà Trưng, Q3, HCM', 'Công ty XYZ', NOW(), NOW());

-- 8. STAFF (Mã: STAFF-XXX)
INSERT INTO staff (staff_code, user_id, full_name, phone, email, location_id, is_active, created_at, updated_at) VALUES
('STAFF-001', 3, 'Phạm Văn Nhân', '0934567890', 'staff1@logistics.vn', 1, true, NOW(), NOW()),
('STAFF-002', 4, 'Hoàng Thị Mai', '0945678901', 'staff2@logistics.vn', 3, true, NOW(), NOW());

-- 9. SHIPPERS (Mã: SHIP-XXX)
INSERT INTO shippers (shipper_code, user_id, full_name, phone, working_area, is_available, current_location_id, is_active, created_at, updated_at) VALUES
('SHIP-001', 5, 'Đinh Văn Tài', '0956789012', 'TP.HCM', true, 1, true, NOW(), NOW()),
('SHIP-002', 6, 'Võ Thị Lan', '0967890123', 'TP.HCM', true, 2, true, NOW(), NOW()),
('SHIP-003', 7, 'Bùi Văn Hùng', '0978901234', 'Hà Nội', true, 3, true, NOW(), NOW());

-- 10. ACTION_TYPES
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATED', 'Tạo đơn', 'Đơn hàng mới được tạo', NOW(), NOW()),
('CONFIRMED', 'Xác nhận', 'Đã xác nhận', NOW(), NOW()),
('PICKUP_ASSIGNED', 'Phân công lấy', 'Đã phân công tài xế', NOW(), NOW()),
('PICKED_UP', 'Đã lấy hàng', 'Tài xế đã lấy hàng', NOW(), NOW()),
('IN_WAREHOUSE', 'Đã nhập kho', 'Hàng đã về kho', NOW(), NOW()),
('IN_TRANSIT', 'Đang vận chuyển', 'Đang vận chuyển', NOW(), NOW()),
('OUT_FOR_DELIVERY', 'Đang giao', 'Đang giao hàng', NOW(), NOW()),
('DELIVERED', 'Đã giao', 'Giao thành công', NOW(), NOW()),
('FAILED', 'Thất bại', 'Giao thất bại', NOW(), NOW()),
('RETURNED', 'Hoàn hàng', 'Đã hoàn trả', NOW(), NOW());

-- 11. CUSTOMER_REQUESTS
INSERT INTO customer_requests (request_code, sender_id, receiver_id, sender_location_id, receiver_location_id, service_type_id, distance_km, parcel_description, shipping_fee, cod_amount, estimated_delivery_time, status, note, created_at, updated_at) VALUES
('REQ-20251229-001', 1, NULL, 5, 7, 1, 1700, 'iPhone 15 Pro', 8500000, 25000000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'IN_TRANSIT', 'Hàng dễ vỡ', NOW(), NOW()),
('REQ-20251229-002', 2, NULL, 6, 8, 2, 1700, 'Quần áo', 3400000, 0, DATE_ADD(NOW(), INTERVAL 5 DAY), 'PENDING', NULL, NOW(), NOW()),
('REQ-20251229-003', 1, NULL, 5, 8, 4, 15, 'Tài liệu hợp đồng', 150000, 0, DATE_ADD(NOW(), INTERVAL 1 DAY), 'DELIVERED', 'Giao trong ngày', NOW(), NOW());

-- 12. TRACKING_CODES
INSERT INTO tracking_codes (request_id, code, created_at) VALUES
(1, 'TRK-20251229-A1B2C3D4', NOW()), (2, 'TRK-20251229-E5F6G7H8', NOW()), (3, 'TRK-20251229-I9J0K1L2', NOW());

-- 13. TRIPS
INSERT INTO trips (trip_type, shipper_id, vehicle_id, start_location_id, end_location_id, started_at, ended_at, cod_amount, status, note, created_at, updated_at) VALUES
('PICKUP', 1, 1, 1, 5, NOW(), NULL, 0, 'IN_PROGRESS', 'Lấy hàng', NOW(), NOW()),
('TRANSFER', 1, 3, 1, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0, 'COMPLETED', 'HCM->HN', NOW(), NOW()),
('DELIVERY', 3, 4, 3, 7, NOW(), NULL, 0, 'IN_PROGRESS', 'Giao tại HN', NOW(), NOW());

-- 14. PARCELS
INSERT INTO parcels (request_id, parcel_code, description, cod_amount, weight_kg, length_cm, width_cm, height_cm, current_location_id, current_shipper_id, current_trip_id, status, created_at, updated_at) VALUES
(1, 'PCL-20251229-1-01', 'iPhone 15 Pro 256GB', 25000000, 0.5, 15, 10, 5, 3, NULL, NULL, 'IN_TRANSIT', NOW(), NOW()),
(2, 'PCL-20251229-2-01', 'Áo sơ mi', 0, 1, 40, 30, 10, 1, NULL, NULL, 'IN_WAREHOUSE', NOW(), NOW()),
(2, 'PCL-20251229-2-02', 'Quần jeans', 0, 0.8, 50, 35, 5, 1, NULL, NULL, 'IN_WAREHOUSE', NOW(), NOW()),
(3, 'PCL-20251229-3-01', 'Hồ sơ', 0, 0.2, 30, 25, 5, NULL, NULL, NULL, 'DELIVERED', NOW(), NOW());

-- 15. PAYMENTS
INSERT INTO payments (request_id, parcel_id, payment_code, payment_type, payer_type, receiver_type, expected_amount, paid_amount, status, created_at, updated_at) VALUES
(1, 1, 'PAY-20251229-1-SHIP', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 8500000, 8500000, 'PAID', NOW(), NOW()),
(1, 1, 'PAY-20251229-1-COD', 'COD', 'RECEIVER', 'SENDER', 25000000, 0, 'UNPAID', NOW(), NOW()),
(2, NULL, 'PAY-20251229-2-SHIP', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 3400000, 0, 'UNPAID', NOW(), NOW()),
(3, 4, 'PAY-20251229-3-SHIP', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 150000, 150000, 'PAID', NOW(), NOW());

-- 16. PAYMENT_TRANSACTIONS
INSERT INTO payment_transactions (payment_id, amount, transaction_type, payment_method, transaction_ref, status, performed_by_id, transaction_at, created_at) VALUES
(1, 8500000, 'IN', 'BANK_TRANSFER', 'VCB-12345678', 'SUCCESS', 3, NOW(), NOW()),
(4, 150000, 'IN', 'CASH', NULL, 'SUCCESS', 3, NOW(), NOW());

-- 17. PARCEL_ACTIONS
INSERT INTO parcel_actions (parcel_id, request_id, action_type_id, from_location_id, to_location_id, actor_user_id, note, created_at) VALUES
(1, 1, 1, NULL, NULL, 8, 'Đơn hàng được tạo', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 1, 4, 5, 1, 5, 'Lấy hàng từ người gửi', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 1, 5, NULL, 1, 3, 'Nhập kho Tân Bình', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 1, 6, 1, 3, 5, 'Vận chuyển đến HN', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 3, 1, NULL, NULL, 8, 'Đơn được tạo', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 3, 4, 5, NULL, 5, 'Đã lấy hàng', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 3, 8, NULL, 8, 5, 'Giao thành công', NOW());

-- 18. CASE_STUDIES
INSERT INTO case_studies (request_id, service_type_id, title, slug, client_name_display, challenge, solution, result, thumbnail_url, is_featured, is_published, created_at, updated_at) VALUES
(3, 4, 'Vận chuyển tài liệu gấp', 'van-chuyen-tai-lieu-gap', 'Công ty ABC', 
 'Cần gửi hồ sơ trong ngày để kịp deadline.', 'Dịch vụ SAME_DAY + GPS tracking.', 'Giao thành công trong 4 tiếng.', '/images/casestudy-1.jpg', true, true, NOW(), NOW());

-- 19. SYSTEM_LOGS
INSERT INTO system_logs (log_level, module_name, action_type, actor_id, target_id, log_details, ip_address, user_agent, created_at) VALUES
('INFO', 'AUTH', 'LOGIN', 1, NULL, 'Admin đăng nhập', '192.168.1.1', 'Chrome', NOW()),
('INFO', 'ORDER', 'CREATE', 8, '1', 'Tạo đơn REQ-20251229-001', '192.168.1.100', 'Chrome', NOW()),
('INFO', 'PAYMENT', 'SUCCESS', 3, '1', 'Thanh toán đơn 1', '192.168.1.50', 'Chrome', NOW()),
('WARN', 'SHIPPER', 'GPS_FAILED', 5, NULL, 'Lỗi cập nhật GPS', '10.0.0.5', 'Mobile', NOW());

-- =====================================================
-- DONE! Script v3.0 - Simplified & Cleaned
-- =====================================================
