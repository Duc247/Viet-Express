package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.UserDTO;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Repositories.RoleRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findActiveUsers() {
        return userRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDTO);
    }

    @Override
    public List<UserDTO> findUsersByRole(String roleName) {
        return userRepository.findByRoleRoleName(roleName).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findActiveUsersByRole(String roleName) {
        return userRepository.findActiveUsersByRole(roleName).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        return userRepository.searchByUsername(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // Should be encoded in production
        user.setRole(roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found")));
        user.setIsActive(true);
        return toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (dto.getUsername() != null)
            user.setUsername(dto.getUsername());
        if (dto.getRoleId() != null) {
            user.setRole(
                    roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found")));
        }
        return toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(newPassword); // Should be encoded in production
        return toDTO(userRepository.save(user));
    }

    @Override
    public void updateLastLogin(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean validatePassword(Long id, String password) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return user.getPassword().equals(password); // Should use encoder in production
        }
        return false;
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoleId(user.getRole().getId());
        dto.setIsActive(user.getIsActive());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
