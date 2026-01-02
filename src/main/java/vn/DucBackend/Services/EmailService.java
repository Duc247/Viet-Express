package vn.DucBackend.Services;

/**
 * Service interface gửi email
 */
public interface EmailService {

    /**
     * Gửi email xác thực tài khoản
     * @param toEmail Email người nhận
     * @param fullName Tên người dùng
     * @param verificationToken Token xác thực
     */
    void sendVerificationEmail(String toEmail, String fullName, String verificationToken);

    /**
     * Gửi email reset mật khẩu
     * @param toEmail Email người nhận
     * @param fullName Tên người dùng
     * @param resetToken Token reset mật khẩu
     */
    void sendPasswordResetEmail(String toEmail, String fullName, String resetToken);

    /**
     * Gửi email thông báo đăng ký thành công
     * @param toEmail Email người nhận
     * @param fullName Tên người dùng
     */
    void sendWelcomeEmail(String toEmail, String fullName);

    /**
     * Gửi email chung
     * @param toEmail Email người nhận
     * @param subject Tiêu đề
     * @param htmlContent Nội dung HTML
     */
    void sendHtmlEmail(String toEmail, String subject, String htmlContent);
}
