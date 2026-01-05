# PROMPT: Tạo Tính Năng Staff Tạo Nhiều Kiện Hàng Cùng Lúc (Bulk Parcel Creation)

## 📋 Mô Tả Yêu Cầu

Tôi cần bạn thêm tính năng cho **Staff** có thể tạo nhiều kiện hàng (parcel) giống nhau cùng lúc để tiết kiệm thời gian. 

Hiện tại Staff chỉ có thể tạo 1 parcel mỗi lần. Tính năng mới cho phép:
- Tick checkbox "Tạo nhiều kiện hàng giống nhau"
- Nhập số lượng parcel muốn tạo
- Xác nhận để tạo tất cả cùng lúc

---

## 🎯 Yêu Cầu Chi Tiết

### 1. Các trường thông tin của mỗi Parcel:

| Trường | Xử lý khi tạo nhiều |
|--------|---------------------|
| `parcelCode` | **Tự động tạo khác nhau** - Format: `PCL-YYYYMMDD-{requestId}-{sequence}` |
| `description` | **Thêm số thứ tự ở đầu** - Format: `#1 - Mô tả gốc`, `#2 - Mô tả gốc`... |
| `codAmount` | **Giống nhau** cho tất cả parcel |
| `weightKg` | **Giống nhau** cho tất cả parcel |
| `lengthCm`, `widthCm`, `heightCm` | **Giống nhau** cho tất cả parcel |
| `status` | Mặc định `CREATED` |
| `currentLocation` | Lấy từ staff's location |

### 2. Flow hoạt động:

```
1. Staff mở trang chi tiết request (/staff/requests/{id})
2. Nhập thông tin parcel (description, COD, weight, dimensions)
3. Tick checkbox "Tạo nhiều kiện hàng giống nhau" 
   → Hiện thêm field "Số lượng"
4. Nhập số lượng (VD: 5)
5. Click nút "Xem trước"
   → Gọi AJAX đến endpoint preview
   → Hiện modal với bảng preview 5 parcel
6. Staff xem và click "Xác nhận tạo"
   → Submit form đến endpoint bulk create
   → Redirect về với success message
```

### 3. Nếu KHÔNG tick checkbox:
- Form hoạt động như bình thường (tạo 1 parcel)
- Giữ nguyên flow cũ, không thêm số thứ tự vào description

---

## 📁 Files Cần Sửa/Tạo

### File 1: TẠO MỚI - `ParcelPreviewDTO.java`

**Path:** `src/main/java/vn/DucBackend/DTO/ParcelPreviewDTO.java`

```java
package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelPreviewDTO {
    private Integer index;           // Số thứ tự (1, 2, 3...)
    private String parcelCode;       // Mã kiện hàng preview
    private String description;      // "#1 - Mô tả gốc"
    private BigDecimal codAmount;
    private BigDecimal weightKg;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
}
```

---

### File 2: SỬA - `StaffRequestController.java`

**Path:** `src/main/java/vn/DucBackend/Controllers/Staff/StaffRequestController.java`

**Thêm 2 endpoints mới vào controller:**

#### Endpoint 1: Preview Bulk Parcels (AJAX - trả về JSON)

```java
@PostMapping("/requests/{id}/preview-bulk-parcels")
@ResponseBody
public List<ParcelPreviewDTO> previewBulkParcels(
        @PathVariable("id") Long requestId,
        @RequestParam("description") String description,
        @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
        @RequestParam(value = "weightKg", required = false) BigDecimal weightKg,
        @RequestParam(value = "lengthCm", required = false) BigDecimal lengthCm,
        @RequestParam(value = "widthCm", required = false) BigDecimal widthCm,
        @RequestParam(value = "heightCm", required = false) BigDecimal heightCm,
        @RequestParam("quantity") Integer quantity) {

    List<ParcelPreviewDTO> previews = new ArrayList<>();
    
    // Lấy số parcel hiện tại của request để tính sequence
    long currentCount = parcelRepository.countByRequestId(requestId);
    String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    
    for (int i = 1; i <= quantity; i++) {
        ParcelPreviewDTO preview = new ParcelPreviewDTO();
        preview.setIndex(i);
        preview.setParcelCode(String.format("PCL-%s-%d-%02d", datePart, requestId, currentCount + i));
        preview.setDescription("#" + i + " - " + description);
        preview.setCodAmount(codAmount);
        preview.setWeightKg(weightKg);
        preview.setLengthCm(lengthCm);
        preview.setWidthCm(widthCm);
        preview.setHeightCm(heightCm);
        previews.add(preview);
    }
    
    return previews;
}
```

#### Endpoint 2: Create Bulk Parcels

```java
@PostMapping("/requests/{id}/create-bulk-parcels")
public String createBulkParcels(
        @PathVariable("id") Long requestId,
        @RequestParam("description") String description,
        @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
        @RequestParam(value = "weightKg", required = false) BigDecimal weightKg,
        @RequestParam(value = "lengthCm", required = false) BigDecimal lengthCm,
        @RequestParam(value = "widthCm", required = false) BigDecimal widthCm,
        @RequestParam(value = "heightCm", required = false) BigDecimal heightCm,
        @RequestParam("quantity") Integer quantity,
        HttpSession session, RedirectAttributes redirectAttributes) {

    Long staffId = getStaffIdFromSession(session);
    if (staffId == null) {
        return "redirect:/auth/login";
    }

    Optional<CustomerRequest> reqOpt = customerRequestRepository.findById(requestId);
    if (reqOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
        return "redirect:/staff/requests";
    }

    CustomerRequest customerRequest = reqOpt.get();
    Staff staff = staffRepository.findById(staffId).orElse(null);
    
    String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    long currentCount = parcelRepository.countByRequestId(requestId);
    
    List<String> createdCodes = new ArrayList<>();
    
    for (int i = 1; i <= quantity; i++) {
        String parcelCode = String.format("PCL-%s-%d-%02d", datePart, requestId, currentCount + i);
        String numberedDescription = "#" + i + " - " + description;
        
        Parcel parcel = new Parcel();
        parcel.setRequest(customerRequest);
        parcel.setParcelCode(parcelCode);
        parcel.setDescription(numberedDescription);
        parcel.setCodAmount(codAmount);
        parcel.setWeightKg(weightKg);
        parcel.setLengthCm(lengthCm);
        parcel.setWidthCm(widthCm);
        parcel.setHeightCm(heightCm);
        parcel.setStatus(Parcel.ParcelStatus.CREATED);
        
        if (staff != null && staff.getLocation() != null) {
            parcel.setCurrentLocation(staff.getLocation());
        }
        
        parcelRepository.save(parcel);
        
        // Tạo parcel action - CREATED
        createParcelAction(parcel, customerRequest, "CREATED", null,
                staff != null ? staff.getLocation() : null,
                getUserIdFromSession(session),
                "Staff tạo kiện hàng: " + numberedDescription);
        
        createdCodes.add(parcelCode);
    }

    redirectAttributes.addFlashAttribute("successMessage",
            "Đã tạo " + quantity + " kiện hàng thành công: " + String.join(", ", createdCodes));
    return "redirect:/staff/requests/" + requestId;
}
```

**Lưu ý:** Thêm import cần thiết:
```java
import org.springframework.web.bind.annotation.ResponseBody;
import vn.DucBackend.DTO.ParcelPreviewDTO;
import java.util.ArrayList;
```

---

### File 3: SỬA - `detail.html`

**Path:** `src/main/resources/templates/staff/request/detail.html`

**Sửa phần form tạo kiện hàng (thay thế form hiện tại):**

```html
<!-- Form tạo kiện hàng -->
<div class="col-lg-7">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white py-3">
            <h6 class="m-0 fw-bold"><i class="fas fa-plus-circle me-2"></i>Tạo kiện hàng mới</h6>
        </div>
        <div class="card-body">
            <form id="parcelForm" method="post">
                <div class="mb-3">
                    <label class="form-label fw-bold">Mô tả kiện hàng <span class="text-danger">*</span></label>
                    <input type="text" id="description" name="description" class="form-control"
                        placeholder="VD: Laptop Dell, Thùng 1/3..." required>
                </div>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">Tiền COD (VNĐ)</label>
                        <input type="number" id="codAmount" name="codAmount" class="form-control" placeholder="0" value="0">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Trọng lượng (kg)</label>
                        <input type="number" id="weightKg" name="weightKg" class="form-control" step="0.1" placeholder="0.0">
                    </div>
                </div>

                <div class="row g-3 mt-2">
                    <div class="col-md-4">
                        <label class="form-label">Dài (cm)</label>
                        <input type="number" id="lengthCm" name="lengthCm" class="form-control" step="0.1" placeholder="0">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Rộng (cm)</label>
                        <input type="number" id="widthCm" name="widthCm" class="form-control" step="0.1" placeholder="0">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Cao (cm)</label>
                        <input type="number" id="heightCm" name="heightCm" class="form-control" step="0.1" placeholder="0">
                    </div>
                </div>

                <!-- PHẦN MỚI: Bulk Create -->
                <div class="mt-4 p-3 border rounded bg-light">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="bulkCheckbox" onchange="toggleBulkMode()">
                        <label class="form-check-label fw-bold" for="bulkCheckbox">
                            <i class="fas fa-layer-group me-1"></i> Tạo nhiều kiện hàng giống nhau
                        </label>
                    </div>
                    
                    <div id="bulkOptions" class="mt-3" style="display: none;">
                        <div class="row align-items-end">
                            <div class="col-md-4">
                                <label class="form-label">Số lượng</label>
                                <input type="number" id="quantity" name="quantity" class="form-control" 
                                    min="2" value="2" placeholder="Số lượng">
                            </div>
                            <div class="col-md-4">
                                <button type="button" class="btn btn-outline-primary w-100" onclick="showPreview()">
                                    <i class="fas fa-eye me-1"></i> Xem trước
                                </button>
                            </div>
                        </div>
                        <small class="text-muted mt-2 d-block">
                            <i class="fas fa-info-circle me-1"></i>
                            Mỗi kiện sẽ có mô tả: #1 - [Mô tả], #2 - [Mô tả]...
                        </small>
                    </div>
                </div>
                <!-- KẾT THÚC PHẦN MỚI -->

                <div class="d-grid mt-4">
                    <button type="submit" id="submitBtn" class="btn btn-success btn-lg">
                        <i class="fas fa-box me-2"></i>Tạo Kiện Hàng
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- MODAL PREVIEW -->
<div class="modal fade" id="previewModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">
                    <i class="fas fa-eye me-2"></i>Xem trước kiện hàng sẽ được tạo
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Mã kiện hàng</th>
                                <th>Mô tả</th>
                                <th>COD</th>
                                <th>Trọng lượng</th>
                            </tr>
                        </thead>
                        <tbody id="previewTableBody">
                            <!-- Filled by JavaScript -->
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times me-1"></i>Hủy
                </button>
                <button type="button" class="btn btn-success" onclick="confirmBulkCreate()">
                    <i class="fas fa-check me-1"></i>Xác nhận tạo
                </button>
            </div>
        </div>
    </div>
</div>

<!-- JAVASCRIPT -->
<script th:inline="javascript">
    const requestId = [[${customerRequest.id}]];
    
    function toggleBulkMode() {
        const isChecked = document.getElementById('bulkCheckbox').checked;
        document.getElementById('bulkOptions').style.display = isChecked ? 'block' : 'none';
        
        // Update form action
        const form = document.getElementById('parcelForm');
        if (isChecked) {
            form.action = '/staff/requests/' + requestId + '/create-bulk-parcels';
        } else {
            form.action = '/staff/requests/' + requestId + '/create-parcel';
        }
    }
    
    function showPreview() {
        const description = document.getElementById('description').value;
        const quantity = document.getElementById('quantity').value;
        
        if (!description) {
            alert('Vui lòng nhập mô tả kiện hàng!');
            return;
        }
        if (!quantity || quantity < 2) {
            alert('Số lượng phải từ 2 trở lên!');
            return;
        }
        
        const formData = new FormData();
        formData.append('description', description);
        formData.append('codAmount', document.getElementById('codAmount').value || 0);
        formData.append('weightKg', document.getElementById('weightKg').value || '');
        formData.append('lengthCm', document.getElementById('lengthCm').value || '');
        formData.append('widthCm', document.getElementById('widthCm').value || '');
        formData.append('heightCm', document.getElementById('heightCm').value || '');
        formData.append('quantity', quantity);
        
        fetch('/staff/requests/' + requestId + '/preview-bulk-parcels', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('previewTableBody');
            tbody.innerHTML = '';
            
            data.forEach(item => {
                const row = `
                    <tr>
                        <td>${item.index}</td>
                        <td><code>${item.parcelCode}</code></td>
                        <td>${item.description}</td>
                        <td>${formatCurrency(item.codAmount)}</td>
                        <td>${item.weightKg ? item.weightKg + ' kg' : '-'}</td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
            
            const modal = new bootstrap.Modal(document.getElementById('previewModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi xem trước!');
        });
    }
    
    function confirmBulkCreate() {
        // Close modal
        bootstrap.Modal.getInstance(document.getElementById('previewModal')).hide();
        // Submit form
        document.getElementById('parcelForm').submit();
    }
    
    function formatCurrency(amount) {
        if (!amount) return '0đ';
        return new Intl.NumberFormat('vi-VN').format(amount) + 'đ';
    }
    
    // Set default form action
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('parcelForm').action = '/staff/requests/' + requestId + '/create-parcel';
    });
</script>
```

---

## ✅ Checklist Sau Khi Implement

- [ ] File `ParcelPreviewDTO.java` đã được tạo
- [ ] Thêm 2 endpoints mới vào `StaffRequestController.java`
- [ ] Thêm import cần thiết trong Controller
- [ ] Cập nhật form trong `detail.html` với checkbox, quantity input
- [ ] Thêm modal preview vào `detail.html`
- [ ] Thêm JavaScript xử lý toggle, preview, submit
- [ ] Test: Tạo 1 parcel (không tick checkbox) - flow cũ vẫn hoạt động
- [ ] Test: Tick checkbox, nhập số lượng, xem preview modal
- [ ] Test: Xác nhận tạo nhiều parcel thành công
- [ ] Verify: Mỗi parcel có mã code khác nhau
- [ ] Verify: Description có format `#1 - ...`, `#2 - ...`

---

## 📝 Lưu Ý Quan Trọng

1. **Backward Compatibility**: Flow tạo 1 parcel (không tick checkbox) phải hoạt động như cũ, KHÔNG thêm số thứ tự vào description

2. **Unique Parcel Code**: Hệ thống tự động tạo `parcelCode` theo format `PCL-YYYYMMDD-{requestId}-{sequence}`, đảm bảo không trùng nhau

3. **Description Format**: Số thứ tự ở ĐẦU mô tả, format: `#1 - Mô tả gốc`

4. **COD Amount**: Mỗi parcel có số tiền COD GIỐNG NHAU (không chia đều)

5. **Không giới hạn số lượng**: Staff có thể nhập bao nhiêu tùy ý

6. Hãy nói rõ những phương pháp này có khả thi không có thể gây lỗi không, có ảnh hưởng đến chức năng khác ngoài tạo parcel không?
