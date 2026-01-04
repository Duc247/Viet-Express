package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Shipper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import vn.DucBackend.Entities.User;

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

    // INSERT: JPQL không hỗ trợ INSERT, sử dụng method save() từ JpaRepository
    // Ví dụ: shipperRepository.save(new Shipper(...));

    // UPDATE shipper - sử dụng đúng tên entity (Shipper) và các field có trong
    // entity
    @Modifying
    @Query("UPDATE Shipper s SET s.fullName = :fullName, s.phone = :phone, s.workingArea = :workingArea, s.isAvailable = :isAvailable, s.updatedAt = :updatedAt WHERE s.user = :user")
    int updateShipper(@Param("user") User user, @Param("fullName") String fullName,
            @Param("phone") String phone, @Param("workingArea") String workingArea,
            @Param("isAvailable") Boolean isAvailable, @Param("updatedAt") LocalDateTime updatedAt);

    // DELETE shipper - sử dụng đúng tên entity (Shipper viết hoa)
    @Modifying
    @Query("DELETE FROM Shipper s WHERE s.user = :user")
    int deleteByUser(@Param("user") User user);

}