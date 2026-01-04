package vn.DucBackend.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình JWT properties từ application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    
    /**
     * Secret key để mã hóa JWT (tối thiểu 256 bits cho HS256)
     */
    private String secret;
    
    /**
     * Thời gian hết hạn Access Token (milliseconds)
     * Default: 1 giờ = 3600000ms
     */
    private Long accessTokenExpiration;
    
    /**
     * Thời gian hết hạn Refresh Token (milliseconds)
     * Default: 7 ngày = 604800000ms
     */
    private Long refreshTokenExpiration;
}
