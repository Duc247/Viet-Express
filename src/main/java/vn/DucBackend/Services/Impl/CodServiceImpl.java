package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.CodTransactionDTO;
import vn.DucBackend.Entities.CodTransaction;
import vn.DucBackend.Repositories.CodTransactionRepository;
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.TrackingCodeRepository;
import vn.DucBackend.Services.CodService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của CodService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CodServiceImpl implements CodService {

    private final CodTransactionRepository codTransactionRepository;
    private final CustomerRequestRepository customerRequestRepository;
    private final ShipperRepository shipperRepository;
    private final TrackingCodeRepository trackingCodeRepository;

    // ==================== CONVERTER ====================

    private CodTransactionDTO toDTO(CodTransaction cod) {
        CodTransactionDTO dto = CodTransactionDTO.builder()
                .id(cod.getId())
                .requestId(cod.getRequest() != null ? cod.getRequest().getId() : null)
                .shipperId(cod.getShipper() != null ? cod.getShipper().getId() : null)
                .shipperName(cod.getShipper() != null && cod.getShipper().getUser() != null
                        ? cod.getShipper().getUser().getFullName()
                        : null)
                .amount(cod.getAmount())
                .collectedAt(cod.getCollectedAt())
                .settledAt(cod.getSettledAt())
                .status(cod.getStatus())
                .paymentMethod(cod.getPaymentMethod())
                .build();

        // Get tracking code
        if (cod.getRequest() != null) {
            trackingCodeRepository.findByRequest_Id(cod.getRequest().getId())
                    .ifPresent(tc -> dto.setTrackingCode(tc.getCode()));
        }

        return dto;
    }

    // ==================== COD TRANSACTION ====================

    @Override
    public CodTransactionDTO collectCod(CodTransactionDTO codDTO) {
        CodTransaction cod = new CodTransaction();

        if (codDTO.getRequestId() != null) {
            customerRequestRepository.findById(codDTO.getRequestId()).ifPresent(cod::setRequest);
        }
        if (codDTO.getShipperId() != null) {
            shipperRepository.findById(codDTO.getShipperId()).ifPresent(cod::setShipper);
        }

        cod.setAmount(codDTO.getAmount());
        cod.setCollectedAt(LocalDateTime.now());
        cod.setStatus("COLLECTED");
        cod.setPaymentMethod(codDTO.getPaymentMethod());

        cod = codTransactionRepository.save(cod);
        return toDTO(cod);
    }

    @Override
    public CodTransactionDTO settleCod(Long codTransactionId) {
        CodTransaction cod = codTransactionRepository.findById(codTransactionId)
                .orElseThrow(() -> new RuntimeException("CodTransaction not found: " + codTransactionId));

        cod.setStatus("SETTLED");
        cod.setSettledAt(LocalDateTime.now());

        cod = codTransactionRepository.save(cod);
        return toDTO(cod);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CodTransactionDTO> findById(Long id) {
        return codTransactionRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodTransactionDTO> findAllByRequestId(Long requestId) {
        return codTransactionRepository.findAllByRequest_Id(requestId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CodTransactionDTO> findAllByShipperIdAndStatus(Long shipperId, String status, Pageable pageable) {
        return codTransactionRepository.findAllByShipper_IdAndStatus(shipperId, status, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CodTransactionDTO> findAllByShipperId(Long shipperId, Pageable pageable) {
        return codTransactionRepository.findAllByShipper_Id(shipperId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CodTransactionDTO> findAllByStatus(String status, Pageable pageable) {
        return codTransactionRepository.findAllByStatus(status, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CodTransactionDTO> findAllByStatusAndCollectedAtBetween(String status, LocalDateTime from,
            LocalDateTime to, Pageable pageable) {
        return codTransactionRepository.findAllByStatusAndCollectedAtBetween(status, from, to, pageable)
                .map(this::toDTO);
    }

    // ==================== THỐNG KÊ ====================

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCodByShipperAndStatus(Long shipperId, String status) {
        return codTransactionRepository.sumAmountByShipper_IdAndStatus(shipperId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCodByRequestId(Long requestId) {
        return codTransactionRepository.sumAmountByRequest_Id(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByShipperIdAndStatus(Long shipperId, String status) {
        return codTransactionRepository.countByShipper_IdAndStatus(shipperId, status);
    }
}
