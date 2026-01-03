package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    List<Role> findByIsActiveTrue();

    @Query("SELECT r FROM Role r WHERE r.roleName IN :names")
    List<Role> findByRoleNameIn(@Param("names") List<String> names);

    boolean existsByRoleName(String roleName);
}