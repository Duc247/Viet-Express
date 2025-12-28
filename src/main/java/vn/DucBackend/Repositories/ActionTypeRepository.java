package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.ActionType;

/**
 * Repository cho ActionType entity - Quản lý loại hành động trong tracking
 * Phục vụ: ghi log tracking timeline (PICKUP, IN_WAREHOUSE, TRANSIT, DELIVERED, etc.)
 */
@Repository
public interface ActionTypeRepository extends JpaRepository<ActionType, Long> {

    // ==================== TÌM KIẾM THEO MÃ HÀNH ĐỘNG ====================
    
    /** Tìm action type theo actionCode - dùng khi ghi log tracking */
    Optional<ActionType> findByActionCode(String actionCode);
    
    /** Kiểm tra actionCode đã tồn tại */
    Boolean existsByActionCode(String actionCode);
    
    // ==================== TÌM KIẾM THEO TÊN HÀNH ĐỘNG ====================
    
    /** Tìm action type theo actionName */
    Optional<ActionType> findByActionName(String actionName);
    
    /** Tìm action type theo tên chứa keyword */
    List<ActionType> findAllByActionNameContainingIgnoreCase(String keyword);
}