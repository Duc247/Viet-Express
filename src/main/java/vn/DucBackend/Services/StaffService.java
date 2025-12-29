package vn.DucBackend.Services;

import vn.DucBackend.DTO.StaffDTO;

import java.util.List;
import java.util.Optional;

public interface StaffService {

    List<StaffDTO> findAllStaff();

    List<StaffDTO> findActiveStaff();

    Optional<StaffDTO> findStaffById(Long id);

    Optional<StaffDTO> findByUserId(Long userId);

    List<StaffDTO> findByLocationId(Long locationId);

    List<StaffDTO> findActiveStaffByLocation(Long locationId);

    List<StaffDTO> searchStaff(String keyword);

    StaffDTO createStaff(StaffDTO dto);

    StaffDTO updateStaff(Long id, StaffDTO dto);

    StaffDTO updateStaffLocation(Long id, Long locationId);

    void toggleStaffStatus(Long id);

    void deleteStaff(Long id);

    boolean existsByUserId(Long userId);
}
