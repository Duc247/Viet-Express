package vn.DucBackend.Services;

import vn.DucBackend.DTO.TripDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripService {

    List<TripDTO> findAllTrips();

    Optional<TripDTO> findTripById(Long id);

    List<TripDTO> findTripsByShipperId(Long shipperId);

    List<TripDTO> findTripsByVehicleId(Long vehicleId);

    List<TripDTO> findTripsByStatus(String status);

    List<TripDTO> findTripsByType(String tripType);

    List<TripDTO> findActiveTrips();

    List<TripDTO> findActiveTripsByShipper(Long shipperId);

    List<TripDTO> findTripsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long countTripsByStatus(String status);

    TripDTO createTrip(TripDTO dto);

    TripDTO updateTrip(Long id, TripDTO dto);

    TripDTO updateTripStatus(Long id, String status);

    TripDTO startTrip(Long id);

    TripDTO completeTrip(Long id);

    TripDTO cancelTrip(Long id);

    TripDTO assignShipperToTrip(Long tripId, Long shipperId);

    TripDTO assignVehicleToTrip(Long tripId, Long vehicleId);

    void deleteTrip(Long id);
}
