package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity để lưu trữ các cấu hình hệ thống
 * Sử dụng key-value pattern để linh hoạt trong việc thêm/sửa cấu hình
 */
@Entity
@Table(name = "system_configs")
@Getter
@Setter
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "config_type", length = 50)
    private String configType; // STRING, NUMBER, BOOLEAN, JSON, EMAIL, URL

    @Column(name = "config_group", length = 50)
    private String configGroup; // GENERAL, EMAIL, PAYMENT, API, SOCIAL, NOTIFICATION

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_public")
    private Boolean isPublic = false; // Có hiển thị cho người dùng không

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (isPublic == null) {
            isPublic = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
