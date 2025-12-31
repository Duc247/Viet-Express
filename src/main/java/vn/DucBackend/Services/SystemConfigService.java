package vn.DucBackend.Services;

import vn.DucBackend.DTO.SystemConfigDTO;
import vn.DucBackend.Entities.SystemConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SystemConfigService {

    /**
     * Lấy tất cả cấu hình
     */
    List<SystemConfig> findAll();

    /**
     * Lấy tất cả cấu hình đang active
     */
    List<SystemConfig> findAllActive();

    /**
     * Lấy cấu hình theo ID
     */
    Optional<SystemConfig> findById(Long id);

    /**
     * Lấy cấu hình theo key
     */
    Optional<SystemConfig> findByKey(String key);

    /**
     * Lấy giá trị cấu hình theo key
     */
    String getValue(String key);

    /**
     * Lấy giá trị cấu hình theo key với default value
     */
    String getValue(String key, String defaultValue);

    /**
     * Lấy tất cả cấu hình theo group
     */
    List<SystemConfig> findByGroup(String group);

    /**
     * Lấy tất cả cấu hình public
     */
    List<SystemConfig> findPublicConfigs();

    /**
     * Lưu hoặc cập nhật cấu hình
     */
    SystemConfig save(SystemConfig config);

    /**
     * Lưu hoặc cập nhật cấu hình từ DTO
     */
    SystemConfig save(SystemConfigDTO configDTO);

    /**
     * Cập nhật giá trị cấu hình theo key
     */
    SystemConfig updateValue(String key, String value);

    /**
     * Cập nhật nhiều cấu hình cùng lúc
     */
    void updateMultiple(Map<String, String> configs);

    /**
     * Xóa cấu hình
     */
    void delete(Long id);

    /**
     * Toggle trạng thái active
     */
    SystemConfig toggleActive(Long id);

    /**
     * Khởi tạo các cấu hình mặc định
     */
    void initDefaultConfigs();

    /**
     * Lấy cấu hình theo group dưới dạng Map
     */
    Map<String, String> getConfigMapByGroup(String group);

    /**
     * Chuyển đổi entity sang DTO
     */
    SystemConfigDTO toDTO(SystemConfig config);

    /**
     * Chuyển đổi DTO sang entity
     */
    SystemConfig toEntity(SystemConfigDTO dto);
}
