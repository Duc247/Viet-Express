package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ShipperDTO;
import vn.DucBackend.Entities.Shipper;
import vn.DucBackend.Entities.Shipper.ShipperStatus;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Repositories.WarehouseRepository;
import vn.DucBackend.Services.ShipperService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của ShipperService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShipperServiceImpl implements ShipperService {

    private final ShipperRepository shipperRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    // ==================== CONVERTER ====================

    private ShipperDTO toDTO(Shipper shipper) {
        return ShipperDTO.builder()
                .id(shipper.getId())
                .userId(shipper.getUser() != null ? shipper.getUser().getId() : null)
                .userFullName(shipper.getUser() != null ? shipper.getUser().getFullName() : null)
                .userPhone(shipper.getUser() != null ? shipper.getUser().getPhone() : null)
                .warehouseId(shipper.getWarehouse() != null ? shipper.getWarehouse().getId() : null)
                .warehouseName(shipper.getWarehouse() != null ? shipper.getWarehouse().getWarehouseName() : null)
                .status(shipper.getStatus() != null ? shipper.getStatus().name() : null)
                .joinedAt(shipper.getJoinedAt())
                .build();
    }

    private Shipper toEntity(ShipperDTO dto) {
        Shipper shipper = new Shipper();
        if (dto.getUserId() != null) {
            userRepository.findById(dto.getUserId()).ifPresent(shipper::setUser);
        }
        if (dto.getWarehouseId() != null) {
            warehouseRepository.findById(dto.getWarehouseId()).ifPresent(shipper::setWarehouse);
        }
        shipper.setStatus(dto.getStatus() != null ? ShipperStatus.valueOf(dto.getStatus()) : ShipperStatus.AVAILABLE);
        shipper.setJoinedAt(LocalDateTime.now());
        return shipper;
    }

    // ==================== TẠO / CẬP NHẬT ====================

    @Override
    public ShipperDTO createShipper(ShipperDTO shipperDTO) {
        Shipper shipper = toEntity(shipperDTO);
        shipper = shipperRepository.save(shipper);
        return toDTO(shipper);
    }

    @Override
    public ShipperDTO updateShipper(Long id, ShipperDTO shipperDTO) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipper not found: " + id));

        if (shipperDTO.getWarehouseId() != null) {
            warehouseRepository.findById(shipperDTO.getWarehouseId()).ifPresent(shipper::setWarehouse);
        }

        shipper = shipperRepository.save(shipper);
        return toDTO(shipper);
    }

    @Override
    public ShipperDTO updateStatus(Long id, String status) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipper not found: " + id));
        shipper.setStatus(ShipperStatus.valueOf(status));
        shipper = shipperRepository.save(shipper);
        return toDTO(shipper);
    }

    // ==================== TÌM KIẾM ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipperDTO> findById(Long id) {
        return shipperRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipperDTO> findByUserId(Long userId) {
        return shipperRepository.findByUser_Id(userId).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        return shipperRepository.existsByUser_Id(userId);
    }

    // ==================== DANH SÁCH ====================

    @Override
    @Transactional(readOnly = true)
    public Page<ShipperDTO> findAll(Pageable pageable) {
        return shipperRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipperDTO> findAllByStatus(String status, Pageable pageable) {
        ShipperStatus shipperStatus = ShipperStatus.valueOf(status);
        return shipperRepository.findAllByStatus(shipperStatus, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipperDTO> findAllByWarehouseId(Long warehouseId, Pageable pageable) {
        return shipperRepository.findAllByWarehouse_Id(warehouseId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipperDTO> findAvailableByWarehouseId(Long warehouseId) {
        return shipperRepository.findAllByWarehouse_IdAndStatus(warehouseId, ShipperStatus.AVAILABLE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== THỐNG KÊ ====================

    @Override
    @Transactional(readOnly = true)
    public Long countByStatus(String status) {
        ShipperStatus shipperStatus = ShipperStatus.valueOf(status);
        return shipperRepository.countByStatus(shipperStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByWarehouseId(Long warehouseId) {
        return shipperRepository.countByWarehouse_Id(warehouseId);
    }
}
