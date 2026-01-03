package vn.DucBackend.Controllers.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Entities.Shipper;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.StaffRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Utils.LoggingHelper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller xử lý đăng nhập - Tích hợp Spring Security
 * Spring Security xử lý authentication, controller này xử lý views và
 * post-login logic
 */
@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private LoggingHelper loggingHelper;

    /**
     * Hiển thị trang đăng nhập
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            Model model) {

        // Nếu đã đăng nhập, redirect về dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return redirectByRole(auth);
        }

        // Hiển thị thông báo
        if (error != null) {
            if (message != null && !message.isEmpty()) {
                try {
                    String decodedMessage = URLDecoder.decode(message, StandardCharsets.UTF_8);
                    model.addAttribute("errorMessage", decodedMessage);
                } catch (Exception e) {
                    model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
                }
            } else {
                model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Đã đăng xuất thành công!");
        }
        if (expired != null) {
            model.addAttribute("errorMessage", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        return "auth/login";
    }

    /**
     * Xử lý sau khi đăng nhập thành công (Spring Security gọi endpoint này)
     */
    @GetMapping("/login-success")
    public String handleLoginSuccess(HttpSession session, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return "redirect:/auth/login?error=true";
        }

        User user = userOpt.get();
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";

        // Cập nhật thời gian đăng nhập
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Ghi log đăng nhập thành công
        loggingHelper.logLoginSuccess(user.getId(), username, roleName, request);

        // Lưu thông tin vào session cho Thymeleaf sử dụng
        session.setAttribute("loggedInUser", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("roleName", roleName);

        // Lưu thông tin chi tiết theo role
        switch (roleName) {
            case "CUSTOMER":
                customerRepository.findByUserId(user.getId()).ifPresent(customer -> {
                    session.setAttribute("customerId", customer.getId());
                    session.setAttribute("customerName", customer.getFullName());
                });
                redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công! Xin chào, " + username);
                return "redirect:/customer/dashboard";

            case "STAFF":
                staffRepository.findByUserId(user.getId()).ifPresent(staff -> {
                    session.setAttribute("staffId", staff.getId());
                    session.setAttribute("staffName", staff.getFullName());
                });
                redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công! Xin chào, " + username);
                return "redirect:/staff/dashboard";

            case "MANAGER":
                // Manager không có entity riêng, dùng User ID làm managerId
                session.setAttribute("managerId", user.getId());
                session.setAttribute("managerName", user.getFullName() != null ? user.getFullName() : username);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đăng nhập thành công! Xin chào Manager " + username);
                return "redirect:/manager/dashboard";

            case "ADMIN":
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đăng nhập thành công! Xin chào Admin " + username);
                return "redirect:/admin/dashboard";

            case "SHIPPER":
                shipperRepository.findByUserId(user.getId()).ifPresent(shipper -> {
                    session.setAttribute("shipperId", shipper.getId());
                    session.setAttribute("shipperName", shipper.getFullName());
                });
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đăng nhập thành công! Xin chào Shipper " + username);
                return "redirect:/shipper/dashboard";

            default:
                return "redirect:/auth/login";
        }
    }

    /**
     * Redirect theo role từ Authentication object
     */
    private String redirectByRole(Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            return "redirect:/manager/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
            return "redirect:/staff/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            return "redirect:/customer/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SHIPPER"))) {
            return "redirect:/shipper/dashboard";
        }
        return "redirect:/auth/login";
    }
}
