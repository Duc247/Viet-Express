package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho PaymentTransaction entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDTO {
    private Long id;
    private Long paymentId;
    private BigDecimal amount;
    private String method; // PaymentMethod: CASH, BANK_TRANSFER, VNPAY, MOMO
    private String gatewayTransactionId;
    private String gatewayResponseCode;
    private String note;
    private LocalDateTime createdAt;
}
