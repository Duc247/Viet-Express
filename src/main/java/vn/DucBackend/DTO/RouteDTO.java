package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RouteDTO {
    private Long id;
    private Long fromLocationId;
    private String fromLocationName;
    private Long toLocationId;
    private String toLocationName;
    private BigDecimal distanceKm;
    private BigDecimal estimatedTimeHours;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
