package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Getter
@Setter
public class SystemLog {

    public enum LogLevel {
        INFO, WARN, ERROR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_level", nullable = false, length = 10)
    private LogLevel logLevel;

    @Column(name = "module_name", length = 50)
    private String moduleName;

    @Column(name = "action_type", length = 100)
    private String actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "log_details", columnDefinition = "TEXT")
    private String logDetails;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (logLevel == null) {
            logLevel = LogLevel.INFO;
        }
    }
}
