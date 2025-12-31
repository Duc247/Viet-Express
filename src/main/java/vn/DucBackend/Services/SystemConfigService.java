package vn.DucBackend.Services;

import vn.DucBackend.DTO.SystemConfigDTO;
import vn.DucBackend.Entities.SystemConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface quản lý SystemConfig (Cấu hình hệ thống)
 * 
 * Repository sử dụng: SystemConfigRepository
 * Controller sử dụng: AdminSystemController
 */
public interface SystemConfigService {

    /** Repository: systemConfigRepository.findAll() */
    List<SystemConfig> findAll();

    /** Repository: systemConfigRepository.findByIsActiveTrue() */
    List<SystemConfig> findAllActive();

    /** Repository: systemConfigRepository.findById() */
    Optional<SystemConfig> findById(Long id);

    /** Repository: systemConfigRepository.findByConfigKey() */
    Optional<SystemConfig> findByKey(String key);

    /** Repository: systemConfigRepository.findByConfigKey() - Lấy value */
    String getValue(String key);

    /**
     * Repository: systemConfigRepository.findByConfigKey() - Lấy value với default
     */
    String getValue(String key, String defaultValue);

    /** Repository: systemConfigRepository.findByConfigGroup() */
    List<SystemConfig> findByGroup(String group);

    /** Repository: systemConfigRepository.findByIsPublicTrue() */
    List<SystemConfig> findPublicConfigs();

    /** Repository: systemConfigRepository.save() */
    SystemConfig save(SystemConfig config);

    /** Repository: systemConfigRepository.save() */
    SystemConfig save(SystemConfigDTO configDTO);

    /**
     * Repository: systemConfigRepository.findByConfigKey(),
     * systemConfigRepository.save()
     */
    SystemConfig updateValue(String key, String value);

    /**
     * Repository: systemConfigRepository.findByConfigKey(),
     * systemConfigRepository.save()
     */
    void updateMultiple(Map<String, String> configs);

    /** Repository: systemConfigRepository.deleteById() */
    void delete(Long id);

    /**
     * Repository: systemConfigRepository.findById(), systemConfigRepository.save()
     */
    SystemConfig toggleActive(Long id);

    /**
     * Repository: systemConfigRepository.existsByConfigKey(),
     * systemConfigRepository.save()
     */
    void initDefaultConfigs();

    /** Repository: systemConfigRepository.findByConfigGroup() */
    Map<String, String> getConfigMapByGroup(String group);

    /** Chuyển đổi entity sang DTO */
    SystemConfigDTO toDTO(SystemConfig config);

    /** Chuyển đổi DTO sang entity */
    SystemConfig toEntity(SystemConfigDTO dto);
}
