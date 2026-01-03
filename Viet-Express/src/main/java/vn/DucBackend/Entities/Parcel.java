package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parcels")
@Getter
@Setter
public class Parcel {

    public enum ParcelStatus {
        CREATED,
        PICKED_UP,
        IN_WAREHOUSE,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        FAILED,
        RETURNED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcel_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private CustomerRequest request;

    @Column(name = "parcel_code", unique = true, nullable = false, length = 50)
    private String parcelCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cod_amount", precision = 15, scale = 2)
    private BigDecimal codAmount;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "length_cm", precision = 10, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", precision = 10, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "height_cm", precision = 10, scale = 2)
    private BigDecimal heightCm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_location_id")
    private Location currentLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_shipper_id")
    private Shipper currentShipper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_trip_id")
    private Trip currentTrip;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ParcelStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ParcelStatus.CREATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
