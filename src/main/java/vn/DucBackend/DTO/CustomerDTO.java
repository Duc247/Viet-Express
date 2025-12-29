package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
