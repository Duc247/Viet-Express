package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.RegisterDTO;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Entities.VerificationToken.TokenType;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.AuthService;
import vn.DucBackend.Services.EmailService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service xử lý authentication - đăng ký, xác thực email, reset password
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int EMAIL_VERIFICATION_EXPIRATION_HOURS = 24;
    private static final int PASSWORD_RESET_EXPIRATION_HOURS = 1;

    @Override
    public User register(RegisterDTO dto) {
        // Kiểm tra email và phone đã tồn tại chưa
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        // Tạo username từ email (phần trước @)
        String username = dto.getEmail().split("@")[0];
        // Nếu username đã tồn tại, thêm số random
        if (userRepository.existsByUsername(username)) {
            username = username + System.currentTimeMillis() % 10000;
        }

        // Lấy role CUSTOMER
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));

        // Tạo User mới (chưa active, chờ xác thực email)
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFullName(dto.getFullName());
        user.setRole(customerRole);
        user.setIsActive(false); // Chưa active, chờ xác thực email
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Tạo Customer record
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setName(dto.getFullName());
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress() != null ? dto.getAddress() : "Chưa cập nhật");
        customerRepository.save(customer);

        // Tạo và gửi email xác thực
        String token = generateVerificationToken();
        VerificationToken verificationToken = new VerificationToken(
                token, savedUser, TokenType.EMAIL_VERIFICATION, EMAIL_VERIFICATION_EXPIRATION_HOURS);
        tokenRepository.save(verificationToken);

        // Gửi email xác thực (async)
        try {
            emailService.sendVerificationEmail(dto.getEmail(), dto.getFullName(), token);
            log.info("Verification email sent to: {}", dto.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage());
            // Không throw exception, user vẫn được tạo, có thể gửi lại email sau
        }

        return savedUser;
    }

    @Override
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository
                .findByTokenAndTokenType(token, TokenType.EMAIL_VERIFICATION)
                .orElse(null);

        if (verificationToken == null) {
            log.warn("Verification token not found: {}", token);
            return false;
        }

        if (!verificationToken.isValid()) {
            log.warn("Verification token is expired or already used: {}", token);
            return false;
        }

        // Kích hoạt user
        User user = verificationToken.getUser();
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Đánh dấu token đã sử dụng
        verificationToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        // Gửi email chào mừng
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }

        log.info("Email verified successfully for user: {}", user.getUsername());
        return true;
    }

    @Override
    public boolean resendVerificationEmail(String email) {
        User user = userRepository.findByUsername(email).orElse(null);
        if (user == null) {
            // Tìm theo email
            user = userRepository.searchByEmail(email).stream().findFirst().orElse(null);
        }

        if (user == null) {
            log.warn("User not found for resend verification: {}", email);
            return false;
        }

        if (user.getIsActive()) {
            log.warn("User already verified: {}", email);
            return false;
        }

        // Xóa token cũ
        tokenRepository.deleteByUserIdAndTokenType(user.getId(), TokenType.EMAIL_VERIFICATION);

        // Tạo token mới
        String token = generateVerificationToken();
        VerificationToken verificationToken = new VerificationToken(
                token, user, TokenType.EMAIL_VERIFICATION, EMAIL_VERIFICATION_EXPIRATION_HOURS);
        tokenRepository.save(verificationToken);

        // Gửi email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), token);
            log.info("Verification email resent to: {}", user.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Failed to resend verification email: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean requestPasswordReset(String email) {
        User user = userRepository.searchByEmail(email).stream().findFirst().orElse(null);

        if (user == null) {
            log.warn("User not found for password reset: {}", email);
            // Không tiết lộ user không tồn tại vì lý do bảo mật
            return true;
        }

        // Xóa token reset cũ
        tokenRepository.deleteByUserIdAndTokenType(user.getId(), TokenType.PASSWORD_RESET);

        // Tạo token mới
        String token = generateVerificationToken();
        VerificationToken resetToken = new VerificationToken(
                token, user, TokenType.PASSWORD_RESET, PASSWORD_RESET_EXPIRATION_HOURS);
        tokenRepository.save(resetToken);

        // Gửi email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
            log.info("Password reset email sent to: {}", user.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        VerificationToken resetToken = tokenRepository
                .findByTokenAndTokenType(token, TokenType.PASSWORD_RESET)
                .orElse(null);

        if (resetToken == null || !resetToken.isValid()) {
            log.warn("Invalid or expired password reset token: {}", token);
            return false;
        }

        // Cập nhật mật khẩu
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Đánh dấu token đã sử dụng
        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);

        log.info("Password reset successfully for user: {}", user.getUsername());
        return true;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public String generateVerificationToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
