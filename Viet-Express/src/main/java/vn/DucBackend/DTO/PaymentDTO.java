package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long requestId;
    private String requestCode;
    private Long parcelId;
    private String parcelCode;
    private String paymentCode;
    private String paymentType;
    private String payerType;
    private String receiverType;
    private BigDecimal expectedAmount;
    private BigDecimal paidAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
