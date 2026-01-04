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

    // ==========================================
    // Methods cho Manager Controllers
    // ==========================================

    /** Lấy tất cả Payment entities */
    java.util.List<vn.DucBackend.Entities.Payment> getAllPaymentEntities();

    /** Lấy Payment entity theo ID */
    vn.DucBackend.Entities.Payment getPaymentEntityById(Long id);

    /** Tìm payments theo request ID - trả về entities */
    java.util.List<vn.DucBackend.Entities.Payment> findPaymentsByRequestIdEntities(Long requestId);

    /** Lưu Payment entity */
    vn.DucBackend.Entities.Payment savePaymentEntity(vn.DucBackend.Entities.Payment payment);

    // ==========================================
    // Methods cho Customer Controllers
    // ==========================================

    /** Tìm payments theo senderId - trả về entities */
    java.util.List<vn.DucBackend.Entities.Payment> findByRequestSenderIdEntities(Long senderId);

    /** Tính tổng expected amount theo request ID */
    java.math.BigDecimal sumExpectedAmountByRequestId(Long requestId);

    /** Tính tổng paid amount theo request ID */
    java.math.BigDecimal sumPaidAmountByRequestId(Long requestId);

    /** Đếm payments theo request ID */
    Long countByRequestId(Long requestId);

    /** Đếm payments đã thanh toán theo request ID */
    Long countPaidByRequestId(Long requestId);

    /** Đếm payments chưa thanh toán theo request ID */
    Long countUnpaidByRequestId(Long requestId);

    /** Đếm payments thanh toán một phần theo request ID */
    Long countPartiallyPaidByRequestId(Long requestId);

    /** Đếm payments phí vận chuyển theo request ID */
    Long countShippingFeeByRequestId(Long requestId);

    /** Đếm payments COD theo request ID */
    Long countCodByRequestId(Long requestId);

    /** Tìm payments theo sender và receiver customer ID */
    java.util.List<vn.DucBackend.Entities.Payment> findPaymentsByCustomerIdEntities(Long customerId);

    /** Tìm payments theo request và keyword */
    java.util.List<vn.DucBackend.Entities.Payment> searchByRequestIdAndKeyword(Long requestId, String keyword);

    /** Tìm payments theo request và status - trả về entities */
    java.util.List<vn.DucBackend.Entities.Payment> findByRequestIdAndStatusEntities(Long requestId, String status);

    /** Tìm payments theo request và type - trả về entities */
    java.util.List<vn.DucBackend.Entities.Payment> findByRequestIdAndTypeEntities(Long requestId, String type);

    /** Tìm payments theo request và scope - trả về entities */
    java.util.List<vn.DucBackend.Entities.Payment> findByRequestIdAndScopeEntities(Long requestId, String scope);
}
