package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.ServiceType;

/**
 * Repository cho ServiceType entity - Quản lý loại dịch vụ vận chuyển
 * Phục vụ: tạo vận đơn, tính phí dịch vụ
 */
@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    // ==================== TÌM KIẾM THEO MÃ / TÊN DỊCH VỤ ====================
    
    /** Tìm service theo mã dịch vụ (serviceCode) */
    Optional<ServiceType> findByServiceCode(String serviceCode);
    
    /** Kiểm tra mã dịch vụ đã tồn tại */
    Boolean existsByServiceCode(String serviceCode);
    
    /** Tìm service theo tên dịch vụ */
    Optional<ServiceType> findByServiceName(String serviceName);
    
    /** Tìm service theo tên chứa keyword */
    List<ServiceType> findAllByServiceNameContainingIgnoreCase(String keyword);
    
    // ==================== LỌC THEO TRẠNG THÁI ====================
    
    /** Lấy danh sách service theo trạng thái (active/inactive) */
    List<ServiceType> findAllByStatus(Boolean status);
    
    Page<ServiceType> findAllByStatus(Boolean status, Pageable pageable);
    
    /** Đếm số service theo trạng thái */
    Long countByStatus(Boolean status);
}