package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Shipper;
import vn.DucBackend.Entities.Shipper.ShipperStatus;

/**
 * Repository cho Shipper entity - Quản lý nhân viên giao hàng
 * Phục vụ: phân công pickup/delivery/return, theo dõi trạng thái shipper
 */
@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Long> {

    // ==================== TÌM KIẾM THEO USER ====================
    
    /** Tìm shipper theo user ID - dùng khi shipper đăng nhập */
    Optional<Shipper> findByUser_Id(Long userId);
    
    /** Kiểm tra user đã có profile shipper chưa */
    Boolean existsByUser_Id(Long userId);
    
    // ==================== LỌC THEO TRẠNG THÁI SHIPPER ====================
    
    /** Lấy danh sách shipper theo trạng thái (AVAILABLE, ON_TRIP, OFF_DUTY) */
    List<Shipper> findAllByStatus(ShipperStatus status);
    
    Page<Shipper> findAllByStatus(ShipperStatus status, Pageable pageable);
    
    /** Đếm số shipper theo trạng thái */
    Long countByStatus(ShipperStatus status);
    
    // ==================== LỌC THEO KHO LÀM VIỆC ====================
    
    /** Lấy danh sách shipper thuộc một kho */
    List<Shipper> findAllByWarehouse_Id(Long warehouseId);
    
    Page<Shipper> findAllByWarehouse_Id(Long warehouseId, Pageable pageable);
    
    /** Lấy shipper available trong một kho - dùng để phân công task */
    List<Shipper> findAllByWarehouse_IdAndStatus(Long warehouseId, ShipperStatus status);
    
    /** Đếm số shipper trong kho */
    Long countByWarehouse_Id(Long warehouseId);
    
    /** Đếm số shipper available trong kho */
    Long countByWarehouse_IdAndStatus(Long warehouseId, ShipperStatus status);
}