package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleDTO {
    private Long id;
    private String roleName;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
