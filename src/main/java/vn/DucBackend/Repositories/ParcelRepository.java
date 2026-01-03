package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Parcel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    Optional<Parcel> findByParcelCode(String parcelCode);

    List<Parcel> findByRequestId(Long requestId);

    List<Parcel> findByStatus(Parcel.ParcelStatus status);

    List<Parcel> findByCurrentShipperId(Long shipperId);

    List<Parcel> findByCurrentTripId(Long tripId);

    List<Parcel> findByCurrentLocationId(Long locationId);

    @Query("SELECT p FROM Parcel p WHERE p.request.id = :requestId ORDER BY p.createdAt")
    List<Parcel> findByRequestIdOrderByCreatedAt(@Param("requestId") Long requestId);

    @Query("SELECT COUNT(p) FROM Parcel p WHERE p.request.id = :requestId")
    Long countByRequestId(@Param("requestId") Long requestId);

    @Query("SELECT p FROM Parcel p WHERE p.currentShipper.id = :shipperId AND p.status IN ('PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY')")
    List<Parcel> findActiveParcelsByShipper(@Param("shipperId") Long shipperId);

    boolean existsByParcelCode(String parcelCode);
}
