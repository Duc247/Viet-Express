package vn.DucBackend.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import vn.DucBackend.Config.JwtProperties;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT Token Provider - Tạo và xác thực JWT tokens
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    
    private SecretKey secretKey;
    
    private static final String AUTHORITIES_KEY = "roles";
    private static final String USER_ID_KEY = "userId";

    @PostConstruct
    public void init() {
        // Tạo secret key từ chuỗi secret trong config
        byte[] keyBytes = Decoders.BASE64.decode(
                Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes())
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tạo Access Token từ Authentication
     */
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Tạo Access Token từ username và roles
     */
    public String generateAccessToken(String username, Long userId, Collection<? extends GrantedAuthority> authorities) {
        return generateToken(username, userId, authorities, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Tạo Refresh Token
     */
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * Tạo Refresh Token từ username
     */
    public String generateRefreshToken(String username, Long userId) {
        return generateToken(username, userId, Collections.emptyList(), jwtProperties.getRefreshTokenExpiration());
    }

    private String generateToken(Authentication authentication, long expiration) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return generateToken(username, null, authorities, expiration);
    }

    private String generateToken(String username, Long userId, Collection<? extends GrantedAuthority> authorities, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_KEY, roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey);

        if (userId != null) {
            builder.claim(USER_ID_KEY, userId);
        }

        return builder.compact();
    }

    /**
     * Lấy username từ token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * Lấy user ID từ token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(USER_ID_KEY, Long.class);
    }

    /**
     * Lấy roles từ token
     */
    public Collection<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = parseToken(token);
        String rolesString = claims.get(AUTHORITIES_KEY, String.class);
        
        if (rolesString == null || rolesString.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(rolesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Lấy Authentication từ token
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        
        Collection<GrantedAuthority> authorities = getAuthoritiesFromToken(token);
        
        UserDetails userDetails = new User(claims.getSubject(), "", authorities);
        
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.error("JWT signature validation failed: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Kiểm tra token có hết hạn không
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    /**
     * Lấy thời gian hết hạn của token
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
