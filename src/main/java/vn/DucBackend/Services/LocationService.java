package vn.DucBackend.Services;

import vn.DucBackend.DTO.LocationDTO;
import vn.DucBackend.DTO.RouteDTO;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    // Location operations
    List<LocationDTO> findAllLocations();

    List<LocationDTO> findActiveLocations();

    List<LocationDTO> findLocationsByType(String locationType);

    Optional<LocationDTO> findLocationById(Long id);

    Optional<LocationDTO> findByWarehouseCode(String warehouseCode);

    List<LocationDTO> findAllWarehouses();

    List<LocationDTO> searchLocations(String keyword);

    LocationDTO createLocation(LocationDTO dto);

    LocationDTO updateLocation(Long id, LocationDTO dto);

    void deleteLocation(Long id);

    void toggleLocationStatus(Long id);

    // Route operations
    List<RouteDTO> findAllRoutes();

    List<RouteDTO> findActiveRoutes();

    Optional<RouteDTO> findRouteById(Long id);

    Optional<RouteDTO> findRouteByLocations(Long fromLocationId, Long toLocationId);

    List<RouteDTO> findRoutesByLocationId(Long locationId);

    RouteDTO createRoute(RouteDTO dto);

    RouteDTO updateRoute(Long id, RouteDTO dto);

    void deleteRoute(Long id);

    void toggleRouteStatus(Long id);
}
