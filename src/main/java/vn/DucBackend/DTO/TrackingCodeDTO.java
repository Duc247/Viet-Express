package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackingCodeDTO {
    private Long id;
    private Long requestId;
    private String requestCode;
    private String code;
    private LocalDateTime createdAt;
}
