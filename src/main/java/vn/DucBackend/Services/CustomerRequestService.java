package vn.DucBackend.Services;

import vn.DucBackend.DTO.CustomerRequestDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý CustomerRequest (Yêu cầu/Đơn hàng của khách)
 * 
 * Repository sử dụng: CustomerRequestRepository, CustomerRepository,
 * LocationRepository, ServiceTypeRepository, ParcelRepository
 * Controller sử dụng: AdminOperationController, CustomerOrderCreateController
 */
public interface CustomerRequestService {

    /** Repository: customerRequestRepository.findAll() */
    List<CustomerRequestDTO> findAllRequests();

    /** Repository: customerRequestRepository.findById() */
    Optional<CustomerRequestDTO> findRequestById(Long id);

    /** Repository: customerRequestRepository.findByRequestCode() */
    Optional<CustomerRequestDTO> findByRequestCode(String requestCode);

    /** Repository: customerRequestRepository.findBySenderId() */
    List<CustomerRequestDTO> findRequestsBySenderId(Long senderId);

    /** Repository: customerRequestRepository.findByReceiverId() */
    List<CustomerRequestDTO> findRequestsByReceiverId(Long receiverId);

    /** Repository: customerRequestRepository.findBySenderIdOrReceiverId() */
    List<CustomerRequestDTO> findRequestsByCustomerId(Long customerId);

    /** Repository: customerRequestRepository.findByStatus() */
    List<CustomerRequestDTO> findRequestsByStatus(String status);

    /** Repository: customerRequestRepository.findActiveRequests() */
    List<CustomerRequestDTO> findActiveRequests();

    /** Repository: customerRequestRepository.findByCreatedAtBetween() */
    List<CustomerRequestDTO> findRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /** Repository: customerRequestRepository.searchByKeyword() */
    List<CustomerRequestDTO> searchRequests(String keyword);

    /** Repository: customerRequestRepository.countByStatus() */
    Long countRequestsByStatus(String status);

    /**
     * Repository: customerRequestRepository.save(), customerRepository.findById(),
     * locationRepository.findById(), serviceTypeRepository.findById()
     */
    CustomerRequestDTO createRequest(CustomerRequestDTO dto);

    /**
     * Repository: customerRequestRepository.findById(),
     * customerRequestRepository.save()
     */
    CustomerRequestDTO updateRequest(Long id, CustomerRequestDTO dto);

    /**
     * Repository: customerRequestRepository.findById(),
     * customerRequestRepository.save()
     */
    CustomerRequestDTO updateRequestStatus(Long id, String status);

    /**
     * Repository: customerRequestRepository.findById(),
     * customerRequestRepository.save()
     */
    CustomerRequestDTO confirmRequest(Long id);

    /**
     * Repository: customerRequestRepository.findById(),
     * customerRequestRepository.save()
     */
    CustomerRequestDTO cancelRequest(Long id);

    /** Repository: customerRequestRepository.deleteById() */
    void deleteRequest(Long id);

    /** Tạo mã vận đơn tự động */
    String generateRequestCode();

    /** Repository: serviceTypeRepository.findById() - Tính phí ship */
    java.math.BigDecimal calculateShippingFee(Long serviceTypeId, java.math.BigDecimal distanceKm);

    /**
     * Repository: serviceTypeRepository.findById() - Tính thời gian giao dự kiến
     */
    LocalDateTime calculateEstimatedDeliveryTime(Long serviceTypeId, java.math.BigDecimal distanceKm);

    // ==========================================
    // MANAGER ASSIGNMENT
    // ==========================================

    /** Gán manager cho đơn hàng */
    CustomerRequestDTO assignManager(Long requestId, Long managerId);

    /** Tìm đơn hàng được giao cho manager */
    List<CustomerRequestDTO> findByAssignedManager(Long managerId);

    /** Đếm số đơn mới được giao cho manager (trong 24h) */
    Long countNewAssignmentsForManager(Long managerId);

    // ==========================================
    // STAFF ASSIGNMENT
    // ==========================================

    /** Tìm đơn hàng được giao cho staff */
    List<CustomerRequestDTO> findByAssignedStaff(Long staffId);

    /** Gán staff cho đơn hàng */
    CustomerRequestDTO assignStaff(Long requestId, Long staffId);

    // ==========================================
    // ENTITY METHODS (for controllers)
    // ==========================================

    /** Lấy CustomerRequest entity theo ID */
    vn.DucBackend.Entities.CustomerRequest getRequestEntityById(Long id);

    /** Lưu CustomerRequest entity */
    vn.DucBackend.Entities.CustomerRequest saveRequestEntity(vn.DucBackend.Entities.CustomerRequest request);

    /** Lấy tất cả CustomerRequest entities */
    java.util.List<vn.DucBackend.Entities.CustomerRequest> getAllRequestEntities();

    /** Tìm requests được gán cho manager - trả về entities */
    java.util.List<vn.DucBackend.Entities.CustomerRequest> findByAssignedManagerEntities(Long managerId);

    /** Tìm requests của customer - trả về entities */
    java.util.List<vn.DucBackend.Entities.CustomerRequest> findByCustomerIdEntities(Long customerId);

    /** Tìm request theo code - trả về entity */
    vn.DucBackend.Entities.CustomerRequest findByRequestCodeEntity(String requestCode);

    /** Lấy parcel actions theo request id - trả về entities */
    java.util.List<vn.DucBackend.Entities.ParcelAction> findParcelActionsByRequestIdEntities(Long requestId);

    // ==========================================
    // Route-Based Shipping Fee
    // ==========================================

    /** Cập nhật phí vận chuyển cho tất cả đơn hàng có cùng route khi tạo Route mới */
    int updateShippingFeeForRoute(Long fromLocationId, Long toLocationId, java.math.BigDecimal distanceKm);
}
