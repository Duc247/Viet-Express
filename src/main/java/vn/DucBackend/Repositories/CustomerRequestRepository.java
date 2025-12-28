package vn.DucBackend.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Entities.CustomerRequest.RequestStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {

    // Find by ID and Customer ID
    Optional<CustomerRequest> findByIdAndCustomer_Id(Long id, Long customerId);

    // Find by tracking code (via TrackingCode entity relationship)
    @Query("SELECT cr FROM CustomerRequest cr JOIN TrackingCode tc ON tc.request = cr WHERE tc.code = :trackingCode")
    Optional<CustomerRequest> findByTrackingCode(@Param("trackingCode") String trackingCode);

    // Find all by Customer ID with pagination
    Page<CustomerRequest> findAllByCustomer_Id(Long customerId, Pageable pageable);

    // Find all by Status with pagination
    Page<CustomerRequest> findAllByStatus(RequestStatus status, Pageable pageable);

    // Find all by Warehouse ID and Status with pagination
    Page<CustomerRequest> findAllByCurrentWarehouse_IdAndStatus(Long warehouseId, RequestStatus status,
            Pageable pageable);

    // Find all by created date range with pagination
    Page<CustomerRequest> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Count by Status
    Long countByStatus(RequestStatus status);

    // Count by Warehouse ID and Status
    Long countByCurrentWarehouse_IdAndStatus(Long warehouseId, RequestStatus status);
}
