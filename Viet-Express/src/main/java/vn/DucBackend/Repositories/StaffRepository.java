package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByUserId(Long userId);

    List<Staff> findByIsActiveTrue();

    List<Staff> findByLocationId(Long locationId);

    @Query("SELECT s FROM Staff s WHERE s.isActive = true AND s.location.id = :locationId")
    List<Staff> findActiveStaffByLocation(@Param("locationId") Long locationId);

    @Query("SELECT s FROM Staff s WHERE s.fullName LIKE %:keyword% OR s.phone LIKE %:keyword% OR s.email LIKE %:keyword%")
    List<Staff> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByUserId(Long userId);

    // update
    @Query("UPDATE Staff s SET s.fullName = :fullName, s.phone = :phone, s.location.id = :locationId, s.updatedAt = :updatedAt WHERE s.user = :user")
    int updateStaff(@Param("user") User user, @Param("fullName") String fullName, @Param("phone") String phone,
            @Param("locationId") Long locationId, @Param("updatedAt") LocalDateTime updatedAt);

    // delete
    @Query("DELETE FROM Staff s WHERE s.user = :user")
    int deleteByUser(@Param("user") User user);
}
