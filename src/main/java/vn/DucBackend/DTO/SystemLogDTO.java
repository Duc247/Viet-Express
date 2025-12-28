package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho SystemLog entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLogDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String action;
    private String objectType;
    private Long objectId;
    private LocalDateTime logTime;
}
