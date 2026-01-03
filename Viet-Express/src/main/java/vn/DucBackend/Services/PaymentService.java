package vn.DucBackend.Services;

import vn.DucBackend.DTO.PaymentDTO;
import vn.DucBackend.DTO.PaymentTransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Payment (Thanh toán) và PaymentTransaction (Giao
 * dịch thanh toán)
 * 
 * Repository sử dụng: PaymentRepository, PaymentTransactionRepository,
 * CustomerRequestRepository, StaffRepository
 * Controller sử dụng: AdminOperationController
 */
public interface PaymentService {

    // ==========================================
    // Payment operations
    // ==========================================

    /** Repository: paymentRepository.findAll() */
    List<PaymentDTO> findAllPayments();

    /** Repository: paymentRepository.findById() */
    Optional<PaymentDTO> findPaymentById(Long id);

    /** Repository: paymentRepository.findByPaymentCode() */
    Optional<PaymentDTO> findByPaymentCode(String paymentCode);

    /** Repository: paymentRepository.findByRequestId() */
    List<PaymentDTO> findPaymentsByRequestId(Long requestId);

    /** Repository: paymentRepository.findByStatus() */
    List<PaymentDTO> findPaymentsByStatus(String status);

    /** Repository: paymentRepository.findUnpaidPayments() */
    List<PaymentDTO> findUnpaidPayments();

    /**
     * Repository: paymentRepository.save(), customerRequestRepository.findById()
     */
    PaymentDTO createPayment(PaymentDTO dto);

    /** Repository: paymentRepository.findById(), paymentRepository.save() */
    PaymentDTO updatePayment(Long id, PaymentDTO dto);

    /** Repository: paymentRepository.findById(), paymentRepository.save() */
    PaymentDTO updatePaymentStatus(Long id, String status);

    /** Repository: paymentRepository.deleteById() */
    void deletePayment(Long id);

    /** Repository: paymentRepository.sumExpectedAmountByRequestId() */
    BigDecimal getTotalExpectedAmount(Long requestId);

    /** Repository: paymentRepository.sumPaidAmountByRequestId() */
    BigDecimal getTotalPaidAmount(Long requestId);

    // ==========================================
    // PaymentTransaction operations
    // ==========================================

    /** Repository: paymentTransactionRepository.findByPaymentId() */
    List<PaymentTransactionDTO> findTransactionsByPaymentId(Long paymentId);

    /** Repository: paymentTransactionRepository.findByCreatedAtBetween() */
    List<PaymentTransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /** Repository: paymentTransactionRepository.findById() */
    Optional<PaymentTransactionDTO> findTransactionById(Long id);

    /** Repository: paymentTransactionRepository.findByTransactionRef() */
    Optional<PaymentTransactionDTO> findByTransactionRef(String transactionRef);

    /**
     * Repository: paymentTransactionRepository.save(), paymentRepository.findById()
     */
    PaymentTransactionDTO createTransaction(PaymentTransactionDTO dto);

    /**
     * Repository: paymentTransactionRepository.save(),
     * paymentRepository.findById(), staffRepository.findById()
     */
    PaymentTransactionDTO recordCashPayment(Long paymentId, BigDecimal amount, Long performedById);

    /**
     * Repository: paymentTransactionRepository.save(), paymentRepository.findById()
     */
    PaymentTransactionDTO recordOnlinePayment(Long paymentId, BigDecimal amount, String method, String transactionRef);

    /**
     * Repository: paymentTransactionRepository.findById(),
     * paymentTransactionRepository.save()
     */
    PaymentTransactionDTO updateTransactionStatus(Long id, String status);

    /** Repository: paymentTransactionRepository.sumSuccessfulPayments() */
    BigDecimal getSuccessfulPaymentTotal(Long paymentId);

    /** Tạo mã thanh toán tự động */
    String generatePaymentCode(Long requestId);
}
