package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Trip;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByShipperId(Long shipperId);

    List<Trip> findByVehicleId(Long vehicleId);

    List<Trip> findByStatus(Trip.TripStatus status);

    List<Trip> findByTripType(Trip.TripType tripType);

    List<Trip> findByStartLocationId(Long locationId);

    List<Trip> findByEndLocationId(Long locationId);

    @Query("SELECT t FROM Trip t WHERE t.shipper.id = :shipperId AND t.status IN ('CREATED', 'IN_PROGRESS')")
    List<Trip> findActiveTripsByShipper(@Param("shipperId") Long shipperId);

    @Query("SELECT t FROM Trip t WHERE t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Trip> findActiveTrips();

    @Query("SELECT t FROM Trip t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Trip> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = :status")
    Long countByStatus(@Param("status") Trip.TripStatus status);

    @Query("SELECT t FROM Trip t WHERE t.shipper.id = :shipperId ORDER BY t.createdAt DESC")
    List<Trip> findByShipperIdOrderByCreatedAtDesc(@Param("shipperId") Long shipperId);
}
