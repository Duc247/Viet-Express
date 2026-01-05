# Feature: Payment Status History

> **Implementation Priority: 1/3**  
> ✅ **COMPLETED**  
> Không phụ thuộc tính năng khác

## Mục tiêu
1. **Tự động ghi lịch sử** khi trạng thái thanh toán (`Payment.status`) thay đổi
2. **Hiển thị lịch sử** trong modal trang payment detail (Customer + Manager)
3. **Phân quyền**: Customer chỉ xem lịch sử các khoản liên quan đến họ

---

## Yêu cầu chi tiết

### 1. Entity mới: PaymentStatusHistory

```java
@Entity
@Table(name = "payment_status_history")
public class PaymentStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    
    @Enumerated(EnumType.STRING)
    private Payment.PaymentStatus oldStatus;  // null nếu tạo mới
    
    @Enumerated(EnumType.STRING)
    private Payment.PaymentStatus newStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private User changedByUser;  // null nếu SYSTEM
    
    private String actorType;  // "MANAGER", "SHIPPER", "CUSTOMER", "SYSTEM"
    private String note;
    private LocalDateTime createdAt;
}
```

### 2. Phương án B: Method duy nhất thay đổi status

**Tạo method `changePaymentStatus()` trong PaymentService:**
```java
Payment changePaymentStatus(Long paymentId, Payment.PaymentStatus newStatus, 
                           User actor, String actorType, String note);
```

**Quy tắc:**
- TẤT CẢ nơi muốn thay đổi `Payment.status` phải gọi qua method này
- Đảm bảo ghi lịch sử đầy đủ, không bỏ sót

### 3. Các nơi cần sửa để gọi `changePaymentStatus()`

| File | Vị trí | Actor |
|------|--------|-------|
| `PaymentServiceImpl.updatePaymentStatus()` | Line 95 | User (Manager) - lấy từ session |
| `PaymentServiceImpl.updatePaymentPaidAmount()` | Line 219, 221 | **SYSTEM** |
| `CustomerPaymentsController.simulatePayment()` | Line 324 | **SYSTEM** |

### 4. Hiển thị trên giao diện

**Customer - Modal trong payments-detail.html:**
- Thêm tab "Lịch sử trạng thái" bên cạnh tab "Giao dịch" hiện có
- Hiển thị: Thời gian | Trạng thái cũ → mới | Người thực hiện | Ghi chú

**Manager - Thêm vào detail.html:**
- Thêm nút "Lịch sử" và modal tương tự Customer

---

## Các file cần sửa/tạo

### Backend (Java)

| File | Thay đổi |
|------|----------|
| `PaymentStatusHistory.java` | **TẠO MỚI** - Entity |
| `PaymentStatusHistoryRepository.java` | **TẠO MỚI** - Repository |
| `PaymentService.java` | Thêm method `changePaymentStatus()` |
| `PaymentServiceImpl.java` | Implement + sửa các method gọi setStatus |
| `CustomerPaymentsController.java` | Thêm API `/payments/{id}/status-history` + sửa simulatePayment |
| `ManagerPaymentController.java` | Thêm API `/payments/{id}/status-history` |

### Frontend (Thymeleaf)

| File | Thay đổi |
|------|----------|
| `customer/order/payments-detail.html` | Thêm tab "Lịch sử trạng thái" trong modal |
| `manager/payment/detail.html` | Thêm nút "Lịch sử" + modal |

---

## Flow hoàn chỉnh

```
1. Manager/Shipper/System thay đổi trạng thái Payment
   ↓
2. Gọi changePaymentStatus(paymentId, newStatus, actor, actorType, note)
   ↓
3. So sánh oldStatus với newStatus
   ↓
4. Nếu khác nhau → Tạo bản ghi PaymentStatusHistory
   ↓
5. Cập nhật Payment.status
   ↓
6. Lưu cả hai
```

---

## Yêu cầu đã xác nhận

- ✅ Phương án B: Method duy nhất `changePaymentStatus()`
- ✅ Actor = User nếu thao tác chủ động (Manager), SYSTEM nếu tự động
- ✅ Không cần sidebar mới, sử dụng modal trong trang detail hiện có
- ✅ Customer (sender/receiver) có thể xem lịch sử các khoản liên quan
