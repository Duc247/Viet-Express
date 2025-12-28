package vn.DucBackend.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.TripItem;
import vn.DucBackend.Entities.TripItem.TripItemStatus;

/**
 * Repository cho TripItem entity - Quản lý vận đơn trong chuyến trung chuyển
 * Phục vụ: thêm/xóa vận đơn vào trip, theo dõi trạng thái vận đơn trong trip
 */
@Repository
public interface TripItemRepository extends JpaRepository<TripItem, Long> {

    // ==================== LỌC THEO TRIP ====================

    /** Lấy tất cả item trong một trip */
    List<TripItem> findAllByTrip_Id(Long tripId);

    Page<TripItem> findAllByTrip_Id(Long tripId, Pageable pageable);

    /** Đếm số item trong trip */
    Long countByTrip_Id(Long tripId);

    // ==================== LỌC THEO VẬN ĐƠN ====================

    /** Lấy tất cả trip item của một vận đơn - theo dõi lịch sử trung chuyển */
    List<TripItem> findAllByRequest_Id(Long requestId);

    /** Kiểm tra vận đơn đã có trong trip chưa */
    Boolean existsByTrip_IdAndRequest_Id(Long tripId, Long requestId);

    // ==================== LỌC THEO TRẠNG THÁI ITEM ====================

    /** Lấy item trong trip theo trạng thái (LOADED, IN_TRANSIT, UNLOADED) */
    List<TripItem> findAllByTrip_IdAndStatus(Long tripId, TripItemStatus status);

    /** Đếm item trong trip theo trạng thái */
    Long countByTrip_IdAndStatus(Long tripId, TripItemStatus status);

    /** Lấy tất cả item theo trạng thái */
    Page<TripItem> findAllByStatus(TripItemStatus status, Pageable pageable);

    List<TripItem> findAllByStatus(TripItemStatus status);
}
