# Changelog - 3 Tính năng mới

> **Ngày hoàn thành:** 2026-01-05  
> **Phiên bản:** v2.0.0

Tài liệu này mô tả chi tiết các thay đổi từ 3 tính năng:
1. Payment Status History
2. Auto ParcelAction + Public Tracking
3. Route-Based Shipping Fee

---

## 1. Payment Status History

### 🎯 Mục tiêu
Khi trạng thái thanh toán (`Payment.status`) thay đổi, hệ thống cần ghi lại lịch sử để:
- Theo dõi ai đã thay đổi (Manager/System)
- Biết thời điểm thay đổi
- Tra cứu lịch sử trong giao diện

### 📁 Chi tiết thay đổi

#### Entity & Repository

| File | Thay đổi | Lý do |
|------|----------|-------|
| `Entities/PaymentTransaction.java` | Thêm `TransactionType.STATUS_CHANGE`, thêm fields `oldPaymentStatus`, `newPaymentStatus`, `actorType` | Sử dụng lại bảng `PaymentTransaction` để lưu lịch sử trạng thái thay vì tạo entity mới, tiết kiệm bảng DB |
| `Repositories/PaymentTransactionRepository.java` | Thêm query `findByPaymentIdAndTransactionTypeOrderByCreatedAtDesc()` | Lấy lịch sử thay đổi trạng thái theo thứ tự thời gian |

#### Service Layer

| File | Thay đổi | Lý do |
|------|----------|-------|
| `Services/PaymentService.java` | Thêm method `changePaymentStatus(paymentId, newStatus, actor, actorType, note)` | Tạo một method duy nhất để thay đổi status, đảm bảo TẤT CẢ thay đổi đều được ghi log |
| `Services/Impl/PaymentServiceImpl.java` | Implement `changePaymentStatus()`, `getStatusHistory()`, sửa `updatePaymentPaidAmount()` để gọi qua method mới | Tập trung logic, tránh bỏ sót log khi có nhiều nơi thay đổi status |

#### Controller Layer

| File | Thay đổi | Lý do |
|------|----------|-------|
| `Controllers/Manager/ManagerPaymentController.java` | Thêm API `/payments/{id}/status-history`, sửa `updatePayment()` dùng `changePaymentStatus()` | Manager cần xem lịch sử và log thao tác của mình |
| `Controllers/Customer/CustomerPaymentsController.java` | Thêm API `/payments/{id}/status-history`, sửa `simulatePayment()` dùng `changePaymentStatus()` với actor=SYSTEM | Customer xem lịch sử, hệ thống mô phỏng thanh toán cần log là SYSTEM |

#### Frontend

| File | Thay đổi | Lý do |
|------|----------|-------|
| `templates/manager/payment/detail.html` | Thêm nút "Lịch sử thanh toán" + Modal hiển thị timeline | Manager xem trực quan lịch sử thay đổi trạng thái |

---

## 2. Auto ParcelAction + Public Tracking

### 🎯 Mục tiêu
- Tự động ghi nhận `ParcelAction` khi có sự kiện (tạo đơn, xác nhận, chốt đơn...)
- Cho phép tra cứu công khai mà không cần đăng nhập
- Xóa entity `TrackingCode` vì không còn cần thiết (dùng `RequestCode`/`ParcelCode` thay thế)

### 📁 Chi tiết thay đổi

#### Tạo mới

| File | Mô tả | Lý do |
|------|-------|-------|
| `Services/ParcelActionService.java` | Interface service ghi action | Tách biệt logic ghi action, dễ inject vào nhiều controller |
| `Services/Impl/ParcelActionServiceImpl.java` | Implementation với method `recordAction()` | Tập trung logic lưu ParcelAction + lookup ActionType |
| `Controllers/PublicTrackingController.java` | Controller `/tracking` không cần đăng nhập | Khách hàng có thể tra cứu bằng mã vận đơn mà không cần tài khoản |
| `templates/public/tracking.html` | Trang tra cứu với form search + timeline | Giao diện thân thiện, ẩn thông tin nhạy cảm (SĐT, giá COD...) |

#### Sửa đổi - Tích hợp ghi Action

| File | Thay đổi | Lý do |
|------|----------|-------|
| `Config/DataInitializer.java` | Thêm 11 ActionType mới (CREATED, RECEIVER_CONFIRMED, CONFIRMED, PICKED_UP...) | Định nghĩa các loại action cho toàn bộ flow vận chuyển |
| `Controllers/Customer/CustomerOrderCreateController.java` | Gọi `trackingService.logCreated()` sau khi tạo đơn | Ghi nhận action CREATED khi khách tạo đơn |
| `Controllers/Customer/CustomerOrderDetailController.java` | Gọi `trackingService.logAction(RECEIVER_CONFIRMED)` khi receiver xác nhận | Ghi nhận người nhận đã đồng ý nhận hàng |
| `Controllers/Manager/ManagerRequestController.java` | Gọi `trackingService.logAction(CONFIRMED)` khi chốt đơn | Ghi nhận Manager đã xác nhận và sẵn sàng vận chuyển |

#### Sửa đổi - Thêm link tra cứu

| File | Thay đổi | Lý do |
|------|----------|-------|
| `templates/manager/fragments/sidebar.html` | Thêm link "Tra cứu vận đơn" | Manager có thể tra cứu nhanh |
| `templates/customer/fragments/sidebar.html` | Thay thế link cũ bằng link mới | Customer dùng trang tra cứu chung |
| `templates/staff/fragments/sidebar.html` | Thêm link "Tra cứu vận đơn" | Staff có thể tra cứu |
| `templates/shipper/fragments/sidebar.html` | Thêm link "Tra cứu vận đơn" | Shipper có thể tra cứu |
| `templates/admin/fragments/sidebar.html` | Thêm link "Tra cứu vận đơn" | Admin có thể tra cứu |
| `templates/public/home.html` | Thêm form tra cứu vào hero section | Người dùng chưa đăng nhập có thể tra cứu ngay từ trang chủ |

#### Xóa entity TrackingCode

| File | Hành động | Lý do |
|------|-----------|-------|
| `Entities/TrackingCode.java` | **XÓA** | Không cần thiết - dùng RequestCode/ParcelCode thay thế |
| `DTO/TrackingCodeDTO.java` | **XÓA** | DTO không còn entity |
| `Repositories/TrackingCodeRepository.java` | **XÓA** | Repository không còn entity |
| `Services/TrackingService.java` | Xóa các method liên quan TrackingCode | Interface sạch hơn, chỉ giữ ParcelAction |
| `Services/Impl/TrackingServiceImpl.java` | Xóa logic TrackingCode | Implementation sạch hơn |
| `Services/CustomerRequestService.java` | Xóa `findByTrackingCodeEntity()`, `findTrackingCodesByRequestIdEntities()` | Không còn cần thiết |
| `Services/Impl/CustomerRequestServiceImpl.java` | Xóa 2 method + TrackingCodeRepository | Giảm dependency |
| `Controllers/Customer/CustomerOrderDetailController.java` | Xóa dòng `trackingCodes` trong model | Template không cần nữa |
| `Controllers/Customer/CustomerTrackingController.java` | Xóa logic tìm theo TRK-xxx | Chỉ dùng REQ-xxx hoặc PCL-xxx |

---

## 3. Route-Based Shipping Fee

### 🎯 Mục tiêu
Khi customer tạo đơn với 2 địa điểm chưa có Route:
- Phí = NULL → hiển thị "Cần cập nhật tuyến đường"
- Manager tạo Route → tự động cập nhật phí cho TẤT CẢ đơn hàng cũ có cùng route

### 📁 Chi tiết thay đổi

#### Tạo mới

| File | Mô tả | Lý do |
|------|-------|-------|
| `Controllers/Manager/ManagerRouteController.java` | CRUD routes với auto-update fee | Manager quản lý tuyến đường, hệ thống tự cập nhật phí |
| `templates/manager/route/routes.html` | Danh sách routes | Giao diện xem danh sách + thống kê |
| `templates/manager/route/form.html` | Form tạo/sửa route với checkbox "Tạo cả chiều về" | Tiện lợi - tạo 2 chiều cùng lúc |

#### Sửa đổi - Backend

| File | Thay đổi | Lý do |
|------|----------|-------|
| `Services/Impl/CustomerRequestServiceImpl.java` | Thêm `RouteRepository`, sửa `createRequest()` check Route trước khi tính phí | Nếu không có Route → fee=null, chờ Manager tạo |
| `Services/Impl/CustomerRequestServiceImpl.java` | Thêm `updateShippingFeeForRoute(fromLocationId, toLocationId, distanceKm)` | Khi tạo Route, tự động cập nhật phí cho các đơn hàng cũ có cùng senderLocation + receiverLocation |
| `Services/CustomerRequestService.java` | Thêm method signature | Interface cần khai báo |
| `Repositories/CustomerRequestRepository.java` | Thêm query `findByLocationAndFeeIsNull()` | Tìm đơn hàng cần cập nhật phí khi có Route mới |

#### Sửa đổi - Frontend

| File | Thay đổi | Lý do |
|------|----------|-------|
| `templates/manager/fragments/sidebar.html` | Thêm link "Tuyến đường" | Manager truy cập nhanh |
| `templates/manager/request/requests.html` | Badge warning "Cần cập nhật tuyến đường" khi fee=null, link đến create route | Manager thấy ngay đơn nào chưa có phí và có thể tạo route |

---

## 📊 Tổng kết

| # | Tính năng | Files mới | Files sửa | Files xóa |
|---|-----------|-----------|-----------|-----------|
| 1 | Payment Status History | 0 | 7 | 0 |
| 2 | Auto Tracking + Public Search | 4 | 12 | 3 |
| 3 | Route-Based Shipping Fee | 3 | 5 | 0 |
| **Tổng** | | **7** | **24** | **3** |

---

## ⚠️ Lưu ý sau khi deploy

1. **ActionTypes**: 11 types mới sẽ được tạo tự động qua `DataInitializer`
2. **Database**: Bảng `routes` đã có sẵn từ Entity định nghĩa trước
3. **TrackingCode**: Bảng `tracking_codes` có thể xóa nếu không còn dùng
4. **Restart required**: Cần restart app để áp dụng thay đổi

---

## 🧪 Cách test

### Payment History
1. Manager vào chi tiết Payment → nhấn "Lịch sử thanh toán"
2. Thay đổi trạng thái → refresh → xem lịch sử mới

### Public Tracking
1. Truy cập `/tracking` (không cần đăng nhập)
2. Nhập mã `REQ-xxx` hoặc `PCL-xxx` → xem thông tin + timeline

### Route-Based Fee
1. Tạo đơn với 2 địa điểm không có Route → phí hiện warning
2. Manager vào `/manager/routes` → Tạo route mới
3. Quay lại danh sách requests → phí đã được cập nhật
