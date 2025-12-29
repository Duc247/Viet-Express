package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.ParcelAction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParcelActionRepository extends JpaRepository<ParcelAction, Long> {

    List<ParcelAction> findByParcelId(Long parcelId);

    List<ParcelAction> findByRequestId(Long requestId);

    List<ParcelAction> findByActionTypeId(Long actionTypeId);

    List<ParcelAction> findByActorUserId(Long userId);

    @Query("SELECT pa FROM ParcelAction pa WHERE pa.parcel.id = :parcelId ORDER BY pa.createdAt DESC")
    List<ParcelAction> findByParcelIdOrderByCreatedAtDesc(@Param("parcelId") Long parcelId);

    @Query("SELECT pa FROM ParcelAction pa WHERE pa.request.id = :requestId ORDER BY pa.createdAt DESC")
    List<ParcelAction> findByRequestIdOrderByCreatedAtDesc(@Param("requestId") Long requestId);

    @Query("SELECT pa FROM ParcelAction pa WHERE pa.createdAt BETWEEN :startDate AND :endDate")
    List<ParcelAction> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT pa FROM ParcelAction pa WHERE pa.fromLocation.id = :locationId OR pa.toLocation.id = :locationId")
    List<ParcelAction> findByLocationId(@Param("locationId") Long locationId);
}