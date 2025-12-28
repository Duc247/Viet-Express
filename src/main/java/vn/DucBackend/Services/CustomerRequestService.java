package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.CustomerRequestDTO;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service interface cho CustomerRequest - Quản lý vận đơn (Core service)
 */
public interface CustomerRequestService {

    // ==================== TẠO / CẬP NHẬT VẬN ĐƠN ====================

    /** Tạo vận đơn mới - Customer tạo đơn */
    CustomerRequestDTO createOrder(CustomerRequestDTO requestDTO);

    /** Cập nhật thông tin vận đơn */
    CustomerRequestDTO updateOrder(Long id, CustomerRequestDTO requestDTO);

    /** Cập nhật trạng thái vận đơn */
    CustomerRequestDTO updateStatus(Long id, String status);

    /** Staff xác nhận đơn hàng - chuyển PENDING -> CONFIRMED */
    CustomerRequestDTO confirmOrder(Long id);

    /** Hủy vận đơn */
    CustomerRequestDTO cancelOrder(Long id);

    /** Cập nhật kho hiện tại của vận đơn */
    CustomerRequestDTO updateCurrentWarehouse(Long id, Long warehouseId);

    // ==================== TÌM KIẾM ====================

    /** Tìm vận đơn theo ID */
    Optional<CustomerRequestDTO> findById(Long id);

    /** Tìm vận đơn theo ID và customer ID - xác thực quyền sở hữu */
    Optional<CustomerRequestDTO> findByIdAndCustomerId(Long id, Long customerId);

    /** Tìm vận đơn theo tracking code */
    Optional<CustomerRequestDTO> findByTrackingCode(String trackingCode);

    // ==================== DANH SÁCH ====================

    /** Lấy tất cả vận đơn */
    Page<CustomerRequestDTO> findAll(Pageable pageable);

    /** Lấy vận đơn của customer */
    Page<CustomerRequestDTO> findAllByCustomerId(Long customerId, Pageable pageable);

    /** Lấy vận đơn theo trạng thái */
    Page<CustomerRequestDTO> findAllByStatus(String status, Pageable pageable);

    /** Lấy vận đơn trong kho theo trạng thái */
    Page<CustomerRequestDTO> findAllByWarehouseIdAndStatus(Long warehouseId, String status, Pageable pageable);

    /** Lấy vận đơn theo thời gian tạo */
    Page<CustomerRequestDTO> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // ==================== THỐNG KÊ ====================

    /** Đếm vận đơn theo trạng thái */
    Long countByStatus(String status);

    /** Đếm vận đơn trong kho theo trạng thái */
    Long countByWarehouseIdAndStatus(Long warehouseId, String status);
}
