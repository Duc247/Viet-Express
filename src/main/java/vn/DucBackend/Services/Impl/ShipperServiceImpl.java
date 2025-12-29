package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ShipperDTO;
import vn.DucBackend.Entities.Shipper;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.TripRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.ShipperService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipperServiceImpl implements ShipperService {
	
	@Autowired
    private final ShipperRepository shipperRepository;
	@Autowired
    private final UserRepository userRepository;
	@Autowired
    private final LocationRepository locationRepository;
	@Autowired
    private final TripRepository tripRepository;

    @Override
    public List<ShipperDTO> findAllShippers() {
        return shipperRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ShipperDTO> findActiveShippers() {
        return shipperRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ShipperDTO> findAvailableShippers() {
        return shipperRepository.findAvailableShippers().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<ShipperDTO> findShipperById(Long id) {
        return shipperRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<ShipperDTO> findByUserId(Long userId) {
        return shipperRepository.findByUserId(userId).map(this::toDTO);
    }

    @Override
    public List<ShipperDTO> findByWorkingArea(String area) {
        return shipperRepository.findByWorkingArea(area).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ShipperDTO> findAvailableShippersByArea(String area) {
        return shipperRepository.findAvailableShippersByArea(area).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipperDTO> searchShippers(String keyword) {
        return shipperRepository.searchByKeyword(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ShipperDTO createShipper(ShipperDTO dto) {
        Shipper shipper = new Shipper();
        shipper.setUser(userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        shipper.setFullName(dto.getFullName());
        shipper.setPhone(dto.getPhone());
        shipper.setWorkingArea(dto.getWorkingArea());
        shipper.setIsActive(true);
        return toDTO(shipperRepository.save(shipper));
    }

    @Override
    public ShipperDTO updateShipper(Long id, ShipperDTO dto) {
        Shipper shipper = shipperRepository.findById(id).orElseThrow(() -> new RuntimeException("Shipper not found"));
        if (dto.getFullName() != null)
            shipper.setFullName(dto.getFullName());
        if (dto.getPhone() != null)
            shipper.setPhone(dto.getPhone());
        if (dto.getWorkingArea() != null)
            shipper.setWorkingArea(dto.getWorkingArea());
        return toDTO(shipperRepository.save(shipper));
    }

    @Override
    public ShipperDTO updateShipperAvailability(Long id, Boolean isAvailable) {
        Shipper shipper = shipperRepository.findById(id).orElseThrow(() -> new RuntimeException("Shipper not found"));
        shipper.setIsAvailable(isAvailable);
        return toDTO(shipperRepository.save(shipper));
    }

    @Override
    public ShipperDTO updateShipperLocation(Long id, Long locationId) {
        Shipper shipper = shipperRepository.findById(id).orElseThrow(() -> new RuntimeException("Shipper not found"));
        shipper.setCurrentLocation(locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found")));
        return toDTO(shipperRepository.save(shipper));
    }

    @Override
    public ShipperDTO assignTripToShipper(Long shipperId, Long tripId) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));
        shipper.setCurrentTrip(
                tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found")));
        shipper.setIsAvailable(false);
        return toDTO(shipperRepository.save(shipper));
    }

    @Override
    public ShipperDTO clearCurrentTrip(Long shipperId) {
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));
        shipper.setCurrentTrip(null);
        shipper.setIsAvailable(true);
        return toDTO(shipperRepository.save(shipper));
    }

    @Override
    public void deleteShipper(Long id) {
        shipperRepository.deleteById(id);
    }

    @Override
    public void toggleShipperStatus(Long id) {
        Shipper shipper = shipperRepository.findById(id).orElseThrow(() -> new RuntimeException("Shipper not found"));
        shipper.setIsActive(!shipper.getIsActive());
        shipperRepository.save(shipper);
    }

    private ShipperDTO toDTO(Shipper shipper) {
        ShipperDTO dto = new ShipperDTO();
        dto.setId(shipper.getId());
        if (shipper.getUser() != null) {
            dto.setUserId(shipper.getUser().getId());
            dto.setUsername(shipper.getUser().getUsername());
        }
        dto.setFullName(shipper.getFullName());
        dto.setPhone(shipper.getPhone());
        dto.setWorkingArea(shipper.getWorkingArea());
        dto.setIsAvailable(shipper.getIsAvailable());
        if (shipper.getCurrentLocation() != null) {
            dto.setCurrentLocationId(shipper.getCurrentLocation().getId());
            dto.setCurrentLocationName(shipper.getCurrentLocation().getName());
        }
        if (shipper.getCurrentTrip() != null) {
            dto.setCurrentTripId(shipper.getCurrentTrip().getId());
        }
        dto.setIsActive(shipper.getIsActive());
        dto.setCreatedAt(shipper.getCreatedAt());
        dto.setUpdatedAt(shipper.getUpdatedAt());
        return dto;
    }
}
