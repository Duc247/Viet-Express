package vn.DucBackend.Services;

import vn.DucBackend.DTO.CaseStudyDTO;

import java.util.List;
import java.util.Optional;

public interface CaseStudyService {

    List<CaseStudyDTO> findAllCaseStudies();

    List<CaseStudyDTO> findPublishedCaseStudies();

    List<CaseStudyDTO> findFeaturedCaseStudies();

    Optional<CaseStudyDTO> findCaseStudyById(Long id);

    Optional<CaseStudyDTO> findBySlug(String slug);

    List<CaseStudyDTO> findByServiceTypeId(Long serviceTypeId);

    List<CaseStudyDTO> searchCaseStudies(String keyword);

    CaseStudyDTO createCaseStudy(CaseStudyDTO dto);

    CaseStudyDTO updateCaseStudy(Long id, CaseStudyDTO dto);

    CaseStudyDTO toggleFeatured(Long id);

    CaseStudyDTO togglePublished(Long id);

    void deleteCaseStudy(Long id);

    String generateSlug(String title);
}
