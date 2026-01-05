package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.PaymentDTO;
import vn.DucBackend.DTO.PaymentTransactionDTO;
import vn.DucBackend.Entities.Payment;
import vn.DucBackend.Entities.PaymentTransaction;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final CustomerRequestRepository requestRepository;
    private final ParcelRepository parcelRepository;
    private final UserRepository userRepository;

    @Override
    public List<PaymentDTO> findAllPayments() {
        return paymentRepository.findAll().stream().map(this::toPaymentDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentDTO> findPaymentById(Long id) {
        return paymentRepository.findById(id).map(this::toPaymentDTO);
    }

    @Override
    public Optional<PaymentDTO> findByPaymentCode(String paymentCode) {
        return paymentRepository.findByPaymentCode(paymentCode).map(this::toPaymentDTO);
    }

    @Override
    public List<PaymentDTO> findPaymentsByRequestId(Long requestId) {
        return paymentRepository.findByRequestId(requestId).stream().map(this::toPaymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> findPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(Payment.PaymentStatus.valueOf(status)).stream().map(this::toPaymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> findUnpaidPayments() {
        return paymentRepository.findUnpaidPayments().stream().map(this::toPaymentDTO).collect(Collectors.toList());
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setRequest(requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found")));
        if (dto.getParcelId() != null) {
            payment.setParcel(parcelRepository.findById(dto.getParcelId()).orElse(null));
        }
        payment.setPaymentCode(
                dto.getPaymentCode() != null ? dto.getPaymentCode() : generatePaymentCode(dto.getRequestId()));
        payment.setPaymentType(Payment.PaymentType.valueOf(dto.getPaymentType()));
        if (dto.getPayerType() != null)
            payment.setPayerType(Payment.PayerType.valueOf(dto.getPayerType()));
        if (dto.getReceiverType() != null)
            payment.setReceiverType(Payment.ReceiverType.valueOf(dto.getReceiverType()));
        payment.setExpectedAmount(dto.getExpectedAmount());
        payment.setPaidAmount(BigDecimal.ZERO);
        payment.setStatus(Payment.PaymentStatus.UNPAID);
        return toPaymentDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentDTO updatePayment(Long id, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        if (dto.getExpectedAmount() != null)
            payment.setExpectedAmount(dto.getExpectedAmount());
        return toPaymentDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentDTO updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(Payment.PaymentStatus.valueOf(status));
        return toPaymentDTO(paymentRepository.save(payment));
    }

    @Override
    public Payment changePaymentStatus(Long paymentId, Payment.PaymentStatus newStatus,
            User actor, String actorType, String note) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Payment.PaymentStatus oldStatus = payment.getStatus();

        // Chỉ ghi lịch sử nếu status thực sự thay đổi
        if (oldStatus != newStatus) {
            // Tạo transaction ghi lịch sử thay đổi status
            PaymentTransaction txn = new PaymentTransaction();
            txn.setPayment(payment);
            txn.setTransactionType(PaymentTransaction.TransactionType.STATUS_CHANGE);
            txn.setOldPaymentStatus(oldStatus);
            txn.setNewPaymentStatus(newStatus);
            txn.setPerformedBy(actor);
            txn.setActorType(actorType != null ? actorType : "SYSTEM");
            txn.setGatewayResponse(note); // Dùng field gatewayResponse để lưu ghi chú
            txn.setStatus(PaymentTransaction.TransactionStatus.SUCCESS);
            txn.setTransactionAt(java.time.LocalDateTime.now());
            transactionRepository.save(txn);

            // Cập nhật status của payment
            payment.setStatus(newStatus);
            paymentRepository.save(payment);
        }

        return payment;
    }

    @Override
    public java.util.List<PaymentTransaction> getStatusHistory(Long paymentId) {
        return transactionRepository.findStatusHistoryByPaymentId(paymentId);
    }

    @Override
    public java.util.List<PaymentTransaction> getStatusHistoryAsc(Long paymentId) {
        return transactionRepository.findStatusHistoryByPaymentIdAsc(paymentId);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalExpectedAmount(Long requestId) {
        BigDecimal result = paymentRepository.sumExpectedAmountByRequestId(requestId);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalPaidAmount(Long requestId) {
        BigDecimal result = paymentRepository.sumPaidAmountByRequestId(requestId);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<PaymentTransactionDTO> findTransactionsByPaymentId(Long paymentId) {
        return transactionRepository.findByPaymentIdOrderByTransactionAtDesc(paymentId).stream()
                .map(this::toTransactionDTO).collect(Collectors.toList());
    }

    @Override
    public List<PaymentTransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate).stream().map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentTransactionDTO> findTransactionById(Long id) {
        return transactionRepository.findById(id).map(this::toTransactionDTO);
    }

    @Override
    public Optional<PaymentTransactionDTO> findByTransactionRef(String transactionRef) {
        return transactionRepository.findByTransactionRef(transactionRef).map(this::toTransactionDTO);
    }

    @Override
    public PaymentTransactionDTO createTransaction(PaymentTransactionDTO dto) {
        PaymentTransaction txn = new PaymentTransaction();
        txn.setPayment(paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found")));
        txn.setAmount(dto.getAmount());
        txn.setTransactionType(PaymentTransaction.TransactionType.valueOf(dto.getTransactionType()));
        txn.setPaymentMethod(PaymentTransaction.PaymentMethod.valueOf(dto.getPaymentMethod()));
        txn.setTransactionRef(dto.getTransactionRef());
        txn.setStatus(PaymentTransaction.TransactionStatus.PENDING);
        if (dto.getPerformedById() != null) {
            txn.setPerformedBy(userRepository.findById(dto.getPerformedById()).orElse(null));
        }
        return toTransactionDTO(transactionRepository.save(txn));
    }

    @Override
    public PaymentTransactionDTO recordCashPayment(Long paymentId, BigDecimal amount, Long performedById) {
        PaymentTransaction txn = new PaymentTransaction();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        txn.setPayment(payment);
        txn.setAmount(amount);
        txn.setTransactionType(PaymentTransaction.TransactionType.IN);
        txn.setPaymentMethod(PaymentTransaction.PaymentMethod.CASH);
        txn.setStatus(PaymentTransaction.TransactionStatus.SUCCESS);
        txn.setTransactionAt(LocalDateTime.now());
        if (performedById != null) {
            txn.setPerformedBy(userRepository.findById(performedById).orElse(null));
        }
        transactionRepository.save(txn);
        updatePaymentPaidAmount(payment);
        return toTransactionDTO(txn);
    }

    @Override
    public PaymentTransactionDTO recordOnlinePayment(Long paymentId, BigDecimal amount, String method,
            String transactionRef) {
        PaymentTransaction txn = new PaymentTransaction();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        txn.setPayment(payment);
        txn.setAmount(amount);
        txn.setTransactionType(PaymentTransaction.TransactionType.IN);
        txn.setPaymentMethod(PaymentTransaction.PaymentMethod.valueOf(method));
        txn.setTransactionRef(transactionRef);
        txn.setStatus(PaymentTransaction.TransactionStatus.PENDING);
        return toTransactionDTO(transactionRepository.save(txn));
    }

    @Override
    public PaymentTransactionDTO updateTransactionStatus(Long id, String status) {
        PaymentTransaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        txn.setStatus(PaymentTransaction.TransactionStatus.valueOf(status));
        if ("SUCCESS".equals(status)) {
            txn.setTransactionAt(LocalDateTime.now());
            transactionRepository.save(txn);
            updatePaymentPaidAmount(txn.getPayment());
        } else {
            transactionRepository.save(txn);
        }
        return toTransactionDTO(txn);
    }

    @Override
    public BigDecimal getSuccessfulPaymentTotal(Long paymentId) {
        BigDecimal result = transactionRepository.sumSuccessfulPayments(paymentId);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public String generatePaymentCode(Long requestId) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("PAY-%s-%d", dateStr, requestId);
    }

    private void updatePaymentPaidAmount(Payment payment) {
        BigDecimal paidAmount = getSuccessfulPaymentTotal(payment.getId());
        payment.setPaidAmount(paidAmount);

        // Xác định status mới dựa trên số tiền đã thanh toán
        Payment.PaymentStatus newStatus = payment.getStatus();
        if (paidAmount.compareTo(payment.getExpectedAmount()) >= 0) {
            newStatus = Payment.PaymentStatus.PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            newStatus = Payment.PaymentStatus.PARTIALLY_PAID;
        }

        // Gọi changePaymentStatus để ghi lịch sử (actor = SYSTEM vì là tự động)
        if (newStatus != payment.getStatus()) {
            changePaymentStatus(payment.getId(), newStatus, null, "SYSTEM", "Tự động cập nhật khi nhận thanh toán");
        } else {
            paymentRepository.save(payment);
        }
    }

    private PaymentDTO toPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setRequestId(payment.getRequest().getId());
        dto.setRequestCode(payment.getRequest().getRequestCode());
        if (payment.getParcel() != null) {
            dto.setParcelId(payment.getParcel().getId());
            dto.setParcelCode(payment.getParcel().getParcelCode());
        }
        dto.setPaymentCode(payment.getPaymentCode());
        dto.setPaymentType(payment.getPaymentType().name());
        if (payment.getPayerType() != null)
            dto.setPayerType(payment.getPayerType().name());
        if (payment.getReceiverType() != null)
            dto.setReceiverType(payment.getReceiverType().name());
        dto.setExpectedAmount(payment.getExpectedAmount());
        dto.setPaidAmount(payment.getPaidAmount());
        dto.setStatus(payment.getStatus().name());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    private PaymentTransactionDTO toTransactionDTO(PaymentTransaction txn) {
        PaymentTransactionDTO dto = new PaymentTransactionDTO();
        dto.setId(txn.getId());
        dto.setPaymentId(txn.getPayment().getId());
        dto.setPaymentCode(txn.getPayment().getPaymentCode());
        dto.setAmount(txn.getAmount());
        dto.setTransactionType(txn.getTransactionType().name());
        if (txn.getPaymentMethod() != null)
            dto.setPaymentMethod(txn.getPaymentMethod().name());
        dto.setTransactionRef(txn.getTransactionRef());
        dto.setStatus(txn.getStatus().name());
        if (txn.getPerformedBy() != null) {
            dto.setPerformedById(txn.getPerformedBy().getId());
            dto.setPerformedByName(txn.getPerformedBy().getUsername());
        }
        dto.setGatewayResponse(txn.getGatewayResponse());
        dto.setTransactionAt(txn.getTransactionAt());
        dto.setCreatedAt(txn.getCreatedAt());
        return dto;
    }

    // ==========================================
    // Methods cho Manager Controllers
    // ==========================================

    @Override
    public java.util.List<Payment> getAllPaymentEntities() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentEntityById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public java.util.List<Payment> findPaymentsByRequestIdEntities(Long requestId) {
        return paymentRepository.findByRequestId(requestId);
    }

    @Override
    public Payment savePaymentEntity(Payment payment) {
        return paymentRepository.save(payment);
    }

    // ==========================================
    // Methods cho Customer Controllers
    // ==========================================

    @Override
    public java.util.List<Payment> findByRequestSenderIdEntities(Long senderId) {
        return paymentRepository.findByRequestSenderId(senderId);
    }

    @Override
    public java.math.BigDecimal sumExpectedAmountByRequestId(Long requestId) {
        java.math.BigDecimal result = paymentRepository.sumExpectedAmountByRequestId(requestId);
        return result != null ? result : java.math.BigDecimal.ZERO;
    }

    @Override
    public java.math.BigDecimal sumPaidAmountByRequestId(Long requestId) {
        java.math.BigDecimal result = paymentRepository.sumPaidAmountByRequestId(requestId);
        return result != null ? result : java.math.BigDecimal.ZERO;
    }

    @Override
    public Long countByRequestId(Long requestId) {
        return paymentRepository.countByRequestId(requestId);
    }

    @Override
    public Long countPaidByRequestId(Long requestId) {
        return paymentRepository.countPaidByRequestId(requestId);
    }

    @Override
    public Long countUnpaidByRequestId(Long requestId) {
        return paymentRepository.countUnpaidByRequestId(requestId);
    }

    @Override
    public Long countPartiallyPaidByRequestId(Long requestId) {
        return paymentRepository.countPartiallyPaidByRequestId(requestId);
    }

    @Override
    public Long countShippingFeeByRequestId(Long requestId) {
        return paymentRepository.countShippingFeeByRequestId(requestId);
    }

    @Override
    public Long countCodByRequestId(Long requestId) {
        return paymentRepository.countCodByRequestId(requestId);
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Payment> findPaymentsByCustomerIdEntities(Long customerId) {
        // Lấy payments mà customer phải trả (dựa trên payerType)
        java.util.List<vn.DucBackend.Entities.Payment> relevantPayments = new java.util.ArrayList<>();

        // Lấy requests mà customer là SENDER -> chỉ lấy payments có payerType = SENDER
        java.util.List<vn.DucBackend.Entities.CustomerRequest> senderRequests = requestRepository
                .findBySenderId(customerId);
        for (vn.DucBackend.Entities.CustomerRequest req : senderRequests) {
            java.util.List<vn.DucBackend.Entities.Payment> payments = paymentRepository.findByRequestId(req.getId());
            for (vn.DucBackend.Entities.Payment p : payments) {
                if (p.getPayerType() == vn.DucBackend.Entities.Payment.PayerType.SENDER) {
                    relevantPayments.add(p);
                }
            }
        }

        // Lấy requests mà customer là RECEIVER -> chỉ lấy payments có payerType =
        // RECEIVER
        java.util.List<vn.DucBackend.Entities.CustomerRequest> receiverRequests = requestRepository
                .findByReceiverId(customerId);
        java.util.Set<Long> existingPaymentIds = relevantPayments.stream()
                .map(vn.DucBackend.Entities.Payment::getId)
                .collect(java.util.stream.Collectors.toSet());

        for (vn.DucBackend.Entities.CustomerRequest req : receiverRequests) {
            java.util.List<vn.DucBackend.Entities.Payment> payments = paymentRepository.findByRequestId(req.getId());
            for (vn.DucBackend.Entities.Payment p : payments) {
                if (p.getPayerType() == vn.DucBackend.Entities.Payment.PayerType.RECEIVER
                        && !existingPaymentIds.contains(p.getId())) {
                    relevantPayments.add(p);
                }
            }
        }

        return relevantPayments;
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Payment> searchByRequestIdAndKeyword(Long requestId, String keyword) {
        return paymentRepository.searchByRequestIdAndKeyword(requestId, keyword);
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Payment> findByRequestIdAndStatusEntities(Long requestId,
            String status) {
        vn.DucBackend.Entities.Payment.PaymentStatus paymentStatus = vn.DucBackend.Entities.Payment.PaymentStatus
                .valueOf(status);
        return paymentRepository.findByRequestIdAndStatus(requestId, paymentStatus);
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Payment> findByRequestIdAndTypeEntities(Long requestId, String type) {
        vn.DucBackend.Entities.Payment.PaymentType paymentType = vn.DucBackend.Entities.Payment.PaymentType
                .valueOf(type);
        return paymentRepository.findByRequestIdAndPaymentType(requestId, paymentType);
    }

    @Override
    public java.util.List<vn.DucBackend.Entities.Payment> findByRequestIdAndScopeEntities(Long requestId,
            String scope) {
        vn.DucBackend.Entities.Payment.PaymentScope paymentScope = vn.DucBackend.Entities.Payment.PaymentScope
                .valueOf(scope);
        return paymentRepository.findByRequestIdAndScope(requestId, paymentScope);
    }
}
