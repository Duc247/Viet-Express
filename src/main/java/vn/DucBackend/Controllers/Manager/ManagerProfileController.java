package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.UserService;

/**
 * Manager Profile Controller
 * Xử lý xem và chỉnh sửa hồ sơ manager
 */
@Controller
@RequestMapping("/manager")
public class ManagerProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    /**
     * Xem hồ sơ manager
     */
    @GetMapping("/profile")
    public String viewProfile(Model model, HttpServletRequest request, 
            @AuthenticationPrincipal UserDetails userDetails) {
        addCommonAttributes(model, request);
        
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        
        if (user == null) {
            return "redirect:/manager/dashboard";
        }
        
        model.addAttribute("user", user);
        return "manager/profile";
    }

    /**
     * Cập nhật hồ sơ manager
     */
    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập lại!");
            return "redirect:/auth/login";
        }
        
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng!");
            return "redirect:/manager/dashboard";
        }
        
        try {
            // Cập nhật thông tin
            if (fullName != null && !fullName.trim().isEmpty()) {
                user.setFullName(fullName.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone.trim());
            }
            if (email != null && !email.trim().isEmpty()) {
                user.setEmail(email.trim());
            }
            
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
        }
        
        return "redirect:/manager/profile";
    }
}
