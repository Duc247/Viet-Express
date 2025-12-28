package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.WarehouseDTO;
import vn.DucBackend.DTO.WarehouseRouteDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface cho Warehouse - Quản lý kho hàng
 */
public interface WarehouseService {

    // ==================== WAREHOUSE ====================

    /** Tạo kho mới */
    WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO);

    /** Cập nhật thông tin kho */
    WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO);

    /** Thay đổi trạng thái kho */
    WarehouseDTO changeStatus(Long id, Boolean status);

    /** Tìm kho theo ID */
    Optional<WarehouseDTO> findById(Long id);

    /** Tìm kho theo tên */
    Optional<WarehouseDTO> findByName(String warehouseName);

    /** Lấy tất cả kho */
    Page<WarehouseDTO> findAll(Pageable pageable);

    /** Lấy kho theo loại */
    Page<WarehouseDTO> findAllByType(String warehouseType, Pageable pageable);

    /** Lấy kho theo trạng thái */
    Page<WarehouseDTO> findAllByStatus(Boolean status, Pageable pageable);

    /** Lấy kho theo tỉnh/thành */
    List<WarehouseDTO> findAllByProvince(String province);

    /** Lấy tất cả kho đang hoạt động */
    List<WarehouseDTO> findAllActive();

    // ==================== WAREHOUSE ROUTE ====================

    /** Tạo tuyến mới */
    WarehouseRouteDTO createRoute(WarehouseRouteDTO routeDTO);

    /** Cập nhật tuyến */
    WarehouseRouteDTO updateRoute(Long id, WarehouseRouteDTO routeDTO);

    /** Tìm tuyến theo kho đi và kho đến */
    Optional<WarehouseRouteDTO> findRouteByFromAndTo(Long fromWarehouseId, Long toWarehouseId);

    /** Kiểm tra tuyến đã tồn tại */
    boolean existsRoute(Long fromWarehouseId, Long toWarehouseId);

    /** Lấy tất cả tuyến từ một kho */
    List<WarehouseRouteDTO> findAllRoutesFromWarehouse(Long fromWarehouseId);

    /** Lấy tất cả tuyến đến một kho */
    List<WarehouseRouteDTO> findAllRoutesToWarehouse(Long toWarehouseId);

    /** Lấy tất cả tuyến đang hoạt động */
    List<WarehouseRouteDTO> findAllActiveRoutes();
}
