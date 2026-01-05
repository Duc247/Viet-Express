# Feature: Route-Based Shipping Fee Calculation

> **Implementation Priority: 3/3**  
> Triển khai sau: `promptAutoTrackingAndPublicSearch.md`  
> Phụ thuộc: Cần có Route entity (đã có sẵn)

## Mục tiêu
Khi customer tạo đơn hàng với 2 địa điểm chưa có Route trong database:
- **Phí vận chuyển** hiển thị: `"Cần cập nhật tuyến đường"`
- Khi **Manager tạo Route** cho 2 địa điểm đó → phí tự động cập nhật cho **TẤT CẢ** đơn hàng có cùng route

---

## Yêu cầu chi tiết

### 1. Khi Customer tạo đơn hàng

**Logic hiện tại:**
```
shippingFee = serviceType.pricePerKm × distanceKm (mặc định 10km)
```

**Logic mới:**
```
1. Lấy senderLocationId và receiverLocationId từ đơn hàng
2. Tìm Route trong database: findByFromLocationIdAndToLocationId(senderLocationId, receiverLocationId)
3. NẾU tìm thấy Route:
   → distanceKm = route.distanceKm
   → shippingFee = serviceType.pricePerKm × distanceKm
4. NẾU KHÔNG tìm thấy Route:
   → distanceKm = NULL
   → shippingFee = NULL (hoặc -1 để đánh dấu "chưa tính")
   → Hiển thị: "Cần cập nhật tuyến đường"
```

### 2. Hiển thị trên giao diện

**Ở các trang hiển thị phí (Customer order list, detail, Manager view...):**
```html
<!-- Nếu shippingFee = NULL hoặc <= 0 -->
<span class="badge bg-warning text-dark">
    <i class="fas fa-exclamation-triangle me-1"></i>
    Cần cập nhật tuyến đường
</span>

<!-- Nếu shippingFee > 0 -->
<span th:text="${#numbers.formatDecimal(order.shippingFee, 0, 'COMMA', 0, 'POINT')} + ' ₫'">
    1,000,000 ₫
</span>
```

### 3. Manager tạo Route (trang riêng biệt)

**Manager cần có trang quản lý Routes:**
- URL: `/manager/routes`
- Chức năng:
  - Xem danh sách Routes hiện có
  - Tạo Route mới: chọn fromLocation, toLocation, nhập distanceKm
  - Sửa/Xóa Route

**Khi Manager tạo Route mới:**
```java
@PostMapping("/routes/create")
public String createRoute(...) {
    // 1. Lưu Route mới
    Route route = new Route();
    route.setFromLocation(fromLocation);
    route.setToLocation(toLocation);
    route.setDistanceKm(distanceKm);
    routeRepository.save(route);
    
    // 2. Cập nhật tất cả đơn hàng có cùng senderLocation và receiverLocation
    updateShippingFeeForRoute(fromLocation.getId(), toLocation.getId(), distanceKm);
    
    return "redirect:/manager/routes";
}
```

### 4. Tự động cập nhật phí cho các đơn hàng

**Service method:**
```java
public void updateShippingFeeForRoute(Long fromLocationId, Long toLocationId, BigDecimal distanceKm) {
    // Tìm tất cả đơn hàng có:
    // - senderLocationId = fromLocationId
    // - receiverLocationId = toLocationId
    // - shippingFee IS NULL hoặc <= 0
    
    List<CustomerRequest> requests = customerRequestRepository
        .findBySenderLocationIdAndReceiverLocationIdAndShippingFeeIsNullOrLessThanEqual(
            fromLocationId, toLocationId, BigDecimal.ZERO);
    
    for (CustomerRequest request : requests) {
        // Tính phí mới
        BigDecimal newFee = request.getServiceType().getPricePerKm().multiply(distanceKm);
        request.setShippingFee(newFee);
        request.setDistanceKm(distanceKm);
        customerRequestRepository.save(request);
    }
}
```

---

## Các file cần sửa/tạo

### Backend (Java)

| File | Thay đổi |
|------|----------|
| `CustomerRequestServiceImpl.java` | Sửa `createRequest()` để check Route trước khi tính phí |
| `CustomerRequestServiceImpl.java` | Thêm method `updateShippingFeeForRoute()` |
| `ManagerRouteController.java` | **TẠO MỚI** - Controller quản lý Routes |
| `RouteRepository.java` | Thêm query `findByFromLocationIdAndToLocationId()` |
| `CustomerRequestRepository.java` | Thêm query tìm đơn hàng theo senderLocation + receiverLocation |

### Frontend (Thymeleaf)

| File | Thay đổi |
|------|----------|
| `customer/orders.html` | Hiển thị "Cần cập nhật tuyến đường" nếu fee = null |
| `customer/order/detail.html` | Tương tự |
| `manager/request/requests.html` | Tương tự |
| `manager/request/detail.html` | Tương tự |
| `manager/routes/routes.html` | **TẠO MỚI** - Trang danh sách Routes |
| `manager/routes/form.html` | **TẠO MỚI** - Form tạo/sửa Route |
| `manager/fragments/sidebar.html` | Thêm link "Quản lý tuyến đường" |

---

## Flow hoàn chỉnh

```
1. Customer tạo đơn hàng (địa chỉ mới)
   ↓
2. Hệ thống check Route:
   - Route không tồn tại → shippingFee = NULL
   ↓
3. Đơn hàng được tạo với shippingFee = NULL
   ↓
4. Customer/Manager thấy: "Cần cập nhật tuyến đường"
   ↓
5. Manager vào trang /manager/routes
   ↓
6. Manager tạo Route mới (chọn 2 location, nhập khoảng cách)
   ↓
7. Hệ thống tự động cập nhật shippingFee cho TẤT CẢ đơn hàng có cùng route
   ↓
8. Customer refresh trang → thấy phí đã được tính
```

---

## Ghi chú bổ sung (ĐÃ XÁC NHẬN)

- ✅ Route là **2 chiều**: Khi Manager tạo A→B, hệ thống tự động tạo luôn B→A với cùng khoảng cách
- ❌ Không cần gửi notification cho Customer khi phí được cập nhật

