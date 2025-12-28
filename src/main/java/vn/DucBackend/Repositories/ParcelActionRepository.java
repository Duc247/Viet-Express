package vn.DucBackend.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.ActionType;
import vn.DucBackend.Entities.ParcelAction;

/**
 * Repository cho ParcelAction entity - Quản lý log tracking timeline
 * Phục vụ: hiển thị lịch sử di chuyển của vận đơn, audit hành động shipper/staff
 */
@Repository
public interface ParcelActionRepository extends JpaRepository<ParcelAction, Long> {

    // ==================== TRACKING TIMELINE THEO VẬN ĐƠN ====================
    
    /** Lấy toàn bộ lịch sử tracking của vận đơn - sắp xếp theo thời gian tăng dần */
    List<ParcelAction> findAllByRequest_IdOrderByActionTimeAsc(Long requestId);
    
    /** Lấy toàn bộ lịch sử tracking của vận đơn - sắp xếp theo thời gian giảm dần */
    List<ParcelAction> findAllByRequest_IdOrderByActionTimeDesc(Long requestId);
    
    /** Lấy action mới nhất của vận đơn - dùng để hiển thị trạng thái hiện tại */
    Optional<ParcelAction> findTop1ByRequest_IdOrderByActionTimeDesc(Long requestId);
    
    /** Lấy action đầu tiên của vận đơn */
    Optional<ParcelAction> findTop1ByRequest_IdOrderByActionTimeAsc(Long requestId);
    
    // ==================== LỌC THEO NGƯỜI THỰC HIỆN ====================
    
    /** Lấy danh sách action theo người thực hiện - dùng cho audit */
    List<ParcelAction> findAllByActorUser_Id(Long actorUserId);
    
    Page<ParcelAction> findAllByActorUser_Id(Long actorUserId, Pageable pageable);
    
    /** Lấy action của user trong khoảng thời gian */
    List<ParcelAction> findAllByActorUser_IdAndActionTimeBetween(Long actorUserId, LocalDateTime from, LocalDateTime to);
    
    // ==================== LỌC THEO KHO ====================
    
    /** Lấy action liên quan đến một kho (từ hoặc đến) */
    List<ParcelAction> findAllByFromWarehouse_IdOrToWarehouse_Id(Long fromWarehouseId, Long toWarehouseId);
    
    Page<ParcelAction> findAllByFromWarehouse_IdOrToWarehouse_Id(Long fromWarehouseId, Long toWarehouseId, Pageable pageable);
    
    /** Lấy action xuất từ một kho */
    List<ParcelAction> findAllByFromWarehouse_Id(Long warehouseId);
    
    /** Lấy action nhập vào một kho */
    List<ParcelAction> findAllByToWarehouse_Id(Long warehouseId);
    
    // ==================== LỌC THEO LOẠI HÀNH ĐỘNG ====================
    
    /** Lấy action của vận đơn theo loại hành động */
    List<ParcelAction> findAllByRequest_IdAndActionType(Long requestId, ActionType actionType);
    
    /** Lấy action của vận đơn theo mã loại hành động */
    List<ParcelAction> findAllByRequest_IdAndActionType_ActionCode(Long requestId, String actionCode);
    
    /** Lấy tất cả action theo loại */
    Page<ParcelAction> findAllByActionType_ActionCode(String actionCode, Pageable pageable);
    
    // ==================== LỌC THEO THỜI GIAN ====================
    
    /** Lấy action trong khoảng thời gian */
    Page<ParcelAction> findAllByActionTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    
    /** Đếm action trong khoảng thời gian */
    Long countByActionTimeBetween(LocalDateTime from, LocalDateTime to);
}