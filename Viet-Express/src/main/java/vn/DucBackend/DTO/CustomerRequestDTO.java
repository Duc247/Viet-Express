package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerRequestDTO {
    private Long id;
    private String requestCode;
    private Long senderId;
    private String senderName;
    private String senderPhone;
    private Long receiverId;
    private String receiverName;
    private String receiverPhone;
    private Long senderLocationId;
    private String senderLocationName;
    private String senderAddress;
    private Long receiverLocationId;
    private String receiverLocationName;
    private String receiverAddress;
    private Long serviceTypeId;
    private String serviceTypeName;
    private BigDecimal distanceKm;
    private String parcelDescription;
    private BigDecimal shippingFee;
    private BigDecimal codAmount;
    private LocalDateTime estimatedDeliveryTime;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
