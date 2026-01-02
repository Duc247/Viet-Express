package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.ServiceType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    Optional<ServiceType> findByCode(String code);

    Optional<ServiceType> findBySlug(String slug);

    List<ServiceType> findByIsActiveTrue();

    @Query("SELECT s FROM ServiceType s WHERE s.isActive = true ORDER BY s.pricePerKm ASC")
    List<ServiceType> findActiveOrderByPrice();

    @Query("SELECT s FROM ServiceType s WHERE s.name LIKE %:keyword% OR s.code LIKE %:keyword%")
    List<ServiceType> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByCode(String code);
}