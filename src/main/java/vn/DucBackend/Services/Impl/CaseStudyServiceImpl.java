package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.CaseStudyDTO;
import vn.DucBackend.Entities.CaseStudy;
import vn.DucBackend.Repositories.CaseStudyRepository;
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.ServiceTypeRepository;
import vn.DucBackend.Services.CaseStudyService;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CaseStudyServiceImpl implements CaseStudyService {

    private final CaseStudyRepository caseStudyRepository;
    private final CustomerRequestRepository requestRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    @Override
    public List<CaseStudyDTO> findAllCaseStudies() {
        return caseStudyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CaseStudyDTO> findPublishedCaseStudies() {
        return caseStudyRepository.findPublishedOrderByCreatedAtDesc().stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CaseStudyDTO> findFeaturedCaseStudies() {
        return caseStudyRepository.findFeaturedCaseStudies().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<CaseStudyDTO> findCaseStudyById(Long id) {
        return caseStudyRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<CaseStudyDTO> findBySlug(String slug) {
        return caseStudyRepository.findBySlug(slug).map(this::toDTO);
    }

    @Override
    public List<CaseStudyDTO> findByServiceTypeId(Long serviceTypeId) {
        return caseStudyRepository.findByServiceTypeId(serviceTypeId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CaseStudyDTO> searchCaseStudies(String keyword) {
        return caseStudyRepository.searchByKeyword(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CaseStudyDTO createCaseStudy(CaseStudyDTO dto) {
        CaseStudy cs = new CaseStudy();
        // Lấy request và service type từ request (không cần chọn riêng)
        if (dto.getRequestId() != null) {
            var request = requestRepository.findById(dto.getRequestId()).orElse(null);
            cs.setRequest(request);
            // Tự động lấy service type từ request
            if (request != null && request.getServiceType() != null) {
                cs.setServiceType(request.getServiceType());
            }
        }
        cs.setTitle(dto.getTitle());
        cs.setSlug(dto.getSlug() != null ? dto.getSlug() : generateSlug(dto.getTitle()));
        cs.setClientNameDisplay(dto.getClientNameDisplay());
        cs.setChallenge(dto.getChallenge());
        cs.setSolution(dto.getSolution());
        cs.setResult(dto.getResult());
        cs.setThumbnailUrl(dto.getThumbnailUrl());
        cs.setImageGallery(dto.getImageGallery());
        cs.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        cs.setIsPublished(dto.getIsPublished() != null ? dto.getIsPublished() : false);
        return toDTO(caseStudyRepository.save(cs));
    }

    @Override
    public CaseStudyDTO updateCaseStudy(Long id, CaseStudyDTO dto) {
        CaseStudy cs = caseStudyRepository.findById(id).orElseThrow(() -> new RuntimeException("CaseStudy not found"));
        if (dto.getTitle() != null)
            cs.setTitle(dto.getTitle());
        if (dto.getSlug() != null)
            cs.setSlug(dto.getSlug());
        if (dto.getClientNameDisplay() != null)
            cs.setClientNameDisplay(dto.getClientNameDisplay());
        if (dto.getChallenge() != null)
            cs.setChallenge(dto.getChallenge());
        if (dto.getSolution() != null)
            cs.setSolution(dto.getSolution());
        if (dto.getResult() != null)
            cs.setResult(dto.getResult());
        if (dto.getThumbnailUrl() != null)
            cs.setThumbnailUrl(dto.getThumbnailUrl());
        if (dto.getImageGallery() != null)
            cs.setImageGallery(dto.getImageGallery());
        return toDTO(caseStudyRepository.save(cs));
    }

    @Override
    public CaseStudyDTO toggleFeatured(Long id) {
        CaseStudy cs = caseStudyRepository.findById(id).orElseThrow(() -> new RuntimeException("CaseStudy not found"));
        Boolean current = cs.getIsFeatured();
        cs.setIsFeatured(current == null ? true : !current);
        return toDTO(caseStudyRepository.save(cs));
    }

    @Override
    public CaseStudyDTO togglePublished(Long id) {
        CaseStudy cs = caseStudyRepository.findById(id).orElseThrow(() -> new RuntimeException("CaseStudy not found"));
        Boolean current = cs.getIsPublished();
        cs.setIsPublished(current == null ? true : !current);
        return toDTO(caseStudyRepository.save(cs));
    }

    @Override
    public void deleteCaseStudy(Long id) {
        caseStudyRepository.deleteById(id);
    }

    @Override
    public String generateSlug(String title) {
        if (title == null)
            return "";
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return slug;
    }

    private CaseStudyDTO toDTO(CaseStudy cs) {
        CaseStudyDTO dto = new CaseStudyDTO();
        dto.setId(cs.getId());
        if (cs.getRequest() != null) {
            dto.setRequestId(cs.getRequest().getId());
            dto.setRequestCode(cs.getRequest().getRequestCode());
        }
        if (cs.getServiceType() != null) {
            dto.setServiceTypeId(cs.getServiceType().getId());
            dto.setServiceTypeName(cs.getServiceType().getName());
        }
        dto.setTitle(cs.getTitle());
        dto.setSlug(cs.getSlug());
        dto.setClientNameDisplay(cs.getClientNameDisplay());
        dto.setChallenge(cs.getChallenge());
        dto.setSolution(cs.getSolution());
        dto.setResult(cs.getResult());
        dto.setThumbnailUrl(cs.getThumbnailUrl());
        dto.setImageGallery(cs.getImageGallery());
        dto.setIsFeatured(cs.getIsFeatured());
        dto.setIsPublished(cs.getIsPublished());
        dto.setCreatedAt(cs.getCreatedAt());
        dto.setUpdatedAt(cs.getUpdatedAt());
        return dto;
    }
}
