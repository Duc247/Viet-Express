package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Shipper;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Long> {

    Optional<Shipper> findByUserId(Long userId);

    List<Shipper> findByIsActiveTrue();

    List<Shipper> findByIsAvailableTrue();

    List<Shipper> findByWorkingArea(String workingArea);

    List<Shipper> findByCurrentLocationId(Long locationId);

    @Query("SELECT s FROM Shipper s WHERE s.isActive = true AND s.isAvailable = true")
    List<Shipper> findAvailableShippers();

    @Query("SELECT s FROM Shipper s WHERE s.isActive = true AND s.isAvailable = true AND s.workingArea = :area")
    List<Shipper> findAvailableShippersByArea(@Param("area") String area);

    @Query("SELECT s FROM Shipper s WHERE s.fullName LIKE %:keyword% OR s.phone LIKE %:keyword%")
    List<Shipper> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByUserId(Long userId);
}