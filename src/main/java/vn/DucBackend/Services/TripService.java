package vn.DucBackend.Services;

import vn.DucBackend.DTO.TripDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Trip (Chuyến xe)
 * 
 * Repository sử dụng: TripRepository, ShipperRepository, VehicleRepository,
 * LocationRepository
 * Controller sử dụng: AdminOperationController
 */
public interface TripService {

    /** Repository: tripRepository.findAll() */
    List<TripDTO> findAllTrips();

    /** Repository: tripRepository.findById() */
    Optional<TripDTO> findTripById(Long id);

    /** Repository: tripRepository.findByShipperId() */
    List<TripDTO> findTripsByShipperId(Long shipperId);

    /** Repository: tripRepository.findByVehicleId() */
    List<TripDTO> findTripsByVehicleId(Long vehicleId);

    /** Repository: tripRepository.findByStatus() */
    List<TripDTO> findTripsByStatus(String status);

    /** Repository: tripRepository.findByTripType() */
    List<TripDTO> findTripsByType(String tripType);

    /** Repository: tripRepository.findActiveTrips() */
    List<TripDTO> findActiveTrips();

    /** Repository: tripRepository.findActiveTripsByShipperId() */
    List<TripDTO> findActiveTripsByShipper(Long shipperId);

    /** Repository: tripRepository.findByStartTimeBetween() */
    List<TripDTO> findTripsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /** Repository: tripRepository.countByStatus() */
    Long countTripsByStatus(String status);

    /**
     * Repository: tripRepository.save(), shipperRepository.findById(),
     * vehicleRepository.findById(), locationRepository.findById()
     */
    TripDTO createTrip(TripDTO dto);

    /** Repository: tripRepository.findById(), tripRepository.save() */
    TripDTO updateTrip(Long id, TripDTO dto);

    /** Repository: tripRepository.findById(), tripRepository.save() */
    TripDTO updateTripStatus(Long id, String status);

    /** Repository: tripRepository.findById(), tripRepository.save() */
    TripDTO startTrip(Long id);

    /** Repository: tripRepository.findById(), tripRepository.save() */
    TripDTO completeTrip(Long id);

    /** Repository: tripRepository.findById(), tripRepository.save() */
    TripDTO cancelTrip(Long id);

    /**
     * Repository: tripRepository.findById(), tripRepository.save(),
     * shipperRepository.findById()
     */
    TripDTO assignShipperToTrip(Long tripId, Long shipperId);

    /**
     * Repository: tripRepository.findById(), tripRepository.save(),
     * vehicleRepository.findById()
     */
    TripDTO assignVehicleToTrip(Long tripId, Long vehicleId);

    /** Repository: tripRepository.deleteById() */
    void deleteTrip(Long id);
}
