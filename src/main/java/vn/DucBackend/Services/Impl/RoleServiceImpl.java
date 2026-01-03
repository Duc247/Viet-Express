package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.RoleDTO;
import vn.DucBackend.Entities.Role;
import vn.DucBackend.Repositories.RoleRepository;
import vn.DucBackend.Services.RoleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ Role (Vai trò người dùng)
 * Được sử dụng bởi: AdminPersonnelController, AdminSystemController
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleDTO> findAllRoles() {
        return roleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<RoleDTO> findRoleById(Long id) {
        return roleRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<RoleDTO> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName).map(this::toDTO);
    }

    @Override
    public RoleDTO createRole(RoleDTO dto) {
        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        return toDTO(roleRepository.save(role));
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        if (dto.getRoleName() != null) {
            role.setRoleName(dto.getRoleName());
        }
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }
        return toDTO(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public void toggleRoleStatus(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setIsActive(!role.getIsActive());
        roleRepository.save(role);
    }

    private RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        return dto;
    }
}
