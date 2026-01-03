package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemLogDTO {
    private Long id;
    private String logLevel;
    private String moduleName;
    private String actionType;
    private Long actorId;
    private String actorName;
    private String targetId;
    private String logDetails;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}
