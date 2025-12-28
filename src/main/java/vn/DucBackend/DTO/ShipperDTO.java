package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Shipper entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userPhone;
    private Long warehouseId;
    private String warehouseName;
    private String status; // ShipperStatus: AVAILABLE, ON_TRIP, OFF_DUTY
    private LocalDateTime joinedAt;
}
