package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    public enum PaymentType {
        SHIPPING_FEE, COD, DEPOSIT
    }

    public enum PayerType {
        SENDER, RECEIVER, COMPANY
    }

    public enum ReceiverType {
        COMPANY, SENDER
    }

    public enum PaymentStatus {
        UNPAID, // Chưa thu
        PARTIALLY_PAID, // Đã thu một phần
        PAID, // Đã thanh toán (cho phí ship)
        COLLECTED_FROM_RECEIVER, // Đã thu COD từ người nhận (shipper đã thu)
        PAID_TO_SENDER, // Đã trả COD cho người gửi (manager xác nhận)
        REFUNDED // Đã hoàn tiền
    }

    /**
     * Phạm vi thanh toán:
     * FULL_REQUEST - Thanh toán toàn bộ cho cả đơn hàng (gắn với request_id,
     * trip_id = null)
     * PER_TRIP - Thanh toán theo từng chuyến (gắn với trip_id)
     */
    public enum PaymentScope {
        FULL_REQUEST, PER_TRIP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private CustomerRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(name = "payment_code", unique = true, length = 50)
    private String paymentCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payer_type", length = 20)
    private PayerType payerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type", length = 20)
    private ReceiverType receiverType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_scope", length = 20)
    private PaymentScope paymentScope;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "expected_amount", precision = 15, scale = 2)
    private BigDecimal expectedAmount;

    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = PaymentStatus.UNPAID;
        }
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
