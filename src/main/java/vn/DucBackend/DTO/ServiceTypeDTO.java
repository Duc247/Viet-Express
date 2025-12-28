package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho ServiceType entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeDTO {
    private Long id;
    private String serviceCode;
    private String serviceName;
    private BigDecimal basePrice;
    private BigDecimal pricePerKg;
    private String description;
    private Boolean status;
}
