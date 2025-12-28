package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.PaymentDTO;
import vn.DucBackend.DTO.PaymentTransactionDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface cho Payment - Quản lý thanh toán
 */
public interface PaymentService {

    // ==================== PAYMENT ====================

    /** Tạo payment mới cho vận đơn */
    PaymentDTO createPayment(PaymentDTO paymentDTO);

    /** Cập nhật thông tin payment */
    PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO);

    /** Cập nhật trạng thái payment */
    PaymentDTO updateStatus(Long id, String status);

    /** Tìm payment theo ID */
    Optional<PaymentDTO> findById(Long id);

    /** Lấy tất cả payments của vận đơn */
    List<PaymentDTO> findAllByRequestId(Long requestId);

    /** Lấy payments theo loại */
    Page<PaymentDTO> findAllByType(String type, Pageable pageable);

    /** Lấy payments theo trạng thái */
    Page<PaymentDTO> findAllByStatus(String status, Pageable pageable);

    /** Tính tổng số tiền cần thanh toán của vận đơn */
    BigDecimal getTotalAmountByRequestId(Long requestId);

    /** Tính tổng số tiền đã thanh toán của vận đơn */
    BigDecimal getPaidAmountByRequestId(Long requestId);

    // ==================== PAYMENT TRANSACTION ====================

    /** Thêm giao dịch thanh toán */
    PaymentTransactionDTO addTransaction(PaymentTransactionDTO transactionDTO);

    /** Lấy tất cả giao dịch của payment */
    List<PaymentTransactionDTO> findAllTransactionsByPaymentId(Long paymentId);

    /** Lấy giao dịch mới nhất của payment */
    Optional<PaymentTransactionDTO> findLatestTransactionByPaymentId(Long paymentId);

    /** Tìm giao dịch theo gateway transaction ID */
    Optional<PaymentTransactionDTO> findTransactionByGatewayId(String gatewayTransactionId);
}
