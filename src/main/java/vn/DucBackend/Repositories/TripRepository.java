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

<<<<<<< Updated upstream
        @Query("SELECT t FROM Trip t WHERE t.status NOT IN ('COMPLETED', 'CANCELLED')")
        List<Trip> findActiveTrips();

        @Query("SELECT t FROM Trip t WHERE t.createdAt BETWEEN :startDate AND :endDate")
        List<Trip> findByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = :status")
        Long countByStatus(@Param("status") Trip.TripStatus status);

=======
        @Query("SELECT t FROM Trip t WHERE t.shipper.id = :shipperId AND t.status IN ('COMPLETED', 'CANCELLED') ORDER BY t.createdAt DESC")
        List<Trip> findCompletedTripsByShipper(@Param("shipperId") Long shipperId);

        @Query("SELECT t FROM Trip t WHERE t.status NOT IN ('COMPLETED', 'CANCELLED')")
        List<Trip> findActiveTrips();

        @Query("SELECT t FROM Trip t WHERE t.createdAt BETWEEN :startDate AND :endDate")
        List<Trip> findByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = :status")
        Long countByStatus(@Param("status") Trip.TripStatus status);

>>>>>>> Stashed changes
        @Query("SELECT t FROM Trip t WHERE t.shipper.id = :shipperId ORDER BY t.createdAt DESC")
        List<Trip> findByShipperIdOrderByCreatedAtDesc(@Param("shipperId") Long shipperId);

        // Lấy tất cả trips của một request
        @Query("SELECT t FROM Trip t WHERE t.request.id = :requestId ORDER BY t.createdAt DESC")
        List<Trip> findTripsByRequestId(@Param("requestId") Long requestId);

        // Count trips by status for a request
        @Query("SELECT COUNT(t) FROM Trip t WHERE t.request.id = :requestId")
        Long countTripsByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT COUNT(t) FROM Trip t WHERE t.request.id = :requestId AND t.status = 'COMPLETED'")
        Long countCompletedTripsByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT COUNT(t) FROM Trip t WHERE t.request.id = :requestId AND t.status = 'IN_PROGRESS'")
        Long countInProgressTripsByRequestId(@Param("requestId") Long requestId);

        @Query("SELECT COUNT(t) FROM Trip t WHERE t.request.id = :requestId AND t.status = 'CREATED'")
        Long countCreatedTripsByRequestId(@Param("requestId") Long requestId);

        // Search trips by description or route
        @Query("SELECT t FROM Trip t WHERE t.request.id = :requestId AND " +
                        "(LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(t.note) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(t.startLocation.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(t.endLocation.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY t.createdAt DESC")
        List<Trip> searchTripsByRequestIdAndKeyword(@Param("requestId") Long requestId,
                        @Param("keyword") String keyword);

        // Filter by status
        @Query("SELECT t FROM Trip t WHERE t.request.id = :requestId AND t.status = :status ORDER BY t.createdAt DESC")
        List<Trip> findTripsByRequestIdAndStatus(@Param("requestId") Long requestId,
                        @Param("status") Trip.TripStatus status);

        // Filter by trip type
        @Query("SELECT t FROM Trip t WHERE t.request.id = :requestId AND t.tripType = :tripType ORDER BY t.createdAt DESC")
        List<Trip> findTripsByRequestIdAndType(@Param("requestId") Long requestId,
                        @Param("tripType") Trip.TripType tripType);
}
