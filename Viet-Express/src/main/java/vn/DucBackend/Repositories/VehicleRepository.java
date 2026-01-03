package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Vehicle;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);

    List<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType);

    List<Vehicle> findByCurrentLocationId(Long locationId);

    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE'")
    List<Vehicle> findAvailableVehicles();

    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE' AND v.vehicleType = :type")
    List<Vehicle> findAvailableVehiclesByType(@Param("type") Vehicle.VehicleType type);

    @Query("SELECT v FROM Vehicle v WHERE v.licensePlate LIKE %:keyword%")
    List<Vehicle> searchByLicensePlate(@Param("keyword") String keyword);

    boolean existsByLicensePlate(String licensePlate);
}
