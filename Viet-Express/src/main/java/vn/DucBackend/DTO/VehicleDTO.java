package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VehicleDTO {
    private Long id;
    private String vehicleType;
    private String licensePlate;
    private BigDecimal capacityWeight;
    private BigDecimal capacityVolume;
    private String status;
    private Long currentLocationId;
    private String currentLocationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
