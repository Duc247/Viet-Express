package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.ShipperDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface cho Shipper - Quản lý nhân viên giao hàng
 */
public interface ShipperService {

    // ==================== TẠO / CẬP NHẬT ====================

    /** Tạo shipper profile mới */
    ShipperDTO createShipper(ShipperDTO shipperDTO);

    /** Cập nhật thông tin shipper */
    ShipperDTO updateShipper(Long id, ShipperDTO shipperDTO);

    /** Cập nhật trạng thái shipper (AVAILABLE, ON_TRIP, OFF_DUTY) */
    ShipperDTO updateStatus(Long id, String status);

    // ==================== TÌM KIẾM ====================

    /** Tìm shipper theo ID */
    Optional<ShipperDTO> findById(Long id);

    /** Tìm shipper theo user ID */
    Optional<ShipperDTO> findByUserId(Long userId);

    /** Kiểm tra user đã có shipper profile chưa */
    boolean existsByUserId(Long userId);

    // ==================== DANH SÁCH ====================

    /** Lấy tất cả shippers */
    Page<ShipperDTO> findAll(Pageable pageable);

    /** Lấy shippers theo trạng thái */
    Page<ShipperDTO> findAllByStatus(String status, Pageable pageable);

    /** Lấy shippers theo kho */
    Page<ShipperDTO> findAllByWarehouseId(Long warehouseId, Pageable pageable);

    /** Lấy shippers available trong kho - dùng để phân công */
    List<ShipperDTO> findAvailableByWarehouseId(Long warehouseId);

    // ==================== THỐNG KÊ ====================

    /** Đếm shipper theo trạng thái */
    Long countByStatus(String status);

    /** Đếm shipper trong kho */
    Long countByWarehouseId(Long warehouseId);
}
