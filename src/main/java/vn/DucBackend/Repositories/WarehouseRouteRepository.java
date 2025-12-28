package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Warehouse;
import vn.DucBackend.Entities.WarehouseRoute;

/**
 * Repository cho WarehouseRoute entity - Quản lý tuyến vận chuyển giữa các kho
 * Phục vụ: định tuyến trung chuyển, tính thời gian vận chuyển ước tính
 */
@Repository
public interface WarehouseRouteRepository extends JpaRepository<WarehouseRoute, Long> {

    // ==================== TÌM KIẾM THEO TUYẾN ====================
    
    /** Tìm tuyến theo kho đi và kho đến */
    Optional<WarehouseRoute> findByFromWarehouse_IdAndToWarehouse_Id(Long fromWarehouseId, Long toWarehouseId);
    
    /** Tìm tuyến theo entity kho đi và kho đến */
    Optional<WarehouseRoute> findByFromWarehouseAndToWarehouse(Warehouse fromWarehouse, Warehouse toWarehouse);
    
    /** Kiểm tra tuyến đã tồn tại */
    Boolean existsByFromWarehouse_IdAndToWarehouse_Id(Long fromWarehouseId, Long toWarehouseId);
    
    // ==================== LẤY DANH SÁCH TUYẾN THEO KHO ====================
    
    /** Lấy tất cả tuyến xuất phát từ một kho */
    List<WarehouseRoute> findAllByFromWarehouse_Id(Long fromWarehouseId);
    
    Page<WarehouseRoute> findAllByFromWarehouse_Id(Long fromWarehouseId, Pageable pageable);
    
    /** Lấy tất cả tuyến đến một kho */
    List<WarehouseRoute> findAllByToWarehouse_Id(Long toWarehouseId);
    
    Page<WarehouseRoute> findAllByToWarehouse_Id(Long toWarehouseId, Pageable pageable);
    
    /** Lấy tuyến từ entity kho */
    List<WarehouseRoute> findAllByFromWarehouse(Warehouse fromWarehouse);
    
    List<WarehouseRoute> findAllByToWarehouse(Warehouse toWarehouse);
    
    // ==================== LỌC THEO TRẠNG THÁI ====================
    
    /** Lấy danh sách tuyến theo trạng thái (active/inactive) */
    List<WarehouseRoute> findAllByStatus(Boolean status);
    
    Page<WarehouseRoute> findAllByStatus(Boolean status, Pageable pageable);
    
    /** Lấy tuyến active từ một kho */
    List<WarehouseRoute> findAllByFromWarehouse_IdAndStatus(Long fromWarehouseId, Boolean status);
    
    /** Đếm số tuyến theo trạng thái */
    Long countByStatus(Boolean status);
}