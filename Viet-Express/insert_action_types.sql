-- =====================================================
-- SQL Script: Insert ActionTypes (Loại thao tác)
-- Hệ thống: Viet-Express Logistics
-- Ngày tạo: 02/01/2026
-- =====================================================

-- Xóa dữ liệu cũ (nếu cần reset)
-- DELETE FROM action_types;

-- =====================================================
-- NHÓM 1: QUẢN LÝ ĐƠN HÀNG (ORDER/REQUEST)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATE_ORDER', 'Tạo đơn hàng', 'Khách hàng tạo đơn hàng mới', NOW(), NOW()),
('UPDATE_ORDER', 'Cập nhật đơn hàng', 'Cập nhật thông tin đơn hàng', NOW(), NOW()),
('CONFIRM_ORDER', 'Xác nhận đơn hàng', 'Manager xác nhận/duyệt đơn hàng', NOW(), NOW()),
('CANCEL_ORDER', 'Hủy đơn hàng', 'Hủy đơn hàng', NOW(), NOW()),
('RECEIVER_CONFIRM', 'Người nhận xác nhận', 'Người nhận xác nhận đồng ý nhận hàng', NOW(), NOW()),
('RECEIVER_REJECT', 'Người nhận từ chối', 'Người nhận từ chối nhận hàng', NOW(), NOW());

-- =====================================================
-- NHÓM 2: QUẢN LÝ KIỆN HÀNG (PARCEL)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATE_PARCEL', 'Tạo kiện hàng', 'Tạo kiện hàng mới từ đơn hàng', NOW(), NOW()),
('UPDATE_PARCEL', 'Cập nhật kiện hàng', 'Cập nhật thông tin kiện hàng', NOW(), NOW()),
('SCAN_PARCEL', 'Quét mã kiện', 'Quét mã vạch/QR kiện hàng', NOW(), NOW()),
('WEIGH_PARCEL', 'Cân kiện hàng', 'Cân đo kích thước và trọng lượng kiện', NOW(), NOW());

-- =====================================================
-- NHÓM 3: LẤY HÀNG (PICKUP)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('ASSIGN_PICKUP', 'Gán shipper lấy hàng', 'Manager gán shipper đi lấy hàng', NOW(), NOW()),
('START_PICKUP', 'Bắt đầu lấy hàng', 'Shipper bắt đầu đi lấy hàng', NOW(), NOW()),
('PICKUP_SUCCESS', 'Lấy hàng thành công', 'Shipper lấy hàng thành công từ sender', NOW(), NOW()),
('PICKUP_FAIL', 'Lấy hàng thất bại', 'Không lấy được hàng (sender vắng, sai địa chỉ...)', NOW(), NOW());

-- =====================================================
-- NHÓM 4: QUẢN LÝ KHO (WAREHOUSE)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('RECEIVE_WAREHOUSE', 'Nhập kho', 'Staff nhận hàng vào kho', NOW(), NOW()),
('DISPATCH_WAREHOUSE', 'Xuất kho', 'Staff xuất hàng khỏi kho', NOW(), NOW()),
('TRANSFER_WAREHOUSE', 'Chuyển kho', 'Chuyển hàng giữa các kho', NOW(), NOW()),
('CHECK_INVENTORY', 'Kiểm kê kho', 'Kiểm tra số lượng hàng trong kho', NOW(), NOW());

-- =====================================================
-- NHÓM 5: VẬN CHUYỂN (TRIP/TRANSIT)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATE_TRIP', 'Tạo chuyến', 'Tạo chuyến vận chuyển mới', NOW(), NOW()),
('ASSIGN_TRIP', 'Gán chuyến', 'Gán shipper cho chuyến vận chuyển', NOW(), NOW()),
('START_TRIP', 'Bắt đầu chuyến', 'Shipper bắt đầu chuyến vận chuyển', NOW(), NOW()),
('END_TRIP', 'Kết thúc chuyến', 'Shipper hoàn thành chuyến vận chuyển', NOW(), NOW()),
('CANCEL_TRIP', 'Hủy chuyến', 'Hủy chuyến vận chuyển', NOW(), NOW()),
('ADD_PARCEL_TRIP', 'Thêm hàng vào chuyến', 'Thêm kiện hàng vào chuyến vận chuyển', NOW(), NOW()),
('REMOVE_PARCEL_TRIP', 'Bỏ hàng khỏi chuyến', 'Loại bỏ kiện hàng khỏi chuyến', NOW(), NOW());

-- =====================================================
-- NHÓM 6: GIAO HÀNG (DELIVERY)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('START_DELIVERY', 'Bắt đầu giao hàng', 'Shipper bắt đầu giao hàng cho receiver', NOW(), NOW()),
('DELIVER_SUCCESS', 'Giao hàng thành công', 'Giao hàng thành công cho receiver', NOW(), NOW()),
('DELIVER_FAIL', 'Giao hàng thất bại', 'Giao hàng thất bại (receiver vắng, từ chối...)', NOW(), NOW()),
('DELIVER_PARTIAL', 'Giao hàng một phần', 'Chỉ giao được một phần đơn hàng', NOW(), NOW()),
('RESCHEDULE_DELIVERY', 'Hẹn giao lại', 'Hẹn lịch giao hàng lần sau', NOW(), NOW());

-- =====================================================
-- NHÓM 7: HOÀN TRẢ (RETURN)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATE_RETURN', 'Tạo hoàn trả', 'Tạo yêu cầu hoàn trả hàng', NOW(), NOW()),
('START_RETURN', 'Bắt đầu hoàn trả', 'Bắt đầu chuyến hoàn trả', NOW(), NOW()),
('RETURN_SUCCESS', 'Hoàn trả thành công', 'Hoàn trả hàng thành công cho sender', NOW(), NOW()),
('RETURN_FAIL', 'Hoàn trả thất bại', 'Hoàn trả thất bại', NOW(), NOW());

-- =====================================================
-- NHÓM 8: THANH TOÁN (PAYMENT)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('CREATE_PAYMENT', 'Tạo thanh toán', 'Tạo phiếu thanh toán', NOW(), NOW()),
('PAYMENT_RECEIVED', 'Nhận thanh toán', 'Nhận tiền thanh toán từ khách', NOW(), NOW()),
('COD_COLLECTED', 'Thu COD', 'Shipper thu tiền COD từ receiver', NOW(), NOW()),
('REFUND', 'Hoàn tiền', 'Hoàn tiền cho khách hàng', NOW(), NOW()),
('PAYMENT_CONFIRMED', 'Xác nhận thanh toán', 'Xác nhận đã nhận thanh toán', NOW(), NOW());

-- =====================================================
-- NHÓM 9: VỊ TRÍ & THEO DÕI (LOCATION/TRACKING)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('UPDATE_LOCATION', 'Cập nhật vị trí', 'Shipper cập nhật vị trí hiện tại', NOW(), NOW()),
('CHECK_IN', 'Check-in', 'Shipper check-in tại địa điểm', NOW(), NOW()),
('CHECK_OUT', 'Check-out', 'Shipper check-out khỏi địa điểm', NOW(), NOW());

-- =====================================================
-- NHÓM 10: QUẢN LÝ TÀI KHOẢN (USER/ACCOUNT)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('USER_LOGIN', 'Đăng nhập', 'Người dùng đăng nhập hệ thống', NOW(), NOW()),
('USER_LOGOUT', 'Đăng xuất', 'Người dùng đăng xuất hệ thống', NOW(), NOW()),
('USER_REGISTER', 'Đăng ký', 'Đăng ký tài khoản mới', NOW(), NOW()),
('USER_UPDATE', 'Cập nhật tài khoản', 'Cập nhật thông tin tài khoản', NOW(), NOW()),
('PASSWORD_CHANGE', 'Đổi mật khẩu', 'Thay đổi mật khẩu', NOW(), NOW()),
('PASSWORD_RESET', 'Reset mật khẩu', 'Yêu cầu reset mật khẩu', NOW(), NOW()),
('ACCOUNT_ACTIVATE', 'Kích hoạt tài khoản', 'Kích hoạt tài khoản', NOW(), NOW()),
('ACCOUNT_DEACTIVATE', 'Vô hiệu hóa tài khoản', 'Vô hiệu hóa tài khoản', NOW(), NOW());

-- =====================================================
-- NHÓM 11: HỆ THỐNG (SYSTEM)
-- =====================================================
INSERT INTO action_types (action_code, name, description, created_at, updated_at) VALUES
('SYSTEM_CONFIG', 'Cấu hình hệ thống', 'Thay đổi cấu hình hệ thống', NOW(), NOW()),
('DATA_EXPORT', 'Xuất dữ liệu', 'Xuất dữ liệu ra file', NOW(), NOW()),
('DATA_IMPORT', 'Nhập dữ liệu', 'Nhập dữ liệu từ file', NOW(), NOW()),
('GENERATE_REPORT', 'Tạo báo cáo', 'Tạo báo cáo thống kê', NOW(), NOW()),
('SEND_NOTIFICATION', 'Gửi thông báo', 'Gửi thông báo cho người dùng', NOW(), NOW()),
('SEND_SMS', 'Gửi SMS', 'Gửi tin nhắn SMS', NOW(), NOW()),
('SEND_EMAIL', 'Gửi Email', 'Gửi email thông báo', NOW(), NOW());

-- =====================================================
-- KIỂM TRA DỮ LIỆU ĐÃ INSERT
-- =====================================================
SELECT 
    action_type_id as ID,
    action_code as 'Mã',
    name as 'Tên',
    description as 'Mô tả'
FROM action_types 
ORDER BY action_type_id;

-- Tổng số ActionTypes
SELECT COUNT(*) as 'Tổng số ActionTypes' FROM action_types;
