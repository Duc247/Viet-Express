package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.LocationDTO;
import vn.DucBackend.DTO.RouteDTO;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Entities.Route;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.RouteRepository;
import vn.DucBackend.Services.LocationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ Location (Địa điểm/Chi nhánh) và Route (Tuyến đường)
 * 
 * Admin Controller sử dụng:
 * - AdminPersonnelController: Lấy danh sách locations cho form (user, shipper,
 * staff)
 * - AdminResourceController: CRUD Location (không dùng service, dùng trực tiếp
 * repository)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;

    /**
     * Lấy tất cả địa điểm
     * Admin: AdminPersonnelController.userCreateForm(), shipperCreateForm(),
     * staffCreateForm() - GET /admin/user/create, /admin/shipper/create,
     * /admin/staff/create
     */
    @Override
    public List<LocationDTO> findAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::toLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> findActiveLocations() {
        return locationRepository.findByIsActiveTrue().stream()
                .map(this::toLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> findLocationsByType(String locationType) {
        Location.LocationType type = Location.LocationType.valueOf(locationType);
        return locationRepository.findByLocationType(type).stream()
                .map(this::toLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LocationDTO> findLocationById(Long id) {
        return locationRepository.findById(id).map(this::toLocationDTO);
    }

    @Override
    public Optional<LocationDTO> findByWarehouseCode(String warehouseCode) {
        return locationRepository.findByWarehouseCode(warehouseCode).map(this::toLocationDTO);
    }

    @Override
    public List<LocationDTO> findAllWarehouses() {
        return locationRepository.findAllActiveWarehouses().stream()
                .map(this::toLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> searchLocations(String keyword) {
        return locationRepository.searchByKeyword(keyword).stream()
                .map(this::toLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LocationDTO createLocation(LocationDTO dto) {
        Location location = new Location();
        location.setLocationType(Location.LocationType.valueOf(dto.getLocationType()));
        location.setWarehouseCode(dto.getWarehouseCode());
        location.setName(dto.getName());
        location.setAddressText(dto.getAddressText());
        location.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return toLocationDTO(locationRepository.save(location));
    }

    @Override
    public LocationDTO updateLocation(Long id, LocationDTO dto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        if (dto.getLocationType() != null) {
            location.setLocationType(Location.LocationType.valueOf(dto.getLocationType()));
        }
        if (dto.getWarehouseCode() != null) {
            location.setWarehouseCode(dto.getWarehouseCode());
        }
        if (dto.getName() != null) {
            location.setName(dto.getName());
        }
        if (dto.getAddressText() != null) {
            location.setAddressText(dto.getAddressText());
        }
        if (dto.getIsActive() != null) {
            location.setIsActive(dto.getIsActive());
        }
        return toLocationDTO(locationRepository.save(location));
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    @Override
    public void toggleLocationStatus(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        location.setIsActive(!location.getIsActive());
        locationRepository.save(location);
    }

    // Route operations
    @Override
    public List<RouteDTO> findAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::toRouteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteDTO> findActiveRoutes() {
        return routeRepository.findByIsActiveTrue().stream()
                .map(this::toRouteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RouteDTO> findRouteById(Long id) {
        return routeRepository.findById(id).map(this::toRouteDTO);
    }

    @Override
    public Optional<RouteDTO> findRouteByLocations(Long fromLocationId, Long toLocationId) {
        return routeRepository.findByFromLocationIdAndToLocationId(fromLocationId, toLocationId)
                .map(this::toRouteDTO);
    }

    @Override
    public List<RouteDTO> findRoutesByLocationId(Long locationId) {
        return routeRepository.findByLocationId(locationId).stream()
                .map(this::toRouteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RouteDTO createRoute(RouteDTO dto) {
        Route route = new Route();
        route.setFromLocation(locationRepository.findById(dto.getFromLocationId())
                .orElseThrow(() -> new RuntimeException("From location not found")));
        route.setToLocation(locationRepository.findById(dto.getToLocationId())
                .orElseThrow(() -> new RuntimeException("To location not found")));
        route.setDistanceKm(dto.getDistanceKm());
        route.setEstimatedTimeHours(dto.getEstimatedTimeHours());
        route.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return toRouteDTO(routeRepository.save(route));
    }

    @Override
    public RouteDTO updateRoute(Long id, RouteDTO dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        if (dto.getFromLocationId() != null) {
            route.setFromLocation(locationRepository.findById(dto.getFromLocationId())
                    .orElseThrow(() -> new RuntimeException("From location not found")));
        }
        if (dto.getToLocationId() != null) {
            route.setToLocation(locationRepository.findById(dto.getToLocationId())
                    .orElseThrow(() -> new RuntimeException("To location not found")));
        }
        if (dto.getDistanceKm() != null) {
            route.setDistanceKm(dto.getDistanceKm());
        }
        if (dto.getEstimatedTimeHours() != null) {
            route.setEstimatedTimeHours(dto.getEstimatedTimeHours());
        }
        return toRouteDTO(routeRepository.save(route));
    }

    @Override
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public void toggleRouteStatus(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        route.setIsActive(!route.getIsActive());
        routeRepository.save(route);
    }

    // Helper methods
    private LocationDTO toLocationDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setLocationType(location.getLocationType().name());
        dto.setWarehouseCode(location.getWarehouseCode());
        dto.setName(location.getName());
        dto.setAddressText(location.getAddressText());
        dto.setIsActive(location.getIsActive());
        dto.setCreatedAt(location.getCreatedAt());
        dto.setUpdatedAt(location.getUpdatedAt());
        return dto;
    }

    private RouteDTO toRouteDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setFromLocationId(route.getFromLocation().getId());
        dto.setFromLocationName(route.getFromLocation().getName());
        dto.setToLocationId(route.getToLocation().getId());
        dto.setToLocationName(route.getToLocation().getName());
        dto.setDistanceKm(route.getDistanceKm());
        dto.setEstimatedTimeHours(route.getEstimatedTimeHours());
        dto.setIsActive(route.getIsActive());
        dto.setCreatedAt(route.getCreatedAt());
        return dto;
    }
}
