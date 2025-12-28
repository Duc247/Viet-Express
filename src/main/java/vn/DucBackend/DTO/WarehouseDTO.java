package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Warehouse entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private Long id;
    private String warehouseName;
    private String address;
    private String ward;
    private String district;
    private String province;
    private String warehouseType; // HUB, DISTRIBUTION_CENTER, PICKUP_POINT
    private Boolean status;
    private String contactPhone;
    private String email;
    private LocalDateTime createdAt;
}
