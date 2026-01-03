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

    List<Payment> findByRequestId(Long requestId);

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
}
