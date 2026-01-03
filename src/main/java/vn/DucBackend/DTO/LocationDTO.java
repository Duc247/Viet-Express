package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LocationDTO {
    private Long id;
    private String locationType;
    private String warehouseCode;
    private String name;
    private String addressText;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
