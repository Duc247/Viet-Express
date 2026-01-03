package vn.DucBackend.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long id;
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String gender;
    private String fullname;
    private String name;
    private String address;
    private LocalDateTime birthday;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
