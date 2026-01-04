package vn.DucBackend.Services;

import vn.DucBackend.DTO.RoleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Role (Vai trò)
 * 
 * Repository sử dụng: RoleRepository
 * Controller sử dụng: AdminPersonnelController
 */
public interface RoleService {

    /** Repository: roleRepository.findAll() */
    List<RoleDTO> findAllRoles();

    /** Repository: roleRepository.findById() */
    Optional<RoleDTO> findRoleById(Long id);

    /** Repository: roleRepository.findByRoleName() */
    Optional<RoleDTO> findByRoleName(String roleName);

    /** Repository: roleRepository.save() */
    RoleDTO createRole(RoleDTO dto);

    /** Repository: roleRepository.findById(), roleRepository.save() */
    RoleDTO updateRole(Long id, RoleDTO dto);

    /** Repository: roleRepository.deleteById() */
    void deleteRole(Long id);

    /** Repository: roleRepository.findById(), roleRepository.save() */
    void toggleRoleStatus(Long id);
}
