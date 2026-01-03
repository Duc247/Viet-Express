package vn.DucBackend.Services;

import vn.DucBackend.DTO.LocationDTO;
import vn.DucBackend.DTO.RouteDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Location (Địa điểm) và Route (Tuyến đường)
 * 
 * Repository sử dụng: LocationRepository, RouteRepository
 * Controller sử dụng: AdminResourceController, AdminPersonnelController
 */
public interface LocationService {

    // ==========================================
    // Location operations
    // ==========================================

    /** Repository: locationRepository.findAll() */
    List<LocationDTO> findAllLocations();

    /** Repository: locationRepository.findByIsActiveTrue() */
    List<LocationDTO> findActiveLocations();

    /** Repository: locationRepository.findByLocationType() */
    List<LocationDTO> findLocationsByType(String locationType);

    /** Repository: locationRepository.findById() */
    Optional<LocationDTO> findLocationById(Long id);

    /** Repository: locationRepository.findByWarehouseCode() */
    Optional<LocationDTO> findByWarehouseCode(String warehouseCode);

    /** Repository: locationRepository.findAllWarehouses() */
    List<LocationDTO> findAllWarehouses();

    /** Repository: locationRepository.searchByKeyword() */
    List<LocationDTO> searchLocations(String keyword);

    /** Repository: locationRepository.save() */
    LocationDTO createLocation(LocationDTO dto);

    /** Repository: locationRepository.findById(), locationRepository.save() */
    LocationDTO updateLocation(Long id, LocationDTO dto);

    /** Repository: locationRepository.deleteById() */
    void deleteLocation(Long id);

    /** Repository: locationRepository.findById(), locationRepository.save() */
    void toggleLocationStatus(Long id);

    // ==========================================
    // Route operations
    // ==========================================

    /** Repository: routeRepository.findAll() */
    List<RouteDTO> findAllRoutes();

    /** Repository: routeRepository.findByIsActiveTrue() */
    List<RouteDTO> findActiveRoutes();

    /** Repository: routeRepository.findById() */
    Optional<RouteDTO> findRouteById(Long id);

    /** Repository: routeRepository.findByFromLocationIdAndToLocationId() */
    Optional<RouteDTO> findRouteByLocations(Long fromLocationId, Long toLocationId);

    /** Repository: routeRepository.findRoutesByLocationId() */
    List<RouteDTO> findRoutesByLocationId(Long locationId);

    /** Repository: routeRepository.save(), locationRepository.findById() */
    RouteDTO createRoute(RouteDTO dto);

    /**
     * Repository: routeRepository.findById(), routeRepository.save(),
     * locationRepository.findById()
     */
    RouteDTO updateRoute(Long id, RouteDTO dto);

    /** Repository: routeRepository.deleteById() */
    void deleteRoute(Long id);

    /** Repository: routeRepository.findById(), routeRepository.save() */
    void toggleRouteStatus(Long id);
}
