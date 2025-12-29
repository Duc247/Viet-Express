package vn.DucBackend.Services;

import vn.DucBackend.DTO.TrackingCodeDTO;
import vn.DucBackend.DTO.ParcelActionDTO;

import java.util.List;
import java.util.Optional;

public interface TrackingService {

    // TrackingCode operations
    Optional<TrackingCodeDTO> findByCode(String code);

    Optional<TrackingCodeDTO> findByRequestId(Long requestId);

    TrackingCodeDTO createTrackingCode(Long requestId);

    String generateTrackingCode();

    // ParcelAction (Tracking Log) operations
    List<ParcelActionDTO> findActionsByParcelId(Long parcelId);

    List<ParcelActionDTO> findActionsByRequestId(Long requestId);

    List<ParcelActionDTO> getTrackingHistory(String trackingCode);

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
