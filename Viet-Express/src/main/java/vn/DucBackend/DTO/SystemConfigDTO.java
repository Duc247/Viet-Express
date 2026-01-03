package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDTO {

    private Long id;
    private String configKey;
    private String configValue;
    private String configType;
    private String configGroup;
    private String description;
    private Boolean isActive;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedByUsername;
}
