package vn.DucBackend.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Trip;
import vn.DucBackend.Entities.Trip.TripStatus;

/**
 * Repository cho Trip entity - Quản lý chuyến trung chuyển
 * Phục vụ: tạo trip, theo dõi tiến độ trung chuyển kho-kho, quản lý xe và tài
 * xế
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // ==================== LỌC THEO TRẠNG THÁI TRIP ====================

    /** Lấy danh sách trip theo trạng thái (READY, DELIVERING, COMPLETED) */
    Page<Trip> findAllByStatus(TripStatus status, Pageable pageable);

    List<Trip> findAllByStatus(TripStatus status);

    /** Đếm số trip theo trạng thái */
    Long countByStatus(TripStatus status);

    // ==================== LỌC THEO TUYẾN KHO ====================

    /** Lấy trip theo tuyến kho đi và kho đến */
    Page<Trip> findAllByFromWarehouse_IdAndToWarehouse_Id(Long fromWarehouseId, Long toWarehouseId, Pageable pageable);

    List<Trip> findAllByFromWarehouse_IdAndToWarehouse_Id(Long fromWarehouseId, Long toWarehouseId);

    /** Lấy trip xuất phát từ một kho */
    Page<Trip> findAllByFromWarehouse_Id(Long fromWarehouseId, Pageable pageable);

    List<Trip> findAllByFromWarehouse_Id(Long fromWarehouseId);

    /** Lấy trip đến một kho */
    Page<Trip> findAllByToWarehouse_Id(Long toWarehouseId, Pageable pageable);

    List<Trip> findAllByToWarehouse_Id(Long toWarehouseId);

    // ==================== LỌC THEO XE ====================

    /** Lấy trip theo xe và trạng thái */
    List<Trip> findAllByVehicle_IdAndStatus(Long vehicleId, TripStatus status);

    /** Lấy tất cả trip của một xe */
    Page<Trip> findAllByVehicle_Id(Long vehicleId, Pageable pageable);

    List<Trip> findAllByVehicle_Id(Long vehicleId);

    // ==================== LỌC THEO TÀI XẾ (DRIVER SHIPPER) ====================

    /** Lấy trip của một driver shipper */
    Page<Trip> findAllByDriverShipper_Id(Long driverShipperId, Pageable pageable);

    List<Trip> findAllByDriverShipper_Id(Long driverShipperId);

    /** Lấy trip của driver shipper theo trạng thái */
    List<Trip> findAllByDriverShipper_IdAndStatus(Long driverShipperId, TripStatus status);

    // ==================== LỌC THEO THỜI GIAN ====================

    /** Lấy trip theo thời gian khởi hành */
    Page<Trip> findAllByStartTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Trip> findAllByStartTimeBetween(LocalDateTime from, LocalDateTime to);

    /** Lấy trip theo thời gian kết thúc */
    Page<Trip> findAllByEndTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    /** Đếm trip theo thời gian khởi hành */
    Long countByStartTimeBetween(LocalDateTime from, LocalDateTime to);
}
