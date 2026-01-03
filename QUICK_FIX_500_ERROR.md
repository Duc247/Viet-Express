# QUICK FIX - LỖI 500 KHI ẤN LẦN ĐẦU

## ✅ ĐÃ SỬA CÁC VẤN ĐỀ SAU:

### 1. ✅ Thêm xử lý LazyInitializationException vào GlobalExceptionHandler
- File: `src/main/java/vn/DucBackend/Config/GlobalExceptionHandler.java`
- Thêm handler riêng cho `LazyInitializationException` với logging chi tiết

### 2. ✅ Sửa CustomerDashboardController - Tránh lazy loading
- File: `src/main/java/vn/DucBackend/Controllers/Customer/CustomerDashboardController.java`
- **VẤN ĐỀ:** Dòng 53-58 truy cập `p.getRequest().getSender()` ngoài transaction
- **FIX:** 
  - Tạo query method mới `findByRequestSenderId()` với JOIN FETCH
  - Thêm `@Transactional(readOnly = true)` vào method

### 3. ✅ Thêm query method mới trong PaymentRepository
- File: `src/main/java/vn/DucBackend/Repositories/PaymentRepository.java`
- Thêm method `findByRequestSenderId()` với JOIN FETCH để load đầy đủ relationship

### 4. ✅ Bật logging DEBUG
- File: `src/main/resources/application.properties`
- Thêm các dòng logging để debug dễ dàng hơn

---

## 🧪 CÁCH TEST:

1. **Restart ứng dụng**
2. **Xóa cache trình duyệt** (Ctrl+Shift+Delete)
3. **Truy cập lại trang bị lỗi** (ví dụ: `/customer/dashboard`)
4. **Xem logs trong console** để tìm lỗi cụ thể

---

## 📋 NẾU VẪN LỖI, LÀM THEO:

1. **Xem logs** - Tìm dòng có `LazyInitializationException` hoặc `could not initialize proxy`
2. **Kiểm tra stack trace** - Xem method nào gây lỗi
3. **Áp dụng fix tương tự:**
   - Thêm `@Transactional` vào controller/service method
   - Sử dụng JOIN FETCH trong query
   - Hoặc sử dụng DTO thay vì Entity

---

## 🔍 CÁC TRANG CẦN KIỂM TRA:

- `/customer/dashboard` ✅ Đã sửa
- `/manager/dashboard` - Có thể cần kiểm tra
- `/admin/dashboard` - Có thể cần kiểm tra
- `/shipper/dashboard` - Có thể cần kiểm tra
- Các trang detail khác

---

## 📝 LƯU Ý:

- Lỗi 500 lần đầu có thể do nhiều nguyên nhân
- Nếu vẫn lỗi, xem file `DEBUG_500_ERROR_GUIDE.md` để debug chi tiết hơn
- Luôn kiểm tra logs trước khi fix

