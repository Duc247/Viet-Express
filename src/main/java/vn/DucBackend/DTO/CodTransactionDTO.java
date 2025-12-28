package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho CodTransaction entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodTransactionDTO {
    private Long id;
    private Long requestId;
    private String trackingCode;
    private Long shipperId;
    private String shipperName;
    private BigDecimal amount;
    private LocalDateTime collectedAt;
    private LocalDateTime settledAt;
    private String status;
    private String paymentMethod;
}
