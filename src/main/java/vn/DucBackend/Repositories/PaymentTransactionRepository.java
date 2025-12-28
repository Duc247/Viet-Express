package vn.DucBackend.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.PaymentTransaction;
import vn.DucBackend.Entities.PaymentTransaction.PaymentMethod;

/**
 * Repository cho PaymentTransaction entity - Quản lý giao dịch thanh toán
 * Phục vụ: ghi nhận từng giao dịch thanh toán, tra cứu lịch sử giao dịch
 */
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    // ==================== LỌC THEO PAYMENT ====================

    /** Lấy tất cả giao dịch của một payment */
    List<PaymentTransaction> findAllByPayment_Id(Long paymentId);

    Page<PaymentTransaction> findAllByPayment_Id(Long paymentId, Pageable pageable);

    /** Lấy giao dịch mới nhất của một payment */
    Optional<PaymentTransaction> findTop1ByPayment_IdOrderByCreatedAtDesc(Long paymentId);

    /** Đếm số giao dịch của payment */
    Long countByPayment_Id(Long paymentId);

    // ==================== LỌC THEO PHƯƠNG THỨC THANH TOÁN ====================

    /**
     * Lấy danh sách giao dịch theo phương thức (CASH, BANK_TRANSFER, VNPAY, MOMO)
     */
    Page<PaymentTransaction> findAllByMethod(PaymentMethod method, Pageable pageable);

    List<PaymentTransaction> findAllByMethod(PaymentMethod method);

    /** Đếm số giao dịch theo phương thức */
    Long countByMethod(PaymentMethod method);

    // ==================== TÌM KIẾM THEO GATEWAY TRANSACTION ====================

    /** Tìm giao dịch theo mã giao dịch từ cổng thanh toán */
    Optional<PaymentTransaction> findByGatewayTransactionId(String gatewayTransactionId);

    /** Kiểm tra mã giao dịch gateway đã tồn tại */
    Boolean existsByGatewayTransactionId(String gatewayTransactionId);

    // ==================== LỌC THEO THỜI GIAN ====================

    /** Lấy giao dịch trong khoảng thời gian */
    Page<PaymentTransaction> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<PaymentTransaction> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    /** Lấy giao dịch theo phương thức trong khoảng thời gian */
    Page<PaymentTransaction> findAllByMethodAndCreatedAtBetween(PaymentMethod method, LocalDateTime from,
            LocalDateTime to, Pageable pageable);

    /** Đếm giao dịch trong khoảng thời gian */
    Long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
