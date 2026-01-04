package vn.DucBackend.Controllers.Api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.DucBackend.Config.JwtProperties;
import vn.DucBackend.DTO.*;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Security.JwtTokenProvider;
import vn.DucBackend.Services.AuthService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API Controller cho Authentication với JWT
 * Dành cho mobile app hoặc SPA frontend
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final AuthService authService;

    /**
     * API Đăng nhập - Trả về JWT tokens
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<JwtResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        
        try {
            // Authenticate với Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tìm user để lấy thông tin
            User user = userRepository.findByUsername(authentication.getName())
                    .orElse(null);
            
            if (user == null) {
                // Thử tìm theo email
                List<User> users = userRepository.searchByEmail(loginRequest.getUsername());
                if (!users.isEmpty()) {
                    user = users.get(0);
                }
            }

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDTO.error("Không tìm thấy thông tin người dùng"));
            }

            // Cập nhật last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Lấy role
            String role = user.getRole() != null ? user.getRole().getRoleName() : "USER";
            
            // Tạo JWT tokens
            String accessToken = jwtTokenProvider.generateAccessToken(
                    user.getUsername(), 
                    user.getId(), 
                    authentication.getAuthorities()
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                    user.getUsername(), 
                    user.getId()
            );

            JwtResponseDTO response = JwtResponseDTO.of(
                    accessToken,
                    refreshToken,
                    jwtProperties.getAccessTokenExpiration(),
                    user.getId(),
                    user.getUsername(),
                    role
            );

            log.info("User {} logged in successfully via API", user.getUsername());
            
            return ResponseEntity.ok(ApiResponseDTO.success("Đăng nhập thành công", response));

        } catch (DisabledException e) {
            log.warn("Login failed - account disabled: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.error("Tài khoản chưa được kích hoạt. Vui lòng xác thực email."));
        } catch (BadCredentialsException e) {
            log.warn("Login failed - bad credentials: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.error("Tên đăng nhập hoặc mật khẩu không đúng"));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Đã xảy ra lỗi. Vui lòng thử lại sau."));
        }
    }

    /**
     * API Refresh Token - Tạo access token mới từ refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<JwtResponseDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request) {
        
        try {
            String refreshToken = request.getRefreshToken();

            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDTO.error("Refresh token không hợp lệ hoặc đã hết hạn"));
            }

            // Lấy thông tin từ refresh token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

            // Tìm user
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDTO.error("Người dùng không tồn tại"));
            }

            // Kiểm tra user còn active không
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDTO.error("Tài khoản đã bị khóa"));
            }

            // Lấy role và tạo authorities
            String role = user.getRole() != null ? user.getRole().getRoleName() : "USER";
            List<GrantedAuthority> authorities = List.of(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role)
            );

            // Tạo access token mới
            String newAccessToken = jwtTokenProvider.generateAccessToken(
                    username, userId, authorities
            );

            JwtResponseDTO response = JwtResponseDTO.of(
                    newAccessToken,
                    refreshToken, // Giữ nguyên refresh token
                    jwtProperties.getAccessTokenExpiration(),
                    userId,
                    username,
                    role
            );

            log.info("Access token refreshed for user: {}", username);
            
            return ResponseEntity.ok(ApiResponseDTO.success("Refresh token thành công", response));

        } catch (Exception e) {
            log.error("Refresh token error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Đã xảy ra lỗi. Vui lòng thử lại sau."));
        }
    }

    /**
     * API Đăng ký tài khoản mới
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<String>> register(
            @Valid @RequestBody RegisterDTO registerDTO) {
        
        try {
            // Validate confirm password
            if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Mật khẩu xác nhận không khớp"));
            }

            // Kiểm tra email đã tồn tại
            if (authService.emailExists(registerDTO.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Email đã được sử dụng"));
            }

            // Kiểm tra phone đã tồn tại
            if (authService.phoneExists(registerDTO.getPhone())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Số điện thoại đã được sử dụng"));
            }

            // Đăng ký
            authService.register(registerDTO);
            
            log.info("New user registered via API: {}", registerDTO.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.success(
                            "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", 
                            null
                    ));

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Đăng ký thất bại: " + e.getMessage()));
        }
    }

    /**
     * API Lấy thông tin user hiện tại từ token
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.error("Chưa đăng nhập"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error("Không tìm thấy thông tin người dùng"));
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setFullName(user.getFullName());
        userDTO.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : "USER");
        userDTO.setIsActive(user.getIsActive());
        userDTO.setLastLoginAt(user.getLastLoginAt());
        userDTO.setCreatedAt(user.getCreatedAt());

        return ResponseEntity.ok(ApiResponseDTO.success(userDTO));
    }

    /**
     * API Kiểm tra email đã tồn tại
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(ApiResponseDTO.success(exists));
    }

    /**
     * API Kiểm tra phone đã tồn tại
     */
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkPhone(@RequestParam String phone) {
        boolean exists = authService.phoneExists(phone);
        return ResponseEntity.ok(ApiResponseDTO.success(exists));
    }
}
