package vn.DucBackend.Services;

import vn.DucBackend.DTO.ShipperDTO;

import java.util.List;
import java.util.Optional;

public interface ShipperService {

    List<ShipperDTO> findAllShippers();

    List<ShipperDTO> findActiveShippers();

    List<ShipperDTO> findAvailableShippers();

    Optional<ShipperDTO> findShipperById(Long id);

    Optional<ShipperDTO> findByUserId(Long userId);

    List<ShipperDTO> findByWorkingArea(String area);

    List<ShipperDTO> findAvailableShippersByArea(String area);

    List<ShipperDTO> searchShippers(String keyword);

    ShipperDTO createShipper(ShipperDTO dto);

    ShipperDTO updateShipper(Long id, ShipperDTO dto);

    ShipperDTO updateShipperAvailability(Long id, Boolean isAvailable);

    ShipperDTO updateShipperLocation(Long id, Long locationId);

    ShipperDTO assignTripToShipper(Long shipperId, Long tripId);

    ShipperDTO clearCurrentTrip(Long shipperId);

    void deleteShipper(Long id);

    void toggleShipperStatus(Long id);
}
