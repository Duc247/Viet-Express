package vn.DucBackend.Services;

import vn.DucBackend.DTO.PaymentDTO;
import vn.DucBackend.DTO.PaymentTransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    // Payment operations
    List<PaymentDTO> findAllPayments();

    Optional<PaymentDTO> findPaymentById(Long id);

    Optional<PaymentDTO> findByPaymentCode(String paymentCode);

    List<PaymentDTO> findPaymentsByRequestId(Long requestId);

    List<PaymentDTO> findPaymentsByStatus(String status);

    List<PaymentDTO> findUnpaidPayments();

    PaymentDTO createPayment(PaymentDTO dto);

    PaymentDTO updatePayment(Long id, PaymentDTO dto);

    PaymentDTO updatePaymentStatus(Long id, String status);

    void deletePayment(Long id);

    BigDecimal getTotalExpectedAmount(Long requestId);

    BigDecimal getTotalPaidAmount(Long requestId);

    // Payment Transaction operations
    List<PaymentTransactionDTO> findTransactionsByPaymentId(Long paymentId);

    List<PaymentTransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Optional<PaymentTransactionDTO> findTransactionById(Long id);

    Optional<PaymentTransactionDTO> findByTransactionRef(String transactionRef);

    PaymentTransactionDTO createTransaction(PaymentTransactionDTO dto);

    PaymentTransactionDTO recordCashPayment(Long paymentId, BigDecimal amount, Long performedById);

    PaymentTransactionDTO recordOnlinePayment(Long paymentId, BigDecimal amount, String method, String transactionRef);

    PaymentTransactionDTO updateTransactionStatus(Long id, String status);

    BigDecimal getSuccessfulPaymentTotal(Long paymentId);

    String generatePaymentCode(Long requestId);
}
