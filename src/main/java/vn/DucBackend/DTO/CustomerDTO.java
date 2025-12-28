package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Customer entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private Long userId;
    private String customerType; // INDIVIDUAL, BUSINESS
    private String businessName;
    private String taxCode;
    private String email;
    private String phone;
    private Boolean status;
    private LocalDateTime createdAt;
}
