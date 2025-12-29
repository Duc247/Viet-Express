package vn.DucBackend.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Auth Controller - Xử lý các trang đăng nhập, đăng ký
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    // ==========================================
    // ĐĂNG NHẬP
    // ==========================================
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String handleLogin() {
        // TODO: Implement authentication logic
        return "redirect:/customer/dashboard";
    }

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

    // ==========================================
    // ĐĂNG XUẤT
    // ==========================================
    @GetMapping("/logout")
    public String logout() {
        // TODO: Clear session
        return "redirect:/auth/login?logout=true";
    }
}
