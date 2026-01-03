package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.User;

import java.util.List;
import java.util.Optional;
import vn.DucBackend.Entities.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByIsActiveTrue();

    List<User> findByRoleId(Long roleId);

    List<User> findByRoleRoleName(String roleName);

    // đây là thể hiện nút tìm theo role kế bên danh sách user
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role.roleName = :roleName")
    List<User> findActiveUsersByRole(@Param("roleName") String roleName);

    // đây là thể hiện nút tìm kiếm
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% or u.email LIKE %:keyword% or u.phone LIKE %:keyword%")
    List<User> searchByUser(@Param("keyword") String keyword);

    // đây là tìm theo email
    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword%")
    List<User> searchByEmail(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.phone LIKE %:keyword%")
    List<User> searchByPhone(@Param("keyword") String keyword);

    // này khi kiểm tra đăng nhập xem trùng tên không , hoặc khi tạo tài khoản
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}