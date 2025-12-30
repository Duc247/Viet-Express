package vn.DucBackend.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Auth Controller - Xử lý đăng ký, quên mật khẩu, xác thực OTP
 * (Login và Logout được xử lý bởi LoginController)
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    // ==========================================
    // ĐĂNG KÝ
    // ==========================================
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister() {
        // TODO: Implement registration logic
        return "redirect:/auth/login?success=registered";
    }

    // ==========================================
    // QUÊN MẬT KHẨU
    // ==========================================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword() {
        // TODO: Send reset email
        return "redirect:/auth/verify-otp";
    }

    // ==========================================
    // XÁC THỰC OTP
    // ==========================================
    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String handleVerifyOtp() {
        // TODO: Verify OTP
        return "redirect:/auth/login?success=reset";
    }
}
