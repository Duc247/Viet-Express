package vn.DucBackend.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Custom handler xử lý khi đăng nhập thất bại
 * Cung cấp thông báo lỗi chi tiết cho người dùng
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage;
        
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Tên đăng nhập hoặc mật khẩu không đúng!";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Tài khoản chưa được kích hoạt. Vui lòng xác thực email.";
        } else if (exception instanceof LockedException) {
            errorMessage = "Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.";
        } else {
            errorMessage = "Đăng nhập thất bại. Vui lòng thử lại.";
        }
        
        // Encode message để tránh lỗi URL
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        
        // Redirect với error message
        getRedirectStrategy().sendRedirect(request, response, 
                "/auth/login?error=true&message=" + encodedMessage);
    }
}
