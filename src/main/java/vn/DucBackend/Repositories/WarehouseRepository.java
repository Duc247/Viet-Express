package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Warehouse;

/**
 * Repository cho Warehouse entity - Quản lý kho hàng
 * Phục vụ: quản lý kho, trung chuyển, theo dõi hàng hóa trong kho
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // ==================== TÌM KIẾM THEO TÊN KHO ====================
    
    /** Tìm warehouse theo tên kho */
    Optional<Warehouse> findByWarehouseName(String warehouseName);
    
    /** Kiểm tra tên kho đã tồn tại */
    Boolean existsByWarehouseName(String warehouseName);
    
    /** Tìm warehouse theo tên chứa keyword */
    List<Warehouse> findAllByWarehouseNameContainingIgnoreCase(String keyword);
    
    Page<Warehouse> findAllByWarehouseNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // ==================== LỌC THEO ĐỊA ĐIỂM ====================
    
    /** Lấy danh sách warehouse theo tỉnh/thành phố */
    List<Warehouse> findAllByProvince(String province);
    
    Page<Warehouse> findAllByProvince(String province, Pageable pageable);
    
    /** Lấy danh sách warehouse theo quận/huyện */
    List<Warehouse> findAllByDistrict(String district);
    
    /** Lấy danh sách warehouse theo tỉnh và quận */
    List<Warehouse> findAllByProvinceAndDistrict(String province, String district);
    
    // ==================== LỌC THEO LOẠI KHO / TRẠNG THÁI ====================
    
    /** Lấy danh sách warehouse theo loại kho (HUB, DISTRIBUTION_CENTER, PICKUP_POINT) */
    List<Warehouse> findAllByWarehouseType(Warehouse.WarehouseType warehouseType);
    
    Page<Warehouse> findAllByWarehouseType(Warehouse.WarehouseType warehouseType, Pageable pageable);
    
    /** Lấy danh sách warehouse theo trạng thái */
    List<Warehouse> findAllByStatus(Boolean status);
    
    Page<Warehouse> findAllByStatus(Boolean status, Pageable pageable);
    
    /** Lấy warehouse active theo loại */
    List<Warehouse> findAllByStatusAndWarehouseType(Boolean status, Warehouse.WarehouseType warehouseType);
    
    /** Đếm số kho theo trạng thái */
    Long countByStatus(Boolean status);
    
    /** Đếm số kho theo loại */
    Long countByWarehouseType(Warehouse.WarehouseType warehouseType);
}