package vn.DucBackend.Services;

import vn.DucBackend.DTO.ParcelDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Parcel (Kiện hàng)
 * 
 * Repository sử dụng: ParcelRepository, CustomerRequestRepository,
 * ShipperRepository, TripRepository, LocationRepository
 * Controller sử dụng: AdminOperationController
 */
public interface ParcelService {

    /** Repository: parcelRepository.findAll() */
    List<ParcelDTO> findAllParcels();

    /** Repository: parcelRepository.findById() */
    Optional<ParcelDTO> findParcelById(Long id);

    /** Repository: parcelRepository.findByParcelCode() */
    Optional<ParcelDTO> findByParcelCode(String parcelCode);

    /** Repository: parcelRepository.findByRequestId() */
    List<ParcelDTO> findParcelsByRequestId(Long requestId);

    /** Repository: parcelRepository.findByStatus() */
    List<ParcelDTO> findParcelsByStatus(String status);

    /** Repository: parcelRepository.findByShipperId() */
    List<ParcelDTO> findParcelsByShipperId(Long shipperId);

    /** Repository: parcelRepository.findByTripId() */
    List<ParcelDTO> findParcelsByTripId(Long tripId);

    /** Repository: parcelRepository.findByCurrentLocationId() */
    List<ParcelDTO> findParcelsByLocationId(Long locationId);

    /** Repository: parcelRepository.findActiveParcelsByShipper() */
    List<ParcelDTO> findActiveParcelsByShipper(Long shipperId);

    /** Repository: parcelRepository.countByRequestId() */
    Long countParcelsByRequestId(Long requestId);

    /** Repository: parcelRepository.save(), customerRequestRepository.findById() */
    ParcelDTO createParcel(ParcelDTO dto);

    /** Repository: parcelRepository.findById(), parcelRepository.save() */
    ParcelDTO updateParcel(Long id, ParcelDTO dto);

    /** Repository: parcelRepository.findById(), parcelRepository.save() */
    ParcelDTO updateParcelStatus(Long id, String status);

    /**
     * Repository: parcelRepository.findById(), parcelRepository.save(),
     * shipperRepository.findById()
     */
    ParcelDTO assignShipperToParcel(Long parcelId, Long shipperId);

    /**
     * Repository: parcelRepository.findById(), parcelRepository.save(),
     * tripRepository.findById()
     */
    ParcelDTO assignTripToParcel(Long parcelId, Long tripId);

    /**
     * Repository: parcelRepository.findById(), parcelRepository.save(),
     * locationRepository.findById()
     */
    ParcelDTO updateParcelLocation(Long parcelId, Long locationId);

    /** Repository: parcelRepository.deleteById() */
    void deleteParcel(Long id);

    /** Tạo mã kiện hàng tự động */
    String generateParcelCode(Long requestId);

    // ==========================================
    // Methods cho Staff Controllers
    // ==========================================

    /** Đếm số parcels theo status */
    Long countParcelsByStatus(String status);

    /** Lấy parcels theo location và status */
    List<ParcelDTO> findByLocationIdAndStatus(Long locationId, String status);

    /** Lấy tất cả parcels ngoại trừ DELIVERED */
    List<ParcelDTO> findAllExceptDelivered();

    /** Nhập kho - Cập nhật status và location */
    ParcelDTO checkinParcel(Long parcelId, Long staffId, String note);

    /** Xuất kho - Cập nhật status */
    ParcelDTO checkoutParcel(Long parcelId, Long staffId, String note);

    /** Lấy Parcel entity theo ID */
    vn.DucBackend.Entities.Parcel getParcelEntityById(Long id);

    /** Tạo parcel với location của staff */
    ParcelDTO createParcelWithLocation(ParcelDTO dto, Long locationId);

    // ==========================================
    // Methods cho Manager Controllers
    // ==========================================

    /** Lấy tất cả Parcel entities */
    java.util.List<vn.DucBackend.Entities.Parcel> getAllParcelEntities();

    /** Lưu Parcel entity */
    vn.DucBackend.Entities.Parcel saveParcelEntity(vn.DucBackend.Entities.Parcel parcel);

    /** Cập nhật location và status cho parcel */
    ParcelDTO updateParcelLocation(Long parcelId, Long locationId, String newStatus);

    /** 
     * Di chuyển kiện hàng qua kho với tracking người thực hiện
     * @param parcelId ID kiện hàng
     * @param toLocationId ID vị trí đích
     * @param userId ID người thực hiện (staff/shipper)
     * @param note Ghi chú
     * @return ParcelDTO đã cập nhật
     */
    ParcelDTO moveParcelToLocation(Long parcelId, Long toLocationId, Long userId, String note);

    /** Tìm parcels theo request ID - trả về entities */
    java.util.List<vn.DucBackend.Entities.Parcel> findByRequestIdEntities(Long requestId);

    /** Tìm parcels theo trip ID - trả về entities */
    java.util.List<vn.DucBackend.Entities.Parcel> findByTripIdEntities(Long tripId);

    // ==========================================
    // Methods cho Customer Controllers
    // ==========================================

    /** Đếm parcels theo request ID */
    Long countByRequestId(Long requestId);

    /** Đếm parcels đã giao theo request ID */
    Long countDeliveredByRequestId(Long requestId);

    /** Đếm parcels đang giao theo request ID */
    Long countInDeliveryByRequestId(Long requestId);

    /** Đếm parcels chờ xử lý theo request ID */
    Long countPendingByRequestId(Long requestId);

    /** Tìm parcels theo request và keyword */
    java.util.List<vn.DucBackend.Entities.Parcel> searchByRequestIdAndKeyword(Long requestId, String keyword);

    /** Tìm parcels theo request và status - trả về entities */
    java.util.List<vn.DucBackend.Entities.Parcel> findByRequestIdAndStatusEntities(Long requestId, String status);

    // ==========================================
    // Bulk Parcel Creation
    // ==========================================

    /**
     * Tạo nhiều kiện hàng giống nhau
     * Description sẽ được thêm số thứ tự: "#1 - Mô tả", "#2 - Mô tả"...
     */
    java.util.List<ParcelDTO> createBulkParcels(Long requestId, String description,
            java.math.BigDecimal codAmount, java.math.BigDecimal weightKg,
            java.math.BigDecimal lengthCm, java.math.BigDecimal widthCm, java.math.BigDecimal heightCm,
            Integer quantity, Long locationId);
}
