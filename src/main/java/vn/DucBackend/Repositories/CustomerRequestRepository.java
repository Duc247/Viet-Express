package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.CustomerRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {

    Optional<CustomerRequest> findByRequestCode(String requestCode);

    List<CustomerRequest> findBySenderId(Long senderId);

    List<CustomerRequest> findByReceiverId(Long receiverId);

    List<CustomerRequest> findByStatus(CustomerRequest.RequestStatus status);

    List<CustomerRequest> findByServiceTypeId(Long serviceTypeId);

    @Query("SELECT r FROM CustomerRequest r WHERE r.sender.id = :customerId OR r.receiver.id = :customerId")
    List<CustomerRequest> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT r FROM CustomerRequest r WHERE r.status NOT IN ('DELIVERED', 'CANCELLED', 'RETURNED')")
    List<CustomerRequest> findActiveRequests();

    @Query("SELECT r FROM CustomerRequest r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<CustomerRequest> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM CustomerRequest r WHERE r.requestCode LIKE %:keyword% OR r.parcelDescription LIKE %:keyword%")
    List<CustomerRequest> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(r) FROM CustomerRequest r WHERE r.status = :status")
    Long countByStatus(@Param("status") CustomerRequest.RequestStatus status);

    boolean existsByRequestCode(String requestCode);
}
