package vn.DucBackend.Services;

import vn.DucBackend.DTO.ParcelActionDTO;

import java.util.List;

/**
 * Service xử lý tracking log (ParcelAction)
 * TrackingCode đã được loại bỏ, sử dụng RequestCode/ParcelCode trực tiếp
 */
public interface TrackingService {

    // ParcelAction (Tracking Log) operations
    List<ParcelActionDTO> findActionsByParcelId(Long parcelId);

    List<ParcelActionDTO> findActionsByRequestId(Long requestId);

    ParcelActionDTO logAction(Long parcelId, Long requestId, String actionCode,
            Long fromLocationId, Long toLocationId,
            Long actorUserId, String note);

    ParcelActionDTO logCreated(Long requestId, Long actorUserId);

    ParcelActionDTO logPickedUp(Long parcelId, Long shipperId, Long locationId);

    ParcelActionDTO logInWarehouse(Long parcelId, Long locationId, Long staffId);

    ParcelActionDTO logInTransit(Long parcelId, Long fromLocationId, Long toLocationId, Long shipperId);

    ParcelActionDTO logDelivered(Long parcelId, Long shipperId, Long locationId);

    ParcelActionDTO logFailed(Long parcelId, Long shipperId, String note);

    ParcelActionDTO logReturned(Long parcelId, Long locationId, Long staffId);
}
