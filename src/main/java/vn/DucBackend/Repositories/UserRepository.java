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
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    List<User> searchByUsername(@Param("keyword") String keyword);

    // đây là tìm theo email
    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword%")
    List<User> searchByEmail(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.phone LIKE %:keyword%")
    List<User> searchByPhone(@Param("keyword") String keyword);

    // này khi kiểm tra đăng nhập xem trùng tên không , hoặc khi tạo tài khoản
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // Tới phần update user - cần @Modifying cho UPDATE/DELETE queries
    @Modifying
    @Query("UPDATE User u SET u.role = :role WHERE u.username = :username")
    int updateRole(@Param("username") String username, @Param("role") Role role);

    @Modifying
    @Query("UPDATE User u SET u.phone = :phone WHERE u.username = :username")
    int updatePhone(@Param("username") String username, @Param("phone") String phone);

    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.username = :username")
    int updateEmail(@Param("username") String username, @Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.avatar = :avatar WHERE u.username = :username")
    int updateAvatar(@Param("username") String username, @Param("avatar") String avatar);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    int updatePassword(@Param("username") String username, @Param("password") String password);

    // INSERT: JPQL không hỗ trợ INSERT, sử dụng method save() từ JpaRepository
    // Ví dụ: userRepository.save(new User(...));

    // DELETE user - sử dụng 'DELETE' thay vì 'remove'
    @Modifying
    @Query("DELETE FROM User u WHERE u.username = :username")
    int deleteByUsernameCustom(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM User u WHERE u.email = :email")
    int deleteByEmailCustom(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.phone = :phone")
    int deleteByPhoneCustom(@Param("phone") String phone);

}