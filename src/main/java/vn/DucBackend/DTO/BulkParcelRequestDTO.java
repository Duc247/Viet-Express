package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO cho bulk parcel creation request
 * Dùng để nhận dữ liệu từ form tạo nhiều kiện hàng giống nhau
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkParcelRequestDTO {
    private Long requestId;
    private String description;
    private BigDecimal codAmount;
    private BigDecimal weightKg;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private Integer quantity;
}
