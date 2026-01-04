package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.TrackingCodeDTO;
import vn.DucBackend.DTO.ParcelActionDTO;
import vn.DucBackend.Entities.ParcelAction;
import vn.DucBackend.Entities.TrackingCode;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.TrackingService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackingServiceImpl implements TrackingService {

    private final TrackingCodeRepository trackingCodeRepository;
    private final ParcelActionRepository parcelActionRepository;
    private final CustomerRequestRepository requestRepository;
    private final ParcelRepository parcelRepository;
    private final ActionTypeRepository actionTypeRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<TrackingCodeDTO> findByCode(String code) {
        return trackingCodeRepository.findByCode(code).map(this::toTrackingCodeDTO);
    }

    @Override
    public Optional<TrackingCodeDTO> findByRequestId(Long requestId) {
        return trackingCodeRepository.findByRequestId(requestId).map(this::toTrackingCodeDTO);
    }

    @Override
    public TrackingCodeDTO createTrackingCode(Long requestId) {
        TrackingCode trackingCode = new TrackingCode();
        trackingCode.setRequest(requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found")));
        trackingCode.setCode(generateTrackingCode());
        return toTrackingCodeDTO(trackingCodeRepository.save(trackingCode));
    }

    @Override
    public String generateTrackingCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TRK-" + dateStr + "-" + uuid;
    }

    @Override
    public List<ParcelActionDTO> findActionsByParcelId(Long parcelId) {
        return parcelActionRepository.findByParcelIdOrderByCreatedAtDesc(parcelId).stream()
                .map(this::toParcelActionDTO).collect(Collectors.toList());
    }

    @Override
    public List<ParcelActionDTO> findActionsByRequestId(Long requestId) {
        return parcelActionRepository.findByRequestIdOrderByCreatedAtDesc(requestId).stream()
                .map(this::toParcelActionDTO).collect(Collectors.toList());
    }

    @Override
    public List<ParcelActionDTO> getTrackingHistory(String trackingCode) {
        Optional<TrackingCode> tc = trackingCodeRepository.findByCode(trackingCode);
        if (tc.isPresent()) {
            return findActionsByRequestId(tc.get().getRequest().getId());
        }
        return List.of();
    }

    @Override
    public ParcelActionDTO logAction(Long parcelId, Long requestId, String actionCode,
            Long fromLocationId, Long toLocationId,
            Long actorUserId, String note) {
        ParcelAction action = new ParcelAction();
        if (parcelId != null) {
            action.setParcel(parcelRepository.findById(parcelId).orElse(null));
        }
        if (requestId != null) {
            action.setRequest(requestRepository.findById(requestId).orElse(null));
        }
        action.setActionType(actionTypeRepository.findByActionCode(actionCode)
                .orElseThrow(() -> new RuntimeException("ActionType not found: " + actionCode)));
        if (fromLocationId != null) {
            action.setFromLocation(locationRepository.findById(fromLocationId).orElse(null));
        }
        if (toLocationId != null) {
            action.setToLocation(locationRepository.findById(toLocationId).orElse(null));
        }
        if (actorUserId != null) {
            action.setActorUser(userRepository.findById(actorUserId).orElse(null));
        }
        action.setNote(note);
        return toParcelActionDTO(parcelActionRepository.save(action));
    }

    @Override
    public ParcelActionDTO logCreated(Long requestId, Long actorUserId) {
        return logAction(null, requestId, "CREATED", null, null, actorUserId, "Đơn hàng được tạo");
    }

    @Override
    public ParcelActionDTO logPickedUp(Long parcelId, Long shipperId, Long locationId) {
        return logAction(parcelId, null, "PICKED_UP", locationId, null, shipperId, "Đã lấy hàng");
    }

    @Override
    public ParcelActionDTO logInWarehouse(Long parcelId, Long locationId, Long staffId) {
        return logAction(parcelId, null, "IN_WAREHOUSE", null, locationId, staffId, "Đã nhập kho");
    }

    @Override
    public ParcelActionDTO logInTransit(Long parcelId, Long fromLocationId, Long toLocationId, Long shipperId) {
        return logAction(parcelId, null, "IN_TRANSIT", fromLocationId, toLocationId, shipperId, "Đang vận chuyển");
    }

    @Override
    public ParcelActionDTO logDelivered(Long parcelId, Long shipperId, Long locationId) {
        return logAction(parcelId, null, "DELIVERED", null, locationId, shipperId, "Giao hàng thành công");
    }

    @Override
    public ParcelActionDTO logFailed(Long parcelId, Long shipperId, String note) {
        return logAction(parcelId, null, "FAILED", null, null, shipperId, note);
    }

    @Override
    public ParcelActionDTO logReturned(Long parcelId, Long locationId, Long staffId) {
        return logAction(parcelId, null, "RETURNED", null, locationId, staffId, "Đã hoàn hàng");
    }

    private TrackingCodeDTO toTrackingCodeDTO(TrackingCode tc) {
        TrackingCodeDTO dto = new TrackingCodeDTO();
        dto.setId(tc.getId());
        dto.setRequestId(tc.getRequest().getId());
        dto.setRequestCode(tc.getRequest().getRequestCode());
        dto.setCode(tc.getCode());
        dto.setCreatedAt(tc.getCreatedAt());
        return dto;
    }

    private ParcelActionDTO toParcelActionDTO(ParcelAction action) {
        ParcelActionDTO dto = new ParcelActionDTO();
        dto.setId(action.getId());
        if (action.getParcel() != null) {
            dto.setParcelId(action.getParcel().getId());
            dto.setParcelCode(action.getParcel().getParcelCode());
        }
        if (action.getRequest() != null) {
            dto.setRequestId(action.getRequest().getId());
            dto.setRequestCode(action.getRequest().getRequestCode());
        }
        dto.setActionTypeId(action.getActionType().getId());
        dto.setActionTypeName(action.getActionType().getName());
        if (action.getFromLocation() != null) {
            dto.setFromLocationId(action.getFromLocation().getId());
            dto.setFromLocationName(action.getFromLocation().getName());
        }
        if (action.getToLocation() != null) {
            dto.setToLocationId(action.getToLocation().getId());
            dto.setToLocationName(action.getToLocation().getName());
        }
        if (action.getActorUser() != null) {
            dto.setActorUserId(action.getActorUser().getId());
            dto.setActorUserName(action.getActorUser().getUsername());
        }
        dto.setNote(action.getNote());
        dto.setCreatedAt(action.getCreatedAt());
        return dto;
    }
}
