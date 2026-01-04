package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.VehicleDTO;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Entities.Vehicle;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.VehicleRepository;
import vn.DucBackend.Services.VehicleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ Vehicle (Phương tiện)
 * 
 * Admin Controller sử dụng:
 * - AdminResourceController: CRUD Vehicle
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<VehicleDTO> findById(Long id) {
        return vehicleRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<VehicleDTO> findByStatus(String status) {
        Vehicle.VehicleStatus vehicleStatus = Vehicle.VehicleStatus.valueOf(status);
        return vehicleRepository.findByStatus(vehicleStatus).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findAvailable() {
        return vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDTO create(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(Vehicle.VehicleType.valueOf(dto.getVehicleType()));
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setCapacityWeight(dto.getCapacityWeight());
        vehicle.setCapacityVolume(dto.getCapacityVolume());
        vehicle.setStatus(dto.getStatus() != null ? Vehicle.VehicleStatus.valueOf(dto.getStatus())
                : Vehicle.VehicleStatus.AVAILABLE);

        if (dto.getCurrentLocationId() != null) {
            Location location = locationRepository.findById(dto.getCurrentLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            vehicle.setCurrentLocation(location);
        }

        return toDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleDTO update(Long id, VehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (dto.getVehicleType() != null) {
            vehicle.setVehicleType(Vehicle.VehicleType.valueOf(dto.getVehicleType()));
        }
        if (dto.getLicensePlate() != null) {
            vehicle.setLicensePlate(dto.getLicensePlate());
        }
        if (dto.getCapacityWeight() != null) {
            vehicle.setCapacityWeight(dto.getCapacityWeight());
        }
        if (dto.getCapacityVolume() != null) {
            vehicle.setCapacityVolume(dto.getCapacityVolume());
        }
        if (dto.getStatus() != null) {
            vehicle.setStatus(Vehicle.VehicleStatus.valueOf(dto.getStatus()));
        }
        if (dto.getCurrentLocationId() != null) {
            Location location = locationRepository.findById(dto.getCurrentLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            vehicle.setCurrentLocation(location);
        }

        return toDTO(vehicleRepository.save(vehicle));
    }

    @Override
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public void toggleStatus(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.INACTIVE);
        } else {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        }
        vehicleRepository.save(vehicle);
    }

    private VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setVehicleType(vehicle.getVehicleType().name());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCapacityWeight(vehicle.getCapacityWeight());
        dto.setCapacityVolume(vehicle.getCapacityVolume());
        dto.setStatus(vehicle.getStatus() != null ? vehicle.getStatus().name() : null);
        if (vehicle.getCurrentLocation() != null) {
            dto.setCurrentLocationId(vehicle.getCurrentLocation().getId());
            dto.setCurrentLocationName(vehicle.getCurrentLocation().getName());
        }
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        return dto;
    }
}
