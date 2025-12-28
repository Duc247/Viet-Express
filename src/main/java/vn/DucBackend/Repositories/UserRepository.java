package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Role;
import vn.DucBackend.Entities.User;

/**
 * Repository cho User entity - Quản lý tài khoản người dùng hệ thống
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ==================== ĐĂNG NHẬP / SPRING SECURITY ====================
    
    /** Tìm user theo username - dùng cho đăng nhập */
    Optional<User> findByUsername(String username);
    
    /** Tìm user theo email - dùng cho đăng nhập / Spring Security */
    Optional<User> findByEmail(String email);
    
    /** Kiểm tra username đã tồn tại - dùng khi đăng ký */
    Boolean existsByUsername(String username);
    
    /** Kiểm tra email đã tồn tại - dùng khi đăng ký */
    Boolean existsByEmail(String email);
    
    // ==================== TÌM KIẾM THEO THÔNG TIN CÁ NHÂN ====================
    
    /** Tìm user theo số điện thoại */
    Optional<User> findByPhone(String phone);
    
    /** Kiểm tra số điện thoại đã tồn tại */
    Boolean existsByPhone(String phone);
    
    /** Tìm user theo họ tên (chứa keyword) */
    List<User> findAllByFullNameContainingIgnoreCase(String keyword);
    
    Page<User> findAllByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // ==================== LỌC THEO TRẠNG THÁI / ROLE ====================
    
    /** Lấy danh sách user theo trạng thái (true = active, false = inactive) */
    List<User> findAllByStatus(Boolean status);
    
    Page<User> findAllByStatus(Boolean status, Pageable pageable);
    
    /** Lấy danh sách user theo role */
    List<User> findAllByRole(Role role);
    
    Page<User> findAllByRole(Role role, Pageable pageable);
    
    /** Lấy danh sách user theo tên role */
    List<User> findAllByRole_RoleName(String roleName);
    
    Page<User> findAllByRole_RoleName(String roleName, Pageable pageable);
    
    /** Đếm số user theo role */
    Long countByRole_RoleName(String roleName);
    
    /** Đếm số user theo trạng thái */
    Long countByStatus(Boolean status);
}