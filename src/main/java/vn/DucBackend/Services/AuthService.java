package vn.DucBackend.Services;

import vn.DucBackend.DTO.RegisterDTO;
import vn.DucBackend.Entities.User;

/**
 * Service interface xử lý authentication
 */
public interface AuthService {

    /**
     * Đăng ký tài khoản mới
     * @param registerDTO Thông tin đăng ký
     * @return User đã tạo
     */
    User register(RegisterDTO registerDTO);

    /**
     * Xác thực email qua token
     * @param token Token xác thực
     * @return true nếu xác thực thành công
     */
    boolean verifyEmail(String token);

    /**
     * Gửi lại email xác thực
     * @param email Email cần gửi lại
     * @return true nếu gửi thành công
     */
    boolean resendVerificationEmail(String email);

    /**
     * Yêu cầu reset mật khẩu
     * @param email Email người dùng
     * @return true nếu gửi email thành công
     */
    boolean requestPasswordReset(String email);

    /**
     * Reset mật khẩu với token
     * @param token Token reset
     * @param newPassword Mật khẩu mới
     * @return true nếu reset thành công
     */
    boolean resetPassword(String token, String newPassword);

    /**
     * Kiểm tra email đã tồn tại chưa
     * @param email Email cần kiểm tra
     * @return true nếu đã tồn tại
     */
    boolean emailExists(String email);

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa
     * @param phone Số điện thoại cần kiểm tra
     * @return true nếu đã tồn tại
     */
    boolean phoneExists(String phone);

    /**
     * Tạo token xác thực mới
     * @return Token mới
     */
    String generateVerificationToken();
}
