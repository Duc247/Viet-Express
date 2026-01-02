package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ServiceTypeDTO;
import vn.DucBackend.Entities.ServiceType;
import vn.DucBackend.Repositories.ServiceTypeRepository;
import vn.DucBackend.Services.ServiceTypeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ ServiceType (Loại dịch vụ)
 * 
 * Admin Controller sử dụng:
 * - AdminResourceController: CRUD ServiceType
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceTypeServiceImpl implements ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    @Override
    public List<ServiceTypeDTO> findAll() {
        return serviceTypeRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceTypeDTO> findActive() {
        return serviceTypeRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ServiceTypeDTO> findById(Long id) {
        return serviceTypeRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<ServiceTypeDTO> findByCode(String code) {
        return serviceTypeRepository.findByCode(code).map(this::toDTO);
    }

    @Override
    public Optional<ServiceTypeDTO> findBySlug(String slug) {
        return serviceTypeRepository.findBySlug(slug).map(this::toDTO);
    }

    @Override
    public ServiceTypeDTO create(ServiceTypeDTO dto) {
        ServiceType serviceType = new ServiceType();
        serviceType.setCode(dto.getCode());
        serviceType.setSlug(
                dto.getSlug() != null && !dto.getSlug().isEmpty() ? dto.getSlug() : generateSlug(dto.getName()));
        serviceType.setName(dto.getName());
        serviceType.setIcon(dto.getIcon());
        serviceType.setDescription(dto.getDescription());
        serviceType.setLongDescription(dto.getLongDescription());
        serviceType.setImageUrl(dto.getImageUrl());
        serviceType.setPricePerKm(dto.getPricePerKm());
        serviceType.setAverageSpeedKmh(dto.getAverageSpeedKmh());
        serviceType.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return toDTO(serviceTypeRepository.save(serviceType));
    }

    // Generate slug from name
    private String generateSlug(String name) {
        if (name == null)
            return "";
        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    @Override
    public ServiceTypeDTO update(Long id, ServiceTypeDTO dto) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceType not found"));

        if (dto.getCode() != null) {
            serviceType.setCode(dto.getCode());
        }
        if (dto.getSlug() != null && !dto.getSlug().isEmpty()) {
            serviceType.setSlug(dto.getSlug());
        }
        if (dto.getName() != null) {
            serviceType.setName(dto.getName());
        }
        if (dto.getIcon() != null) {
            serviceType.setIcon(dto.getIcon());
        }
        if (dto.getDescription() != null) {
            serviceType.setDescription(dto.getDescription());
        }
        if (dto.getLongDescription() != null) {
            serviceType.setLongDescription(dto.getLongDescription());
        }
        if (dto.getImageUrl() != null) {
            serviceType.setImageUrl(dto.getImageUrl());
        }
        if (dto.getPricePerKm() != null) {
            serviceType.setPricePerKm(dto.getPricePerKm());
        }
        if (dto.getAverageSpeedKmh() != null) {
            serviceType.setAverageSpeedKmh(dto.getAverageSpeedKmh());
        }
        if (dto.getIsActive() != null) {
            serviceType.setIsActive(dto.getIsActive());
        }

        return toDTO(serviceTypeRepository.save(serviceType));
    }

    @Override
    public void delete(Long id) {
        serviceTypeRepository.deleteById(id);
    }

    @Override
    public void toggleStatus(Long id) {
        ServiceType serviceType = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceType not found"));
        serviceType.setIsActive(!serviceType.getIsActive());
        serviceTypeRepository.save(serviceType);
    }

    private ServiceTypeDTO toDTO(ServiceType serviceType) {
        ServiceTypeDTO dto = new ServiceTypeDTO();
        dto.setId(serviceType.getId());
        dto.setCode(serviceType.getCode());
        dto.setSlug(serviceType.getSlug());
        dto.setName(serviceType.getName());
        dto.setIcon(serviceType.getIcon());
        dto.setDescription(serviceType.getDescription());
        dto.setLongDescription(serviceType.getLongDescription());
        dto.setImageUrl(serviceType.getImageUrl());
        dto.setPricePerKm(serviceType.getPricePerKm());
        dto.setAverageSpeedKmh(serviceType.getAverageSpeedKmh());
        dto.setIsActive(serviceType.getIsActive());
        dto.setCreatedAt(serviceType.getCreatedAt());
        dto.setUpdatedAt(serviceType.getUpdatedAt());
        return dto;
    }
}
