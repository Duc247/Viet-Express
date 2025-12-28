package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.TripDTO;
import vn.DucBackend.DTO.TripItemDTO;
import vn.DucBackend.Entities.Trip;
import vn.DucBackend.Entities.Trip.TripStatus;
import vn.DucBackend.Entities.TripItem;
import vn.DucBackend.Entities.TripItem.TripItemStatus;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.TripService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của TripService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripItemRepository tripItemRepository;
    private final VehicleRepository vehicleRepository;
    private final ShipperRepository shipperRepository;
    private final WarehouseRepository warehouseRepository;
    private final CustomerRequestRepository customerRequestRepository;
    private final TrackingCodeRepository trackingCodeRepository;

    // ==================== CONVERTER ====================

    private TripDTO toDTO(Trip trip) {
        return TripDTO.builder()
                .id(trip.getId())
                .vehicleId(trip.getVehicle() != null ? trip.getVehicle().getId() : null)
                .vehicleLicensePlate(trip.getVehicle() != null ? trip.getVehicle().getLicensePlate() : null)
                .driverShipperId(trip.getDriverShipper() != null ? trip.getDriverShipper().getId() : null)
                .driverShipperName(trip.getDriverShipper() != null && trip.getDriverShipper().getUser() != null
                        ? trip.getDriverShipper().getUser().getFullName()
                        : null)
                .fromWarehouseId(trip.getFromWarehouse() != null ? trip.getFromWarehouse().getId() : null)
                .fromWarehouseName(trip.getFromWarehouse() != null ? trip.getFromWarehouse().getWarehouseName() : null)
                .toWarehouseId(trip.getToWarehouse() != null ? trip.getToWarehouse().getId() : null)
                .toWarehouseName(trip.getToWarehouse() != null ? trip.getToWarehouse().getWarehouseName() : null)
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .status(trip.getStatus() != null ? trip.getStatus().name() : null)
                .build();
    }

    private TripItemDTO toItemDTO(TripItem item) {
        TripItemDTO dto = TripItemDTO.builder()
                .id(item.getId())
                .tripId(item.getTrip() != null ? item.getTrip().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .status(item.getStatus() != null ? item.getStatus().name() : null)
                .build();

        // Get tracking code
        if (item.getRequest() != null) {
            trackingCodeRepository.findByRequest_Id(item.getRequest().getId())
                    .ifPresent(tc -> dto.setTrackingCode(tc.getCode()));
        }

        return dto;
    }

    private Trip toEntity(TripDTO dto) {
        Trip trip = new Trip();

        if (dto.getVehicleId() != null) {
            vehicleRepository.findById(dto.getVehicleId()).ifPresent(trip::setVehicle);
        }
        if (dto.getDriverShipperId() != null) {
            shipperRepository.findById(dto.getDriverShipperId()).ifPresent(trip::setDriverShipper);
        }
        if (dto.getFromWarehouseId() != null) {
            warehouseRepository.findById(dto.getFromWarehouseId()).ifPresent(trip::setFromWarehouse);
        }
        if (dto.getToWarehouseId() != null) {
            warehouseRepository.findById(dto.getToWarehouseId()).ifPresent(trip::setToWarehouse);
        }

        trip.setStatus(TripStatus.READY);
        return trip;
    }

    // ==================== TRIP ====================

    @Override
    public TripDTO createTrip(TripDTO tripDTO) {
        Trip trip = toEntity(tripDTO);
        trip = tripRepository.save(trip);
        return toDTO(trip);
    }

    @Override
    public TripDTO updateTrip(Long id, TripDTO tripDTO) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + id));

        if (tripDTO.getVehicleId() != null) {
            vehicleRepository.findById(tripDTO.getVehicleId()).ifPresent(trip::setVehicle);
        }
        if (tripDTO.getDriverShipperId() != null) {
            shipperRepository.findById(tripDTO.getDriverShipperId()).ifPresent(trip::setDriverShipper);
        }

        trip = tripRepository.save(trip);
        return toDTO(trip);
    }

    @Override
    public TripDTO startTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + id));
        trip.setStatus(TripStatus.DELIVERING);
        trip.setStartTime(LocalDateTime.now());
        trip = tripRepository.save(trip);
        return toDTO(trip);
    }

    @Override
    public TripDTO completeTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + id));
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(LocalDateTime.now());
        trip = tripRepository.save(trip);
        return toDTO(trip);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TripDTO> findById(Long id) {
        return tripRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> findAll(Pageable pageable) {
        return tripRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> findAllByStatus(String status, Pageable pageable) {
        TripStatus tripStatus = TripStatus.valueOf(status);
        return tripRepository.findAllByStatus(tripStatus, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> findAllByRoute(Long fromWarehouseId, Long toWarehouseId, Pageable pageable) {
        return tripRepository.findAllByFromWarehouse_IdAndToWarehouse_Id(fromWarehouseId, toWarehouseId, pageable)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> findAllByVehicleIdAndStatus(Long vehicleId, String status) {
        TripStatus tripStatus = TripStatus.valueOf(status);
        return tripRepository.findAllByVehicle_IdAndStatus(vehicleId, tripStatus)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> findAllByDriverId(Long driverShipperId, Pageable pageable) {
        return tripRepository.findAllByDriverShipper_Id(driverShipperId, pageable).map(this::toDTO);
    }

    // ==================== TRIP ITEM ====================

    @Override
    public TripItemDTO addItemToTrip(Long tripId, Long requestId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + tripId));

        TripItem item = new TripItem();
        item.setTrip(trip);
        customerRequestRepository.findById(requestId).ifPresent(item::setRequest);
        item.setStatus(TripItemStatus.LOADED);

        item = tripItemRepository.save(item);
        return toItemDTO(item);
    }

    @Override
    public void removeItemFromTrip(Long tripId, Long requestId) {
        tripItemRepository.findAllByTrip_Id(tripId).stream()
                .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(requestId))
                .findFirst()
                .ifPresent(item -> tripItemRepository.deleteById(item.getId()));
    }

    @Override
    public TripItemDTO updateItemStatus(Long tripItemId, String status) {
        TripItem item = tripItemRepository.findById(tripItemId)
                .orElseThrow(() -> new RuntimeException("TripItem not found: " + tripItemId));
        item.setStatus(TripItemStatus.valueOf(status));
        item = tripItemRepository.save(item);
        return toItemDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripItemDTO> findAllItemsByTripId(Long tripId) {
        return tripItemRepository.findAllByTrip_Id(tripId)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripItemDTO> findAllItemsByRequestId(Long requestId) {
        return tripItemRepository.findAllByRequest_Id(requestId)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsItemInTrip(Long tripId, Long requestId) {
        return tripItemRepository.existsByTrip_IdAndRequest_Id(tripId, requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countItemsByTripId(Long tripId) {
        return tripItemRepository.countByTrip_Id(tripId);
    }
}
