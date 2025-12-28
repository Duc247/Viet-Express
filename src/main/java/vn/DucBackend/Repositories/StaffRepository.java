package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Staff;

/**
 * Repository cho Staff entity - Quản lý nhân viên kho
 * Phục vụ: quản lý staff, xác thực đăng nhập staff, phân công xử lý vận đơn tại
 * kho
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    // ==================== TÌM KIẾM THEO USER LIÊN KẾT ====================

    /** Tìm staff theo user ID - dùng khi staff đăng nhập để lấy profile */
    Optional<Staff> findByUser_Id(Long userId);

    /** Kiểm tra user đã có profile staff chưa */
    Boolean existsByUser_Id(Long userId);

    // ==================== LỌC THEO KHO LÀM VIỆC ====================

    /** Lấy danh sách staff thuộc một kho */
    List<Staff> findAllByWarehouse_Id(Long warehouseId);

    Page<Staff> findAllByWarehouse_Id(Long warehouseId, Pageable pageable);

    /** Đếm số staff trong kho */
    Long countByWarehouse_Id(Long warehouseId);

    // ==================== LỌC THEO TRẠNG THÁI ====================

    /** Lấy danh sách staff theo trạng thái (active/inactive) */
    List<Staff> findAllByStatus(Boolean status);

    Page<Staff> findAllByStatus(Boolean status, Pageable pageable);

    /** Lấy staff active trong một kho */
    List<Staff> findAllByWarehouse_IdAndStatus(Long warehouseId, Boolean status);

    Page<Staff> findAllByWarehouse_IdAndStatus(Long warehouseId, Boolean status, Pageable pageable);

    /** Đếm số staff theo trạng thái */
    Long countByStatus(Boolean status);

    /** Đếm số staff active trong kho */
    Long countByWarehouse_IdAndStatus(Long warehouseId, Boolean status);

    // ==================== LỌC THEO VỊ TRÍ ====================

    /** Lấy danh sách staff theo vị trí công việc */
    List<Staff> findAllByStaffPosition(String staffPosition);

    Page<Staff> findAllByStaffPosition(String staffPosition, Pageable pageable);

    /** Lấy staff theo vị trí trong một kho cụ thể */
    List<Staff> findAllByWarehouse_IdAndStaffPosition(Long warehouseId, String staffPosition);
}
