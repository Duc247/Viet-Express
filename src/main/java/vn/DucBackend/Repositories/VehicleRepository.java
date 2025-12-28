package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Vehicle;
import vn.DucBackend.Entities.Vehicle.VehicleStatus;

/**
 * Repository cho Vehicle entity - Quản lý phương tiện vận chuyển
 * Phục vụ: quản lý xe vận chuyển, phân bổ xe cho trip trung chuyển
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // ==================== TÌM KIẾM THEO BIỂN SỐ XE ====================

    /** Tìm xe theo biển số - dùng khi tra cứu hoặc phân bổ xe */
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    /** Kiểm tra biển số đã tồn tại - dùng khi thêm xe mới */
    Boolean existsByLicensePlate(String licensePlate);

    // ==================== LỌC THEO TRẠNG THÁI XE ====================

    /** Lấy danh sách xe theo trạng thái (AVAILABLE, ON_TRIP, MAINTENANCE) */
    List<Vehicle> findAllByStatus(VehicleStatus status);

    Page<Vehicle> findAllByStatus(VehicleStatus status, Pageable pageable);

    /** Đếm số xe theo trạng thái */
    Long countByStatus(VehicleStatus status);

    // ==================== LỌC THEO LOẠI XE ====================

    /** Lấy danh sách xe theo loại (type field) */
    List<Vehicle> findAllByType(String type);

    Page<Vehicle> findAllByType(String type, Pageable pageable);

    /** Lấy xe available theo loại - dùng khi phân bổ xe cho trip */
    List<Vehicle> findAllByStatusAndType(VehicleStatus status, String type);

    /** Đếm số xe theo loại */
    Long countByType(String type);
}
