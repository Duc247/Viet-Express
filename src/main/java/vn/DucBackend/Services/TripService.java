package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.TripDTO;
import vn.DucBackend.DTO.TripItemDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface cho Trip - Quản lý chuyến trung chuyển
 */
public interface TripService {

    // ==================== TRIP ====================

    /** Tạo chuyến mới */
    TripDTO createTrip(TripDTO tripDTO);

    /** Cập nhật thông tin chuyến */
    TripDTO updateTrip(Long id, TripDTO tripDTO);

    /** Bắt đầu chuyến - chuyển READY -> DELIVERING */
    TripDTO startTrip(Long id);

    /** Hoàn thành chuyến - chuyển DELIVERING -> COMPLETED */
    TripDTO completeTrip(Long id);

    /** Tìm chuyến theo ID */
    Optional<TripDTO> findById(Long id);

    /** Lấy tất cả chuyến */
    Page<TripDTO> findAll(Pageable pageable);

    /** Lấy chuyến theo trạng thái */
    Page<TripDTO> findAllByStatus(String status, Pageable pageable);

    /** Lấy chuyến theo tuyến kho */
    Page<TripDTO> findAllByRoute(Long fromWarehouseId, Long toWarehouseId, Pageable pageable);

    /** Lấy chuyến của xe theo trạng thái */
    List<TripDTO> findAllByVehicleIdAndStatus(Long vehicleId, String status);

    /** Lấy chuyến của driver */
    Page<TripDTO> findAllByDriverId(Long driverShipperId, Pageable pageable);

    // ==================== TRIP ITEM ====================

    /** Thêm vận đơn vào chuyến */
    TripItemDTO addItemToTrip(Long tripId, Long requestId);

    /** Xóa vận đơn khỏi chuyến */
    void removeItemFromTrip(Long tripId, Long requestId);

    /** Cập nhật trạng thái item trong chuyến */
    TripItemDTO updateItemStatus(Long tripItemId, String status);

    /** Lấy tất cả items trong chuyến */
    List<TripItemDTO> findAllItemsByTripId(Long tripId);

    /** Lấy lịch sử trung chuyển của vận đơn */
    List<TripItemDTO> findAllItemsByRequestId(Long requestId);

    /** Kiểm tra vận đơn đã có trong chuyến chưa */
    boolean existsItemInTrip(Long tripId, Long requestId);

    /** Đếm số items trong chuyến */
    Long countItemsByTripId(Long tripId);
}
