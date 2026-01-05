# Feature: Auto ParcelAction + Public Tracking + Remove TrackingCode

> **Implementation Priority: 2/3**  
> Triển khai sau: `promptPaymentStatusHistory.md`  
> Triển khai trước: `promptRouteBasedFee.md`

## Mục tiêu
1. **ParcelAction tự động ghi nhận** khi có sự kiện (tạo/sửa/xóa/đổi trạng thái)
2. **Thêm tra cứu vào sidebar** của mỗi role + header trang landing
3. **Trang tra cứu công khai** - nhập mã đơn/kiện → xem thông tin cơ bản
4. **Xóa entity TrackingCode** an toàn

---

## PHẦN 1: TỰ ĐỘNG GHI NHẬN PARCEL ACTION

### Các sự kiện cần ghi nhận:

| Sự kiện | Actor | ActionType | Ghi chú |
|---------|-------|------------|---------|
| Customer tạo đơn | Customer | CREATED | Tạo đơn hàng mới |
| Receiver xác nhận | Receiver | RECEIVER_CONFIRMED | Người nhận xác nhận |
| Manager chốt đơn | Manager | CONFIRMED | Manager xác nhận + phân công |
| Staff tạo parcel | Staff | PARCEL_CREATED | Tạo kiện hàng |
| Staff sửa parcel | Staff | PARCEL_UPDATED | Cập nhật thông tin kiện |
| Shipper lấy hàng | Shipper | PICKED_UP | Đã lấy hàng từ người gửi |
| Shipper nhập kho | Shipper | IN_WAREHOUSE | Hàng đã về kho |
| Shipper xuất kho | Shipper | IN_TRANSIT | Đang vận chuyển |
| Shipper giao hàng | Shipper | OUT_FOR_DELIVERY | Đang giao |
| Giao thành công | Shipper | DELIVERED | Đã giao thành công |
| Giao thất bại | Shipper | FAILED | Giao không thành công |
| Hoàn hàng | Shipper | RETURNED | Hoàn trả về người gửi |
| Hủy đơn | Customer/Manager | CANCELLED | Đơn bị hủy |

### Cần thêm ActionType mới (nếu chưa có):
```sql
INSERT INTO action_types (action_code, name, description) VALUES
('RECEIVER_CONFIRMED', 'Người nhận xác nhận', 'Người nhận đã xác nhận đơn hàng'),
('PARCEL_CREATED', 'Tạo kiện hàng', 'Staff tạo kiện hàng mới'),
('PARCEL_UPDATED', 'Cập nhật kiện', 'Staff cập nhật thông tin kiện'),
('CANCELLED', 'Hủy đơn', 'Đơn hàng bị hủy');
```

### Tạo Service method ghi nhận action:
```java
@Service
public class ParcelActionService {
    
    public void recordAction(Long parcelId, Long requestId, String actionCode, 
                            Long fromLocationId, Long toLocationId, 
                            Long actorUserId, String note) {
        ParcelAction action = new ParcelAction();
        action.setParcel(parcelRepository.findById(parcelId).orElse(null));
        action.setRequest(requestRepository.findById(requestId).orElse(null));
        action.setActionType(actionTypeRepository.findByActionCode(actionCode).orElse(null));
        action.setFromLocation(fromLocationId != null ? locationRepository.findById(fromLocationId).orElse(null) : null);
        action.setToLocation(toLocationId != null ? locationRepository.findById(toLocationId).orElse(null) : null);
        action.setActorUser(userRepository.findById(actorUserId).orElse(null));
        action.setNote(note);
        parcelActionRepository.save(action);
    }
}
```

### Các file cần sửa để gọi recordAction():

| File | Vị trí cần thêm |
|------|-----------------|
| `CustomerOrderCreateController.java` | Sau khi tạo đơn thành công |
| `CustomerReceiverConfirmController.java` | Sau khi receiver xác nhận |
| `ManagerRequestController.java` | Khi confirm đơn |
| `StaffParcelController.java` | Khi tạo/sửa parcel |
| `ShipperController.java` | Khi cập nhật trạng thái trip |

---

## PHẦN 2: THÊM TRA CỨU VÀO SIDEBAR + HEADER

### 2.1 Header trang Landing (chưa đăng nhập)
File: `templates/fragments/header.html` hoặc `templates/index.html`
```html
<nav class="navbar">
    ...
    <form action="/tracking" method="get" class="d-flex">
        <input type="text" name="code" placeholder="Nhập mã đơn/kiện..." class="form-control me-2">
        <button type="submit" class="btn btn-primary">
            <i class="fas fa-search"></i> Tra cứu
        </button>
    </form>
    ...
</nav>
```

### 2.2 Sidebar mỗi role
Thêm vào các file sidebar:
- `manager/fragments/sidebar.html`
- `staff/fragments/sidebar.html`  
- `shipper/fragments/sidebar.html`
- `customer/fragments/sidebar.html`
- `admin/fragments/sidebar.html`

```html
<li class="nav-item">
    <a th:href="@{/{role}/tracking}" class="nav-link">
        <i class="fas fa-search-location me-2"></i>Tra cứu vận đơn
    </a>
</li>
```

---

## PHẦN 3: TRANG TRA CỨU CÔNG KHAI

### URL: `/tracking?code=xxx`
- Không cần đăng nhập
- Nhập mã RequestCode (REQ-xxx) hoặc ParcelCode (PCL-xxx)

### Controller: `PublicTrackingController.java`
```java
@Controller
public class PublicTrackingController {
    
    @GetMapping("/tracking")
    public String tracking(@RequestParam(value = "code", required = false) String code, Model model) {
        if (code == null || code.trim().isEmpty()) {
            return "public/tracking";
        }
        
        // Thử tìm theo RequestCode
        Optional<CustomerRequest> requestOpt = requestRepository.findByRequestCode(code.trim());
        if (requestOpt.isPresent()) {
            model.addAttribute("order", requestOpt.get());
            model.addAttribute("actions", parcelActionRepository.findByRequestIdOrderByCreatedAtDesc(requestOpt.get().getId()));
            model.addAttribute("found", true);
            return "public/tracking";
        }
        
        // Thử tìm theo ParcelCode
        Optional<Parcel> parcelOpt = parcelRepository.findByParcelCode(code.trim());
        if (parcelOpt.isPresent()) {
            model.addAttribute("parcel", parcelOpt.get());
            model.addAttribute("order", parcelOpt.get().getRequest());
            model.addAttribute("actions", parcelActionRepository.findByParcelIdOrderByCreatedAtDesc(parcelOpt.get().getId()));
            model.addAttribute("found", true);
            return "public/tracking";
        }
        
        model.addAttribute("errorMessage", "Không tìm thấy đơn hàng/kiện hàng với mã: " + code);
        return "public/tracking";
    }
}
```

### Thông tin HIỂN THỊ (công khai):
| Thông tin | Hiển thị |
|-----------|----------|
| Mã đơn hàng | ✅ REQ-xxx |
| Mã kiện hàng | ✅ PCL-xxx |
| Tên người gửi | ✅ Chỉ tên, không SĐT |
| Tên người nhận | ✅ Chỉ tên, không SĐT |
| Địa chỉ gửi | ✅ Chỉ quận/thành phố |
| Địa chỉ nhận | ✅ Chỉ quận/thành phố |
| Trạng thái | ✅ |
| Lịch sử vận chuyển | ✅ |

### Thông tin ẨN (nhạy cảm):
| Thông tin | Ẩn |
|-----------|-----|
| SĐT người gửi/nhận | ❌ |
| Giá trị COD | ❌ |
| Phí vận chuyển | ❌ |
| Email | ❌ |
| Địa chỉ chi tiết | ❌ |

---

## PHẦN 4: XÓA ENTITY TRACKING CODE

### Bước 1: Tìm tất cả references
```
TrackingCode entity
TrackingCodeRepository
TrackingCodeDTO
TrackingService (các method liên quan)
TrackingServiceImpl
CustomerTrackingController (sửa để dùng RequestCode/ParcelCode)
insert_data*.sql (xóa INSERT INTO tracking_codes)
```

### Bước 2: Xóa/Sửa các file
| File | Hành động |
|------|-----------|
| `TrackingCode.java` | XÓA |
| `TrackingCodeRepository.java` | XÓA |
| `TrackingCodeDTO.java` | XÓA |
| `TrackingService.java` | Xóa các method liên quan đến TrackingCode |
| `TrackingServiceImpl.java` | Xóa các method liên quan đến TrackingCode |
| `CustomerTrackingController.java` | Sửa logic để dùng RequestCode/ParcelCode |
| `CustomerOrderDetailController.java` | Xóa reference đến trackingCodeRepository |
| `insert_data.sql` | Xóa INSERT INTO tracking_codes |
| `insert_data2.sql` | Xóa INSERT INTO tracking_codes (nếu có) |

### Bước 3: Xóa bảng trong database
```sql
DROP TABLE IF EXISTS tracking_codes;
```

---

## DANH SÁCH FILE CẦN TẠO/SỬA

### TẠO MỚI:
| File | Mô tả |
|------|-------|
| `ParcelActionService.java` | Service ghi nhận action |
| `PublicTrackingController.java` | Controller tra cứu công khai |
| `templates/public/tracking.html` | Trang tra cứu công khai |

### SỬA:
| File | Thay đổi |
|------|----------|
| `CustomerOrderCreateController.java` | Gọi recordAction() khi tạo đơn |
| Các controller khác | Gọi recordAction() tương ứng |
| Tất cả sidebar | Thêm link tra cứu |
| `templates/index.html` hoặc header | Thêm form tra cứu |
| `ActionTypeRepository.java` | Thêm findByActionCode() |

### XÓA:
| File | Lý do |
|------|-------|
| `TrackingCode.java` | Không cần thiết |
| `TrackingCodeRepository.java` | Không cần thiết |
| `TrackingCodeDTO.java` | Không cần thiết |

---

## YÊU CẦU ĐÃ XÁC NHẬN
- ✅ Tự động ghi nhận TẤT CẢ sự kiện
- ✅ Ẩn thông tin nhạy cảm (SĐT, COD, phí ship, email, địa chỉ chi tiết)
- ✅ Tra cứu ở landing page (chưa đăng nhập) + sidebar mỗi role
- ✅ Xóa TrackingCode entity
