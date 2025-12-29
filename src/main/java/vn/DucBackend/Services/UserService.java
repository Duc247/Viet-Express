package vn.DucBackend.Services;

import vn.DucBackend.DTO.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> findAllUsers();

    List<UserDTO> findActiveUsers();

    Optional<UserDTO> findUserById(Long id);

    Optional<UserDTO> findByUsername(String username);

    List<UserDTO> findUsersByRole(String roleName);

    List<UserDTO> findActiveUsersByRole(String roleName);

    List<UserDTO> searchUsers(String keyword);

    UserDTO createUser(UserDTO dto);

    UserDTO updateUser(Long id, UserDTO dto);

    UserDTO updatePassword(Long id, String newPassword);

    void updateLastLogin(Long id);

    void toggleUserStatus(Long id);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean validatePassword(Long id, String password);
}
