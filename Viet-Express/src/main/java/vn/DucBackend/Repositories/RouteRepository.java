package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Route;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByIsActiveTrue();

    Optional<Route> findByFromLocationIdAndToLocationId(Long fromLocationId, Long toLocationId);

    List<Route> findByFromLocationId(Long fromLocationId);

    List<Route> findByToLocationId(Long toLocationId);

    @Query("SELECT r FROM Route r WHERE r.fromLocation.id = :locationId OR r.toLocation.id = :locationId")
    List<Route> findByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT r FROM Route r WHERE r.isActive = true AND (r.fromLocation.id = :fromId OR r.toLocation.id = :toId)")
    List<Route> findActiveRoutesByLocations(@Param("fromId") Long fromId, @Param("toId") Long toId);
}
