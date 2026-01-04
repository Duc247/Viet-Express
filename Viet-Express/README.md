# 🚚 VIET-EXPRESS LOGISTICS SYSTEM

## Hệ thống Quản lý Vận chuyển & Thu hộ COD

---

## 📋 MỤC LỤC

1. [Tổng quan hệ thống](#tổng-quan-hệ-thống)
2. [Ví dụ thực tế: Đơn hàng 105 thùng bia](#ví-dụ-thực-tế-đơn-hàng-105-thùng-bia)
3. [Chi tiết vai trò và quy trình](#chi-tiết-vai-trò-và-quy-trình)
4. [Luồng thanh toán COD và Phí ship](#luồng-thanh-toán-cod-và-phí-ship)
5. [Tính năng đã triển khai](#tính-năng-đã-triển-khai)
6. [Tính năng chưa triển khai](#tính-năng-chưa-triển-khai)
7. [Hướng dẫn cài đặt](#hướng-dẫn-cài-đặt)

---

## 📖 TỔNG QUAN HỆ THỐNG

Viet-Express là hệ thống quản lý logistics toàn diện, hỗ trợ:
- Đặt đơn hàng với xác nhận từ người nhận
- Quản lý kiện hàng và vận chuyển qua nhiều kho
- Thu hộ COD theo từng chuyến
- Thanh toán phí ship linh hoạt

---

## 🍺 VÍ DỤ THỰC TẾ: ĐƠN HÀNG 105 THÙNG BIA

### Thông tin đơn hàng

| Mục | Chi tiết |
|-----|----------|
| **Người gửi (A)** | Anh Minh - 0901234567 |
| **Người nhận (B)** | Cửa hàng Bia Hà Nội - 0987654321 |
| **Hàng hóa** | 105 thùng bia Heineken |
| **Điểm lấy hàng** | Địa chỉ A (Quận 1, TP.HCM) |
| **Điểm giao hàng** | Địa chỉ B (Quận 7, Hà Nội) |
| **Trung chuyển** | Kho C (Đà Nẵng) → Kho D (Thanh Hóa) |
| **Tổng phí COD** | 10.500.000đ (100.000đ/thùng × 105 thùng) |
| **Tổng phí Ship** | 2.000.000đ (Manager có thể điều chỉnh theo trip) |

---

### 🚀 BƯỚC 1: NGƯỜI GỬI TẠO ĐƠN HÀNG

**Customer A thao tác:**

1. Đăng nhập vào hệ thống với tài khoản Customer
2. Vào **"Tạo đơn hàng mới"**
3. Điền thông tin:
   - **Người gửi:** Tự động lấy từ tài khoản đang đăng nhập (không thể thay đổi)
   - **SĐT người nhận:** 0987654321 (bắt buộc)
   - **Địa chỉ lấy hàng:** Quận 1, TP.HCM
   - **Địa chỉ giao hàng:** Quận 7, Hà Nội
   - **Mô tả hàng:** 105 thùng bia Heineken
   - **Tiền COD:** 10.500.000đ
   - *(Phí ship sẽ do Manager nhập sau)*

4. Bấm **"Tạo đơn hàng"**

**Trạng thái đơn:** `PENDING` (Chờ người nhận xác nhận)

---

### 📱 BƯỚC 2: NGƯỜI NHẬN XÁC NHẬN

**Customer B (người nhận) thao tác:**

1. Đăng nhập vào hệ thống (tài khoản được tạo tự động nếu chưa có theo SĐT)
2. Vào **"Đơn hàng của tôi"**
3. Thấy đơn hàng với trạng thái **"Chờ bạn xác nhận"**
4. Xem chi tiết đơn và bấm:
   - ✅ **"Xác nhận nhận hàng"** - Đồng ý nhận
   - ❌ **"Từ chối"** - Không nhận (đơn sẽ bị hủy)

**Trạng thái đơn:** `RECEIVER_CONFIRMED` (Người nhận đã xác nhận, chờ Manager chốt)

> ⚠️ **LƯU Ý:** Trong giai đoạn này, người gửi vẫn có thể chỉnh sửa đơn hàng vì Manager chưa chốt.

---

### 🔐 BƯỚC 3: MANAGER CHỐT ĐƠN

**Manager thao tác:**

1. Vào **"Quản lý yêu cầu"**
2. Thấy đơn với trạng thái **"Sẵn sàng chốt đơn"** (receiver đã xác nhận)
3. Xem chi tiết đơn:
   - Thiết lập **điểm lấy hàng** (gắn với Location/Kho gần nhất)
   - Thiết lập **điểm giao hàng** (gắn với Location/Kho gần nhất)
   - Nhập/xác nhận **phí ship:** 2.000.000đ
4. Bấm **"Chốt đơn"**

**Trạng thái đơn:** `CONFIRMED` (Đã chốt đơn, cả 2 bên đều đã xác nhận)

> ⚠️ **QUAN TRỌNG:** 
> - Manager **KHÔNG THỂ** chốt đơn nếu người nhận chưa xác nhận
> - Sau khi chốt đơn, người gửi **KHÔNG THỂ** chỉnh sửa đơn nữa

---

### 💰 BƯỚC 4: MANAGER TẠO PAYMENTS

Sau khi chốt đơn, Manager tạo các khoản thanh toán:

#### 4a. Tùy chọn 1: Thanh toán theo từng Trip (Mặc định)

Manager tạo payments cho từng chuyến vận chuyển (xem bước 5).

#### 4b. Tùy chọn 2: Thanh toán 1 lần toàn bộ (Prepaid) ⚠️ *CHƯA TRIỂN KHAI*

Nếu khách hàng chọn trả trước toàn bộ:
- Phí ship: 2.000.000đ (trả 1 lần)
- Hoặc COD: 10.500.000đ (trả 1 lần thay vì thu từ người nhận)

---

### 👷 BƯỚC 5: GIAO VIỆC CHO STAFF

**Manager thao tác:**

1. Trong chi tiết đơn hàng, vào phần **"Giao việc tạo kiện hàng"**
2. Chọn Staff đang làm việc tại kho gần điểm lấy hàng nhất
3. Bấm **"Giao việc"**

**Staff được giao sẽ thấy đơn trong "Tiếp nhận yêu cầu"**

---

### 📦 BƯỚC 6: STAFF TẠO KIỆN HÀNG

**Staff thao tác:**

1. Vào **"Tiếp nhận yêu cầu"** - thấy đơn được giao
2. Xem chi tiết đơn, tạo 105 kiện hàng (mỗi kiện = 1 thùng bia):

```
Kiện 1: PCL-20260101-1-01, COD: 100.000đ, Trọng lượng: 15kg
Kiện 2: PCL-20260101-1-02, COD: 100.000đ, Trọng lượng: 15kg
...
Kiện 105: PCL-20260101-1-105, COD: 100.000đ, Trọng lượng: 15kg
```

Mỗi kiện có:
- Mã kiện (parcel_code)
- Mô tả
- Tiền COD của kiện đó
- Kích thước/trọng lượng

---

### 🚚 BƯỚC 7: MANAGER TẠO TRIPS VÀ XẾP HÀNG

Do xe chỉ chở được tối đa **20 kiện/chuyến**, Manager cần chia thành nhiều chuyến:

#### Trip 1: PICKUP (A → Kho C) - 20 kiện
```
Loại: PICKUP
Điểm đi: Địa chỉ A (TP.HCM)
Điểm đến: Kho C (Đà Nẵng)
Shipper: Nguyễn Văn X
Kiện: 1-20 (20 kiện)
COD Trip này: 20 × 100.000đ = 2.000.000đ
Phí ship Trip này: 400.000đ (Manager chia từ tổng 2.000.000đ)
```

#### Trip 2: PICKUP (A → Kho C) - 20 kiện
```
Kiện: 21-40 (20 kiện)
COD: 2.000.000đ
Phí ship: 400.000đ
```

#### Trip 3-6: Tương tự...

*(Tổng 6 trips PICKUP từ A → Kho C cho 105 kiện)*

#### Trip 7: TRANSFER (Kho C → Kho D) - 50 kiện
```
Loại: TRANSFER
Điểm đi: Kho C (Đà Nẵng)
Điểm đến: Kho D (Thanh Hóa)
Kiện: Tất cả 105 kiện (chia thành nhiều chuyến nếu cần)
```

#### Trip 8-13: DELIVERY (Kho D → B) - 20 kiện mỗi chuyến
```
Loại: DELIVERY
Điểm đi: Kho D (Thanh Hóa)
Điểm đến: Địa chỉ B (Hà Nội)
```

---

### 💵 BƯỚC 8: THU TIỀN THEO TRIP

#### Khi Trip DELIVERY khởi hành:

Hệ thống tự động tạo Payment COD cho trip đó:

| Trip | Số kiện | COD | Phí Ship | Tổng thu |
|------|---------|-----|----------|----------|
| Delivery 1 | 20 | 2.000.000đ | 400.000đ | 2.400.000đ |
| Delivery 2 | 20 | 2.000.000đ | 300.000đ | 2.300.000đ |
| ... | ... | ... | ... | ... |
| Delivery 6 | 5 | 500.000đ | 100.000đ | 600.000đ |

#### Trạng thái thanh toán COD theo Trip:

| Trạng thái | Mô tả | Ai cập nhật |
|------------|-------|-------------|
| `UNPAID` | Chưa thu từ người nhận | Mặc định khi tạo |
| `PAID` | Đã thu từ người nhận | Shipper/Hệ thống tự động |
| `PAID_TO_SENDER` | Đã trả COD cho người gửi | **Chỉ Manager** |

> ⚠️ **CHÚ Ý:** Trạng thái `PAID_TO_SENDER` chỉ Manager mới có quyền cập nhật thủ công sau khi hoàn tiền cho người gửi.

---

### 📊 TỔNG KẾT TÀI CHÍNH

| Khoản | Số tiền | Người trả | Người nhận tiền |
|-------|---------|-----------|-----------------|
| Phí Ship | 2.000.000đ | Người nhận B | Công ty |
| Tiền COD | 10.500.000đ | Người nhận B | Người gửi A (qua công ty) |

---

## 👥 CHI TIẾT VAI TRÒ VÀ QUY TRÌNH

### 🔵 CUSTOMER (Người gửi/Người nhận)

#### Người gửi có thể:
- ✅ Tạo đơn hàng mới
- ✅ Xem danh sách đơn hàng (gửi đi + nhận)
- ✅ Chỉnh sửa đơn **nếu** chưa CONFIRMED
- ✅ Hủy đơn **nếu** chưa CONFIRMED
- ✅ Theo dõi tracking

#### Người nhận có thể:
- ✅ Xác nhận/Từ chối đơn hàng khi status = PENDING
- ✅ Thanh toán COD khi nhận hàng
- ❌ KHÔNG thể chỉnh sửa đơn

---

### 🟠 MANAGER

#### Quy trình làm việc:

1. **Xem danh sách yêu cầu**
   - Lọc theo trạng thái: PENDING, RECEIVER_CONFIRMED, CONFIRMED...
   
2. **Chốt đơn** *(chỉ khi status = RECEIVER_CONFIRMED)*
   - Thiết lập điểm lấy/giao
   - Xác nhận phí ship
   - Bấm "Chốt đơn" → status = CONFIRMED

3. **Tạo Payment** *(chỉ sau khi CONFIRMED)*
   - Tạo payment COD cho đơn/trip
   - Tạo payment phí ship
   - Điều chỉnh số tiền nếu cần

4. **Giao việc cho Staff**
   - Chọn Staff tại kho phù hợp
   - Staff sẽ tạo kiện hàng

5. **Tạo Trip và xếp hàng**
   - Tạo trip: PICKUP / TRANSFER / DELIVERY
   - Gán shipper, xe
   - Xếp kiện vào trip

6. **Khởi hành Trip**
   - Đổi status trip → IN_PROGRESS
   - Hệ thống tự tạo Payment COD cho trip (nếu có)

7. **Xác nhận thanh toán**
   - Cập nhật payment status: UNPAID → PAID
   - Xác nhận đã trả COD cho người gửi: PAID → PAID_TO_SENDER ⚠️ *CHƯA TRIỂN KHAI*

---

### 🟡 STAFF

#### Quy trình làm việc:

1. **Tiếp nhận yêu cầu**
   - Xem danh sách request được Manager giao
   
2. **Tạo kiện hàng**
   - Từ request, tạo từng parcel với mã, mô tả, COD, kích thước

3. **Quản lý kho** ⚠️ *ĐANG PHÁT TRIỂN*
   - Nhập kho: Khi shipper giao hàng đến
   - Xuất kho: Khi shipper lấy hàng đi

---

### 🟢 SHIPPER

#### Quy trình làm việc (⚠️ *MỘT SỐ CHỨC NĂNG CHƯA TRIỂN KHAI*):

1. **Xem trips được giao**
2. **Lấy hàng (PICKUP)**
3. **Vận chuyển (TRANSFER)**
4. **Giao hàng (DELIVERY)**
5. **Thu COD từ người nhận**

---

## 💰 LUỒNG THANH TOÁN COD VÀ PHÍ SHIP

### Thanh toán theo Trip (Mặc định)

```
                     TRIP KHỞI HÀNH
                          │
                          ▼
              ┌───────────────────────┐
              │ Tự động tạo Payment   │
              │ COD cho Trip          │
              │ Status: UNPAID        │
              └───────────┬───────────┘
                          │
                          ▼
              ┌───────────────────────┐
              │ Shipper giao hàng     │
              │ Thu tiền từ receiver  │
              └───────────┬───────────┘
                          │
                          ▼
              ┌───────────────────────┐
              │ Cập nhật Payment      │
              │ Status: PAID          │
              │ (Đã thu từ receiver)  │
              └───────────┬───────────┘
                          │
                          ▼
              ┌───────────────────────┐
              │ Manager xác nhận      │
              │ Đã trả cho sender     │
              │ Status: PAID_TO_SENDER│ ⚠️ CHƯA TRIỂN KHAI
              └───────────────────────┘
```

### Thanh toán trước toàn bộ (Prepaid) ⚠️ CHƯA TRIỂN KHAI

Khách hàng có thể chọn:
- Trả phí ship 1 lần ngay từ đầu
- Trả luôn tiền COD (sender không cần chờ thu từ receiver)

---

## ✅ TÍNH NĂNG ĐÃ TRIỂN KHAI

### Customer
- [x] Tạo đơn hàng với người gửi là chính họ
- [x] Nhập SĐT người nhận (bắt buộc)
- [x] Người nhận xác nhận/từ chối đơn
- [x] Chỉnh sửa đơn trước khi cả 2 bên xác nhận
- [x] Hủy đơn trước khi CONFIRMED
- [x] Xem danh sách và chi tiết đơn hàng

### Manager
- [x] Xem danh sách yêu cầu
- [x] Chốt đơn (chỉ khi receiver đã xác nhận)
- [x] Thiết lập điểm lấy/giao hàng (Location)
- [x] Giao việc cho Staff
- [x] Tạo Trip (PICKUP, TRANSFER, DELIVERY, RETURN)
- [x] Xếp kiện vào Trip
- [x] Tạo Payment thủ công
- [x] Validation: không tạo payment vượt quá tổng phí

### Staff
- [x] Xem requests được giao
- [x] Tạo kiện hàng từ request
- [x] Ghi log parcel actions

### Trip & Payment
- [x] Tự động tạo COD payment khi trip khởi hành
- [x] Payment liên kết với Trip và Request
- [x] Trạng thái: UNPAID, PAID, PARTIALLY_PAID, REFUNDED

---

## ❌ TÍNH NĂNG CHƯA TRIỂN KHAI

### Payment Status mở rộng
- [ ] `PAID_TO_SENDER` - Đã trả COD cho người gửi
- [ ] Chỉ Manager được cập nhật trạng thái này

### Tùy chọn thanh toán
- [ ] Prepaid: Trả trước toàn bộ phí ship
- [ ] Prepaid: Trả trước COD (sender trả, không thu từ receiver)
- [ ] Tiền cọc (DEPOSIT) cho đơn hàng

### Manager
- [ ] Nút "Chốt đơn" khi status = RECEIVER_CONFIRMED trong UI
- [ ] Điều chỉnh phí ship theo từng trip linh hoạt

### Staff - Quản lý kho
- [ ] Nhập kho khi shipper đến
- [ ] Xuất kho khi shipper lấy đi
- [ ] Tra cứu kiện hàng bằng mã

### Shipper
- [ ] Giao diện mobile cho shipper
- [ ] Xác nhận thu COD từ người nhận
- [ ] Cập nhật trạng thái giao hàng
- [ ] Chụp ảnh xác nhận

### Tracking
- [ ] Mã tracking code cho khách tra cứu
- [ ] Thông báo realtime khi thay đổi trạng thái

---

## 🛠️ HƯỚNG DẪN CÀI ĐẶT

### Yêu cầu
- Java 21+
- Maven 3.8+
- MySQL 8.0+

### Cài đặt

```bash
# Clone repo
git clone https://github.com/Duc247/Viet-Express.git
cd Viet-Express

# Cấu hình database
# Sửa file src/main/resources/application.properties

# Chạy ứng dụng
mvn spring-boot:run
```

### Truy cập
- **Trang chủ:** http://localhost:8081
- **Admin:** http://localhost:8081/admin/dashboard
- **Manager:** http://localhost:8081/manager/dashboard
- **Staff:** http://localhost:8081/staff/dashboard
- **Customer:** http://localhost:8081/customer/dashboard

---

## 📞 Liên hệ

**Viet Express Team**  
Email: contact@vietexpress.vn  
GitHub: https://github.com/Duc247

---

*Phiên bản: 1.0.0 | Cập nhật: 01/01/2026*
