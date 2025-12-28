package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.UserDTO;

import java.util.Optional;

/**
 * Service interface cho User - Quản lý tài khoản người dùng
 */
public interface UserService {

    // ==================== TẠO / CẬP NHẬT ====================

    /** Tạo user mới */
    UserDTO createUser(UserDTO userDTO);

    /** Cập nhật thông tin user */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /** Thay đổi trạng thái user (active/inactive) */
    UserDTO changeStatus(Long id, Boolean status);

    // ==================== TÌM KIẾM ====================

    /** Tìm user theo ID */
    Optional<UserDTO> findById(Long id);

    /** Tìm user theo email - dùng cho đăng nhập */
    Optional<UserDTO> findByEmail(String email);

    /** Tìm user theo username */
    Optional<UserDTO> findByUsername(String username);

    /** Kiểm tra email đã tồn tại */
    boolean existsByEmail(String email);

    /** Kiểm tra username đã tồn tại */
    boolean existsByUsername(String username);

    // ==================== DANH SÁCH ====================

    /** Lấy tất cả users */
    Page<UserDTO> findAll(Pageable pageable);

    /** Lấy users theo role */
    Page<UserDTO> findAllByRoleName(String roleName, Pageable pageable);

    /** Lấy users theo trạng thái */
    Page<UserDTO> findAllByStatus(Boolean status, Pageable pageable);

    /** Tìm kiếm users theo tên */
    Page<UserDTO> searchByFullName(String keyword, Pageable pageable);
}
