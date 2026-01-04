package vn.DucBackend.Services;

import vn.DucBackend.DTO.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý User
 * 
 * Repository sử dụng: UserRepository, RoleRepository, CustomerRepository,
 * ShipperRepository, StaffRepository, LocationRepository
 * Controller sử dụng: AdminPersonnelController
 */
public interface UserService {

    /** Repository: userRepository.findAll() */
    List<UserDTO> findAllUsers();

    /** Repository: userRepository.findByIsActiveTrue() */
    List<UserDTO> findActiveUsers();

    /** Repository: userRepository.findById() */
    Optional<UserDTO> findUserById(Long id);

    /** Repository: userRepository.findByUsername() */
    Optional<UserDTO> findByUsername(String username);

    /** Repository: userRepository.findByRoleRoleName() */
    List<UserDTO> findUsersByRole(String roleName);

    /** Repository: userRepository.findActiveUsersByRole() */
    List<UserDTO> findActiveUsersByRole(String roleName);

    /** Repository: userRepository.searchByUser() */
    List<UserDTO> searchUsers(String keyword);

    /**
     * Repository: userRepository.save(), roleRepository.findById(),
     * customerRepository/shipperRepository/staffRepository.save()
     */
    UserDTO createUser(UserDTO dto);

    /**
     * Repository: userRepository.findById(), userRepository.save(),
     * roleRepository.findById()
     */
    UserDTO updateUser(Long id, UserDTO dto);

    /** Repository: userRepository.findById(), userRepository.save() */
    UserDTO updatePassword(Long id, String newPassword);

    /** Repository: userRepository.findById(), userRepository.save() */
    void updateLastLogin(Long id);

    /** Repository: userRepository.findById(), userRepository.save() */
    void toggleUserStatus(Long id);

    /** Repository: userRepository.deleteById() */
    void deleteUser(Long id);

    /** Repository: userRepository.existsByUsername() */
    boolean existsByUsername(String username);

    /** Repository: userRepository.findById() */
    boolean validatePassword(Long id, String password);
}
