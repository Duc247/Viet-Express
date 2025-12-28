package vn.DucBackend.Services;

import vn.DucBackend.DTO.ParcelActionDTO;
import vn.DucBackend.DTO.TrackingCodeDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface cho Tracking - Quản lý tracking timeline
 */
public interface TrackingService {

    // ==================== TRACKING CODE ====================

    /** Sinh tracking code mới cho vận đơn */
    TrackingCodeDTO generateTrackingCode(Long requestId);

    /** Tìm tracking code theo request ID */
    Optional<TrackingCodeDTO> findTrackingCodeByRequestId(Long requestId);

    /** Tìm tracking code theo mã code */
    Optional<TrackingCodeDTO> findByCode(String code);

    /** Kiểm tra mã tracking đã tồn tại */
    boolean existsByCode(String code);

    // ==================== PARCEL ACTION (TRACKING LOG) ====================

    /** Thêm action mới vào tracking timeline */
    ParcelActionDTO addParcelAction(Long requestId, String actionCode, Long actorUserId,
            Long fromWarehouseId, Long toWarehouseId, String note);

    /** Lấy danh sách tracking timeline theo request ID (order by time ASC) */
    List<ParcelActionDTO> getTrackingTimeline(Long requestId);

    /** Lấy danh sách tracking timeline theo tracking code */
    List<ParcelActionDTO> getTrackingTimelineByCode(String trackingCode);

    /** Lấy action mới nhất của vận đơn */
    Optional<ParcelActionDTO> getLatestAction(Long requestId);

    /** Lấy action đầu tiên của vận đơn */
    Optional<ParcelActionDTO> getFirstAction(Long requestId);
}
