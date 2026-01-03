# Hướng Dẫn Test Bugs Đã Sửa

## Thông tin Server
- **Base URL**: http://localhost:8081
- **Trang chủ**: http://localhost:8081/
- **Port mặc định**: 8081 (kiểm tra trong application.properties nếu khác)

---

## 1. Bug 1: Sửa lỗi màu text "Vận chuyển nhanh chóng..."
**URL**: http://localhost:8081/

**Cách test:**
1. Truy cập trang chủ
2. Kiểm tra phần hero section (phần đầu trang)
3. Text "Vận chuyển nhanh chóng, an toàn & tin cậy" phải có màu gradient (tím/xanh) thay vì màu cố định
4. Kiểm tra các từ "nhanh chóng", "an toàn", "tin cậy" có màu gradient đẹp

---

## 2. Bug 2: Fix lỗi 500 khi ấn lần đầu
**URL**: http://localhost:8081/customer/tracking

**Cách test:**
1. Đăng nhập với tài khoản Customer
2. Vào trang tracking: http://localhost:8081/customer/tracking
3. Nhập mã request code (VD: REQ-20251229-XXXXXX)
4. Nhấn "Tra cứu" - **Không được lỗi 500 ở lần nhấn đầu tiên**
5. Kiểm tra kết quả hiển thị đúng

---

## 3. Bug 3: Gửi hàng khách vãng lai
**URL**: http://localhost:8081/request

**Cách test:**
1. **KHÔNG đăng nhập** (hoặc đăng xuất)
2. Truy cập: http://localhost:8081/request
3. Điền form:
   - Họ và tên: Nguyễn Văn A
   - Số điện thoại: 0901234567
   - Email: test@example.com (tùy chọn)
   - Tên hàng hóa: Máy giặt
   - Trọng lượng: 20
   - Nơi đi: 123 Đường ABC, Quận 1, TP.HCM
   - Nơi đến: 456 Đường XYZ, Ba Đình, Hà Nội
   - Ghi chú: Test gửi hàng
4. Nhấn "GỬI YÊU CẦU NGAY"
5. Kiểm tra:
   - Redirect về trang chủ với thông báo thành công
   - Có mã đơn hàng trong thông báo
   - Email được gửi (nếu có email)

---

## 4. Bug 4: Gói vận chuyển hiển thị đầy đủ dịch vụ
**URL**: http://localhost:8081/customer/create-order

**Cách test:**
1. Đăng nhập với tài khoản Customer
2. Vào: http://localhost:8081/customer/create-order
3. Kiểm tra phần "Gói vận chuyển" (sidebar bên phải)
4. **Phải hiển thị TẤT CẢ các dịch vụ active** từ database, không chỉ 3 cái cố định
5. Mỗi dịch vụ có radio button, tên, giá, mô tả

---

## 5. Bug 5: Thêm số lượng kiện hàng, chia kích thước
**URL**: http://localhost:8081/customer/create-order

**Cách test:**
1. Đăng nhập với tài khoản Customer
2. Vào: http://localhost:8081/customer/create-order
3. Cuộn xuống phần "Thông tin hàng hóa & COD"
4. Kiểm tra:
   - **Có trường "Số lượng kiện hàng"** (input number, mặc định = 1)
   - **Kích thước được chia thành 3 ô riêng:**
     - Chiều dài (cm)
     - Chiều rộng (cm)
     - Chiều cao (cm)
   - Không còn trường "Kích thước (D x R x C)" chung nữa

---

## 6. Bug 6: Implement tracking (theo dõi đơn)
**URL**: http://localhost:8081/customer/tracking

**Cách test:**
1. Đăng nhập với tài khoản Customer
2. Vào: http://localhost:8081/customer/tracking
3. Nhập mã request code (VD: REQ-20251229-XXXXXX) - dùng mã từ một đơn hàng của bạn
4. Nhấn "Tra cứu"
5. Kiểm tra:
   - Hiển thị thông tin đơn hàng (người gửi, người nhận, địa chỉ)
   - Hiển thị trạng thái đơn hàng (badge màu)
   - **Có timeline "Lịch sử vận chuyển"** hiển thị các ParcelAction
   - Có link "Xem chi tiết đơn hàng"

---

## 7. Bug 7: Thanh toán hiển thị tổng hợp, search
**URL**: http://localhost:8081/customer/orders/{id}/payments

**Cách test:**
1. Đăng nhập với tài khoản Customer
2. Vào danh sách đơn hàng: http://localhost:8081/customer/orders
3. Chọn một đơn hàng có thanh toán
4. Vào tab "Thanh toán" hoặc URL: http://localhost:8081/customer/orders/{id}/payments
5. Kiểm tra:
   - Có thống kê tổng hợp (total payments, paid, unpaid, etc.)
   - Có form search với input để tìm kiếm
   - Search theo payment code hoặc description

---

## 8. Bug 8: Hồ sơ customer
**URL**: http://localhost:8081/customer/profile

**Cách test:**
1. Đăng nhập với tài khoản Customer
2. Vào: http://localhost:8081/customer/profile
3. Kiểm tra:
   - Hiển thị thông tin customer hiện tại
   - Có form chỉnh sửa với các trường: Họ tên, SĐT, Email, Địa chỉ, Giới tính, Công ty
   - Sửa thông tin và nhấn "Lưu thay đổi"
   - Kiểm tra thông báo thành công và thông tin được cập nhật

---

## 9. Bug 9: Staff bắt buộc gắn location warehouse
**URL**: http://localhost:8081/admin/staff/create (hoặc /admin/staff/edit/{id})

**Cách test:**
1. Đăng nhập với tài khoản Admin
2. Vào: http://localhost:8081/admin/staff
3. Tạo staff mới hoặc sửa staff: http://localhost:8081/admin/staff/create
4. Kiểm tra:
   - Dropdown "Chi nhánh làm việc" **CHỈ hiển thị các location có type = WAREHOUSE**
   - Không hiển thị location type khác (SENDER, RECEIVER)
   - Nếu chọn location không phải warehouse, sẽ có validation error

---

## 10. Bug 10: Manager login
**URL**: http://localhost:8081/auth/login

**Cách test:**
1. Đăng xuất nếu đang đăng nhập
2. Vào: http://localhost:8081/auth/login
3. Đăng nhập với tài khoản có role MANAGER
4. Kiểm tra:
   - Đăng nhập thành công
   - Redirect đến: http://localhost:8081/manager/dashboard
   - Không có lỗi
   - Session có managerId và managerName

---

## 11. Bug 11: Shipper trip detail (xem chi tiết chuyến xe)
**URL**: http://localhost:8081/shipper/trip/{id}

**Cách test:**
1. Đăng nhập với tài khoản Shipper
2. Vào danh sách chuyến xe: http://localhost:8081/shipper/trips
3. Chọn một chuyến xe và vào chi tiết
4. Kiểm tra:
   - Hiển thị đầy đủ thông tin:
     - Điểm đi, Điểm đến
     - Loại chuyến (Trip Type)
     - Biển số xe (Vehicle License Plate)
     - Tên shipper
     - Thời gian bắt đầu
     - Tiền COD
     - Ghi chú
   - **KHÔNG được trống** như trước

---

## 12. Bug 12: Shipper history (lịch sử khác chuyến xe)
**URL**: http://localhost:8081/shipper/history

**Cách test:**
1. Đăng nhập với tài khoản Shipper
2. Vào: http://localhost:8081/shipper/history
3. Kiểm tra:
   - **CHỈ hiển thị các trips có status = COMPLETED hoặc CANCELLED**
   - Không hiển thị trips CREATED hoặc IN_PROGRESS
   - So sánh với http://localhost:8081/shipper/trips?filter=active (chỉ hiển thị active)
   - So sánh với http://localhost:8081/shipper/trips?filter=all (hiển thị tất cả)

---

## 13. Bug 13: Shipper profile (hồ sơ shipper)
**URL**: http://localhost:8081/shipper/profile

**Cách test:**
1. Đăng nhập với tài khoản Shipper
2. Vào: http://localhost:8081/shipper/profile
3. Kiểm tra:
   - Hiển thị thông tin shipper hiện tại
   - Sửa thông tin (Họ tên, SĐT, Khu vực làm việc, Vị trí hiện tại)
   - Nhấn "Lưu thay đổi"
   - **Kiểm tra:**
     - Thông báo thành công
     - Thông tin được cập nhật đúng
     - **isActive và isAvailable KHÔNG bị set về false** (giữ nguyên giá trị cũ)

---

## Tài khoản Test (nếu có)

Nếu cần tạo tài khoản test, có thể dùng các role:
- **ADMIN**: Quyền quản trị
- **MANAGER**: Quản lý đơn hàng
- **STAFF**: Nhân viên kho
- **CUSTOMER**: Khách hàng
- **SHIPPER**: Tài xế

---

## Lưu ý khi Test

1. **Đảm bảo database có dữ liệu test:**
   - Có ít nhất 1 customer, 1 staff, 1 shipper, 1 manager
   - Có ít nhất 1 request/order để test tracking và payments
   - Có ít nhất 1 location type WAREHOUSE

2. **Session/Cookie:**
   - Mỗi role cần đăng nhập riêng
   - Đăng xuất trước khi test login của role khác

3. **Lỗi thường gặp:**
   - 404: Kiểm tra URL đúng chưa
   - 403: Kiểm tra đã đăng nhập với role đúng chưa
   - 500: Kiểm tra log console để xem chi tiết lỗi

4. **Browser DevTools:**
   - Mở F12 để xem Console và Network tab
   - Kiểm tra lỗi JavaScript
   - Kiểm tra HTTP status codes

---

## Checklist Test

- [ ] Bug 1: Màu text hero
- [ ] Bug 2: Tracking không lỗi 500
- [ ] Bug 3: Gửi hàng khách vãng lai
- [ ] Bug 4: Gói vận chuyển đầy đủ
- [ ] Bug 5: Số lượng kiện và kích thước riêng
- [ ] Bug 6: Tracking hiển thị timeline
- [ ] Bug 7: Payments có search
- [ ] Bug 8: Customer profile
- [ ] Bug 9: Staff chỉ chọn warehouse
- [ ] Bug 10: Manager login
- [ ] Bug 11: Shipper trip detail đầy đủ
- [ ] Bug 12: Shipper history chỉ completed
- [ ] Bug 13: Shipper profile update

