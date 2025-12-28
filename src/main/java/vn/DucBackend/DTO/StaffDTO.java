package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Staff entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userPhone;
    private Long warehouseId;
    private String warehouseName;
    private String staffPosition;
    private Boolean status;
    private LocalDateTime joinedAt;
}
