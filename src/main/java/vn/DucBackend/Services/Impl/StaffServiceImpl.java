package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.StaffDTO;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.StaffRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.StaffService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

	private final StaffRepository staffRepository;
	private final UserRepository userRepository;
	private final LocationRepository locationRepository;

	@Override
	public List<StaffDTO> findAllStaff() {
		return staffRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	@Override
	public List<StaffDTO> findActiveStaff() {
		return staffRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
	}

	@Override
	public Optional<StaffDTO> findStaffById(Long id) {
		return staffRepository.findById(id).map(this::toDTO);
	}

<<<<<<< Updated upstream
=======
	/**
	 * Tìm kiếm Staff theo keyword (fullName, phone, email)
	 * Sử dụng bởi: AdminPersonnelController.staffSearch() - GET /admin/staff/search
	 */
	@Override
	public List<StaffDTO> searchStaff(String keyword) {
		return staffRepository.searchByKeyword(keyword).stream().map(this::toDTO).collect(Collectors.toList());
	}

	/**
	 * Tạo Staff mới từ User có sẵn
	 * Sử dụng bởi: AdminPersonnelController.staffCreate() - POST
	 * /admin/staff/create
	 */
	@Override
	public StaffDTO createStaff(StaffDTO dto) {
		Staff staff = new Staff();
		if (dto.getUserId() != null) {
			staff.setUser(userRepository.findById(dto.getUserId()).orElse(null));
		}
		if (dto.getLocationId() != null) {
			Location location = locationRepository.findById(dto.getLocationId())
					.orElseThrow(() -> new RuntimeException("Location not found"));
			// Validation: Staff phải gắn location có type là WAREHOUSE
			if (location.getLocationType() != Location.LocationType.WAREHOUSE) {
				throw new RuntimeException("Staff phải được gắn với location có type là WAREHOUSE!");
			}
			staff.setLocation(location);
		} else {
			throw new RuntimeException("Staff bắt buộc phải gắn với một location có type là WAREHOUSE!");
		}
		staff.setFullName(dto.getFullName());
		staff.setPhone(dto.getPhone());
		staff.setEmail(dto.getEmail());
		staff.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
		staff.setCreatedAt(dto.getCreatedAt());
		staff.setUpdatedAt(dto.getUpdatedAt());
		return toDTO(staffRepository.save(staff));
	}

	/**
	 * Cập nhật thông tin Staff
	 * Sử dụng bởi: AdminPersonnelController.staffUpdate() - POST
	 * /admin/staff/edit/{id}
	 */
	@Override
	public StaffDTO updateStaff(Long id, StaffDTO dto) {
		Staff staff = staffRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Staff not found"));
		if (dto.getFullName() != null)
			staff.setFullName(dto.getFullName());
		if (dto.getPhone() != null)
			staff.setPhone(dto.getPhone());
		if (dto.getEmail() != null)
			staff.setEmail(dto.getEmail());
		if (dto.getLocationId() != null) {
			Location location = locationRepository.findById(dto.getLocationId())
					.orElseThrow(() -> new RuntimeException("Location not found"));
			// Validation: Staff phải gắn location có type là WAREHOUSE
			if (location.getLocationType() != Location.LocationType.WAREHOUSE) {
				throw new RuntimeException("Staff phải được gắn với location có type là WAREHOUSE!");
			}
			staff.setLocation(location);
		} else if (staff.getLocation() == null) {
			throw new RuntimeException("Staff bắt buộc phải gắn với một location có type là WAREHOUSE!");
		}
		if (dto.getIsActive() != null)
			staff.setIsActive(dto.getIsActive());
		else
			staff.setIsActive(false);
		staff.setUpdatedAt(LocalDateTime.now());
		return toDTO(staffRepository.save(staff));
	}

	/**
	 * Xóa Staff theo ID
	 * Sử dụng bởi: AdminPersonnelController.staffDelete() - POST
	 * /admin/staff/delete/{id}
	 */
	@Override
	public void deleteStaff(Long id) {
		staffRepository.deleteById(id);
	}

	/**
	 * Bật/tắt trạng thái active của Staff
	 * Sử dụng bởi: AdminPersonnelController.staffToggleStatus() - POST
	 * /admin/staff/toggle-status/{id}
	 */
	@Override
	public void toggleStaffStatus(Long id) {
		Staff staff = staffRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Staff not found"));
		staff.setIsActive(!staff.getIsActive());
		staffRepository.save(staff);
	}

>>>>>>> Stashed changes
	@Override
	public Optional<StaffDTO> findByUserId(Long userId) {
		return staffRepository.findByUserId(userId).map(this::toDTO);
	}

	@Override
	public List<StaffDTO> findByLocationId(Long locationId) {
		return staffRepository.findByLocationId(locationId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	@Override
	public List<StaffDTO> findActiveStaffByLocation(Long locationId) {
		return staffRepository.findActiveStaffByLocation(locationId).stream().map(this::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<StaffDTO> searchStaff(String keyword) {
		return staffRepository.searchByKeyword(keyword).stream().map(this::toDTO).collect(Collectors.toList());
	}

	@Override
	public StaffDTO createStaff(StaffDTO dto) {
		Staff staff = new Staff();
		staff.setUser(userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found")));
		staff.setFullName(dto.getFullName());
		staff.setPhone(dto.getPhone());
		staff.setEmail(dto.getEmail());
		if (dto.getLocationId() != null) {
			staff.setLocation(locationRepository.findById(dto.getLocationId())
					.orElseThrow(() -> new RuntimeException("Location not found")));
		}
		staff.setIsActive(true);
		return toDTO(staffRepository.save(staff));
	}

	@Override
	public StaffDTO updateStaff(Long id, StaffDTO dto) {
		Staff staff = staffRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Staff not found"));
		if (dto.getFullName() != null) {
			staff.setFullName(dto.getFullName());
		}
		if (dto.getPhone() != null) {
			staff.setPhone(dto.getPhone());
		}
		if (dto.getEmail() != null) {
			staff.setEmail(dto.getEmail());
		}
		if (dto.getLocationId() != null) {
			staff.setLocation(locationRepository.findById(dto.getLocationId())
					.orElseThrow(() -> new RuntimeException("Location not found")));
		}
		return toDTO(staffRepository.save(staff));
	}

	@Override
	public StaffDTO updateStaffLocation(Long id, Long locationId) {
		Staff staff = staffRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Staff not found"));
		staff.setLocation(locationRepository.findById(locationId)
				.orElseThrow(() -> new RuntimeException("Location not found")));
		return toDTO(staffRepository.save(staff));
	}

	@Override
	public void toggleStaffStatus(Long id) {
		Staff staff = staffRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Staff not found"));
		staff.setIsActive(!staff.getIsActive());
		staffRepository.save(staff);
	}

	@Override
	public void deleteStaff(Long id) {
		staffRepository.deleteById(id);
	}

	@Override
	public boolean existsByUserId(Long userId) {
		return staffRepository.existsByUserId(userId);
	}

	private StaffDTO toDTO(Staff staff) {
		StaffDTO dto = new StaffDTO();
		dto.setId(staff.getId());
		if (staff.getUser() != null) {
			dto.setUserId(staff.getUser().getId());
			dto.setUsername(staff.getUser().getUsername());
		}
		dto.setFullName(staff.getFullName());
		dto.setPhone(staff.getPhone());
		dto.setEmail(staff.getEmail());
		if (staff.getLocation() != null) {
			dto.setLocationId(staff.getLocation().getId());
			dto.setLocationName(staff.getLocation().getName());
		}
		dto.setIsActive(staff.getIsActive());
		dto.setCreatedAt(staff.getCreatedAt());
		dto.setUpdatedAt(staff.getUpdatedAt());
		return dto;
	}
}
