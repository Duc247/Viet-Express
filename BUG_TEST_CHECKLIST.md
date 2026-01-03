# CHECKLIST KIỂM TRA TỪNG BUG

## 📋 HƯỚNG DẪN SỬ DỤNG

1. **Khởi động ứng dụng:**
   ```bash
   mvn spring-boot:run
   # hoặc
   ./mvnw spring-boot:run
   ```

2. **Mở trình duyệt:** `http://localhost:8081`

3. **Đăng nhập với các tài khoản test:**
   - Customer
   - Staff
   - Manager
   - Shipper
   - Admin

4. **Kiểm tra từng bug theo thứ tự dưới đây**

---

## ✅ BUG #1: LỖI MÀU TEXT TRANG CHỦ

### Test Steps:
1. Truy cập: `http://localhost:8081/`
2. Kiểm tra dòng text: "Vận chuyển nhanh chóng, an toàn và đáng tin cậy"
3. **Expected:** Text màu trắng, hiển thị rõ trên background gradient
4. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/resources/templates/public/home.html`
- Tìm dòng có: `<span class="text-white">`
- **Status:** ✅ Đã sửa

---

## ✅ BUG #2: LỖI 500 KHI ẤN LẦN ĐẦU

### Test Steps:
1. **Xóa cache trình duyệt** (Ctrl+Shift+Delete)
2. Truy cập: `http://localhost:8081/customer/dashboard` (hoặc trang khác)
3. **Lần 1:** [ ] Lỗi 500 / [ ] OK
4. **Lần 2 (F5):** [ ] Lỗi 500 / [ ] OK
5. **Xem logs console** - Tìm `LazyInitializationException`

### Code Check:
- File: `src/main/java/vn/DucBackend/Controllers/Customer/CustomerDashboardController.java`
- Có `@Transactional(readOnly = true)`? [ ] Yes / [ ] No
- File: `src/main/java/vn/DucBackend/Repositories/PaymentRepository.java`
- Có method `findByRequestSenderId()`? [ ] Yes / [ ] No

### Status:
- ✅ Đã sửa CustomerDashboardController
- ⚠️ Cần test thực tế

---

## ✅ BUG #3: LỖI NÚT GỬI HÀNG CHO KHÁCH VÃNG LAI

### Test Steps:
1. Truy cập: `http://localhost:8081/request`
2. Điền form "Gửi yêu cầu vận chuyển":
   - Tên khách hàng: Test User
   - Số điện thoại: 0123456789
   - Địa chỉ lấy hàng: 123 Đường ABC
   - Địa chỉ giao hàng: 456 Đường XYZ
3. Click "Gửi yêu cầu"
4. **Expected:** 
   - Hiển thị thông báo thành công
   - Có mã đơn hàng
   - Redirect về trang request
5. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/java/vn/DucBackend/Controllers/WebController.java`
- Method `handleRequestSubmit()` có logic xử lý? [ ] Yes / [ ] No
- **Status:** ✅ Đã sửa

---

## ✅ BUG #4: GÓI VẬN CHUYỂN CUSTOMER CHỈ CÓ 3 DỊCH VỤ CỐ ĐỊNH

### Test Steps:
1. Đăng nhập với tài khoản Customer
2. Truy cập: `http://localhost:8081/customer/create-order`
3. Kiểm tra phần "Chọn gói vận chuyển"
4. **Expected:** 
   - Hiển thị TẤT CẢ service types từ database
   - Không chỉ có 3 dịch vụ cố định
5. **Actual:** [ ] Pass / [ ] Fail
6. **Số lượng dịch vụ hiển thị:** _____

### Code Check:
- File: `src/main/resources/templates/customer/order/create-order.html`
- Có `th:each="service : ${serviceTypes}"`? [ ] Yes / [ ] No
- File: `src/main/java/vn/DucBackend/Controllers/Customer/CustomerOrderCreateController.java`
- Có load `serviceTypeRepository.findByIsActiveTrue()`? [ ] Yes / [ ] No
- **Status:** ✅ Đã sửa

---

## ✅ BUG #5: THÊM SỐ LƯỢNG KIỆN HÀNG VÀ TÁCH KÍCH THƯỚC

### Test Steps:
1. Đăng nhập với tài khoản Customer
2. Truy cập: `http://localhost:8081/customer/create-order`
3. Kiểm tra form tạo đơn:
   - [ ] Có field "Số lượng kiện hàng" (quantity)
   - [ ] Có field "Dài (cm)" (lengthCm)
   - [ ] Có field "Rộng (cm)" (widthCm)
   - [ ] Có field "Cao (cm)" (heightCm)
4. Điền form và submit
5. **Expected:** Dữ liệu được lưu vào database
6. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/resources/templates/customer/order/create-order.html`
- Có các input fields? [ ] Yes / [ ] No
- File: `src/main/java/vn/DucBackend/Controllers/Customer/CustomerOrderCreateController.java`
- Có nhận parameters `quantity`, `lengthCm`, `widthCm`, `heightCm`? [ ] Yes / [ ] No
- **Status:** ✅ Đã sửa

---

## ✅ BUG #6: TRACKING CHƯA LÀM

### Test Steps:
1. Đăng nhập với tài khoản Customer
2. Truy cập: `http://localhost:8081/customer/tracking` (hoặc link tracking)
3. **Expected:** 
   - Có trang tracking
   - Có thể nhập mã đơn hàng
   - Hiển thị trạng thái đơn hàng
4. **Actual:** [ ] Pass / [ ] Fail / [ ] Chưa làm

### Code Check:
- File: `src/main/resources/templates/customer/tracking.html` - [ ] Exists / [ ] Not exists
- **Status:** ⚠️ Chưa làm (không phải bug, là feature chưa hoàn thiện)

---

## ✅ BUG #7: THANH TOÁN CUSTOMER - TÌM KIẾM THEO MÃ REQUEST/TRIP

### Test Steps:
1. Đăng nhập với tài khoản Customer
2. Truy cập: `http://localhost:8081/customer/payments`
3. Kiểm tra form tìm kiếm:
   - [ ] Có field tìm kiếm chung
   - [ ] Có field "Mã đơn hàng" (requestSearch)
   - [ ] Có field "Mã chuyến đi" (tripSearch)
4. Test tìm kiếm:
   - Nhập mã đơn hàng → [ ] Có kết quả
   - Nhập mã chuyến đi → [ ] Có kết quả
5. Kiểm tra bảng kết quả:
   - [ ] Hiển thị cột "Mã đơn hàng"
   - [ ] Hiển thị cột "Mã chuyến đi"
6. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/resources/templates/customer/payments.html`
- Có input `requestSearch` và `tripSearch`? [ ] Yes / [ ] No
- File: `src/main/java/vn/DucBackend/Repositories/PaymentRepository.java`
- Có method `searchByRequestIdAndKeywordOrRequestCodeOrTripCode()`? [ ] Yes / [ ] No
- **Status:** ✅ Đã sửa

---

## ✅ BUG #8: HỒ SƠ CUSTOMER CHƯA LÀM

### Test Steps:
1. Đăng nhập với tài khoản Customer
2. Truy cập: `http://localhost:8081/customer/profile`
3. **Expected:**
   - Hiển thị thông tin customer
   - Có form chỉnh sửa
   - Có thể cập nhật thông tin
4. **Actual:** [ ] Pass / [ ] Fail / [ ] 404 Not Found

### Code Check:
- File: `src/main/java/vn/DucBackend/Controllers/Customer/CustomerProfileController.java` - [ ] Exists / [ ] Not exists
- File: `src/main/resources/templates/customer/profile.html` - [ ] Exists / [ ] Not exists
- **Status:** ✅ Đã sửa

---

## ✅ BUG #9: STAFF PHẢI GẮN LOCATION WAREHOUSE

### Test Steps:
1. Đăng nhập với tài khoản Admin
2. Truy cập: `http://localhost:8081/admin/staff/create`
3. Tạo staff mới:
   - Chọn location có type KHÔNG phải WAREHOUSE → [ ] Có lỗi validation
   - Chọn location có type WAREHOUSE → [ ] Tạo thành công
4. Sửa staff:
   - Đổi location sang type không phải WAREHOUSE → [ ] Có lỗi validation
5. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/java/vn/DucBackend/Services/Impl/StaffServiceImpl.java`
- Method `createStaff()` có validation? [ ] Yes / [ ] No
- Method `updateStaff()` có validation? [ ] Yes / [ ] No
- **Status:** ✅ Đã sửa

---

## ✅ BUG #10: KIỂM TRA ĐĂNG NHẬP MANAGER

### Test Steps:
1. Truy cập: `http://localhost:8081/auth/login`
2. Đăng nhập với tài khoản Manager
3. **Expected:**
   - Đăng nhập thành công
   - Redirect đến `/manager/dashboard`
   - Hiển thị thông báo "Đăng nhập thành công"
4. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/java/vn/DucBackend/Controllers/Auth/LoginController.java`
- Case "MANAGER" có redirect đúng? [ ] Yes / [ ] No
- **Status:** ✅ Đã kiểm tra, hoạt động đúng

---

## ✅ BUG #11: CHI TIẾT CHUYẾN XE SHIPPER TRỐNG

### Test Steps:
1. Đăng nhập với tài khoản Shipper
2. Truy cập: `http://localhost:8081/shipper/trips`
3. Click vào một chuyến xe để xem chi tiết
4. **Expected:**
   - Hiển thị thông tin chuyến xe đầy đủ
   - Có bảng "Kiện hàng trong chuyến"
   - Có thông tin phương tiện (nếu có)
   - Có thông tin loại chuyến
5. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/resources/templates/shipper/trip/detail.html`
- Có section "Kiện hàng trong chuyến"? [ ] Yes / [ ] No
- File: `src/main/java/vn/DucBackend/Controllers/Shipper/ShipperTripController.java`
- Có load `parcels`? [ ] Yes / [ ] No
- **Status:** ✅ Đã sửa

---

## ✅ BUG #12: LỊCH SỬ SHIPPER GIỐNG CHUYẾN XE

### Test Steps:
1. Đăng nhập với tài khoản Shipper
2. Truy cập: `http://localhost:8081/shipper/history`
3. **Expected:**
   - Hiển thị trang RIÊNG (không phải `/shipper/trips`)
   - Có thống kê (Tổng chuyến, Đã hoàn thành, Tổng COD)
   - Có filter theo status
   - Hiển thị dạng bảng với thông tin chi tiết
4. So sánh với `/shipper/trips`:
   - [ ] Khác nhau về layout
   - [ ] Có thống kê
   - [ ] Có filter
5. **Actual:** [ ] Pass / [ ] Fail

### Code Check:
- File: `src/main/resources/templates/shipper/history.html` - [ ] Exists / [ ] Not exists
- File: `src/main/java/vn/DucBackend/Controllers/Shipper/ShipperHistoryController.java`
- Có tính toán thống kê? [ ] Yes / [ ] No
- **Status:** ✅ Đã sửa

---

## ✅ BUG #13: HỒ SƠ SHIPPER BỊ LỖI

### Test Steps:
1. Đăng nhập với tài khoản Shipper
2. Truy cập: `http://localhost:8081/shipper/profile`
3. **Expected:**
   - Hiển thị thông tin shipper
   - Có form chỉnh sửa (nếu có)
   - Không bị lỗi 404 hoặc 500
4. **Actual:** [ ] Pass / [ ] Fail / [ ] 404 Not Found

### Code Check:
- File: `src/main/java/vn/DucBackend/Controllers/Shipper/ShipperProfileController.java` - [ ] Exists / [ ] Not exists
- File: `src/main/resources/templates/shipper/profile.html` - [ ] Exists / [ ] Not exists
- **Status:** ✅ Đã sửa

---

## 📊 TỔNG KẾT

### Đã test:
- [ ] Bug #1: Lỗi màu text trang chủ
- [ ] Bug #2: Lỗi 500 khi ấn lần đầu
- [ ] Bug #3: Lỗi nút gửi hàng cho khách vãng lai
- [ ] Bug #4: Gói vận chuyển customer
- [ ] Bug #5: Số lượng kiện hàng và kích thước
- [ ] Bug #6: Tracking chưa làm
- [ ] Bug #7: Thanh toán customer - tìm kiếm
- [ ] Bug #8: Hồ sơ customer
- [ ] Bug #9: Staff phải gắn location warehouse
- [ ] Bug #10: Đăng nhập manager
- [ ] Bug #11: Chi tiết chuyến xe shipper
- [ ] Bug #12: Lịch sử shipper
- [ ] Bug #13: Hồ sơ shipper

### Kết quả:
- **Pass:** _____ / 13
- **Fail:** _____ / 13
- **Chưa làm:** _____ / 13

### Ghi chú:
_________________________________________________
_________________________________________________
_________________________________________________

