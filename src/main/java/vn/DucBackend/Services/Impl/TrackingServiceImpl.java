package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ParcelActionDTO;
import vn.DucBackend.DTO.TrackingCodeDTO;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.TrackingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation của TrackingService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TrackingServiceImpl implements TrackingService {

    private final TrackingCodeRepository trackingCodeRepository;
    private final ParcelActionRepository parcelActionRepository;
    private final CustomerRequestRepository customerRequestRepository;
    private final ActionTypeRepository actionTypeRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;

    // ==================== CONVERTER ====================

    private TrackingCodeDTO toTrackingCodeDTO(TrackingCode trackingCode) {
        return TrackingCodeDTO.builder()
                .id(trackingCode.getId())
                .requestId(trackingCode.getRequest() != null ? trackingCode.getRequest().getId() : null)
                .code(trackingCode.getCode())
                .status(trackingCode.getStatus())
                .createdAt(trackingCode.getCreatedAt())
                .build();
    }

    private ParcelActionDTO toParcelActionDTO(ParcelAction action) {
        return ParcelActionDTO.builder()
                .id(action.getId())
                .requestId(action.getRequest() != null ? action.getRequest().getId() : null)
                .actionTypeId(action.getActionType() != null ? action.getActionType().getId() : null)
                .actionCode(action.getActionType() != null ? action.getActionType().getActionCode() : null)
                .actionName(action.getActionType() != null ? action.getActionType().getActionName() : null)
                .fromWarehouseId(action.getFromWarehouse() != null ? action.getFromWarehouse().getId() : null)
                .fromWarehouseName(
                        action.getFromWarehouse() != null ? action.getFromWarehouse().getWarehouseName() : null)
                .toWarehouseId(action.getToWarehouse() != null ? action.getToWarehouse().getId() : null)
                .toWarehouseName(action.getToWarehouse() != null ? action.getToWarehouse().getWarehouseName() : null)
                .actorUserId(action.getActorUser() != null ? action.getActorUser().getId() : null)
                .actorUserName(action.getActorUser() != null ? action.getActorUser().getFullName() : null)
                .actionTime(action.getActionTime())
                .note(action.getNote())
                .build();
    }

    private String generateCode() {
        String code;
        do {
            code = "VN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        } while (trackingCodeRepository.existsByCode(code));
        return code;
    }

    // ==================== TRACKING CODE ====================

    @Override
    public TrackingCodeDTO generateTrackingCode(Long requestId) {
        CustomerRequest request = customerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("CustomerRequest not found: " + requestId));

        // Check if already exists
        Optional<TrackingCode> existing = trackingCodeRepository.findByRequest_Id(requestId);
        if (existing.isPresent()) {
            return toTrackingCodeDTO(existing.get());
        }

        TrackingCode trackingCode = new TrackingCode();
        trackingCode.setRequest(request);
        trackingCode.setCode(generateCode());
        trackingCode.setStatus(true);
        trackingCode.setCreatedAt(LocalDateTime.now());
        trackingCode = trackingCodeRepository.save(trackingCode);

        return toTrackingCodeDTO(trackingCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrackingCodeDTO> findTrackingCodeByRequestId(Long requestId) {
        return trackingCodeRepository.findByRequest_Id(requestId).map(this::toTrackingCodeDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrackingCodeDTO> findByCode(String code) {
        return trackingCodeRepository.findByCode(code).map(this::toTrackingCodeDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return trackingCodeRepository.existsByCode(code);
    }

    // ==================== PARCEL ACTION ====================

    @Override
    public ParcelActionDTO addParcelAction(Long requestId, String actionCode, Long actorUserId,
            Long fromWarehouseId, Long toWarehouseId, String note) {
        CustomerRequest request = customerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("CustomerRequest not found: " + requestId));

        ActionType actionType = actionTypeRepository.findByActionCode(actionCode)
                .orElseThrow(() -> new RuntimeException("ActionType not found: " + actionCode));

        ParcelAction action = new ParcelAction();
        action.setRequest(request);
        action.setActionType(actionType);
        action.setActionTime(LocalDateTime.now());
        action.setNote(note);

        if (actorUserId != null) {
            userRepository.findById(actorUserId).ifPresent(action::setActorUser);
        }
        if (fromWarehouseId != null) {
            warehouseRepository.findById(fromWarehouseId).ifPresent(action::setFromWarehouse);
        }
        if (toWarehouseId != null) {
            warehouseRepository.findById(toWarehouseId).ifPresent(action::setToWarehouse);
        }

        action = parcelActionRepository.save(action);
        return toParcelActionDTO(action);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelActionDTO> getTrackingTimeline(Long requestId) {
        return parcelActionRepository.findAllByRequest_IdOrderByActionTimeAsc(requestId)
                .stream()
                .map(this::toParcelActionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelActionDTO> getTrackingTimelineByCode(String trackingCode) {
        TrackingCode tc = trackingCodeRepository.findByCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("TrackingCode not found: " + trackingCode));
        return getTrackingTimeline(tc.getRequest().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParcelActionDTO> getLatestAction(Long requestId) {
        return parcelActionRepository.findTop1ByRequest_IdOrderByActionTimeDesc(requestId)
                .map(this::toParcelActionDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParcelActionDTO> getFirstAction(Long requestId) {
        return parcelActionRepository.findTop1ByRequest_IdOrderByActionTimeAsc(requestId)
                .map(this::toParcelActionDTO);
    }
}
