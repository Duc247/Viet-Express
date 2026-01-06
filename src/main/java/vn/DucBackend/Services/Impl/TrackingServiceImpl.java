package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ParcelActionDTO;
import vn.DucBackend.Entities.ParcelAction;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.TrackingService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý tracking log (ParcelAction)
 * TrackingCode entity đã được loại bỏ, sử dụng RequestCode/ParcelCode trực tiếp
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TrackingServiceImpl implements TrackingService {

    private final ParcelActionRepository parcelActionRepository;
    private final CustomerRequestRepository requestRepository;
    private final ParcelRepository parcelRepository;
    private final ActionTypeRepository actionTypeRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

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
    public ParcelActionDTO logAction(Long parcelId, Long requestId, String actionCode,
            Long fromLocationId, Long toLocationId,
            Long actorUserId, String note) {

        // Kiểm tra ActionType tồn tại trước, nếu không thì skip (không throw exception)
        var actionTypeOpt = actionTypeRepository.findByActionCode(actionCode);
        if (actionTypeOpt.isEmpty()) {
            // ActionType không tồn tại - skip logging, không throw exception
            return null;
        }

        ParcelAction action = new ParcelAction();
        if (parcelId != null) {
            action.setParcel(parcelRepository.findById(parcelId).orElse(null));
        }
        if (requestId != null) {
            action.setRequest(requestRepository.findById(requestId).orElse(null));
        }
        action.setActionType(actionTypeOpt.get());
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
