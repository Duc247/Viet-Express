package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_request")
@Getter
@Setter
public class CustomerRequest {

    public enum RequestStatus {
        PENDING,
        CONFIRMED,
        PICKUP_ASSIGNED,
        PICKED_UP,
        IN_WAREHOUSE,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        RETURNED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @Column(name = "expected_pickup_time")
    private LocalDateTime expectedPickupTime;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "image_order")
    private String imageOrder;

    // Pickup snapshot
    @Column(name = "pickup_contact_name")
    private String pickupContactName;

    @Column(name = "pickup_contact_phone")
    private String pickupContactPhone;

    @Column(name = "pickup_address_detail", nullable = false)
    private String pickupAddressDetail;

    @Column(name = "pickup_ward")
    private String pickupWard;

    @Column(name = "pickup_district")
    private String pickupDistrict;

    @Column(name = "pickup_province")
    private String pickupProvince;

    // Delivery snapshot
    @Column(name = "delivery_contact_name")
    private String deliveryContactName;

    @Column(name = "delivery_contact_phone")
    private String deliveryContactPhone;

    @Column(name = "delivery_address_detail", nullable = false)
    private String deliveryAddressDetail;

    @Column(name = "delivery_ward")
    private String deliveryWard;

    @Column(name = "delivery_district")
    private String deliveryDistrict;

    @Column(name = "delivery_province")
    private String deliveryProvince;

    // Parcel dimensions
    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(name = "length", precision = 10, scale = 2)
    private BigDecimal length;

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    @Column(name = "cod_amount", precision = 15, scale = 2)
    private BigDecimal codAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_warehouse_id")
    private Warehouse currentWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RequestStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
