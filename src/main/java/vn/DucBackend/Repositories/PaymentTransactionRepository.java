package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.PaymentTransaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

        List<PaymentTransaction> findByPaymentId(Long paymentId);

        List<PaymentTransaction> findByStatus(PaymentTransaction.TransactionStatus status);

        List<PaymentTransaction> findByTransactionType(PaymentTransaction.TransactionType transactionType);

        List<PaymentTransaction> findByPaymentMethod(PaymentTransaction.PaymentMethod paymentMethod);

        List<PaymentTransaction> findByPerformedById(Long userId);

        @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId ORDER BY pt.transactionAt DESC")
        List<PaymentTransaction> findByPaymentIdOrderByTransactionAtDesc(@Param("paymentId") Long paymentId);

        @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.transactionAt BETWEEN :startDate AND :endDate")
        List<PaymentTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT SUM(pt.amount) FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId AND pt.status = 'SUCCESS' AND pt.transactionType = 'IN'")
        java.math.BigDecimal sumSuccessfulPayments(@Param("paymentId") Long paymentId);

        @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.transactionRef = :ref")
        java.util.Optional<PaymentTransaction> findByTransactionRef(@Param("ref") String transactionRef);

        @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId " +
                        "AND (LOWER(pt.transactionRef) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(pt.gatewayResponse) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "ORDER BY pt.transactionAt DESC")
        List<PaymentTransaction> findByPaymentIdAndKeyword(@Param("paymentId") Long paymentId,
                        @Param("keyword") String keyword);

        // --- Queries cho Status Change History ---

        /**
         * Lấy lịch sử thay đổi status của một payment, sắp xếp mới nhất trước
         */
        @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId " +
                        "AND pt.transactionType = 'STATUS_CHANGE' ORDER BY pt.transactionAt DESC")
        List<PaymentTransaction> findStatusHistoryByPaymentId(@Param("paymentId") Long paymentId);

        /**
         * Lấy lịch sử thay đổi status của một payment, sắp xếp cũ nhất trước
         */
        @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId " +
                        "AND pt.transactionType = 'STATUS_CHANGE' ORDER BY pt.transactionAt ASC")
        List<PaymentTransaction> findStatusHistoryByPaymentIdAsc(@Param("paymentId") Long paymentId);

        /**
         * Đếm số lần thay đổi status của một payment
         */
        @Query("SELECT COUNT(pt) FROM PaymentTransaction pt WHERE pt.payment.id = :paymentId " +
                        "AND pt.transactionType = 'STATUS_CHANGE'")
        Long countStatusChangesByPaymentId(@Param("paymentId") Long paymentId);
}
