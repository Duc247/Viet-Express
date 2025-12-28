package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho ParcelAction entity (Tracking log)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelActionDTO {
    private Long id;
    private Long requestId;
    private Long actionTypeId;
    private String actionCode;
    private String actionName;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private Long actorUserId;
    private String actorUserName;
    private LocalDateTime actionTime;
    private String note;
}
