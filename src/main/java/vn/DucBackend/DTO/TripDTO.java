package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Trip entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private Long id;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private Long driverShipperId;
    private String driverShipperName;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // TripStatus: READY, DELIVERING, COMPLETED
}
