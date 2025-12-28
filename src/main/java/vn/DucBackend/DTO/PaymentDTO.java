package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho Payment entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long requestId;
    private String trackingCode;
    private String type; // PaymentType: SHIPPING_FEE, COD
    private Long payerCustomerId;
    private String payerCustomerName;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String status; // PaymentStatus: UNPAID, PARTIAL, PAID, REMITTED
    private LocalDateTime createdAt;
}
