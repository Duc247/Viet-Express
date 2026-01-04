package vn.DucBackend.Services;

import vn.DucBackend.DTO.CustomerRequestDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý CustomerRequest (Yêu cầu/Đơn hàng của khách)
 * 
 * Repository sử dụng: CustomerRequestRepository, CustomerRepository, LocationRepository, ServiceTypeRepository, ParcelRepository
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

    /** Repository: customerRequestRepository.save(), customerRepository.findById(), locationRepository.findById(), serviceTypeRepository.findById() */
    CustomerRequestDTO createRequest(CustomerRequestDTO dto);

    /** Repository: customerRequestRepository.findById(), customerRequestRepository.save() */
    CustomerRequestDTO updateRequest(Long id, CustomerRequestDTO dto);

    /** Repository: customerRequestRepository.findById(), customerRequestRepository.save() */
    CustomerRequestDTO updateRequestStatus(Long id, String status);

    /** Repository: customerRequestRepository.findById(), customerRequestRepository.save() */
    CustomerRequestDTO confirmRequest(Long id);

    /** Repository: customerRequestRepository.findById(), customerRequestRepository.save() */
    CustomerRequestDTO cancelRequest(Long id);

    /** Repository: customerRequestRepository.deleteById() */
    void deleteRequest(Long id);

    /** Tạo mã vận đơn tự động */
    String generateRequestCode();

    /** Repository: serviceTypeRepository.findById() - Tính phí ship */
    java.math.BigDecimal calculateShippingFee(Long serviceTypeId, java.math.BigDecimal distanceKm);

    /** Repository: serviceTypeRepository.findById() - Tính thời gian giao dự kiến */
    LocalDateTime calculateEstimatedDeliveryTime(Long serviceTypeId, java.math.BigDecimal distanceKm);
}
