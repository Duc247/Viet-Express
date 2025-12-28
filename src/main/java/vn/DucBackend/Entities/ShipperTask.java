package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipper_tasks")
@Getter
@Setter
public class ShipperTask {

    public enum TaskType {
        PICKUP, DELIVERY, RETURN
    }

    public enum TaskStatus {
        ASSIGNED, IN_PROGRESS, DONE, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = false)
    private Shipper shipper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private CustomerRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = 20)
    private TaskType taskType;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", length = 20)
    private TaskStatus taskStatus;

    @Column(name = "result_note", columnDefinition = "TEXT")
    private String resultNote;
}
