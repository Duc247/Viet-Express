package vn.DucBackend.Repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Payment;
import vn.DucBackend.Entities.Payment.PaymentStatus;
import vn.DucBackend.Entities.Payment.PaymentType;

/**
 * Repository cho Payment entity - Quản lý thanh toán
 * Phục vụ: thanh toán phí vận chuyển, quản lý COD, theo dõi trạng thái thanh
 * toán
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ==================== LỌC THEO VẬN ĐƠN ====================

    /** Lấy tất cả payment của một vận đơn */
    List<Payment> findAllByRequest_Id(Long requestId);

    /** Lấy payment của vận đơn theo loại */
    List<Payment> findAllByRequest_IdAndType(Long requestId, PaymentType type);

    // ==================== LỌC THEO TRẠNG THÁI ====================

    /** Lấy danh sách payment theo trạng thái (UNPAID, PARTIAL, PAID, REMITTED) */
    Page<Payment> findAllByStatus(PaymentStatus status, Pageable pageable);

    List<Payment> findAllByStatus(PaymentStatus status);

    /** Đếm số payment theo trạng thái */
    Long countByStatus(PaymentStatus status);

    // ==================== LỌC THEO LOẠI THANH TOÁN ====================

    /** Lấy danh sách payment theo loại (SHIPPING_FEE, COD) */
    Page<Payment> findAllByType(PaymentType type, Pageable pageable);

    List<Payment> findAllByType(PaymentType type);

    /** Đếm số payment theo loại */
    Long countByType(PaymentType type);

    /** Lấy payment theo loại và trạng thái */
    Page<Payment> findAllByTypeAndStatus(PaymentType type, PaymentStatus status, Pageable pageable);

    // ==================== LỌC THEO NGƯỜI TRẢ TIỀN ====================

    /** Lấy payment mà customer đã thanh toán */
    Page<Payment> findAllByPayerCustomer_Id(Long payerCustomerId, Pageable pageable);

    List<Payment> findAllByPayerCustomer_Id(Long payerCustomerId);

    // ==================== LỌC THEO THỜI GIAN ====================

    /** Lấy payment theo thời gian tạo */
    Page<Payment> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Payment> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // ==================== TÍNH TỔNG TIỀN (CUSTOM QUERY) ====================

    /** Tính tổng số tiền thanh toán của một vận đơn */
    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p WHERE p.request.id = :requestId")
    BigDecimal sumTotalAmountByRequest_Id(@Param("requestId") Long requestId);

    /** Tính tổng số tiền đã thanh toán của một vận đơn */
    @Query("SELECT COALESCE(SUM(p.paidAmount), 0) FROM Payment p WHERE p.request.id = :requestId")
    BigDecimal sumPaidAmountByRequest_Id(@Param("requestId") Long requestId);

    /** Tính tổng doanh thu theo loại và trạng thái trong khoảng thời gian */
    @Query("SELECT COALESCE(SUM(p.paidAmount), 0) FROM Payment p WHERE p.type = :type AND p.status = :status AND p.createdAt BETWEEN :from AND :to")
    BigDecimal sumPaidAmountByTypeAndStatusAndCreatedAtBetween(
            @Param("type") PaymentType type,
            @Param("status") PaymentStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
