package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho Vehicle entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String licensePlate;
    private String type;
    private BigDecimal maxWeight;
    private BigDecimal maxVolume;
    private String status; // VehicleStatus: AVAILABLE, ON_TRIP, MAINTENANCE
}
