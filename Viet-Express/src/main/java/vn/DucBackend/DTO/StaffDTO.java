package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StaffDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private Long locationId;
    private String locationName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
