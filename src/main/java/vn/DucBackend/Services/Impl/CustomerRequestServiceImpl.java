package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.CustomerRequestDTO;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Entities.CustomerRequest.RequestStatus;
import vn.DucBackend.Entities.TrackingCode;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.CustomerRequestService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation của CustomerRequestService - Core Order Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerRequestServiceImpl implements CustomerRequestService {

    private final CustomerRequestRepository customerRequestRepository;
    private final CustomerRepository customerRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final WarehouseRepository warehouseRepository;
    private final TrackingCodeRepository trackingCodeRepository;

    // ==================== CONVERTER ====================

    private CustomerRequestDTO toDTO(CustomerRequest request) {
        CustomerRequestDTO dto = CustomerRequestDTO.builder()
                .id(request.getId())
                .customerId(request.getCustomer() != null ? request.getCustomer().getId() : null)
                .serviceTypeId(request.getServiceType() != null ? request.getServiceType().getId() : null)
                .serviceTypeName(request.getServiceType() != null ? request.getServiceType().getServiceName() : null)
                .expectedPickupTime(request.getExpectedPickupTime())
                .note(request.getNote())
                .imageOrder(request.getImageOrder())
                .pickupContactName(request.getPickupContactName())
                .pickupContactPhone(request.getPickupContactPhone())
                .pickupAddressDetail(request.getPickupAddressDetail())
                .pickupWard(request.getPickupWard())
                .pickupDistrict(request.getPickupDistrict())
                .pickupProvince(request.getPickupProvince())
                .deliveryContactName(request.getDeliveryContactName())
                .deliveryContactPhone(request.getDeliveryContactPhone())
                .deliveryAddressDetail(request.getDeliveryAddressDetail())
                .deliveryWard(request.getDeliveryWard())
                .deliveryDistrict(request.getDeliveryDistrict())
                .deliveryProvince(request.getDeliveryProvince())
                .weight(request.getWeight())
                .length(request.getLength())
                .width(request.getWidth())
                .height(request.getHeight())
                .codAmount(request.getCodAmount())
                .currentWarehouseId(
                        request.getCurrentWarehouse() != null ? request.getCurrentWarehouse().getId() : null)
                .currentWarehouseName(
                        request.getCurrentWarehouse() != null ? request.getCurrentWarehouse().getWarehouseName() : null)
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .createdAt(request.getCreatedAt())
                .build();

        // Get tracking code
        trackingCodeRepository.findByRequest_Id(request.getId())
                .ifPresent(tc -> dto.setTrackingCode(tc.getCode()));

        return dto;
    }

    private CustomerRequest toEntity(CustomerRequestDTO dto) {
        CustomerRequest request = new CustomerRequest();

        if (dto.getCustomerId() != null) {
            customerRepository.findById(dto.getCustomerId()).ifPresent(request::setCustomer);
        }
        if (dto.getServiceTypeId() != null) {
            serviceTypeRepository.findById(dto.getServiceTypeId()).ifPresent(request::setServiceType);
        }

        request.setExpectedPickupTime(dto.getExpectedPickupTime());
        request.setNote(dto.getNote());
        request.setImageOrder(dto.getImageOrder());
        request.setPickupContactName(dto.getPickupContactName());
        request.setPickupContactPhone(dto.getPickupContactPhone());
        request.setPickupAddressDetail(dto.getPickupAddressDetail());
        request.setPickupWard(dto.getPickupWard());
        request.setPickupDistrict(dto.getPickupDistrict());
        request.setPickupProvince(dto.getPickupProvince());
        request.setDeliveryContactName(dto.getDeliveryContactName());
        request.setDeliveryContactPhone(dto.getDeliveryContactPhone());
        request.setDeliveryAddressDetail(dto.getDeliveryAddressDetail());
        request.setDeliveryWard(dto.getDeliveryWard());
        request.setDeliveryDistrict(dto.getDeliveryDistrict());
        request.setDeliveryProvince(dto.getDeliveryProvince());
        request.setWeight(dto.getWeight());
        request.setLength(dto.getLength());
        request.setWidth(dto.getWidth());
        request.setHeight(dto.getHeight());
        request.setCodAmount(dto.getCodAmount());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return request;
    }

    private String generateTrackingCode() {
        String code;
        do {
            code = "VN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        } while (trackingCodeRepository.existsByCode(code));
        return code;
    }

    // ==================== TẠO / CẬP NHẬT VẬN ĐƠN ====================

    @Override
    public CustomerRequestDTO createOrder(CustomerRequestDTO requestDTO) {
        CustomerRequest request = toEntity(requestDTO);
        request = customerRequestRepository.save(request);

        // Generate tracking code
        TrackingCode trackingCode = new TrackingCode();
        trackingCode.setRequest(request);
        trackingCode.setCode(generateTrackingCode());
        trackingCode.setStatus(true);
        trackingCode.setCreatedAt(LocalDateTime.now());
        trackingCodeRepository.save(trackingCode);

        return toDTO(request);
    }

    @Override
    public CustomerRequestDTO updateOrder(Long id, CustomerRequestDTO requestDTO) {
        CustomerRequest request = customerRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CustomerRequest not found: " + id));

        if (requestDTO.getNote() != null)
            request.setNote(requestDTO.getNote());
        if (requestDTO.getExpectedPickupTime() != null)
            request.setExpectedPickupTime(requestDTO.getExpectedPickupTime());

        request = customerRequestRepository.save(request);
        return toDTO(request);
    }

    @Override
    public CustomerRequestDTO updateStatus(Long id, String status) {
        CustomerRequest request = customerRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CustomerRequest not found: " + id));
        request.setStatus(RequestStatus.valueOf(status));
        request = customerRequestRepository.save(request);
        return toDTO(request);
    }

    @Override
    public CustomerRequestDTO confirmOrder(Long id) {
        return updateStatus(id, RequestStatus.CONFIRMED.name());
    }

    @Override
    public CustomerRequestDTO cancelOrder(Long id) {
        return updateStatus(id, RequestStatus.CANCELLED.name());
    }

    @Override
    public CustomerRequestDTO updateCurrentWarehouse(Long id, Long warehouseId) {
        CustomerRequest request = customerRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CustomerRequest not found: " + id));

        warehouseRepository.findById(warehouseId).ifPresent(request::setCurrentWarehouse);
        request = customerRequestRepository.save(request);
        return toDTO(request);
    }

    // ==================== TÌM KIẾM ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerRequestDTO> findById(Long id) {
        return customerRequestRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerRequestDTO> findByIdAndCustomerId(Long id, Long customerId) {
        return customerRequestRepository.findByIdAndCustomer_Id(id, customerId).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerRequestDTO> findByTrackingCode(String trackingCode) {
        return customerRequestRepository.findByTrackingCode(trackingCode).map(this::toDTO);
    }

    // ==================== DANH SÁCH ====================

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerRequestDTO> findAll(Pageable pageable) {
        return customerRequestRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerRequestDTO> findAllByCustomerId(Long customerId, Pageable pageable) {
        return customerRequestRepository.findAllByCustomer_Id(customerId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerRequestDTO> findAllByStatus(String status, Pageable pageable) {
        RequestStatus requestStatus = RequestStatus.valueOf(status);
        return customerRequestRepository.findAllByStatus(requestStatus, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerRequestDTO> findAllByWarehouseIdAndStatus(Long warehouseId, String status, Pageable pageable) {
        RequestStatus requestStatus = RequestStatus.valueOf(status);
        return customerRequestRepository.findAllByCurrentWarehouse_IdAndStatus(warehouseId, requestStatus, pageable)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerRequestDTO> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return customerRequestRepository.findAllByCreatedAtBetween(from, to, pageable).map(this::toDTO);
    }

    // ==================== THỐNG KÊ ====================

    @Override
    @Transactional(readOnly = true)
    public Long countByStatus(String status) {
        RequestStatus requestStatus = RequestStatus.valueOf(status);
        return customerRequestRepository.countByStatus(requestStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByWarehouseIdAndStatus(Long warehouseId, String status) {
        RequestStatus requestStatus = RequestStatus.valueOf(status);
        return customerRequestRepository.countByCurrentWarehouse_IdAndStatus(warehouseId, requestStatus);
    }
}
