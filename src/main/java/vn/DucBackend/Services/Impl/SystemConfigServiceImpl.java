package vn.DucBackend.Services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.SystemConfigDTO;
import vn.DucBackend.Entities.SystemConfig;
import vn.DucBackend.Repositories.SystemConfigRepository;
import vn.DucBackend.Services.SystemConfigService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ SystemConfig (Cấu hình hệ thống)
 * 
 * Admin Controller sử dụng: AdminSystemController
 * - findAll(): GET /admin/system-config
 * - findById(): GET /admin/system-config/edit/{id}
 * - save(): POST /admin/system-config/save
 * - delete(): GET /admin/system-config/delete/{id}
 * - toggleActive(): GET /admin/system-config/toggle/{id}
 * - initDefaultConfigs(): @PostConstruct, GET
 * /admin/system-config/init-defaults
 */
@Service
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    /**
     * Lấy tất cả cấu hình
     * Admin: AdminSystemController.systemConfigList() - GET /admin/system-config
     */
    @Override
    public List<SystemConfig> findAll() {
        return systemConfigRepository.findAll();
    }

    @Override
    public List<SystemConfig> findAllActive() {
        return systemConfigRepository.findByIsActiveTrue();
    }

    /**
     * Tìm cấu hình theo ID
     * Admin: AdminSystemController.systemConfigEditForm() - GET
     * /admin/system-config/edit/{id}
     */
    @Override
    public Optional<SystemConfig> findById(Long id) {
        return systemConfigRepository.findById(id);
    }

    @Override
    public Optional<SystemConfig> findByKey(String key) {
        return systemConfigRepository.findByConfigKey(key);
    }

    @Override
    public String getValue(String key) {
        return systemConfigRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(null);
    }

    @Override
    public String getValue(String key, String defaultValue) {
        return systemConfigRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue);
    }

    @Override
    public List<SystemConfig> findByGroup(String group) {
        return systemConfigRepository.findByConfigGroupAndIsActiveTrue(group);
    }

    @Override
    public List<SystemConfig> findPublicConfigs() {
        return systemConfigRepository.findByIsPublicTrueAndIsActiveTrue();
    }

    /**
     * Lưu cấu hình (tạo mới hoặc cập nhật)
     * Admin: AdminSystemController.systemConfigSave() - POST
     * /admin/system-config/save
     */
    @Override
    public SystemConfig save(SystemConfig config) {
        return systemConfigRepository.save(config);
    }

    @Override
    public SystemConfig save(SystemConfigDTO configDTO) {
        SystemConfig config = toEntity(configDTO);
        return systemConfigRepository.save(config);
    }

    @Override
    public SystemConfig updateValue(String key, String value) {
        Optional<SystemConfig> optional = systemConfigRepository.findByConfigKey(key);
        if (optional.isPresent()) {
            SystemConfig config = optional.get();
            config.setConfigValue(value);
            return systemConfigRepository.save(config);
        }
        return null;
    }

    @Override
    public void updateMultiple(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            updateValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Xóa cấu hình
     * Admin: AdminSystemController.systemConfigDelete() - GET
     * /admin/system-config/delete/{id}
     */
    @Override
    public void delete(Long id) {
        systemConfigRepository.deleteById(id);
    }

    /**
     * Bật/tắt trạng thái cấu hình
     * Admin: AdminSystemController.systemConfigToggle() - GET
     * /admin/system-config/toggle/{id}
     */
    @Override
    public SystemConfig toggleActive(Long id) {
        Optional<SystemConfig> optional = systemConfigRepository.findById(id);
        if (optional.isPresent()) {
            SystemConfig config = optional.get();
            config.setIsActive(!config.getIsActive());
            return systemConfigRepository.save(config);
        }
        return null;
    }

    /**
     * Khởi tạo các cấu hình mặc định
     * Admin: AdminSystemController.init() - @PostConstruct
     * Admin: AdminSystemController.initDefaultConfigs() - GET
     * /admin/system-config/init-defaults
     */
    @Override
    public void initDefaultConfigs() {
        List<DefaultConfig> defaults = Arrays.asList(
                // General Settings
                new DefaultConfig("SITE_NAME", "Viet Express", "STRING", "GENERAL", "Tên website"),
                new DefaultConfig("SITE_DESCRIPTION", "Dịch vụ vận chuyển nhanh chóng, an toàn", "STRING", "GENERAL",
                        "Mô tả website"),
                new DefaultConfig("SITE_LOGO", "/images/logo.png", "URL", "GENERAL", "Logo website"),
                new DefaultConfig("SITE_FAVICON", "/images/favicon.ico", "URL", "GENERAL", "Favicon"),
                new DefaultConfig("CONTACT_EMAIL", "contact@vietexpress.vn", "EMAIL", "GENERAL", "Email liên hệ"),
                new DefaultConfig("CONTACT_PHONE", "1900 1234", "STRING", "GENERAL", "Số điện thoại liên hệ"),
                new DefaultConfig("CONTACT_ADDRESS", "123 Đường ABC, Quận 1, TP.HCM", "STRING", "GENERAL",
                        "Địa chỉ liên hệ"),

                // Email Settings
                new DefaultConfig("SMTP_HOST", "smtp.gmail.com", "STRING", "EMAIL", "SMTP Host"),
                new DefaultConfig("SMTP_PORT", "587", "NUMBER", "EMAIL", "SMTP Port"),
                new DefaultConfig("SMTP_USERNAME", "", "STRING", "EMAIL", "SMTP Username"),
                new DefaultConfig("SMTP_PASSWORD", "", "STRING", "EMAIL", "SMTP Password"),
                new DefaultConfig("SMTP_FROM_EMAIL", "", "EMAIL", "EMAIL", "Email gửi đi"),
                new DefaultConfig("SMTP_FROM_NAME", "Viet Express", "STRING", "EMAIL", "Tên người gửi"),

                // Payment Settings
                new DefaultConfig("COD_ENABLED", "true", "BOOLEAN", "PAYMENT", "Cho phép COD"),
                new DefaultConfig("COD_FEE_PERCENT", "0", "NUMBER", "PAYMENT", "Phí COD (%)"),
                new DefaultConfig("BANK_TRANSFER_ENABLED", "true", "BOOLEAN", "PAYMENT", "Cho phép chuyển khoản"),
                new DefaultConfig("BANK_NAME", "", "STRING", "PAYMENT", "Tên ngân hàng"),
                new DefaultConfig("BANK_ACCOUNT_NUMBER", "", "STRING", "PAYMENT", "Số tài khoản"),
                new DefaultConfig("BANK_ACCOUNT_NAME", "", "STRING", "PAYMENT", "Chủ tài khoản"),

                // API Settings
                new DefaultConfig("GOOGLE_MAPS_API_KEY", "", "STRING", "API", "Google Maps API Key"),
                new DefaultConfig("VNPAY_TMN_CODE", "", "STRING", "API", "VNPay Terminal Code"),
                new DefaultConfig("VNPAY_SECRET_KEY", "", "STRING", "API", "VNPay Secret Key"),
                new DefaultConfig("VNPAY_RETURN_URL", "", "URL", "API", "VNPay Return URL"),

                // Social Settings
                new DefaultConfig("FACEBOOK_APP_ID", "", "STRING", "SOCIAL", "Facebook App ID"),
                new DefaultConfig("FACEBOOK_APP_SECRET", "", "STRING", "SOCIAL", "Facebook App Secret"),
                new DefaultConfig("FACEBOOK_PAGE_URL", "", "URL", "SOCIAL", "Facebook Page URL"),
                new DefaultConfig("ZALO_OA_ID", "", "STRING", "SOCIAL", "Zalo OA ID"),

                // Notification Settings
                new DefaultConfig("EMAIL_NOTIFICATION_ENABLED", "true", "BOOLEAN", "NOTIFICATION",
                        "Gửi email thông báo"),
                new DefaultConfig("SMS_NOTIFICATION_ENABLED", "false", "BOOLEAN", "NOTIFICATION", "Gửi SMS thông báo"),
                new DefaultConfig("PUSH_NOTIFICATION_ENABLED", "true", "BOOLEAN", "NOTIFICATION",
                        "Gửi push notification"));

        for (DefaultConfig def : defaults) {
            if (!systemConfigRepository.existsByConfigKey(def.key)) {
                SystemConfig config = new SystemConfig();
                config.setConfigKey(def.key);
                config.setConfigValue(def.value);
                config.setConfigType(def.type);
                config.setConfigGroup(def.group);
                config.setDescription(def.description);
                config.setIsActive(true);
                config.setIsPublic(false);
                systemConfigRepository.save(config);
            }
        }
    }

    @Override
    public Map<String, String> getConfigMapByGroup(String group) {
        List<SystemConfig> configs = systemConfigRepository.findByConfigGroupAndIsActiveTrue(group);
        return configs.stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        c -> c.getConfigValue() != null ? c.getConfigValue() : "",
                        (existing, replacement) -> existing));
    }

    @Override
    public SystemConfigDTO toDTO(SystemConfig config) {
        SystemConfigDTO dto = new SystemConfigDTO();
        dto.setId(config.getId());
        dto.setConfigKey(config.getConfigKey());
        dto.setConfigValue(config.getConfigValue());
        dto.setConfigType(config.getConfigType());
        dto.setConfigGroup(config.getConfigGroup());
        dto.setDescription(config.getDescription());
        dto.setIsActive(config.getIsActive());
        dto.setIsPublic(config.getIsPublic());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());
        if (config.getUpdatedBy() != null) {
            dto.setUpdatedByUsername(config.getUpdatedBy().getUsername());
        }
        return dto;
    }

    @Override
    public SystemConfig toEntity(SystemConfigDTO dto) {
        SystemConfig config;
        if (dto.getId() != null) {
            config = systemConfigRepository.findById(dto.getId()).orElse(new SystemConfig());
        } else {
            config = new SystemConfig();
        }
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setConfigType(dto.getConfigType());
        config.setConfigGroup(dto.getConfigGroup());
        config.setDescription(dto.getDescription());
        config.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        config.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : false);
        return config;
    }

    // Helper class for default configs
    private static class DefaultConfig {
        String key, value, type, group, description;

        DefaultConfig(String key, String value, String type, String group, String description) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.group = group;
            this.description = description;
        }
    }
}
