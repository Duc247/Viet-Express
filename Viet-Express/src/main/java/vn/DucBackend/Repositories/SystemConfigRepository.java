package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.SystemConfig;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    /**
     * Tìm config theo key
     */
    Optional<SystemConfig> findByConfigKey(String configKey);

    /**
     * Tìm tất cả config theo group
     */
    List<SystemConfig> findByConfigGroup(String configGroup);

    /**
     * Tìm tất cả config đang active
     */
    List<SystemConfig> findByIsActiveTrue();

    /**
     * Tìm config public (có thể hiển thị cho người dùng)
     */
    List<SystemConfig> findByIsPublicTrueAndIsActiveTrue();

    /**
     * Tìm config theo group và đang active
     */
    List<SystemConfig> findByConfigGroupAndIsActiveTrue(String configGroup);

    /**
     * Kiểm tra key đã tồn tại chưa
     */
    boolean existsByConfigKey(String configKey);

    /**
     * Tìm theo nhiều group
     */
    List<SystemConfig> findByConfigGroupIn(List<String> groups);
}
