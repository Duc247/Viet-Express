package vn.DucBackend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Cấu hình để bật tính năng Async (gửi email bất đồng bộ)
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
