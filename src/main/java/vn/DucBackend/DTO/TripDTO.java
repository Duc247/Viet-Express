package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TripDTO {
    private Long id;
    private Long shipperId;
    private String shipperName;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private String tripType;
    private Long startLocationId;
    private String startLocationName;
    private Long endLocationId;
    private String endLocationName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal codAmount;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
