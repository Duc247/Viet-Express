package vn.DucBackend.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.SystemLog;

/**
 * Repository cho SystemLog entity - Quản lý log hệ thống (Audit Trail)
 * Phục vụ: audit hành động user, theo dõi thay đổi entity, truy vết lỗi
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    // ==================== LỌC THEO NGƯỜI THỰC HIỆN ====================

    /** Lấy log theo user ID - kiểm tra hành động của một user */
    Page<SystemLog> findAllByUser_Id(Long userId, Pageable pageable);

    List<SystemLog> findAllByUser_Id(Long userId);

    /** Đếm số log của user */
    Long countByUser_Id(Long userId);

    // ==================== LỌC THEO ĐỐI TƯỢNG BỊ TÁC ĐỘNG ====================

    /** Lấy log theo loại object và ID - xem lịch sử thay đổi của một entity */
    Page<SystemLog> findAllByObjectTypeAndObjectId(String objectType, Long objectId, Pageable pageable);

    List<SystemLog> findAllByObjectTypeAndObjectId(String objectType, Long objectId);

    /** Lấy log theo loại object */
    Page<SystemLog> findAllByObjectType(String objectType, Pageable pageable);

    List<SystemLog> findAllByObjectType(String objectType);

    /** Đếm log theo loại object */
    Long countByObjectType(String objectType);

    // ==================== LỌC THEO HÀNH ĐỘNG ====================

    /** Lấy log theo hành động (CREATE, UPDATE, DELETE, etc.) */
    Page<SystemLog> findAllByAction(String action, Pageable pageable);

    List<SystemLog> findAllByAction(String action);

    /** Đếm log theo hành động */
    Long countByAction(String action);

    // ==================== LỌC THEO THỜI GIAN ====================

    /** Lấy log trong khoảng thời gian */
    Page<SystemLog> findAllByLogTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<SystemLog> findAllByLogTimeBetween(LocalDateTime from, LocalDateTime to);

    /** Lấy log của user trong khoảng thời gian */
    Page<SystemLog> findAllByUser_IdAndLogTimeBetween(Long userId, LocalDateTime from, LocalDateTime to,
            Pageable pageable);

    List<SystemLog> findAllByUser_IdAndLogTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);

    /** Lấy log theo hành động và thời gian */
    Page<SystemLog> findAllByActionAndLogTimeBetween(String action, LocalDateTime from, LocalDateTime to,
            Pageable pageable);

    /** Đếm log trong khoảng thời gian */
    Long countByLogTimeBetween(LocalDateTime from, LocalDateTime to);

    // ==================== KẾT HỢP ĐIỀU KIỆN ====================

    /** Lấy log của user theo hành động */
    Page<SystemLog> findAllByUser_IdAndAction(Long userId, String action, Pageable pageable);

    /** Lấy log theo object type và hành động */
    Page<SystemLog> findAllByObjectTypeAndAction(String objectType, String action, Pageable pageable);
}
