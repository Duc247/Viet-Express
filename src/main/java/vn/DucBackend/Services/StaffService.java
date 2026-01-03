package vn.DucBackend.Services;

import java.util.List;
import java.util.Optional;

import vn.DucBackend.DTO.StaffDTO;

/**
 * Service interface quản lý Staff (Nhân viên)
 * 
 * Repository sử dụng: StaffRepository, UserRepository, LocationRepository
 * Controller sử dụng: AdminPersonnelController
 */
public interface StaffService {

    // ==========================================
    // Methods cho AdminPersonnelController
    // ==========================================

    /** Repository: staffRepository.findAll() */
    List<StaffDTO> findAllStaff();

    /** Repository: staffRepository.findById() */
    Optional<StaffDTO> findStaffById(Long id);

    /** Repository: staffRepository.searchByKeyword() */
    List<StaffDTO> searchStaff(String keyword);

    /**
     * Repository: staffRepository.save(), userRepository.findById(),
     * locationRepository.findById()
     */
    StaffDTO createStaff(StaffDTO dto);

    /**
     * Repository: staffRepository.findById(), staffRepository.save(),
     * userRepository.findById(), locationRepository.findById()
     */
    StaffDTO updateStaff(Long id, StaffDTO dto);

    /** Repository: staffRepository.deleteById() */
    void deleteStaff(Long id);

    /** Repository: staffRepository.findById(), staffRepository.save() */
    void toggleStaffStatus(Long id);

    // ==========================================
    // Methods khác
    // ==========================================

    /** Repository: staffRepository.findByUserId() */
    Optional<StaffDTO> findByUserId(Long userId);

    /** Repository: staffRepository.findByIsActiveTrue() */
    List<StaffDTO> findByIsActiveTrue();

    /** Repository: staffRepository.findByLocationId() */
    List<StaffDTO> findByLocationId(Long locationId);

    /** Repository: staffRepository.findActiveStaffByLocation() */
    List<StaffDTO> findActiveStaffByLocation(Long locationId);

    /** Repository: staffRepository.existsByUserId() */
    boolean existsByUserId(Long userId);
}
