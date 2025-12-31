USE logisticsdb;
-- =====================================================
-- LOGISTICS DB - INSERT DATA SCRIPT (v5.0 - With Descriptions)
-- Chạy sau khi ứng dụng tạo bảng (ddl-auto=update)
-- Bao gồm: Ví dụ đơn hàng 55 thùng Durex với đầy đủ trips, payments và descriptions
-- =====================================================

-- 1. ROLES
INSERT INTO roles (role_name, description, is_active, created_at, updated_at) VALUES
('ADMIN', 'Quản trị viên - Toàn quyền quản lý hệ thống', true, NOW(), NOW()),
('MANAGER', 'Quản lý kho - Xác nhận đơn, tạo trip, phân công', true, NOW(), NOW()),
('STAFF', 'Nhân viên kho - Tạo parcel, quản lý hàng hóa', true, NOW(), NOW()),
('SHIPPER', 'Tài xế - Lấy hàng, vận chuyển, giao hàng', true, NOW(), NOW()),
('CUSTOMER', 'Khách hàng - Tạo đơn, theo dõi vận chuyển', true, NOW(), NOW());

-- 2. USERS
INSERT INTO users (username, password, full_name, email, phone, role_id, is_active, created_at, updated_at) VALUES
('admin', '123456', 'Administrator', 'admin@logistics.vn', '0900000000', 1, true, NOW(), NOW()),
('manager1', '123456', 'Trần Văn Quản', 'manager1@logistics.vn', '0900000001', 2, true, NOW(), NOW()),
('manager2', '123456', 'Nguyễn Thị Quản', 'manager2@logistics.vn', '0900000002', 2, true, NOW(), NOW()),
('staff1', '123456', 'Phạm Văn Nhân', 'staff1@logistics.vn', '0934567890', 3, true, NOW(), NOW()),
('staff2', '123456', 'Hoàng Thị Mai', 'staff2@logistics.vn', '0945678901', 3, true, NOW(), NOW()),
('shipper1', '123456', 'Đinh Văn Tài', 'shipper1@logistics.vn', '0956789012', 4, true, NOW(), NOW()),
('shipper2', '123456', 'Võ Thị Lan', 'shipper2@logistics.vn', '0967890123', 4, true, NOW(), NOW()),
('shipper3', '123456', 'Bùi Văn Hùng', 'shipper3@logistics.vn', '0978901234', 4, true, NOW(), NOW()),
('shipper4', '123456', 'Lê Văn Minh', 'shipper4@logistics.vn', '0989012345', 4, true, NOW(), NOW()),
('customer1', '123456', 'Nguyễn Văn A', 'customerA@email.com', '0901234567', 5, true, NOW(), NOW()),
('customer2', '123456', 'Trần Thị B', 'customerB@email.com', '0912345678', 5, true, NOW(), NOW()),
('customer3', '123456', 'Lê Minh Tâm', 'khach3@email.com', '0923456789', 5, true, NOW(), NOW());

-- 3. LOCATIONS (with descriptions)
INSERT INTO locations (location_type, warehouse_code, name, address_text, description, is_active, created_at, updated_at) VALUES
-- Warehouses
('WAREHOUSE', 'WH-HCM-01', 'Kho Tân Bình (Kho C)', '123 Cộng Hòa, P13, Tân Bình, HCM', 
 'Kho chính tại HCM, diện tích 2000m2, có hệ thống phân loại tự động, sức chứa 50000 kiện', true, NOW(), NOW()),
('WAREHOUSE', 'WH-HCM-02', 'Kho Quận 7', '456 Nguyễn Thị Thập, Q7, HCM', 
 'Kho phụ khu vực phía Nam, diện tích 1000m2, chuyên xử lý hàng nhỏ', true, NOW(), NOW()),
('WAREHOUSE', 'WH-HN-01', 'Kho Hoàng Mai (Kho D)', '789 Giải Phóng, Hoàng Mai, HN', 
 'Kho chính tại Hà Nội, diện tích 2500m2, có bãi đỗ xe tải lớn, sức chứa 60000 kiện', true, NOW(), NOW()),
('WAREHOUSE', 'WH-DN-01', 'Kho Đà Nẵng', '321 Nguyễn Văn Linh, Hải Châu, ĐN', 
 'Kho miền Trung, diện tích 1500m2, gần sân bay, thuận tiện vận chuyển liên tỉnh', true, NOW(), NOW()),
-- Sender locations
('SENDER', NULL, 'Điểm A - Công ty Durex VN', '12 Lý Thường Kiệt, Q10, HCM', 
 'Nhà máy sản xuất Durex, địa điểm lấy hàng, có bãi đậu xe lớn, giờ làm việc 7h-17h', true, NOW(), NOW()),
('SENDER', NULL, 'Địa chỉ gửi 2', '56 Trần Hưng Đạo, Q1, HCM', 
 'Văn phòng công ty, lấy hàng tài liệu và hàng nhỏ', true, NOW(), NOW()),
-- Receiver locations
('RECEIVER', NULL, 'Điểm B - Nhà phân phối HN', '78 Nguyễn Trãi, Ba Đình, HN', 
 'Kho nhà phân phối tại Hà Nội, nhận hàng 24/7, có thang máy tải hàng', true, NOW(), NOW()),
('RECEIVER', NULL, 'Địa chỉ nhận 2', '90 Hai Bà Trưng, Hoàn Kiếm, HN', 
 'Văn phòng làm việc, chỉ nhận hàng trong giờ hành chính', true, NOW(), NOW());

-- 4. ROUTES (with descriptions)
INSERT INTO routes (from_location_id, to_location_id, distance_km, estimated_time_hours, description, is_active, created_at) VALUES
(5, 1, 10.00, 0.50, 'Tuyến nội thành HCM, đường Lý Thường Kiệt -> Cộng Hòa, ít kẹt xe', true, NOW()),
(1, 3, 1700.00, 24.00, 'Tuyến cao tốc HCM-HN qua Quốc lộ 1A, đi xe tải lớn, chạy đêm', true, NOW()),
(3, 7, 15.00, 0.75, 'Tuyến nội thành Hà Nội, Hoàng Mai -> Ba Đình, thường kẹt xe giờ cao điểm', true, NOW()),
(1, 4, 800.00, 12.00, 'Tuyến HCM-Đà Nẵng qua Quốc lộ 1A, điểm dừng chân tại Nha Trang', true, NOW()),
(3, 4, 900.00, 14.00, 'Tuyến HN-ĐN qua Quốc lộ 1A, đường núi nhiều khúc cua', true, NOW()),
(1, 2, 15.00, 0.50, 'Tuyến nội thành HCM, Tân Bình -> Q7, qua cầu Phú Mỹ', true, NOW());

-- 5. SERVICE_TYPES
INSERT INTO service_types (code, name, description, price_per_km, average_speed_kmh, is_active, created_at, updated_at) VALUES
('EXPRESS', 'Giao nhanh', 'Giao 24h nội thành, 48h liên tỉnh, ưu tiên xử lý', 5000, 60, true, NOW(), NOW()),
('STANDARD', 'Tiêu chuẩn', 'Giao 3-5 ngày, phù hợp hàng thông thường', 2000, 40, true, NOW(), NOW()),
('ECONOMY', 'Tiết kiệm', 'Giao 5-7 ngày, giá rẻ nhất, không bảo hiểm', 1000, 30, true, NOW(), NOW()),
('SAME_DAY', 'Giao trong ngày', 'Giao nội thành trong ngày, tối đa 50km', 10000, 80, true, NOW(), NOW());

-- 6. VEHICLES (with descriptions)
INSERT INTO vehicles (vehicle_type, license_plate, capacity_weight, capacity_volume, description, status, current_location_id, created_at, updated_at) VALUES
('MOTORBIKE', '59A-12345', 30, 0.5, 'Honda SH 150cc, 2022, chạy tài liệu và hàng nhỏ nội thành', 'AVAILABLE', 1, NOW(), NOW()),
('MOTORBIKE', '59A-67890', 30, 0.5, 'Yamaha Exciter 155, 2023, chạy tài liệu gấp', 'AVAILABLE', 1, NOW(), NOW()),
('VAN', '51B-11111', 500, 10, 'Ford Transit 2021, 16 chỗ cải tạo thành xe tải, chở tối đa 10 thùng trung', 'AVAILABLE', 1, NOW(), NOW()),
('VAN', '51B-22222', 500, 10, 'Hyundai Solati 2022, xe tải nhỏ chở hàng nội thành', 'AVAILABLE', 1, NOW(), NOW()),
('TRUCK', '51C-33333', 2000, 30, 'Hino 300 Series, 2020, tải trọng 3.5 tấn, chạy liên tỉnh, chở được 55 thùng', 'AVAILABLE', 3, NOW(), NOW()),
('TRUCK', '30H-44444', 2000, 30, 'Isuzu QKR77, 2021, tải 2.4 tấn, chạy liên tỉnh', 'AVAILABLE', 3, NOW(), NOW()),
('CONTAINER', '51D-55555', 10000, 60, 'Container 20ft, Dongfeng 2019, chở hàng cồng kềnh', 'AVAILABLE', 4, NOW(), NOW());

-- 7. CUSTOMERS (with descriptions)
INSERT INTO customers (customer_code, user_id, name, full_name, phone, email, address, company_name, description, created_at, updated_at) VALUES
('CUST-20251230-001', 10, 'Nguyễn Văn A', 'Nguyễn Văn A', '0901234567', 'customerA@email.com', '12 Lý Thường Kiệt, Q10, HCM', 'Công ty Durex VN', 
 'Khách hàng VIP, doanh nghiệp lớn, thường xuyên vận chuyển số lượng lớn, ưu tiên cao', NOW(), NOW()),
('CUST-20251230-002', 11, 'Trần Thị B', 'Trần Thị B', '0912345678', 'customerB@email.com', '78 Nguyễn Trãi, Ba Đình, HN', 'Nhà phân phối HN', 
 'Khách hàng doanh nghiệp, nhận hàng 24/7, có kho riêng', NOW(), NOW()),
('CUST-20251230-003', 12, 'Lê Minh Tâm', 'Lê Minh Tâm', '0923456789', 'khach3@email.com', '789 Hai Bà Trưng, Q3, HCM', 'Công ty XYZ', 
 'Khách hàng cá nhân, mua lẻ, thanh toán online', NOW(), NOW());

-- 8. STAFF (with descriptions)
INSERT INTO staff (staff_code, user_id, full_name, phone, email, location_id, description, is_active, created_at, updated_at) VALUES
('STAFF-001', 4, 'Phạm Văn Nhân', '0934567890', 'staff1@logistics.vn', 1, 
 'Nhân viên kho trưởng tại HCM, 5 năm kinh nghiệm, chuyên xử lý đơn hàng lớn', true, NOW(), NOW()),
('STAFF-002', 5, 'Hoàng Thị Mai', '0945678901', 'staff2@logistics.vn', 3, 
 'Nhân viên kho tại Hà Nội, 3 năm kinh nghiệm, giỏi sắp xếp hàng hóa', true, NOW(), NOW());

-- 9. SHIPPERS (with descriptions)
INSERT INTO shippers (shipper_code, user_id, full_name, phone, working_area, description, is_available, current_location_id, is_active, created_at, updated_at) VALUES
('SHIP-001', 6, 'Đinh Văn Tài', '0956789012', 'TP.HCM', 
 'Tài xế 10 năm kinh nghiệm, bằng lái B2, chuyên chạy xe Van và Truck nội thành', true, 1, true, NOW(), NOW()),
('SHIP-002', 7, 'Võ Thị Lan', '0967890123', 'TP.HCM', 
 'Tài xế nữ 5 năm kinh nghiệm, bằng lái A2, chuyên giao hàng gấp nội thành bằng xe máy', true, 1, true, NOW(), NOW()),
('SHIP-003', 8, 'Bùi Văn Hùng', '0978901234', 'Hà Nội', 
 'Tài xế 15 năm kinh nghiệm, bằng lái C, chuyên chạy tuyến HN-HCM, quen đường cao tốc', true, 3, true, NOW(), NOW()),
('SHIP-004', 9, 'Lê Văn Minh', '0989012345', 'Hà Nội', 
 'Tài xế trẻ 3 năm kinh nghiệm, bằng lái B2, chuyên giao hàng nội thành Hà Nội', true, 3, true, NOW(), NOW());

-- 10. ACTION_TYPES
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATED', 'Tạo đơn', 'Đơn hàng mới được tạo bởi khách hàng', NOW(), NOW()),
('CONFIRMED', 'Xác nhận', 'Manager đã xác nhận đơn và thu tiền cọc', NOW(), NOW()),
('PICKUP_ASSIGNED', 'Phân công lấy', 'Đã phân công tài xế lấy hàng tại điểm gửi', NOW(), NOW()),
('PICKED_UP', 'Đã lấy hàng', 'Tài xế đã lấy hàng thành công từ người gửi', NOW(), NOW()),
('IN_WAREHOUSE', 'Đã nhập kho', 'Hàng đã về kho và được nhân viên tiếp nhận', NOW(), NOW()),
('IN_TRANSIT', 'Đang vận chuyển', 'Đang vận chuyển liên tỉnh trên đường', NOW(), NOW()),
('OUT_FOR_DELIVERY', 'Đang giao', 'Đang trên đường giao hàng đến người nhận', NOW(), NOW()),
('DELIVERED', 'Đã giao', 'Giao hàng thành công, đã ký nhận', NOW(), NOW()),
('FAILED', 'Thất bại', 'Giao hàng thất bại, người nhận từ chối hoặc không có mặt', NOW(), NOW()),
('RETURNED', 'Hoàn hàng', 'Đã hoàn trả hàng về người gửi', NOW(), NOW());

-- =====================================================
-- 11. CUSTOMER_REQUESTS (with descriptions)
-- =====================================================
INSERT INTO customer_requests (request_code, sender_id, receiver_id, sender_location_id, receiver_location_id, service_type_id, distance_km, parcel_description, shipping_fee, cod_amount, estimated_delivery_time, status, note, description, created_at, updated_at) VALUES
('REQ-20251230-001', 1, 2, 5, 7, 1, 1725, '55 thùng Durex', 5000000, 1000000, DATE_ADD(NOW(), INTERVAL 3 DAY), 'IN_TRANSIT', 'Hàng dễ vỡ cần cẩn thận', 
 'Đơn hàng vận chuyển 55 thùng Durex từ HCM đến HN. Khách VIP đã đặt cọc 3 triệu. Yêu cầu giao đúng hẹn, thu COD 1 triệu. Luồng: Điểm A → Kho C → Kho D → Điểm B. Cần 6 chuyến PICKUP, 1 TRANSFER, 6 DELIVERY.', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
('REQ-20251230-002', 3, NULL, 6, 8, 2, 1700, 'Quần áo mùa đông', 3400000, 500000, DATE_ADD(NOW(), INTERVAL 5 DAY), 'PENDING', 'Chờ Customer xác nhận', 
 'Đơn hàng quần áo mùa đông gồm áo khoác và quần jeans. Chờ khách xác nhận thông tin và đặt cọc trước khi xử lý. Dịch vụ tiêu chuẩn 3-5 ngày.', NOW(), NOW()),
('REQ-20251230-003', 1, NULL, 5, 8, 4, 15, 'Tài liệu hợp đồng', 150000, 0, DATE_ADD(NOW(), INTERVAL 1 DAY), 'DELIVERED', 'Đã giao thành công', 
 'Đơn giao tài liệu hợp đồng gấp trong ngày. Dịch vụ SAME_DAY priority. Đã hoàn thành trong 4 tiếng, ký nhận đầy đủ.', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
('REQ-20251230-004', 2, NULL, 7, 6, 1, 1700, 'Điện thoại Samsung', 8500000, 15000000, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CONFIRMED', 'Chờ lấy hàng', 
 'Đơn hàng điện thoại Samsung Galaxy S24 Ultra 512GB. Hàng giá trị cao, cần bảo hiểm. Người nhận trả phí ship. COD 15 triệu.', NOW(), NOW()),
('REQ-20251230-005', 1, 2, 5, 7, 1, 10, 'Thiết bị y tế', 2000000, 50000000, DATE_ADD(NOW(), INTERVAL 1 DAY), 'IN_TRANSIT', 'Hàng y tế khẩn cấp', 
 'Vận chuyển thiết bị y tế từ kho thuốc đến bệnh viện. Yêu cầu xe chuyên dụng, nhiệt độ bảo quản lạnh. Phí ship thanh toán theo từng chặng, COD thu một lần.', NOW(), NOW());


-- 12. TRACKING_CODES
INSERT INTO tracking_codes (request_id, code, created_at) VALUES
(1, 'TRK-20251230-DUREX55', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 'TRK-20251230-CLOTHES', NOW()),
(3, 'TRK-20251230-DOCS01', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 'TRK-20251230-PHONE01', NOW()),
(5, 'TRK-20251230-MEDIC', NOW());

-- =====================================================
-- 13. TRIPS (with descriptions)
-- =====================================================
INSERT INTO trips (trip_type, shipper_id, vehicle_id, start_location_id, end_location_id, started_at, ended_at, cod_amount, status, note, description, created_at, updated_at) VALUES
-- PICKUP trips: Điểm A -> Kho C (6 chuyến lấy hàng 55 thùng)
('PICKUP', 1, 3, 5, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 47 HOUR), 0, 'COMPLETED', 'Chuyến 1/6', 
 'Chuyến lấy hàng đầu tiên cho đơn 55 thùng Durex. Shipper Đinh Văn Tài lái xe Van Ford Transit đến Công ty Durex VN lấy 10 thùng. Thời gian dự kiến 30 phút.', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
('PICKUP', 1, 3, 5, 1, DATE_SUB(NOW(), INTERVAL 46 HOUR), DATE_SUB(NOW(), INTERVAL 45 HOUR), 0, 'COMPLETED', 'Chuyến 2/6', 
 'Chuyến lấy hàng thứ 2. Tiếp tục lấy 10 thùng Durex từ điểm A về kho Tân Bình.', DATE_SUB(NOW(), INTERVAL 46 HOUR), NOW()),
('PICKUP', 2, 4, 5, 1, DATE_SUB(NOW(), INTERVAL 44 HOUR), DATE_SUB(NOW(), INTERVAL 43 HOUR), 0, 'COMPLETED', 'Chuyến 3/6', 
 'Chuyến lấy hàng thứ 3. Shipper Võ Thị Lan hỗ trợ lấy 10 thùng bằng xe Hyundai Solati.', DATE_SUB(NOW(), INTERVAL 44 HOUR), NOW()),
('PICKUP', 2, 4, 5, 1, DATE_SUB(NOW(), INTERVAL 42 HOUR), DATE_SUB(NOW(), INTERVAL 41 HOUR), 0, 'COMPLETED', 'Chuyến 4/6', 
 'Chuyến lấy hàng thứ 4. Tiếp tục lấy 10 thùng, tổng đã lấy 40 thùng.', DATE_SUB(NOW(), INTERVAL 42 HOUR), NOW()),
('PICKUP', 1, 3, 5, 1, DATE_SUB(NOW(), INTERVAL 40 HOUR), DATE_SUB(NOW(), INTERVAL 39 HOUR), 0, 'COMPLETED', 'Chuyến 5/6', 
 'Chuyến lấy hàng thứ 5. Lấy 10 thùng, tổng đã lấy 50 thùng.', DATE_SUB(NOW(), INTERVAL 40 HOUR), NOW()),
('PICKUP', 2, 4, 5, 1, DATE_SUB(NOW(), INTERVAL 38 HOUR), DATE_SUB(NOW(), INTERVAL 37 HOUR), 0, 'COMPLETED', 'Chuyến 6/6', 
 'Chuyến lấy hàng cuối cùng. Lấy 5 thùng còn lại. Hoàn thành gom 55 thùng tại kho Tân Bình.', DATE_SUB(NOW(), INTERVAL 38 HOUR), NOW()),

-- TRANSFER trip: Kho C -> Kho D (1 chuyến xe tải lớn 55 thùng)
('TRANSFER', 1, 5, 1, 3, DATE_SUB(NOW(), INTERVAL 36 HOUR), DATE_SUB(NOW(), INTERVAL 12 HOUR), 0, 'COMPLETED', 'Trung chuyển HCM->HN', 
 'Chuyến trung chuyển liên tỉnh 55 thùng từ kho Tân Bình HCM đến kho Hoàng Mai HN. Sử dụng xe tải Hino 3.5 tấn. Đi qua cao tốc, chạy đêm để tránh kẹt xe. Thời gian 24 tiếng.', DATE_SUB(NOW(), INTERVAL 36 HOUR), NOW()),

-- DELIVERY trips: Kho D -> Điểm B (6 chuyến giao hàng)
('DELIVERY', 3, 6, 3, 7, DATE_SUB(NOW(), INTERVAL 10 HOUR), DATE_SUB(NOW(), INTERVAL 9 HOUR), 0, 'COMPLETED', 'Giao chuyến 1/6', 
 'Chuyến giao hàng đầu tiên tại HN. Shipper Bùi Văn Hùng giao 10 thùng đến nhà phân phối. Người nhận ký nhận đầy đủ.', DATE_SUB(NOW(), INTERVAL 10 HOUR), NOW()),
('DELIVERY', 3, 6, 3, 7, DATE_SUB(NOW(), INTERVAL 8 HOUR), DATE_SUB(NOW(), INTERVAL 7 HOUR), 0, 'COMPLETED', 'Giao chuyến 2/6', 
 'Chuyến giao hàng thứ 2. Tiếp tục giao 10 thùng, tổng đã giao 20 thùng.', DATE_SUB(NOW(), INTERVAL 8 HOUR), NOW()),
('DELIVERY', 4, 6, 3, 7, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR), 0, 'COMPLETED', 'Giao chuyến 3/6', 
 'Chuyến giao hàng thứ 3. Shipper Lê Văn Minh hỗ trợ giao 10 thùng.', DATE_SUB(NOW(), INTERVAL 6 HOUR), NOW()),
('DELIVERY', 4, 6, 3, 7, DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR), 0, 'COMPLETED', 'Giao chuyến 4/6', 
 'Chuyến giao hàng thứ 4. Tổng đã giao 40 thùng, còn 15 thùng.', DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW()),
('DELIVERY', 3, 6, 3, 7, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), 0, 'IN_PROGRESS', 'Đang giao chuyến 5/6', 
 'Chuyến giao hàng thứ 5 đang thực hiện. Shipper đang trên đường giao 10 thùng.', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
('DELIVERY', 4, 6, 3, 7, NULL, NULL, 1000000, 'CREATED', 'Chờ giao chuyến 6/6 + THU COD', 
 'Chuyến giao hàng cuối cùng. Giao 5 thùng còn lại và thu COD 1 triệu từ nhà phân phối. Cần xác nhận trước khi xuất phát.', NOW(), NOW()),

-- Request 3: Đơn giao trong ngày (đã hoàn thành)
('PICKUP', 1, 1, 5, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 23 HOUR), 0, 'COMPLETED', 'Lấy tài liệu gấp', 
 'Chuyến lấy tài liệu hợp đồng gấp. Sử dụng xe máy SH để di chuyển nhanh trong nội thành.', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
('DELIVERY', 1, 1, 1, 8, DATE_SUB(NOW(), INTERVAL 22 HOUR), DATE_SUB(NOW(), INTERVAL 20 HOUR), 0, 'COMPLETED', 'Giao tài liệu thành công', 
 'Chuyến giao tài liệu trong ngày. Dịch vụ SAME_DAY hoàn thành trong 4 tiếng. Khách hàng ký nhận đầy đủ.', DATE_SUB(NOW(), INTERVAL 22 HOUR), NOW()),

-- Request 4: Đơn mới xác nhận
('PICKUP', 3, 6, 7, 3, NULL, NULL, 0, 'CREATED', 'Chờ lấy điện thoại Samsung', 
 'Chuyến lấy điện thoại Samsung Galaxy S24 Ultra. Hàng giá trị cao cần cẩn thận. Chờ xác nhận thời gian từ người gửi.', NOW(), NOW()),

-- TRIPS cho Request 5 (Thiết bị y tế):
-- Trip 13: Pickup từ kho thuốc về kho trung tâm
('PICKUP', 1, 5, 5, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 4 HOUR), 0, 'COMPLETED', 'Lấy hàng y tế', 
 'Lấy thiết bị y tế bằng xe chuyên dụng có kho lạnh. Đã lấy xong.', DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW()),
-- Trip 14: Delivery từ kho trung tâm đến bệnh viện
('DELIVERY', 2, 5, 1, 7, DATE_SUB(NOW(), INTERVAL 4 HOUR), NULL, 50000000, 'IN_PROGRESS', 'Giao hàng y tế', 
 'Đang giao thiết bị đến bệnh viện. Cần thu COD 50tr.', DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW());


-- =====================================================
-- 14. PARCELS - 55 thùng Durex + các đơn khác
-- =====================================================
INSERT INTO parcels (request_id, parcel_code, description, cod_amount, weight_kg, length_cm, width_cm, height_cm, current_location_id, current_shipper_id, current_trip_id, status, created_at, updated_at) VALUES
-- Thùng 1-10 (Đã giao)
(1, 'PCL-20251230-1-01', 'Thùng Durex 01 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-02', 'Thùng Durex 02 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-03', 'Thùng Durex 03 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-04', 'Thùng Durex 04 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-05', 'Thùng Durex 05 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-06', 'Thùng Durex 06 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-07', 'Thùng Durex 07 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-08', 'Thùng Durex 08 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-09', 'Thùng Durex 09 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-10', 'Thùng Durex 10 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
-- Thùng 11-20 (Đã giao)
(1, 'PCL-20251230-1-11', 'Thùng Durex 11 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-12', 'Thùng Durex 12 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-13', 'Thùng Durex 13 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-14', 'Thùng Durex 14 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-15', 'Thùng Durex 15 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-16', 'Thùng Durex 16 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-17', 'Thùng Durex 17 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-18', 'Thùng Durex 18 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-19', 'Thùng Durex 19 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-20', 'Thùng Durex 20 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
-- Thùng 21-30 (Đã giao)
(1, 'PCL-20251230-1-21', 'Thùng Durex 21 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-22', 'Thùng Durex 22 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-23', 'Thùng Durex 23 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-24', 'Thùng Durex 24 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-25', 'Thùng Durex 25 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-26', 'Thùng Durex 26 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-27', 'Thùng Durex 27 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-28', 'Thùng Durex 28 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-29', 'Thùng Durex 29 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-30', 'Thùng Durex 30 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
-- Thùng 31-40 (Đã giao)
(1, 'PCL-20251230-1-31', 'Thùng Durex 31 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-32', 'Thùng Durex 32 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-33', 'Thùng Durex 33 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-34', 'Thùng Durex 34 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-35', 'Thùng Durex 35 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-36', 'Thùng Durex 36 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-37', 'Thùng Durex 37 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-38', 'Thùng Durex 38 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-39', 'Thùng Durex 39 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-40', 'Thùng Durex 40 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 7, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
-- Thùng 41-50 (Đang giao - trip 12)
(1, 'PCL-20251230-1-41', 'Thùng Durex 41 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-42', 'Thùng Durex 42 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-43', 'Thùng Durex 43 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-44', 'Thùng Durex 44 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-45', 'Thùng Durex 45 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-46', 'Thùng Durex 46 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-47', 'Thùng Durex 47 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-48', 'Thùng Durex 48 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-49', 'Thùng Durex 49 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-50', 'Thùng Durex 50 - Hộp 144 chiếc', 0, 5, 40, 30, 30, 3, 3, 12, 'OUT_FOR_DELIVERY', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
-- Thùng 51-55 (Chờ giao - trip 13)
(1, 'PCL-20251230-1-51', 'Thùng Durex 51 - Hộp 144 chiếc (Chia COD)', 200000, 5, 40, 30, 30, 3, 4, 13, 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-52', 'Thùng Durex 52 - Hộp 144 chiếc (Chia COD)', 200000, 5, 40, 30, 30, 3, 4, 13, 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-53', 'Thùng Durex 53 - Hộp 144 chiếc (Chia COD)', 200000, 5, 40, 30, 30, 3, 4, 13, 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-54', 'Thùng Durex 54 - Hộp 144 chiếc (Chia COD)', 200000, 5, 40, 30, 30, 3, 4, 13, 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, 'PCL-20251230-1-55', 'Thùng Durex 55 - Hộp 144 chiếc + THU COD', 200000, 5, 40, 30, 30, 3, 4, 13, 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

-- Request 2: Quần áo
(2, 'PCL-20251230-2-01', 'Áo khoác mùa đông cao cấp', 250000, 1.5, 50, 40, 15, 1, NULL, NULL, 'CREATED', NOW(), NOW()),
(2, 'PCL-20251230-2-02', 'Quần jeans nam nữ', 250000, 1.2, 45, 35, 10, 1, NULL, NULL, 'CREATED', NOW(), NOW()),

-- Request 3: Tài liệu (đã giao)
(3, 'PCL-20251230-3-01', 'Hồ sơ hợp đồng và tài liệu pháp lý', 0, 0.5, 35, 25, 5, 8, NULL, NULL, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

-- Request 4: Điện thoại (mới xác nhận)
(4, 'PCL-20251230-4-01', 'Samsung Galaxy S24 Ultra 512GB mới nguyên seal', 15000000, 0.3, 20, 10, 8, 7, NULL, NULL, 'CREATED', NOW(), NOW()),

-- Request 5: Thiết bị y tế (1 kiện)
(5, 'PCL-20251230-5-01', 'Thiết bị y tế - Máy thở', 50000000, 150, 100, 80, 120, 5, 2, 13, 'IN_TRANSIT', DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW());

-- =====================================================
-- 17. PARCEL_ACTIONS
-- =====================================================
INSERT INTO parcel_actions (parcel_id, request_id, action_type_id, from_location_id, to_location_id, actor_user_id, note, created_at) VALUES
-- Request 1: Actions cho thùng đầu tiên
(1, 1, 1, NULL, NULL, 10, 'Customer A tạo đơn 55 thùng Durex', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 1, 2, NULL, NULL, 2, 'Manager xác nhận, thu cọc 3 triệu qua VCB', DATE_SUB(NOW(), INTERVAL 47 HOUR)),
(1, 1, 4, 5, 1, 6, 'Shipper Đinh Văn Tài lấy thùng 1-10 từ điểm A', DATE_SUB(NOW(), INTERVAL 46 HOUR)),
(1, 1, 5, NULL, 1, 4, 'Nhập kho Tân Bình, kiểm tra chất lượng OK', DATE_SUB(NOW(), INTERVAL 45 HOUR)),
(1, 1, 6, 1, 3, 6, 'Trung chuyển HCM->HN bằng xe tải Hino, chạy đêm', DATE_SUB(NOW(), INTERVAL 36 HOUR)),
(1, 1, 5, NULL, 3, 5, 'Nhập kho Hoàng Mai HN, xếp hàng chờ giao', DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(1, 1, 7, 3, 7, 8, 'Xuất kho giao cho nhà phân phối', DATE_SUB(NOW(), INTERVAL 10 HOUR)),
(1, 1, 8, NULL, 7, 8, 'Giao thành công thùng 1, ký nhận đầy đủ', DATE_SUB(NOW(), INTERVAL 9 HOUR)),

-- Request 3: Đơn đã hoàn thành
(58, 3, 1, NULL, NULL, 10, 'Tạo đơn giao tài liệu gấp', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(58, 3, 4, 5, 1, 6, 'Lấy tài liệu bằng xe SH', DATE_SUB(NOW(), INTERVAL 23 HOUR)),
(58, 3, 8, NULL, 8, 6, 'Giao tài liệu thành công trong 4 tiếng', DATE_SUB(NOW(), INTERVAL 20 HOUR)),

-- Request 5: Thiết bị y tế (flow PER_TRIP)
(59, 5, 1, NULL, NULL, 10, 'Tạo đơn thiết bị y tế khẩn cấp', DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(59, 5, 4, 5, 1, 2, 'Xe chuyên dụng lấy hàng tại kho thuốc', DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(59, 5, 7, 1, 7, 5, 'Xuất kho giao đến bệnh viện', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(59, 5, 6, NULL, 7, 5, 'Đang giao hàng đến bệnh viện', DATE_SUB(NOW(), INTERVAL 4 HOUR));

-- 18. CASE_STUDIES
INSERT INTO case_studies (request_id, service_type_id, title, slug, client_name_display, challenge, solution, result, thumbnail_url, is_featured, is_published, created_at, updated_at) VALUES
(1, 1, 'Vận chuyển 55 thùng hàng liên tỉnh', 'van-chuyen-55-thung-lien-tinh', 'Công ty Durex VN', 
 'Cần vận chuyển 55 thùng hàng từ HCM ra Hà Nội trong 3 ngày, đảm bảo an toàn và thu COD.', 
 'Sử dụng dịch vụ Express với hệ thống tracking real-time, phân chia thành 6 chuyến PICKUP, 1 chuyến TRANSFER lớn và 6 chuyến DELIVERY.', 
 'Giao thành công 100% trong 2.5 ngày, thu COD đầy đủ, khách hàng hài lòng.', '/images/casestudy-durex.jpg', true, true, NOW(), NOW()),
(3, 4, 'Giao tài liệu gấp trong ngày', 'giao-tai-lieu-gap-trong-ngay', 'Công ty ABC', 
 'Cần gửi hồ sơ trong ngày để kịp deadline ký hợp đồng quan trọng.', 
 'Dịch vụ SAME_DAY với shipper chuyên dụng, GPS tracking realtime, ưu tiên xử lý.', 
 'Giao thành công trong 4 tiếng, khách hàng ký hợp đồng kịp thời hạn.', '/images/casestudy-docs.jpg', true, true, NOW(), NOW());

-- 19. SYSTEM_LOGS
INSERT INTO system_logs (log_level, module_name, action_type, actor_id, target_id, log_details, ip_address, user_agent, created_at) VALUES
('INFO', 'AUTH', 'LOGIN', 1, NULL, 'Admin đăng nhập vào hệ thống', '192.168.1.1', 'Chrome', NOW()),
('INFO', 'ORDER', 'CREATE', 10, '1', 'Customer A tạo đơn 55 thùng Durex', '192.168.1.100', 'Chrome', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('INFO', 'ORDER', 'CONFIRM', 2, '1', 'Manager Trần Văn Quản xác nhận đơn', '192.168.1.50', 'Chrome', DATE_SUB(NOW(), INTERVAL 47 HOUR)),
('INFO', 'PAYMENT', 'DEPOSIT', 10, '1', 'Khách đặt cọc 3,000,000đ qua VCB', '192.168.1.100', 'Chrome', DATE_SUB(NOW(), INTERVAL 47 HOUR)),
('INFO', 'TRIP', 'CREATE', 2, '1', 'Tạo 6 chuyến PICKUP cho đơn Durex', '192.168.1.50', 'Chrome', DATE_SUB(NOW(), INTERVAL 46 HOUR)),
('INFO', 'TRIP', 'CREATE', 2, '7', 'Tạo chuyến TRANSFER HCM->HN', '192.168.1.50', 'Chrome', DATE_SUB(NOW(), INTERVAL 36 HOUR)),
('INFO', 'TRIP', 'COMPLETE', 6, '7', 'Hoàn thành trung chuyển 55 thùng', '10.0.0.6', 'Mobile', DATE_SUB(NOW(), INTERVAL 12 HOUR)),
('INFO', 'PAYMENT', 'SUCCESS', 4, '5', 'Thanh toán đơn tài liệu qua VNPAY', '192.168.1.60', 'Chrome', DATE_SUB(NOW(), INTERVAL 20 HOUR)),
('WARN', 'SHIPPER', 'GPS_FAILED', 8, NULL, 'Lỗi cập nhật GPS shipper 3', '10.0.0.8', 'Mobile', NOW());

-- =====================================================
-- 20. PAYMENTS (with trip_id and payment_scope)
-- =====================================================
INSERT INTO payments (request_id, parcel_id, trip_id, payment_code, payment_type, payer_type, receiver_type, payment_scope, description, expected_amount, paid_amount, status, created_at, updated_at) VALUES
-- Request 1: Thanh toán TOÀN BỘ cho cả đơn Durex (FULL_REQUEST) - TUÂN THỦ NGUYÊN TẮC: Đã Full thì không Per Trip
(1, NULL, NULL, 'PAY-001-SHIP-FULL', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'FULL_REQUEST', 'Phí vận chuyển TOÀN BỘ đơn 55 thùng Durex từ HCM đến HN. Bao gồm 6 chuyến PICKUP + 1 TRANSFER + 6 DELIVERY.', 5000000.00, 3000000.00, 'PARTIALLY_PAID', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(1, NULL, NULL, 'PAY-001-COD-FULL', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu hộ COD TOÀN BỘ tiền hàng 55 thùng Durex. Thu một lần khi giao hàng hoàn tất.', 1000000.00, 0.00, 'UNPAID', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

-- Request 2: Đơn chờ xử lý
(2, NULL, NULL, 'PAY-002-SHIP-1', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'FULL_REQUEST', 'Phí vận chuyển toàn bộ quần áo mùa đông. Chờ khách xác nhận.', 3400000.00, 0.00, 'UNPAID', NOW(), NOW()),
(2, NULL, NULL, 'PAY-002-COD-1', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu hộ COD toàn bộ đơn quần áo.', 500000.00, 0.00, 'UNPAID', NOW(), NOW()),

-- Request 3: Đơn đã hoàn thành - thanh toán theo trip (PER_TRIP)
(3, NULL, 11, 'PAY-003-SHIP-T11', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'PER_TRIP', 'Phí giao tài liệu SAME_DAY - chuyến delivery cuối. Đã thanh toán qua VNPay.', 150000.00, 150000.00, 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 20 HOUR)),

-- Request 4: Thanh toán TOÀN BỘ (FULL_REQUEST)
(4, NULL, NULL, 'PAY-004-SHIP-FULL', 'SHIPPING_FEE', 'RECEIVER', 'COMPANY', 'FULL_REQUEST', 'Phí vận chuyển TOÀN BỘ điện thoại Samsung. Người nhận trả phí. Bao gồm bảo hiểm hàng hóa.', 8500000.00, 0.00, 'UNPAID', NOW(), NOW()),
(4, NULL, NULL, 'PAY-004-COD-FULL', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu hộ COD TOÀN BỘ 15 triệu tiền điện thoại Samsung.', 15000000.00, 0.00, 'UNPAID', NOW(), NOW()),

-- Request 5: DEMO PER_TRIP (Đơn hàng thiết bị y tế - Phí ship trả theo từng chặng, COD thu cuối cùng)
-- Trip 13 (Pickup): Phí 500k -> Đã thanh toán
(5, NULL, 13, 'PAY-005-SHIP-T13', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'PER_TRIP', 'Phí lấy hàng tại kho thuốc. Trip PICKUP #13.', 500000.00, 500000.00, 'PAID', DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW()),
-- Trip 14 (Delivery): Phí 700k -> Chưa thanh toán
(5, NULL, 14, 'PAY-005-SHIP-T14', 'SHIPPING_FEE', 'SENDER', 'COMPANY', 'PER_TRIP', 'Phí giao hàng đến bệnh viện. Trip DELIVERY #14.', 700000.00, 0.00, 'UNPAID', DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW()),
-- COD: Thu toàn bộ 50tr (FULL_REQUEST)
(5, NULL, NULL, 'PAY-005-COD-FULL', 'COD', 'RECEIVER', 'SENDER', 'FULL_REQUEST', 'Thu hộ COD thiết bị y tế. Thu 1 lần khi giao hàng xong.', 50000000.00, 0.00, 'UNPAID', DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW());


-- =====================================================
-- 21. PAYMENT_TRANSACTIONS
-- =====================================================
INSERT INTO payment_transactions (payment_id, amount, transaction_type, payment_method, transaction_ref, status, performed_by_id, transaction_at, created_at) VALUES
-- Request 1: Đặt cọc 3 triệu
(1, 3000000, 'IN', 'BANK_TRANSFER', 'VCB-20251230-001', 'SUCCESS', 10, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Request 3: Thanh toán đầy đủ 150k
(5, 150000, 'IN', 'VNPAY', 'VNPAY-20251230-001', 'SUCCESS', 10, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Request 5: Thanh toán lẻ trip 13 (500k)
(9, 500000, 'IN', 'CASH', 'CASH-T13-RE5', 'SUCCESS', 10, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR));

-- =====================================================
-- DONE! Script v5.2 - Clean and Synced
-- =====================================================
