package vn.DucBackend.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String fullName;
    private String avatar;
    private Long roleId;
    private String roleName;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Các trường dành riêng cho từng role
    // CUSTOMER
    private String address;
    private String companyName;
    private String gender;

    // SHIPPER
    private String workingArea;
    private Long currentLocationId;

    // STAFF
    private Long locationId;
}
