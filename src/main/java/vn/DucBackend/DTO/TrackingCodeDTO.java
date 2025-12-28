package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho TrackingCode entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingCodeDTO {
    private Long id;
    private Long requestId;
    private String code;
    private Boolean status;
    private LocalDateTime createdAt;
}
