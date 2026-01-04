package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.TrackingCode;

import java.util.Optional;

@Repository
public interface TrackingCodeRepository extends JpaRepository<TrackingCode, Long> {

    Optional<TrackingCode> findByCode(String code);

    Optional<TrackingCode> findByRequestId(Long requestId);

    @Query("SELECT t FROM TrackingCode t WHERE t.code LIKE %:keyword%")
    java.util.List<TrackingCode> searchByCode(@Param("keyword") String keyword);

    boolean existsByCode(String code);

    boolean existsByRequestId(Long requestId);
}