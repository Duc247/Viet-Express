package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByIsActiveTrue();

    List<Location> findByLocationType(Location.LocationType locationType);

    List<Location> findByLocationTypeAndIsActiveTrue(Location.LocationType locationType);

    Optional<Location> findByWarehouseCode(String warehouseCode);

    @Query("SELECT l FROM Location l WHERE l.locationType = :locationType AND (l.isActive = true OR l.isActive IS NULL)")
    List<Location> findWarehousesByType(@Param("locationType") Location.LocationType locationType);

    @Query("SELECT l FROM Location l WHERE l.name LIKE %:keyword% OR l.addressText LIKE %:keyword%")
    List<Location> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByWarehouseCode(String warehouseCode);
}
