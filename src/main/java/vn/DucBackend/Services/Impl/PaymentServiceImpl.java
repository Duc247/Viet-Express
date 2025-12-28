package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.PaymentDTO;
import vn.DucBackend.DTO.PaymentTransactionDTO;
import vn.DucBackend.Entities.Payment;
import vn.DucBackend.Entities.Payment.PaymentStatus;
import vn.DucBackend.Entities.Payment.PaymentType;
import vn.DucBackend.Entities.PaymentTransaction;
import vn.DucBackend.Entities.PaymentTransaction.PaymentMethod;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của PaymentService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CustomerRequestRepository customerRequestRepository;
    private final CustomerRepository customerRepository;
    private final TrackingCodeRepository trackingCodeRepository;

    // ==================== CONVERTER ====================

    private PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = PaymentDTO.builder()
                .id(payment.getId())
                .requestId(payment.getRequest() != null ? payment.getRequest().getId() : null)
                .type(payment.getType() != null ? payment.getType().name() : null)
                .payerCustomerId(payment.getPayerCustomer() != null ? payment.getPayerCustomer().getId() : null)
                .payerCustomerName(
                        payment.getPayerCustomer() != null ? payment.getPayerCustomer().getBusinessName() : null)
                .totalAmount(payment.getTotalAmount())
                .paidAmount(payment.getPaidAmount())
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .createdAt(payment.getCreatedAt())
                .build();

        // Get tracking code
        if (payment.getRequest() != null) {
            trackingCodeRepository.findByRequest_Id(payment.getRequest().getId())
                    .ifPresent(tc -> dto.setTrackingCode(tc.getCode()));
        }

        return dto;
    }

    private PaymentTransactionDTO toTransactionDTO(PaymentTransaction transaction) {
        return PaymentTransactionDTO.builder()
                .id(transaction.getId())
                .paymentId(transaction.getPayment() != null ? transaction.getPayment().getId() : null)
                .amount(transaction.getAmount())
                .method(transaction.getMethod() != null ? transaction.getMethod().name() : null)
                .gatewayTransactionId(transaction.getGatewayTransactionId())
                .gatewayResponseCode(transaction.getGatewayResponseCode())
                .note(transaction.getNote())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private Payment toEntity(PaymentDTO dto) {
        Payment payment = new Payment();

        if (dto.getRequestId() != null) {
            customerRequestRepository.findById(dto.getRequestId()).ifPresent(payment::setRequest);
        }
        if (dto.getType() != null) {
            payment.setType(PaymentType.valueOf(dto.getType()));
        }
        if (dto.getPayerCustomerId() != null) {
            customerRepository.findById(dto.getPayerCustomerId()).ifPresent(payment::setPayerCustomer);
        }

        payment.setTotalAmount(dto.getTotalAmount());
        payment.setPaidAmount(dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO);
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setCreatedAt(LocalDateTime.now());

        return payment;
    }

    // ==================== PAYMENT ====================

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    @Override
    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        if (paymentDTO.getTotalAmount() != null)
            payment.setTotalAmount(paymentDTO.getTotalAmount());
        if (paymentDTO.getPaidAmount() != null)
            payment.setPaidAmount(paymentDTO.getPaidAmount());

        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    @Override
    public PaymentDTO updateStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        payment.setStatus(PaymentStatus.valueOf(status));
        payment = paymentRepository.save(payment);
        return toDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> findById(Long id) {
        return paymentRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> findAllByRequestId(Long requestId) {
        return paymentRepository.findAllByRequest_Id(requestId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> findAllByType(String type, Pageable pageable) {
        PaymentType paymentType = PaymentType.valueOf(type);
        return paymentRepository.findAllByType(paymentType, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> findAllByStatus(String status, Pageable pageable) {
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
        return paymentRepository.findAllByStatus(paymentStatus, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByRequestId(Long requestId) {
        return paymentRepository.sumTotalAmountByRequest_Id(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPaidAmountByRequestId(Long requestId) {
        return paymentRepository.sumPaidAmountByRequest_Id(requestId);
    }

    // ==================== PAYMENT TRANSACTION ====================

    @Override
    public PaymentTransactionDTO addTransaction(PaymentTransactionDTO transactionDTO) {
        Payment payment = paymentRepository.findById(transactionDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionDTO.getPaymentId()));

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(payment);
        transaction.setAmount(transactionDTO.getAmount());
        if (transactionDTO.getMethod() != null) {
            transaction.setMethod(PaymentMethod.valueOf(transactionDTO.getMethod()));
        }
        transaction.setGatewayTransactionId(transactionDTO.getGatewayTransactionId());
        transaction.setGatewayResponseCode(transactionDTO.getGatewayResponseCode());
        transaction.setNote(transactionDTO.getNote());
        transaction.setCreatedAt(LocalDateTime.now());

        transaction = paymentTransactionRepository.save(transaction);

        // Update paid amount in payment
        BigDecimal newPaidAmount = payment.getPaidAmount().add(transactionDTO.getAmount());
        payment.setPaidAmount(newPaidAmount);

        // Update payment status
        if (newPaidAmount.compareTo(payment.getTotalAmount()) >= 0) {
            payment.setStatus(PaymentStatus.PAID);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            payment.setStatus(PaymentStatus.PARTIAL);
        }
        paymentRepository.save(payment);

        return toTransactionDTO(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionDTO> findAllTransactionsByPaymentId(Long paymentId) {
        return paymentTransactionRepository.findAllByPayment_Id(paymentId)
                .stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentTransactionDTO> findLatestTransactionByPaymentId(Long paymentId) {
        return paymentTransactionRepository.findTop1ByPayment_IdOrderByCreatedAtDesc(paymentId)
                .map(this::toTransactionDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentTransactionDTO> findTransactionByGatewayId(String gatewayTransactionId) {
        return paymentTransactionRepository.findByGatewayTransactionId(gatewayTransactionId)
                .map(this::toTransactionDTO);
    }
}
