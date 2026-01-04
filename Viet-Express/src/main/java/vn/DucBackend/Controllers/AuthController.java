package vn.DucBackend.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.DucBackend.DTO.RegisterDTO;
import vn.DucBackend.Services.AuthService;

/**
 * Auth Controller - Xử lý đăng ký, xác thực email, quên mật khẩu
 * (Login và Logout được xử lý bởi LoginController và Spring Security)
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // ==========================================
    // ĐĂNG KÝ
    // ==========================================
    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerDTO")) {
            model.addAttribute("registerDTO", new RegisterDTO());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerDTO", registerDTO);
            return "auth/register";
        }

        // Kiểm tra mật khẩu xác nhận
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", 
                    "Mật khẩu xác nhận không khớp");
            model.addAttribute("registerDTO", registerDTO);
            return "auth/register";
        }

        // Kiểm tra email đã tồn tại
        if (authService.emailExists(registerDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.email", 
                    "Email đã được sử dụng");
            model.addAttribute("registerDTO", registerDTO);
            return "auth/register";
        }

        // Kiểm tra phone đã tồn tại
        if (authService.phoneExists(registerDTO.getPhone())) {
            bindingResult.rejectValue("phone", "error.phone", 
                    "Số điện thoại đã được sử dụng");
            model.addAttribute("registerDTO", registerDTO);
            return "auth/register";
        }

        try {
            authService.register(registerDTO);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
            return "redirect:/auth/register-success";
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            model.addAttribute("errorMessage", "Đăng ký thất bại: " + e.getMessage());
            model.addAttribute("registerDTO", registerDTO);
            return "auth/register";
        }
    }

    @GetMapping("/register-success")
    public String registerSuccessPage() {
        return "auth/register-success";
    }

    // ==========================================
    // XÁC THỰC EMAIL
    // ==========================================
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, 
                              RedirectAttributes redirectAttributes) {
        boolean success = authService.verifyEmail(token);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Xác thực email thành công! Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Link xác thực không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu gửi lại.");
            return "redirect:/auth/resend-verification";
        }
    }

    @GetMapping("/resend-verification")
    public String resendVerificationPage() {
        return "auth/resend-verification";
    }

    @PostMapping("/resend-verification")
    public String handleResendVerification(@RequestParam("email") String email,
                                           RedirectAttributes redirectAttributes) {
        boolean success = authService.resendVerificationEmail(email);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Email xác thực đã được gửi lại. Vui lòng kiểm tra hộp thư.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Không thể gửi email xác thực. Email không tồn tại hoặc đã được xác thực.");
        }
        return "redirect:/auth/resend-verification";
    }

    // ==========================================
    // QUÊN MẬT KHẨU
    // ==========================================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes) {
        authService.requestPasswordReset(email);
        // Luôn hiển thị thông báo thành công để không tiết lộ email có tồn tại hay không
        redirectAttributes.addFlashAttribute("successMessage", 
                "Nếu email tồn tại trong hệ thống, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu.");
        return "redirect:/auth/forgot-password";
    }

    // ==========================================
    // RESET MẬT KHẨU
    // ==========================================
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        
        // Validate password
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự");
            return "redirect:/auth/reset-password?token=" + token;
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
            return "redirect:/auth/reset-password?token=" + token;
        }

        boolean success = authService.resetPassword(token, password);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
            return "redirect:/auth/forgot-password";
        }
    }

    // ==========================================
    // API KIỂM TRA VALIDATION (AJAX)
    // ==========================================
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailExists(@RequestParam("email") String email) {
        return authService.emailExists(email);
    }

    @GetMapping("/check-phone")
    @ResponseBody
    public boolean checkPhoneExists(@RequestParam("phone") String phone) {
        return authService.phoneExists(phone);
    }
}
