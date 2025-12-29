package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByIsActiveTrue();

    List<User> findByRoleId(Long roleId);

    List<User> findByRoleRoleName(String roleName);

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role.roleName = :roleName")
    List<User> findActiveUsersByRole(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    List<User> searchByUsername(@Param("keyword") String keyword);

    boolean existsByUsername(String username);
}