package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.CaseStudy;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseStudyRepository extends JpaRepository<CaseStudy, Long> {

    Optional<CaseStudy> findBySlug(String slug);

    List<CaseStudy> findByIsPublishedTrue();

    List<CaseStudy> findByIsFeaturedTrueAndIsPublishedTrue();

    List<CaseStudy> findByServiceTypeId(Long serviceTypeId);

    @Query("SELECT c FROM CaseStudy c WHERE c.isPublished = true ORDER BY c.createdAt DESC")
    List<CaseStudy> findPublishedOrderByCreatedAtDesc();

    @Query("SELECT c FROM CaseStudy c WHERE c.isFeatured = true AND c.isPublished = true ORDER BY c.createdAt DESC")
    List<CaseStudy> findFeaturedCaseStudies();

    @Query("SELECT c FROM CaseStudy c WHERE c.title LIKE %:keyword% OR c.challenge LIKE %:keyword% OR c.solution LIKE %:keyword%")
    List<CaseStudy> searchByKeyword(@Param("keyword") String keyword);

    boolean existsBySlug(String slug);
}
