package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse {

    public enum WarehouseType {
        HUB, DISTRIBUTION_CENTER, PICKUP_POINT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long id;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "ward")
    private String ward;

    @Column(name = "district")
    private String district;

    @Column(name = "province")
    private String province;

    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_type", length = 30)
    private WarehouseType warehouseType;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
