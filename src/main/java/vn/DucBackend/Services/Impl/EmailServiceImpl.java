package vn.DucBackend.Services.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.DucBackend.Services.EmailService;

/**
 * Service gửi email với nội dung HTML
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@logistics.vn}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    @Override
    @Async
    public void sendVerificationEmail(String toEmail, String fullName, String verificationToken) {
        String subject = "Xác thực tài khoản - Logistics";
        String verifyUrl = baseUrl + "/auth/verify?token=" + verificationToken;
        
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .btn { display: inline-block; padding: 15px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .btn:hover { background: #5a6fd6; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .warning { background: #fff3cd; padding: 10px; border-radius: 5px; margin-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚚 Logistics</h1>
                        <p>Xác thực tài khoản của bạn</p>
                    </div>
                    <div class="content">
                        <h2>Xin chào %s!</h2>
                        <p>Cảm ơn bạn đã đăng ký tài khoản tại Logistics. Để hoàn tất quá trình đăng ký, vui lòng xác thực email của bạn bằng cách nhấn vào nút bên dưới:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="btn">✅ Xác thực tài khoản</a>
                        </div>
                        
                        <p>Hoặc copy đường link sau vào trình duyệt:</p>
                        <p style="word-break: break-all; background: #eee; padding: 10px; border-radius: 5px;">%s</p>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý:</strong> Link xác thực này sẽ hết hạn sau 24 giờ. Nếu bạn không yêu cầu đăng ký tài khoản, vui lòng bỏ qua email này.
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2026 Logistics. Mọi quyền được bảo lưu.</p>
                        <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, verifyUrl, verifyUrl);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
        String subject = "Đặt lại mật khẩu - Logistics";
        String resetUrl = baseUrl + "/auth/reset-password?token=" + resetToken;
        
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .btn { display: inline-block; padding: 15px 30px; background: #f5576c; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .warning { background: #fff3cd; padding: 10px; border-radius: 5px; margin-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Đặt lại mật khẩu</h1>
                    </div>
                    <div class="content">
                        <h2>Xin chào %s!</h2>
                        <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Nhấn vào nút bên dưới để tạo mật khẩu mới:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="btn">🔑 Đặt lại mật khẩu</a>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý:</strong> Link này sẽ hết hạn sau 1 giờ. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này và đảm bảo tài khoản của bạn vẫn an toàn.
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2026 Logistics. Mọi quyền được bảo lưu.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, resetUrl);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String fullName) {
        String subject = "Chào mừng đến với Logistics!";
        
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .btn { display: inline-block; padding: 15px 30px; background: #11998e; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .feature { padding: 10px; margin: 10px 0; background: white; border-radius: 5px; border-left: 4px solid #11998e; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Chào mừng!</h1>
                        <p>Tài khoản của bạn đã được kích hoạt thành công</p>
                    </div>
                    <div class="content">
                        <h2>Xin chào %s!</h2>
                        <p>Chúc mừng bạn đã trở thành thành viên của Logistics. Bạn có thể bắt đầu sử dụng các dịch vụ của chúng tôi ngay bây giờ:</p>
                        
                        <div class="feature">
                            <strong>📦 Gửi hàng nhanh chóng</strong>
                            <p>Đặt đơn hàng và theo dõi trực tiếp trên hệ thống</p>
                        </div>
                        
                        <div class="feature">
                            <strong>🔍 Theo dõi đơn hàng</strong>
                            <p>Cập nhật trạng thái đơn hàng theo thời gian thực</p>
                        </div>
                        
                        <div class="feature">
                            <strong>💳 Thanh toán an toàn</strong>
                            <p>Nhiều phương thức thanh toán tiện lợi</p>
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="%s/auth/login" class="btn">🚀 Đăng nhập ngay</a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2026 Logistics. Mọi quyền được bảo lưu.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, baseUrl);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }
}
