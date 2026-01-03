package vn.DucBackend.Services;

import vn.DucBackend.DTO.CustomerRequestDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRequestService {

    List<CustomerRequestDTO> findAllRequests();

    Optional<CustomerRequestDTO> findRequestById(Long id);

    Optional<CustomerRequestDTO> findByRequestCode(String requestCode);

    List<CustomerRequestDTO> findRequestsBySenderId(Long senderId);

    List<CustomerRequestDTO> findRequestsByReceiverId(Long receiverId);

    List<CustomerRequestDTO> findRequestsByCustomerId(Long customerId);

    List<CustomerRequestDTO> findRequestsByStatus(String status);

    List<CustomerRequestDTO> findActiveRequests();

    List<CustomerRequestDTO> findRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<CustomerRequestDTO> searchRequests(String keyword);

    Long countRequestsByStatus(String status);

    CustomerRequestDTO createRequest(CustomerRequestDTO dto);

    CustomerRequestDTO updateRequest(Long id, CustomerRequestDTO dto);

    CustomerRequestDTO updateRequestStatus(Long id, String status);

    CustomerRequestDTO confirmRequest(Long id);

    CustomerRequestDTO cancelRequest(Long id);

    void deleteRequest(Long id);

    String generateRequestCode();

    java.math.BigDecimal calculateShippingFee(Long serviceTypeId, java.math.BigDecimal distanceKm);

    LocalDateTime calculateEstimatedDeliveryTime(Long serviceTypeId, java.math.BigDecimal distanceKm);
}
