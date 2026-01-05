package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.CustomerRequestDTO;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Entities.ParcelAction;
import vn.DucBackend.Entities.Route;
import vn.DucBackend.Entities.ServiceType;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.CustomerRequestService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private final CustomerRequestRepository requestRepository;
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserRepository userRepository;
    private final ParcelActionRepository parcelActionRepository;
    private final RouteRepository routeRepository;

    @Override
    public List<CustomerRequestDTO> findAllRequests() {
        return requestRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerRequestDTO> findRequestById(Long id) {
        return requestRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<CustomerRequestDTO> findByRequestCode(String requestCode) {
        return requestRepository.findByRequestCode(requestCode).map(this::toDTO);
    }

    @Override
    public List<CustomerRequestDTO> findRequestsBySenderId(Long senderId) {
        return requestRepository.findBySenderId(senderId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CustomerRequestDTO> findRequestsByReceiverId(Long receiverId) {
        return requestRepository.findByReceiverId(receiverId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CustomerRequestDTO> findRequestsByCustomerId(Long customerId) {
        return requestRepository.findByCustomerId(customerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CustomerRequestDTO> findRequestsByStatus(String status) {
        return requestRepository.findByStatus(CustomerRequest.RequestStatus.valueOf(status)).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerRequestDTO> findActiveRequests() {
        return requestRepository.findActiveRequests().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CustomerRequestDTO> findRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return requestRepository.findByDateRange(startDate, endDate).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerRequestDTO> searchRequests(String keyword) {
        return requestRepository.searchByKeyword(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Long countRequestsByStatus(String status) {
        return requestRepository.countByStatus(CustomerRequest.RequestStatus.valueOf(status));
    }

    @Override
    public CustomerRequestDTO createRequest(CustomerRequestDTO dto) {
        CustomerRequest request = new CustomerRequest();
        request.setRequestCode(dto.getRequestCode() != null ? dto.getRequestCode() : generateRequestCode());
        request.setSender(customerRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found")));
        if (dto.getReceiverId() != null) {
            request.setReceiver(customerRepository.findById(dto.getReceiverId()).orElse(null));
        }
        request.setSenderLocation(locationRepository.findById(dto.getSenderLocationId())
                .orElseThrow(() -> new RuntimeException("Sender location not found")));
        request.setReceiverLocation(locationRepository.findById(dto.getReceiverLocationId())
                .orElseThrow(() -> new RuntimeException("Receiver location not found")));
        request.setServiceType(serviceTypeRepository.findById(dto.getServiceTypeId())
                .orElseThrow(() -> new RuntimeException("Service type not found")));

        // Check Route để tính phí - nếu không có Route thì fee=null
        Optional<Route> routeOpt = routeRepository.findByFromLocationIdAndToLocationId(
                dto.getSenderLocationId(), dto.getReceiverLocationId());

        if (routeOpt.isPresent()) {
            Route route = routeOpt.get();
            request.setDistanceKm(route.getDistanceKm());
            request.setShippingFee(calculateShippingFee(dto.getServiceTypeId(), route.getDistanceKm()));
            request.setEstimatedDeliveryTime(
                    calculateEstimatedDeliveryTime(dto.getServiceTypeId(), route.getDistanceKm()));
        } else {
            // Không có Route → fee=null, chờ Manager tạo Route
            request.setDistanceKm(null);
            request.setShippingFee(null);
            request.setEstimatedDeliveryTime(null);
        }

        request.setParcelDescription(dto.getParcelDescription());
        request.setCodAmount(dto.getCodAmount());
        request.setNote(dto.getNote());
        request.setStatus(CustomerRequest.RequestStatus.PENDING);
        return toDTO(requestRepository.save(request));
    }

    @Override
    public CustomerRequestDTO updateRequest(Long id, CustomerRequestDTO dto) {
        CustomerRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (dto.getParcelDescription() != null)
            request.setParcelDescription(dto.getParcelDescription());
        if (dto.getCodAmount() != null)
            request.setCodAmount(dto.getCodAmount());
        if (dto.getNote() != null)
            request.setNote(dto.getNote());
        return toDTO(requestRepository.save(request));
    }

    @Override
    public CustomerRequestDTO updateRequestStatus(Long id, String status) {
        CustomerRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(CustomerRequest.RequestStatus.valueOf(status));
        return toDTO(requestRepository.save(request));
    }

    @Override
    public CustomerRequestDTO confirmRequest(Long id) {
        return updateRequestStatus(id, "CONFIRMED");
    }

    @Override
    public CustomerRequestDTO cancelRequest(Long id) {
        return updateRequestStatus(id, "CANCELLED");
    }

    @Override
    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

    @Override
    public String generateRequestCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "REQ-" + dateStr + "-" + uuid;
    }

    @Override
    public BigDecimal calculateShippingFee(Long serviceTypeId, BigDecimal distanceKm) {
        ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId).orElse(null);
        if (serviceType != null && serviceType.getPricePerKm() != null && distanceKm != null) {
            return serviceType.getPricePerKm().multiply(distanceKm);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public LocalDateTime calculateEstimatedDeliveryTime(Long serviceTypeId, BigDecimal distanceKm) {
        ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId).orElse(null);
        if (serviceType != null && serviceType.getAverageSpeedKmh() != null && distanceKm != null) {
            double hours = distanceKm.doubleValue() / serviceType.getAverageSpeedKmh().doubleValue();
            return LocalDateTime.now().plusHours((long) Math.ceil(hours));
        }
        return LocalDateTime.now().plusDays(3);
    }

    // ==========================================
    // MANAGER ASSIGNMENT IMPLEMENTATION
    // ==========================================

    @Override
    public CustomerRequestDTO assignManager(Long requestId, Long managerId) {
        CustomerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found: " + managerId));

        // Kiểm tra user có role MANAGER không
        if (!"MANAGER".equals(manager.getRole().getRoleName())) {
            throw new RuntimeException("User is not a Manager: " + manager.getUsername());
        }

        request.setAssignedManager(manager);
        request.setManagerAssignedAt(LocalDateTime.now());
        return toDTO(requestRepository.save(request));
    }

    @Override
    public List<CustomerRequestDTO> findByAssignedManager(Long managerId) {
        return requestRepository.findByAssignedManager(managerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countNewAssignmentsForManager(Long managerId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return requestRepository.countNewAssignmentsForManager(managerId, since);
    }

    // ==========================================
    // STAFF ASSIGNMENT IMPLEMENTATION
    // ==========================================

    @Override
    public List<CustomerRequestDTO> findByAssignedStaff(Long staffId) {
        return requestRepository.findByAssignedStaffId(staffId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerRequestDTO assignStaff(Long requestId, Long staffId) {
        CustomerRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        vn.DucBackend.Entities.Staff staff = null; // Will need to inject StaffRepository
        // Note: This implementation would need StaffRepository injection
        // For now, just set the staff ID reference
        request.setAssignedAt(LocalDateTime.now());
        return toDTO(requestRepository.save(request));
    }

    @Override
    public CustomerRequest getRequestEntityById(Long id) {
        return requestRepository.findById(id).orElse(null);
    }

    private CustomerRequestDTO toDTO(CustomerRequest request) {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setId(request.getId());
        dto.setRequestCode(request.getRequestCode());

        // Safe handling for sender - may be deleted from DB
        try {
            if (request.getSender() != null) {
                dto.setSenderId(request.getSender().getId());
                dto.setSenderName(request.getSender().getName());
                dto.setSenderPhone(request.getSender().getPhone());
            }
        } catch (Exception e) {
            dto.setSenderName("[Đã xóa]");
        }

        // Safe handling for receiver - may be deleted from DB
        try {
            if (request.getReceiver() != null) {
                dto.setReceiverId(request.getReceiver().getId());
                dto.setReceiverName(request.getReceiver().getName());
                dto.setReceiverPhone(request.getReceiver().getPhone());
            }
        } catch (Exception e) {
            dto.setReceiverName("[Đã xóa]");
        }
        dto.setSenderLocationId(request.getSenderLocation().getId());
        dto.setSenderLocationName(request.getSenderLocation().getName());
        dto.setSenderAddress(request.getSenderLocation().getAddressText());
        dto.setReceiverLocationId(request.getReceiverLocation().getId());
        dto.setReceiverLocationName(request.getReceiverLocation().getName());
        dto.setReceiverAddress(request.getReceiverLocation().getAddressText());
        dto.setServiceTypeId(request.getServiceType().getId());
        dto.setServiceTypeName(request.getServiceType().getName());
        dto.setDistanceKm(request.getDistanceKm());
        dto.setParcelDescription(request.getParcelDescription());
        dto.setShippingFee(request.getShippingFee());
        dto.setCodAmount(request.getCodAmount());
        dto.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        dto.setStatus(request.getStatus().name());
        dto.setNote(request.getNote());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());

        // Staff được giao
        if (request.getAssignedStaff() != null) {
            dto.setAssignedStaffId(request.getAssignedStaff().getId());
            dto.setAssignedStaffName(request.getAssignedStaff().getFullName());
            dto.setAssignedAt(request.getAssignedAt());
        }

        // Manager được giao
        if (request.getAssignedManager() != null) {
            dto.setAssignedManagerId(request.getAssignedManager().getId());
            dto.setAssignedManagerName(request.getAssignedManager().getFullName());
            dto.setManagerAssignedAt(request.getManagerAssignedAt());
        }

        return dto;
    }

    // ==========================================
    // Methods cho Manager Controllers
    // ==========================================

    @Override
    public java.util.List<CustomerRequest> getAllRequestEntities() {
        return requestRepository.findAll();
    }

    @Override
    public java.util.List<CustomerRequest> findByAssignedManagerEntities(Long managerId) {
        return requestRepository.findByAssignedManagerId(managerId);
    }

    @Override
    public CustomerRequest saveRequestEntity(CustomerRequest request) {
        return requestRepository.save(request);
    }

    // ==========================================
    // Methods cho Customer Controllers
    // ==========================================

    @Override
    public java.util.List<CustomerRequest> findByCustomerIdEntities(Long customerId) {
        return requestRepository.findByCustomerId(customerId);
    }

    @Override
    public CustomerRequest findByRequestCodeEntity(String requestCode) {
        return requestRepository.findByRequestCode(requestCode).orElse(null);
    }

    @Override
    public java.util.List<ParcelAction> findParcelActionsByRequestIdEntities(Long requestId) {
        return parcelActionRepository.findByRequestIdOrderByCreatedAtDesc(requestId);
    }

    // ==========================================
    // Route-Based Shipping Fee
    // ==========================================

    @Override
    public int updateShippingFeeForRoute(Long fromLocationId, Long toLocationId, BigDecimal distanceKm) {
        // Tìm tất cả đơn hàng có senderLocation và receiverLocation khớp, nhưng fee =
        // null
        List<CustomerRequest> requests = requestRepository.findByLocationAndFeeIsNull(fromLocationId, toLocationId);

        int count = 0;
        for (CustomerRequest request : requests) {
            if (request.getServiceType() != null && request.getServiceType().getPricePerKm() != null) {
                BigDecimal newFee = request.getServiceType().getPricePerKm().multiply(distanceKm);
                request.setDistanceKm(distanceKm);
                request.setShippingFee(newFee);
                request.setEstimatedDeliveryTime(calculateEstimatedDeliveryTime(
                        request.getServiceType().getId(), distanceKm));
                requestRepository.save(request);
                count++;
            }
        }
        return count;
    }
}
