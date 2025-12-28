package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Role;

/**
 * Repository cho Role entity - Quản lý vai trò người dùng
 * Phục vụ phân quyền trong hệ thống logistics
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // ==================== TÌM KIẾM THEO TÊN ROLE ====================
    
    /** Tìm role theo roleName - dùng khi gán quyền cho user */
    Optional<Role> findByRoleName(String roleName);
    
    /** Kiểm tra roleName đã tồn tại - dùng khi tạo role mới */
    Boolean existsByRoleName(String roleName);
    
    // ==================== LỌC THEO TRẠNG THÁI ====================
    
    /** Lấy danh sách role theo trạng thái */
    List<Role> findAllByStatus(Boolean status);
}