package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.UserDTO;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Repositories.RoleRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation của UserService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // ==================== CONVERTER ====================

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .build();
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(dto.getPasswordHash());
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        if (dto.getRoleId() != null) {
            roleRepository.findById(dto.getRoleId()).ifPresent(user::setRole);
        }
        return user;
    }

    // ==================== TẠO / CẬP NHẬT ====================

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        user = userRepository.save(user);
        return toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        if (userDTO.getFullName() != null)
            user.setFullName(userDTO.getFullName());
        if (userDTO.getPhone() != null)
            user.setPhone(userDTO.getPhone());
        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());
        if (userDTO.getPasswordHash() != null)
            user.setPasswordHash(userDTO.getPasswordHash());
        if (userDTO.getRoleId() != null) {
            roleRepository.findById(userDTO.getRoleId()).ifPresent(user::setRole);
        }
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);
        return toDTO(user);
    }

    @Override
    public UserDTO changeStatus(Long id, Boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        return toDTO(user);
    }

    // ==================== TÌM KIẾM ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ==================== DANH SÁCH ====================

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllByRoleName(String roleName, Pageable pageable) {
        return userRepository.findAllByRole_RoleName(roleName, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllByStatus(Boolean status, Pageable pageable) {
        return userRepository.findAllByStatus(status, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchByFullName(String keyword, Pageable pageable) {
        return userRepository.findAllByFullNameContainingIgnoreCase(keyword, pageable).map(this::toDTO);
    }
}
