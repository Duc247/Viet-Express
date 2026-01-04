package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.TripDTO;
import vn.DucBackend.Entities.Trip;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.TripRepository;
import vn.DucBackend.Repositories.VehicleRepository;
import vn.DucBackend.Services.TripService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final ShipperRepository shipperRepository;
    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<TripDTO> findAllTrips() {
        return tripRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<TripDTO> findTripById(Long id) {
        return tripRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<TripDTO> findTripsByShipperId(Long shipperId) {
        return tripRepository.findByShipperId(shipperId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> findTripsByVehicleId(Long vehicleId) {
        return tripRepository.findByVehicleId(vehicleId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> findTripsByStatus(String status) {
        return tripRepository.findByStatus(Trip.TripStatus.valueOf(status)).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> findTripsByType(String tripType) {
        return tripRepository.findByTripType(Trip.TripType.valueOf(tripType)).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> findActiveTrips() {
        return tripRepository.findActiveTrips().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> findActiveTripsByShipper(Long shipperId) {
        return tripRepository.findActiveTripsByShipper(shipperId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> findCompletedTripsByShipper(Long shipperId) {
        return tripRepository.findCompletedTripsByShipper(shipperId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> findTripsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.findByDateRange(startDate, endDate).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countTripsByStatus(String status) {
        return tripRepository.countByStatus(Trip.TripStatus.valueOf(status));
    }

    @Override
    public TripDTO createTrip(TripDTO dto) {
        Trip trip = new Trip();
        trip.setTripType(Trip.TripType.valueOf(dto.getTripType()));
        trip.setStartLocation(locationRepository.findById(dto.getStartLocationId())
                .orElseThrow(() -> new RuntimeException("Start location not found")));
        trip.setEndLocation(locationRepository.findById(dto.getEndLocationId())
                .orElseThrow(() -> new RuntimeException("End location not found")));
        if (dto.getShipperId() != null) {
            trip.setShipper(shipperRepository.findById(dto.getShipperId()).orElse(null));
        }
        if (dto.getVehicleId() != null) {
            trip.setVehicle(vehicleRepository.findById(dto.getVehicleId()).orElse(null));
        }
        trip.setCodAmount(dto.getCodAmount());
        trip.setNote(dto.getNote());
        trip.setStatus(Trip.TripStatus.CREATED);
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public TripDTO updateTrip(Long id, TripDTO dto) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));

        if (dto.getTripType() != null) {
            trip.setTripType(Trip.TripType.valueOf(dto.getTripType()));
        }
        if (dto.getStartLocationId() != null) {
            trip.setStartLocation(locationRepository.findById(dto.getStartLocationId())
                    .orElseThrow(() -> new RuntimeException("Start location not found")));
        }
        if (dto.getEndLocationId() != null) {
            trip.setEndLocation(locationRepository.findById(dto.getEndLocationId())
                    .orElseThrow(() -> new RuntimeException("End location not found")));
        }

        // allow unassign
        trip.setShipper(dto.getShipperId() != null ? shipperRepository.findById(dto.getShipperId()).orElse(null) : null);
        trip.setVehicle(dto.getVehicleId() != null ? vehicleRepository.findById(dto.getVehicleId()).orElse(null) : null);

        if (dto.getStatus() != null) {
            trip.setStatus(Trip.TripStatus.valueOf(dto.getStatus()));
        }
        if (dto.getCodAmount() != null) {
            trip.setCodAmount(dto.getCodAmount());
        }
        if (dto.getNote() != null) {
            trip.setNote(dto.getNote());
        }
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public TripDTO updateTripStatus(Long id, String status) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(Trip.TripStatus.valueOf(status));
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public TripDTO startTrip(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(Trip.TripStatus.IN_PROGRESS);
        trip.setStartedAt(LocalDateTime.now());
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public TripDTO completeTrip(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(Trip.TripStatus.COMPLETED);
        trip.setEndedAt(LocalDateTime.now());
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public TripDTO cancelTrip(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(Trip.TripStatus.CANCELLED);
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public TripDTO assignShipperToTrip(Long tripId, Long shipperId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setShipper(
                shipperRepository.findById(shipperId).orElseThrow(() -> new RuntimeException("Shipper not found")));
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public TripDTO assignVehicleToTrip(Long tripId, Long vehicleId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setVehicle(
                vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("Vehicle not found")));
        return toDTO(tripRepository.save(trip));
    }

    @Override
    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    private TripDTO toDTO(Trip trip) {
        TripDTO dto = new TripDTO();
        dto.setId(trip.getId());
        if (trip.getShipper() != null) {
            dto.setShipperId(trip.getShipper().getId());
            dto.setShipperName(trip.getShipper().getFullName());
        }
        if (trip.getVehicle() != null) {
            dto.setVehicleId(trip.getVehicle().getId());
            dto.setVehicleLicensePlate(trip.getVehicle().getLicensePlate());
        }
        if (trip.getTripType() != null) {
            dto.setTripType(trip.getTripType().name());
        }
        if (trip.getStartLocation() != null) {
            dto.setStartLocationId(trip.getStartLocation().getId());
            dto.setStartLocationName(trip.getStartLocation().getName());
        }
        if (trip.getEndLocation() != null) {
            dto.setEndLocationId(trip.getEndLocation().getId());
            dto.setEndLocationName(trip.getEndLocation().getName());
        }
        if (trip.getStatus() != null) {
            dto.setStatus(trip.getStatus().name());
        }
        dto.setStartedAt(trip.getStartedAt());
        dto.setEndedAt(trip.getEndedAt());
        dto.setCodAmount(trip.getCodAmount());
        dto.setNote(trip.getNote());
        dto.setCreatedAt(trip.getCreatedAt());
        dto.setUpdatedAt(trip.getUpdatedAt());
        return dto;
    }

    // ==========================================
    // Methods cho Manager Controllers
    // ==========================================

    @Override
    public java.util.List<Trip> getAllTripEntities() {
        return tripRepository.findAll();
    }

    @Override
    public Trip getTripEntityById(Long id) {
        return tripRepository.findById(id).orElse(null);
    }

    @Override
    public java.util.List<Trip> findTripsByRequestIdEntities(Long requestId) {
        return tripRepository.findTripsByRequestId(requestId);
    }

    @Override
    public Trip saveTripEntity(Trip trip) {
        return tripRepository.save(trip);
    }

    // ==========================================
    // Methods cho Customer Controllers
    // ==========================================

    @Override
    public Long countTripsByRequestId(Long requestId) {
        return tripRepository.countTripsByRequestId(requestId);
    }

    @Override
    public Long countCompletedTripsByRequestId(Long requestId) {
        return tripRepository.countCompletedTripsByRequestId(requestId);
    }

    @Override
    public Long countInProgressTripsByRequestId(Long requestId) {
        return tripRepository.countInProgressTripsByRequestId(requestId);
    }

    @Override
    public Long countCreatedTripsByRequestId(Long requestId) {
        return tripRepository.countCreatedTripsByRequestId(requestId);
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Trip> searchTripsByRequestIdAndKeyword(Long requestId, String keyword) {
        return tripRepository.searchTripsByRequestIdAndKeyword(requestId, keyword);
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Trip> findTripsByRequestIdAndStatusEntities(Long requestId, String status) {
        vn.DucBackend.Entities.Trip.TripStatus tripStatus = vn.DucBackend.Entities.Trip.TripStatus.valueOf(status);
        return tripRepository.findTripsByRequestIdAndStatus(requestId, tripStatus);
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Trip> findTripsByRequestIdAndTypeEntities(Long requestId, String type) {
        vn.DucBackend.Entities.Trip.TripType tripType = vn.DucBackend.Entities.Trip.TripType.valueOf(type);
        return tripRepository.findTripsByRequestIdAndType(requestId, tripType);
    }
}
