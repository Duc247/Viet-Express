package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
public class PaymentTransaction {

    public enum TransactionType {
        IN, // Nạp tiền vào payment
        OUT, // Rút tiền từ payment
        STATUS_CHANGE // Thay đổi trạng thái payment (không liên quan đến tiền)
    }

    public enum PaymentMethod {
        CASH, VNPAY, MOMO, BANK_TRANSFER
    }

    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED
    }

    // --- Fields cho Status Change History ---
    @Enumerated(EnumType.STRING)
    @Column(name = "old_payment_status", length = 30)
    private Payment.PaymentStatus oldPaymentStatus; // Status trước khi thay đổi (null nếu tạo mới)

    @Enumerated(EnumType.STRING)
    @Column(name = "new_payment_status", length = 30)
    private Payment.PaymentStatus newPaymentStatus; // Status sau khi thay đổi

    @Column(name = "actor_type", length = 20)
    private String actorType; // MANAGER, SHIPPER, CUSTOMER, SYSTEM
    // --- End Status Change History fields ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 10)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "transaction_at")
    private LocalDateTime transactionAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionAt == null) {
            transactionAt = LocalDateTime.now();
        }
        if (status == null) {
            status = TransactionStatus.PENDING;
        }
    }
}
