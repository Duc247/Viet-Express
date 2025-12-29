package vn.DucBackend.Services.Impl;

import java.util.List;
import java.util.Optional;

import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Services.StaffService;

public class StaffServiceImpl implements StaffService {

	

	@Override
	public Optional<Staff> findByUserId(Long userId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<Staff> findByIsActiveTrue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Staff> findByLocationId(Long locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Staff> findActiveStaffByLocation(Long locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Staff> searchByKeyword(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsByUserId(Long userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Staff> getAllStaffs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Staff> getStaffById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Staff createStaff(Staff staff) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Staff updateStaff(Long id, Staff staff) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteStaff(Long id) {
		// TODO Auto-generated method stub
		
	}

}
