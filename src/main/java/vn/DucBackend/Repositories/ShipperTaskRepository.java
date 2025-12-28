package vn.DucBackend.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.ShipperTask;
import vn.DucBackend.Entities.ShipperTask.TaskStatus;
import vn.DucBackend.Entities.ShipperTask.TaskType;

/**
 * Repository cho ShipperTask entity - Quản lý phân công shipper
 * Phục vụ: phân công pickup/delivery/return, theo dõi tiến độ task của shipper
 */
@Repository
public interface ShipperTaskRepository extends JpaRepository<ShipperTask, Long> {

    // ==================== LỌC THEO SHIPPER ====================

    /**
     * Lấy danh sách task của shipper theo trạng thái - dùng cho shipper xem task
     * được giao
     */
    Page<ShipperTask> findAllByShipper_IdAndTaskStatus(Long shipperId, TaskStatus taskStatus, Pageable pageable);

    List<ShipperTask> findAllByShipper_IdAndTaskStatus(Long shipperId, TaskStatus taskStatus);

    /** Lấy tất cả task của shipper */
    Page<ShipperTask> findAllByShipper_Id(Long shipperId, Pageable pageable);

    List<ShipperTask> findAllByShipper_Id(Long shipperId);

    /** Đếm số task của shipper theo trạng thái */
    Long countByShipper_IdAndTaskStatus(Long shipperId, TaskStatus taskStatus);

    // ==================== LỌC THEO VẬN ĐƠN ====================

    /**
     * Lấy tất cả task liên quan đến một vận đơn - dùng cho tracking lịch sử phân
     * công
     */
    List<ShipperTask> findAllByRequest_Id(Long requestId);

    /** Kiểm tra vận đơn đã có task với loại và trạng thái cụ thể chưa */
    Boolean existsByRequest_IdAndTaskTypeAndTaskStatus(Long requestId, TaskType taskType, TaskStatus taskStatus);

    /**
     * Lấy task mới nhất của vận đơn theo loại - dùng để kiểm tra trạng thái hiện
     * tại
     */
    Optional<ShipperTask> findTop1ByRequest_IdAndTaskTypeOrderByAssignedAtDesc(Long requestId, TaskType taskType);

    /** Lấy task mới nhất của vận đơn */
    Optional<ShipperTask> findTop1ByRequest_IdOrderByAssignedAtDesc(Long requestId);

    // ==================== LỌC THEO LOẠI TASK ====================

    /** Lấy danh sách task theo loại (PICKUP/DELIVERY/RETURN) */
    Page<ShipperTask> findAllByTaskType(TaskType taskType, Pageable pageable);

    List<ShipperTask> findAllByTaskType(TaskType taskType);

    /** Lấy task của shipper theo loại */
    List<ShipperTask> findAllByShipper_IdAndTaskType(Long shipperId, TaskType taskType);

    // ==================== LỌC THEO TRẠNG THÁI ====================

    /** Lấy danh sách task theo trạng thái */
    Page<ShipperTask> findAllByTaskStatus(TaskStatus taskStatus, Pageable pageable);

    List<ShipperTask> findAllByTaskStatus(TaskStatus taskStatus);

    /** Đếm số task theo trạng thái */
    Long countByTaskStatus(TaskStatus taskStatus);

    // ==================== LỌC THEO THỜI GIAN ====================

    /** Lấy task được giao trong khoảng thời gian */
    Page<ShipperTask> findAllByAssignedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    /** Lấy task của shipper trong khoảng thời gian */
    List<ShipperTask> findAllByShipper_IdAndAssignedAtBetween(Long shipperId, LocalDateTime from, LocalDateTime to);

    /** Lấy task hoàn thành trong khoảng thời gian */
    Page<ShipperTask> findAllByCompletedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    /** Đếm task được giao trong khoảng thời gian */
    Long countByAssignedAtBetween(LocalDateTime from, LocalDateTime to);
}
