package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Payment;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

        Optional<Payment> findByPaymentCode(String paymentCode);

        @Query("SELECT p FROM Payment p WHERE p.request.id = :requestId")
        List<Payment> findByRequestId(@Param("requestId") Long requestId);

        List<Payment> findByParcelId(Long parcelId);

        List<Payment> findByStatus(Payment.PaymentStatus status);

        List<Payment> findByPaymentType(Payment.PaymentType paymentType);

        @Query("SELECT p FROM Payment p WHERE p.request.id = :requestId AND p.paymentType = :type")
        Optional<Payment> findByRequestIdAndType(@Param("requestId") Long requestId,
                        @Param("type") Payment.PaymentType type);

        @Query("SELECT p FROM Payment p WHERE p.status IN ('UNPAID', 'PARTIALLY_PAID')")
        List<Payment> findUnpaidPayments();

        @Query("SELECT SUM(p.expectedAmount) FROM Payment p WHERE p.request.id = :requestId")
        java.math.BigDecimal sumExpectedAmountByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT SUM(p.paidAmount) FROM Payment p WHERE p.request.id = :requestId")
        java.math.BigDecimal sumPaidAmountByRequestId(@Param("requestId") Long requestId);

        boolean existsByPaymentCode(String paymentCode);

        // Count payments by request
        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId")
        Long countByRequestId(@Param("requestId") Long requestId);

        // Count by status for a request
        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId AND p.status = 'PAID'")
        Long countPaidByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId AND p.status = 'UNPAID'")
        Long countUnpaidByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId AND p.status = 'PARTIALLY_PAID'")
        Long countPartiallyPaidByRequestId(@Param("requestId") Long requestId);

        // Count by type for a request
        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId AND p.paymentType = 'SHIPPING_FEE'")
        Long countShippingFeeByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId AND p.paymentType = 'COD'")
        Long countCodByRequestId(@Param("requestId") Long requestId);

        // Search payments
        @Query("SELECT p FROM Payment p WHERE p.request.id = :requestId AND " +
                        "(LOWER(p.paymentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
<<<<<<< Updated upstream
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.request.requestCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "(p.trip IS NOT NULL AND CAST(p.trip.id AS string) LIKE CONCAT('%', :keyword, '%')))")
        List<Payment> searchByRequestIdAndKeyword(@Param("requestId") Long requestId, @Param("keyword") String keyword);
        
        // Search payments by request code or trip code (for customer payments page)
        @Query("SELECT p FROM Payment p WHERE p.request.sender.id = :customerId OR p.request.receiver.id = :customerId " +
                        "AND (LOWER(p.request.requestCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.paymentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "(p.trip IS NOT NULL AND CAST(p.trip.id AS string) LIKE CONCAT('%', :keyword, '%')))")
        List<Payment> searchByCustomerIdAndKeyword(@Param("customerId") Long customerId, @Param("keyword") String keyword);
=======
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        List<Payment> searchByRequestIdAndKeyword(@Param("requestId") Long requestId, @Param("keyword") String keyword);
>>>>>>> Stashed changes

        // Filter by status
        @Query("SELECT p FROM Payment p WHERE p.request.id = :requestId AND p.status = :status")
        List<Payment> findByRequestIdAndStatus(@Param("requestId") Long requestId,
                        @Param("status") Payment.PaymentStatus status);

        // Filter by type
        @Query("SELECT p FROM Payment p WHERE p.request.id = :requestId AND p.paymentType = :paymentType")
        List<Payment> findByRequestIdAndPaymentType(@Param("requestId") Long requestId,
                        @Param("paymentType") Payment.PaymentType paymentType);

        // Filter by scope
        @Query("SELECT p FROM Payment p WHERE p.request.id = :requestId AND p.paymentScope = :scope")
        List<Payment> findByRequestIdAndScope(@Param("requestId") Long requestId,
                        @Param("scope") Payment.PaymentScope scope);

        // Filter by trip_id
        @Query("SELECT p FROM Payment p WHERE p.request.id = :requestId AND p.trip.id = :tripId")
        List<Payment> findByRequestIdAndTripId(@Param("requestId") Long requestId, @Param("tripId") Long tripId);

        // Count by scope
        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId AND p.paymentScope = 'FULL_REQUEST'")
        Long countFullRequestByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT COUNT(p) FROM Payment p WHERE p.request.id = :requestId AND p.paymentScope = 'PER_TRIP'")
        Long countPerTripByRequestId(@Param("requestId") Long requestId);
<<<<<<< Updated upstream

        // Find payments by customer ID (sender) - Fix lazy loading issue
        @Query("SELECT p FROM Payment p " +
               "LEFT JOIN FETCH p.request r " +
               "LEFT JOIN FETCH r.sender " +
               "WHERE r.sender.id = :customerId")
        List<Payment> findByRequestSenderId(@Param("customerId") Long customerId);
=======
>>>>>>> Stashed changes
}
