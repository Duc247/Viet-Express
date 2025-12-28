package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho WarehouseRoute entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRouteDTO {
    private Long id;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private Integer estimatedTime;
    private String description;
    private Boolean status;
}
