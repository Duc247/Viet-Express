package vn.DucBackend.Services;

import java.util.List;
import java.util.Optional;

import vn.DucBackend.DTO.StaffDTO;

public interface StaffService {

	// Methods for AdminPersonnelController
	List<StaffDTO> findAllStaff();

	Optional<StaffDTO> findStaffById(Long id);

	List<StaffDTO> searchStaff(String keyword);

	StaffDTO createStaff(StaffDTO dto);

	StaffDTO updateStaff(Long id, StaffDTO dto);

	void deleteStaff(Long id);

	void toggleStaffStatus(Long id);

	// Other methods
	Optional<StaffDTO> findByUserId(Long userId);

	List<StaffDTO> findByIsActiveTrue();

	List<StaffDTO> findByLocationId(Long locationId);

	List<StaffDTO> findActiveStaffByLocation(Long locationId);

	boolean existsByUserId(Long userId);
}
