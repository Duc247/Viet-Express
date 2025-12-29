package vn.DucBackend.Services;

import java.util.List;
import java.util.Optional;

import vn.DucBackend.Entities.Staff;

public interface StaffService {
	 Optional<Staff> findByUserId(Long userId);

	    List<Staff> findByIsActiveTrue();

	    List<Staff> findByLocationId(Long locationId);
	    List<Staff> findActiveStaffByLocation(Long locationId);
	    List<Staff> searchByKeyword(String keyword);   
	    boolean existsByUserId(Long userId);
	    List<Staff> getAllStaffs();
	    Optional<Staff> getStaffById(Long id);
	    Staff createStaff(Staff staff);
	    Staff updateStaff(Long id, Staff staff);
	    void deleteStaff(Long id);
	    

}
