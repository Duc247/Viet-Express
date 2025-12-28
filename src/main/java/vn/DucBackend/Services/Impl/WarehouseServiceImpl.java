package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.WarehouseDTO;
import vn.DucBackend.DTO.WarehouseRouteDTO;
import vn.DucBackend.Entities.Warehouse;
import vn.DucBackend.Entities.Warehouse.WarehouseType;
import vn.DucBackend.Entities.WarehouseRoute;
import vn.DucBackend.Repositories.WarehouseRepository;
import vn.DucBackend.Repositories.WarehouseRouteRepository;
import vn.DucBackend.Services.WarehouseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của WarehouseService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseRouteRepository warehouseRouteRepository;

    // ==================== CONVERTER ====================

    private WarehouseDTO toDTO(Warehouse warehouse) {
        return WarehouseDTO.builder()
                .id(warehouse.getId())
                .warehouseName(warehouse.getWarehouseName())
                .address(warehouse.getAddress())
                .ward(warehouse.getWard())
                .district(warehouse.getDistrict())
                .province(warehouse.getProvince())
                .warehouseType(warehouse.getWarehouseType() != null ? warehouse.getWarehouseType().name() : null)
                .status(warehouse.getStatus())
                .contactPhone(warehouse.getContactPhone())
                .email(warehouse.getEmail())
                .createdAt(warehouse.getCreatedAt())
                .build();
    }

    private Warehouse toEntity(WarehouseDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName(dto.getWarehouseName());
        warehouse.setAddress(dto.getAddress());
        warehouse.setWard(dto.getWard());
        warehouse.setDistrict(dto.getDistrict());
        warehouse.setProvince(dto.getProvince());
        if (dto.getWarehouseType() != null) {
            warehouse.setWarehouseType(WarehouseType.valueOf(dto.getWarehouseType()));
        }
        warehouse.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        warehouse.setContactPhone(dto.getContactPhone());
        warehouse.setEmail(dto.getEmail());
        warehouse.setCreatedAt(LocalDateTime.now());
        return warehouse;
    }

    private WarehouseRouteDTO toRouteDTO(WarehouseRoute route) {
        return WarehouseRouteDTO.builder()
                .id(route.getId())
                .fromWarehouseId(route.getFromWarehouse() != null ? route.getFromWarehouse().getId() : null)
                .fromWarehouseName(
                        route.getFromWarehouse() != null ? route.getFromWarehouse().getWarehouseName() : null)
                .toWarehouseId(route.getToWarehouse() != null ? route.getToWarehouse().getId() : null)
                .toWarehouseName(route.getToWarehouse() != null ? route.getToWarehouse().getWarehouseName() : null)
                .estimatedTime(route.getEstimatedTime())
                .description(route.getDescription())
                .status(route.getStatus())
                .build();
    }

    // ==================== WAREHOUSE ====================

    @Override
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = toEntity(warehouseDTO);
        warehouse = warehouseRepository.save(warehouse);
        return toDTO(warehouse);
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found: " + id));

        if (warehouseDTO.getWarehouseName() != null)
            warehouse.setWarehouseName(warehouseDTO.getWarehouseName());
        if (warehouseDTO.getAddress() != null)
            warehouse.setAddress(warehouseDTO.getAddress());
        if (warehouseDTO.getWard() != null)
            warehouse.setWard(warehouseDTO.getWard());
        if (warehouseDTO.getDistrict() != null)
            warehouse.setDistrict(warehouseDTO.getDistrict());
        if (warehouseDTO.getProvince() != null)
            warehouse.setProvince(warehouseDTO.getProvince());
        if (warehouseDTO.getWarehouseType() != null) {
            warehouse.setWarehouseType(WarehouseType.valueOf(warehouseDTO.getWarehouseType()));
        }
        if (warehouseDTO.getContactPhone() != null)
            warehouse.setContactPhone(warehouseDTO.getContactPhone());
        if (warehouseDTO.getEmail() != null)
            warehouse.setEmail(warehouseDTO.getEmail());

        warehouse = warehouseRepository.save(warehouse);
        return toDTO(warehouse);
    }

    @Override
    public WarehouseDTO changeStatus(Long id, Boolean status) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found: " + id));
        warehouse.setStatus(status);
        warehouse = warehouseRepository.save(warehouse);
        return toDTO(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseDTO> findById(Long id) {
        return warehouseRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseDTO> findByName(String warehouseName) {
        return warehouseRepository.findByWarehouseName(warehouseName).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> findAll(Pageable pageable) {
        return warehouseRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> findAllByType(String warehouseType, Pageable pageable) {
        WarehouseType type = WarehouseType.valueOf(warehouseType);
        return warehouseRepository.findAllByWarehouseType(type, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> findAllByStatus(Boolean status, Pageable pageable) {
        return warehouseRepository.findAllByStatus(status, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findAllByProvince(String province) {
        return warehouseRepository.findAllByProvince(province)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findAllActive() {
        return warehouseRepository.findAllByStatus(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== WAREHOUSE ROUTE ====================

    @Override
    public WarehouseRouteDTO createRoute(WarehouseRouteDTO routeDTO) {
        WarehouseRoute route = new WarehouseRoute();

        if (routeDTO.getFromWarehouseId() != null) {
            warehouseRepository.findById(routeDTO.getFromWarehouseId()).ifPresent(route::setFromWarehouse);
        }
        if (routeDTO.getToWarehouseId() != null) {
            warehouseRepository.findById(routeDTO.getToWarehouseId()).ifPresent(route::setToWarehouse);
        }
        route.setEstimatedTime(routeDTO.getEstimatedTime());
        route.setDescription(routeDTO.getDescription());
        route.setStatus(routeDTO.getStatus() != null ? routeDTO.getStatus() : true);

        route = warehouseRouteRepository.save(route);
        return toRouteDTO(route);
    }

    @Override
    public WarehouseRouteDTO updateRoute(Long id, WarehouseRouteDTO routeDTO) {
        WarehouseRoute route = warehouseRouteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WarehouseRoute not found: " + id));

        if (routeDTO.getEstimatedTime() != null)
            route.setEstimatedTime(routeDTO.getEstimatedTime());
        if (routeDTO.getDescription() != null)
            route.setDescription(routeDTO.getDescription());
        if (routeDTO.getStatus() != null)
            route.setStatus(routeDTO.getStatus());

        route = warehouseRouteRepository.save(route);
        return toRouteDTO(route);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseRouteDTO> findRouteByFromAndTo(Long fromWarehouseId, Long toWarehouseId) {
        return warehouseRouteRepository.findByFromWarehouse_IdAndToWarehouse_Id(fromWarehouseId, toWarehouseId)
                .map(this::toRouteDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsRoute(Long fromWarehouseId, Long toWarehouseId) {
        return warehouseRouteRepository.existsByFromWarehouse_IdAndToWarehouse_Id(fromWarehouseId, toWarehouseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseRouteDTO> findAllRoutesFromWarehouse(Long fromWarehouseId) {
        return warehouseRouteRepository.findAllByFromWarehouse_Id(fromWarehouseId)
                .stream()
                .map(this::toRouteDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseRouteDTO> findAllRoutesToWarehouse(Long toWarehouseId) {
        return warehouseRouteRepository.findAllByToWarehouse_Id(toWarehouseId)
                .stream()
                .map(this::toRouteDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseRouteDTO> findAllActiveRoutes() {
        return warehouseRouteRepository.findAllByStatus(true)
                .stream()
                .map(this::toRouteDTO)
                .collect(Collectors.toList());
    }
}
