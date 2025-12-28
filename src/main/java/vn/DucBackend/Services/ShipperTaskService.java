package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.ShipperTaskDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface cho ShipperTask - Quản lý phân công shipper
 */
public interface ShipperTaskService {

    // ==================== PHÂN CÔNG TASK ====================

    /** Phân công task mới cho shipper */
    ShipperTaskDTO assignTask(ShipperTaskDTO taskDTO);

    /** Cập nhật trạng thái task */
    ShipperTaskDTO updateTaskStatus(Long id, String taskStatus, String resultNote);

    /** Hoàn thành task */
    ShipperTaskDTO completeTask(Long id, String resultNote);

    /** Đánh dấu task thất bại */
    ShipperTaskDTO failTask(Long id, String resultNote);

    // ==================== TÌM KIẾM ====================

    /** Tìm task theo ID */
    Optional<ShipperTaskDTO> findById(Long id);

    /** Lấy task mới nhất của vận đơn theo loại task */
    Optional<ShipperTaskDTO> findLatestByRequestIdAndTaskType(Long requestId, String taskType);

    /** Kiểm tra vận đơn đã có task với loại và trạng thái cụ thể chưa */
    boolean existsByRequestIdAndTaskTypeAndStatus(Long requestId, String taskType, String taskStatus);

    // ==================== DANH SÁCH ====================

    /** Lấy tất cả tasks */
    Page<ShipperTaskDTO> findAll(Pageable pageable);

    /** Lấy tasks của shipper theo trạng thái */
    Page<ShipperTaskDTO> findAllByShipperIdAndStatus(Long shipperId, String taskStatus, Pageable pageable);

    /** Lấy tất cả tasks của shipper */
    Page<ShipperTaskDTO> findAllByShipperId(Long shipperId, Pageable pageable);

    /** Lấy tasks của vận đơn */
    List<ShipperTaskDTO> findAllByRequestId(Long requestId);

    /** Lấy tasks theo trạng thái */
    Page<ShipperTaskDTO> findAllByTaskStatus(String taskStatus, Pageable pageable);

    // ==================== THỐNG KÊ ====================

    /** Đếm tasks của shipper theo trạng thái */
    Long countByShipperIdAndStatus(Long shipperId, String taskStatus);
}
