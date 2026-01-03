package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShipperDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String phone;
    private String workingArea;
    private Boolean isAvailable;
    private Long currentLocationId;
    private String currentLocationName;
    private Long currentTripId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
