package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.UserDTO;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ User
 * Được sử dụng bởi: AdminPersonnelController (CRUD User, tạo
 * Customer/Shipper/Staff tự động theo role)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final ShipperRepository shipperRepository;
    private final StaffRepository staffRepository;
    private final LocationRepository locationRepository;

    /**
     * Lấy danh sách tất cả User
     * Sử dụng bởi: AdminPersonnelController.userList() - GET /admin/user
     */
    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findActiveUsers() {
        return userRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Tìm User theo ID
     * Sử dụng bởi: AdminPersonnelController.userEditForm() - GET
     * /admin/user/edit/{id}
     */
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

    /**
     * Tìm kiếm User theo keyword (username, email, phone)
     * Sử dụng bởi: AdminPersonnelController.userSearch() - GET /admin/user/search
     */
    @Override
    public List<UserDTO> searchUsers(String keyword) {
        return userRepository.searchByUser(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Tạo User mới và tự động tạo Customer/Shipper/Staff theo role
     * Sử dụng bởi: AdminPersonnelController.userCreate() - POST /admin/user/create
     * - Nếu role = CUSTOMER → tạo Customer
     * - Nếu role = SHIPPER → tạo Shipper
     * - Nếu role = STAFF/MANAGER → tạo Staff
     */
    @Override
    public UserDTO createUser(UserDTO dto) {
        // Tạo User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFullName(dto.getFullName());
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Tự động tạo Customer/Shipper/Staff dựa trên role
        String roleName = role.getRoleName().toUpperCase();
        // Hỗ trợ cả tên tiếng Anh và tiếng Việt
        if (roleName.contains("CUSTOMER") || roleName.contains("KHÁCH") || roleName.contains("KHACH")) {
            Customer customer = new Customer();
            customer.setUser(savedUser);
            customer.setName(dto.getFullName());
            customer.setFullName(dto.getFullName());
            customer.setEmail(dto.getEmail());
            customer.setPhone(dto.getPhone());
            customer.setAddress(dto.getAddress());
            customer.setCompanyName(dto.getCompanyName());
            customer.setGender(dto.getGender());
            customerRepository.save(customer);
        } else if (roleName.contains("SHIPPER") || roleName.contains("TÀI XẾ") || roleName.contains("TAI XE")
                || roleName.contains("GIAO")) {
            Shipper shipper = new Shipper();
            shipper.setUser(savedUser);
            shipper.setFullName(dto.getFullName());
            shipper.setPhone(dto.getPhone());
            if (dto.getWorkingArea() != null) {
                shipper.setWorkingArea(dto.getWorkingArea());
            }
            if (dto.getCurrentLocationId() != null) {
                shipper.setCurrentLocation(locationRepository.findById(dto.getCurrentLocationId()).orElse(null));
            }
            shipper.setIsActive(true);
            shipper.setIsAvailable(true);
            shipperRepository.save(shipper);
        } else if (roleName.contains("STAFF") || roleName.contains("NHÂN VIÊN") || roleName.contains("NHAN VIEN")
                || roleName.contains("MANAGER") || roleName.contains("QUẢN LÝ")) {
            Staff staff = new Staff();
            staff.setUser(savedUser);
            staff.setFullName(dto.getFullName());
            staff.setPhone(dto.getPhone());
            staff.setEmail(dto.getEmail());
            if (dto.getLocationId() != null) {
                staff.setLocation(locationRepository.findById(dto.getLocationId()).orElse(null));
            }
            staff.setIsActive(true);
            staffRepository.save(staff);
        }
        return toDTO(savedUser);
    }

    /**
     * Cập nhật thông tin User
     * Sử dụng bởi: AdminPersonnelController.userUpdate() - POST
     * /admin/user/edit/{id}
     */
    @Override
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (dto.getUsername() != null)
            user.setUsername(dto.getUsername());
        if (dto.getRoleId() != null) {
            user.setRole(
                    roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found")));
        }
        user.setUpdatedAt(LocalDateTime.now());
        return toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(newPassword); // Should be encoded in production
        user.setUpdatedAt(LocalDateTime.now());
        return toDTO(userRepository.save(user));
    }

    @Override
    public void updateLastLogin(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Bật/tắt trạng thái active của User
     * Sử dụng bởi: AdminPersonnelController.userToggleStatus() - POST
     * /admin/user/toggle-status/{id}
     */
    @Override
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Xóa User theo ID
     * Sử dụng bởi: AdminPersonnelController.userDelete() - POST
     * /admin/user/delete/{id}
     */
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
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setFullName(user.getFullName());
        dto.setRoleId(user.getRole().getId());
        dto.setRoleName(user.getRole().getRoleName());
        dto.setIsActive(user.getIsActive());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
