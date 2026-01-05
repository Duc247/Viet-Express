package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.ParcelActionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParcelActionServiceImpl implements ParcelActionService {

    private final ParcelActionRepository parcelActionRepository;
    private final ParcelRepository parcelRepository;
    private final CustomerRequestRepository requestRepository;
    private final ActionTypeRepository actionTypeRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParcelAction recordAction(Long parcelId, Long requestId, String actionCode,
            Long fromLocationId, Long toLocationId,
            Long actorUserId, String note) {

        ActionType actionType = actionTypeRepository.findByActionCode(actionCode).orElse(null);
        if (actionType == null) {
            log.warn("ActionType không tồn tại: {}", actionCode);
            return null;
        }

        ParcelAction action = new ParcelAction();

        // Set parcel nếu có
        if (parcelId != null) {
            action.setParcel(parcelRepository.findById(parcelId).orElse(null));
        }

        // Set request nếu có
        if (requestId != null) {
            action.setRequest(requestRepository.findById(requestId).orElse(null));
        }

        action.setActionType(actionType);

        // Set locations nếu có
        if (fromLocationId != null) {
            action.setFromLocation(locationRepository.findById(fromLocationId).orElse(null));
        }
        if (toLocationId != null) {
            action.setToLocation(locationRepository.findById(toLocationId).orElse(null));
        }

        // Set actor
        if (actorUserId != null) {
            action.setActorUser(userRepository.findById(actorUserId).orElse(null));
        }

        action.setNote(note);

        ParcelAction saved = parcelActionRepository.save(action);
        log.info("Đã ghi nhận action: {} cho request={}, parcel={}", actionCode, requestId, parcelId);

        return saved;
    }

    @Override
    @Transactional
    public ParcelAction recordRequestAction(Long requestId, String actionCode, Long actorUserId, String note) {
        return recordAction(null, requestId, actionCode, null, null, actorUserId, note);
    }

    @Override
    @Transactional
    public ParcelAction recordParcelAction(Long parcelId, String actionCode, Long actorUserId, String note) {
        // Lấy requestId từ parcel
        Long requestId = null;
        if (parcelId != null) {
            Parcel parcel = parcelRepository.findById(parcelId).orElse(null);
            if (parcel != null && parcel.getRequest() != null) {
                requestId = parcel.getRequest().getId();
            }
        }
        return recordAction(parcelId, requestId, actionCode, null, null, actorUserId, note);
    }

    @Override
    public List<ParcelAction> getActionsByRequestId(Long requestId) {
        return parcelActionRepository.findByRequestIdOrderByCreatedAtDesc(requestId);
    }

    @Override
    public List<ParcelAction> getActionsByParcelId(Long parcelId) {
        return parcelActionRepository.findByParcelIdOrderByCreatedAtDesc(parcelId);
    }
}
