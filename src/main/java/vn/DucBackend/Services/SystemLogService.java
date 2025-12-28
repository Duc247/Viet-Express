package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.SystemLogDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface cho SystemLog - Audit trail
 */
public interface SystemLogService {

    // ==================== GHI LOG ====================

    /** Ghi log hành động */
    SystemLogDTO log(Long userId, String action, String objectType, Long objectId);

    // ==================== TRA CỨU ====================

    /** Lấy logs của user */
    Page<SystemLogDTO> findAllByUserId(Long userId, Pageable pageable);

    /** Lấy logs theo loại object và ID - xem lịch sử thay đổi entity */
    Page<SystemLogDTO> findAllByObjectTypeAndObjectId(String objectType, Long objectId, Pageable pageable);

    /** Lấy logs theo loại object */
    Page<SystemLogDTO> findAllByObjectType(String objectType, Pageable pageable);

    /** Lấy logs theo hành động */
    Page<SystemLogDTO> findAllByAction(String action, Pageable pageable);

    /** Lấy logs theo thời gian */
    Page<SystemLogDTO> findAllByLogTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    /** Lấy logs của user trong khoảng thời gian */
    List<SystemLogDTO> findAllByUserIdAndLogTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);

    // ==================== THỐNG KÊ ====================

    /** Đếm logs của user */
    Long countByUserId(Long userId);

    /** Đếm logs theo loại object */
    Long countByObjectType(String objectType);

    /** Đếm logs trong khoảng thời gian */
    Long countByLogTimeBetween(LocalDateTime from, LocalDateTime to);
}
