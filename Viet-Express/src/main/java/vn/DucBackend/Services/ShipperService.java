package vn.DucBackend.Services;

import vn.DucBackend.DTO.ShipperDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Shipper (Tài xế giao hàng)
 * 
 * Repository sử dụng: ShipperRepository, UserRepository, LocationRepository,
 * TripRepository
 * Controller sử dụng: AdminPersonnelController
 */
public interface ShipperService {

    /** Repository: shipperRepository.findAll() */
    List<ShipperDTO> findAllShippers();

    /** Repository: shipperRepository.findByIsActiveTrue() */
    List<ShipperDTO> findActiveShippers();

    /** Repository: shipperRepository.findAvailableShippers() */
    List<ShipperDTO> findAvailableShippers();

    /** Repository: shipperRepository.findById() */
    Optional<ShipperDTO> findShipperById(Long id);

    /** Repository: shipperRepository.findByUserId() */
    Optional<ShipperDTO> findByUserId(Long userId);

    /** Repository: shipperRepository.findByWorkingArea() */
    List<ShipperDTO> findByWorkingArea(String area);

    /** Repository: shipperRepository.findAvailableShippersByArea() */
    List<ShipperDTO> findAvailableShippersByArea(String area);

    /** Repository: shipperRepository.searchByKeyword() */
    List<ShipperDTO> searchShippers(String keyword);

    /**
     * Repository: shipperRepository.save(), userRepository.findById(),
     * locationRepository.findById(), tripRepository.findById()
     */
    ShipperDTO createShipper(ShipperDTO dto);

    /**
     * Repository: shipperRepository.findById(), shipperRepository.save(),
     * locationRepository.findById(), tripRepository.findById()
     */
    ShipperDTO updateShipper(Long id, ShipperDTO dto);

    /** Repository: shipperRepository.findById(), shipperRepository.save() */
    ShipperDTO updateShipperAvailability(Long id, Boolean isAvailable);

    /**
     * Repository: shipperRepository.findById(), shipperRepository.save(),
     * locationRepository.findById()
     */
    ShipperDTO updateShipperLocation(Long id, Long locationId);

    /**
     * Repository: shipperRepository.findById(), shipperRepository.save(),
     * tripRepository.findById()
     */
    ShipperDTO assignTripToShipper(Long shipperId, Long tripId);

    /** Repository: shipperRepository.findById(), shipperRepository.save() */
    ShipperDTO clearCurrentTrip(Long shipperId);

    /** Repository: shipperRepository.deleteById() */
    void deleteShipper(Long id);

    /** Repository: shipperRepository.findById(), shipperRepository.save() */
    void toggleShipperStatus(Long id);
}
