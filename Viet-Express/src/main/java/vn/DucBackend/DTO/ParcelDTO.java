package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParcelDTO {
    private Long id;
    private Long requestId;
    private String requestCode;
    private String parcelCode;
    private String description;
    private BigDecimal codAmount;
    private BigDecimal weightKg;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private Long currentLocationId;
    private String currentLocationName;
    private Long currentShipperId;
    private String currentShipperName;
    private Long currentTripId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
