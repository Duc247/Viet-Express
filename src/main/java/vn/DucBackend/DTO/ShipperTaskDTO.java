package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho ShipperTask entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperTaskDTO {
    private Long id;
    private Long shipperId;
    private String shipperName;
    private Long requestId;
    private String trackingCode;
    private String taskType; // TaskType: PICKUP, DELIVERY, RETURN
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private String taskStatus; // TaskStatus: ASSIGNED, IN_PROGRESS, DONE, FAILED
    private String resultNote;
}
