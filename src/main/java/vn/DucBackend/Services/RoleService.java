package vn.DucBackend.Services;

import vn.DucBackend.DTO.RoleDTO;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<RoleDTO> findAllRoles();

    Optional<RoleDTO> findRoleById(Long id);

    Optional<RoleDTO> findByRoleName(String roleName);

    RoleDTO createRole(RoleDTO dto);

    RoleDTO updateRole(Long id, RoleDTO dto);

    void deleteRole(Long id);
}
