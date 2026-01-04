package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentTransactionDTO {
    private Long id;
    private Long paymentId;
    private String paymentCode;
    private BigDecimal amount;
    private String transactionType;
    private String paymentMethod;
    private String transactionRef;
    private String status;
    private Long performedById;
    private String performedByName;
    private String gatewayResponse;
    private LocalDateTime transactionAt;
    private LocalDateTime createdAt;
}
