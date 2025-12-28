package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.TrackingCode;

/**
 * Repository cho TrackingCode entity - Quản lý mã tracking vận đơn
 * Phục vụ: tra cứu vận đơn, sinh mã tracking khi tạo đơn
 */
@Repository
public interface TrackingCodeRepository extends JpaRepository<TrackingCode, Long> {

    // ==================== TÌM KIẾM THEO MÃ TRACKING ====================
    
    /** Tìm tracking code theo mã - dùng cho khách hàng tra cứu */
    Optional<TrackingCode> findByCode(String code);
    
    /** Kiểm tra mã tracking đã tồn tại - dùng khi sinh mã mới */
    Boolean existsByCode(String code);
    
    // ==================== TÌM KIẾM THEO VẬN ĐƠN ====================
    
    /** Tìm tracking code theo request ID */
    Optional<TrackingCode> findByRequest_Id(Long requestId);
    
    /** Kiểm tra vận đơn đã có tracking code chưa */
    Boolean existsByRequest_Id(Long requestId);
    
    // ==================== LỌC THEO TRẠNG THÁI ====================
    
    /** Lấy danh sách tracking code theo trạng thái */
    List<TrackingCode> findAllByStatus(Boolean status);
    
    /** Đếm số tracking code theo trạng thái */
    Long countByStatus(Boolean status);
}