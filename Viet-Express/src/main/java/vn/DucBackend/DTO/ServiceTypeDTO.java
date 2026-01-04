package vn.DucBackend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceTypeDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal pricePerKm;
    private BigDecimal averageSpeedKmh;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
