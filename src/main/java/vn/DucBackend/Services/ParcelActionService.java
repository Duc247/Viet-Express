package vn.DucBackend.Services;

import vn.DucBackend.Entities.ParcelAction;

import java.util.List;

/**
 * Service để ghi nhận các action liên quan đến parcel/request
 * Được sử dụng để tự động log lịch sử vận chuyển
 */
public interface ParcelActionService {

    /**
     * Ghi nhận một action mới
     * 
     * @param parcelId       ID của parcel (có thể null nếu action ở cấp request)
     * @param requestId      ID của request
     * @param actionCode     Mã loại action (CREATED, PICKED_UP, DELIVERED, etc.)
     * @param fromLocationId Location xuất phát (có thể null)
     * @param toLocationId   Location đích (có thể null)
     * @param actorUserId    ID của user thực hiện action
     * @param note           Ghi chú (có thể null)
     * @return ParcelAction đã được lưu
     */
    ParcelAction recordAction(Long parcelId, Long requestId, String actionCode,
            Long fromLocationId, Long toLocationId,
            Long actorUserId, String note);

    /**
     * Ghi nhận action cho request (không có parcel cụ thể)
     */
    ParcelAction recordRequestAction(Long requestId, String actionCode, Long actorUserId, String note);

    /**
     * Ghi nhận action cho parcel cụ thể
     */
    ParcelAction recordParcelAction(Long parcelId, String actionCode, Long actorUserId, String note);

    /**
     * Lấy lịch sử action của một request, sắp xếp mới nhất trước
     */
    List<ParcelAction> getActionsByRequestId(Long requestId);

    /**
     * Lấy lịch sử action của một parcel, sắp xếp mới nhất trước
     */
    List<ParcelAction> getActionsByParcelId(Long parcelId);
}
