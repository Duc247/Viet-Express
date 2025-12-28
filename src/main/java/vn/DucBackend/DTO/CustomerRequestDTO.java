package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho CustomerRequest entity (Vận đơn)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    private Long id;
    private Long customerId;
    private Long serviceTypeId;
    private String serviceTypeName;
    private LocalDateTime expectedPickupTime;
    private String note;
    private String imageOrder;

    // Pickup snapshot
    private String pickupContactName;
    private String pickupContactPhone;
    private String pickupAddressDetail;
    private String pickupWard;
    private String pickupDistrict;
    private String pickupProvince;

    // Delivery snapshot
    private String deliveryContactName;
    private String deliveryContactPhone;
    private String deliveryAddressDetail;
    private String deliveryWard;
    private String deliveryDistrict;
    private String deliveryProvince;

    // Parcel dimensions
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal codAmount;

    private Long currentWarehouseId;
    private String currentWarehouseName;
    private String status; // RequestStatus enum value
    private LocalDateTime createdAt;

    // Tracking code (from TrackingCode entity)
    private String trackingCode;
}
