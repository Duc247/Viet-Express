package vn.DucBackend.Controllers.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.StaffRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller xử lý đăng nhập cho Customer và Staff
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

    /**
     * Hiển thị trang đăng nhập
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        // Nếu đã đăng nhập rồi thì redirect về trang chính
        if (session.getAttribute("loggedInUser") != null) {
            User user = (User) session.getAttribute("loggedInUser");
            return redirectByRole(user.getRole().getRoleName());
        }
        return "auth/login";
    }

    /**
     * Xử lý đăng nhập
     */
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Tìm user theo username
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập không tồn tại!");
            return "redirect:/auth/login";
        }

        User user = userOpt.get();

        // Kiểm tra mật khẩu (plain text - chưa mã hóa)
        if (!user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không đúng!");
            return "redirect:/auth/login";
        }

        // Kiểm tra tài khoản có active không
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản đã bị khóa!");
            return "redirect:/auth/login";
        }

        String roleName = user.getRole().getRoleName();

        // Cho phép các role: CUSTOMER, STAFF, MANAGER, ADMIN, SHIPPER
        java.util.List<String> allowedRoles = java.util.Arrays.asList(
                "CUSTOMER", "STAFF", "MANAGER", "ADMIN", "SHIPPER");
        if (!allowedRoles.contains(roleName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền đăng nhập vào hệ thống này!");
            return "redirect:/auth/login";
        }

        // Cập nhật thời gian đăng nhập
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Lưu thông tin vào session
        session.setAttribute("loggedInUser", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("roleName", roleName);

        // Lưu thông tin chi tiết theo role
        if (roleName.equals("CUSTOMER")) {
            Optional<Customer> customerOpt = customerRepository.findByUserId(user.getId());
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                session.setAttribute("customerId", customer.getId());
                session.setAttribute("customerName", customer.getFullName());
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng nhập thành công! Xin chào, " + user.getUsername());
            return "redirect:/customer/dashboard";

        } else if (roleName.equals("STAFF")) {
            Optional<Staff> staffOpt = staffRepository.findByUserId(user.getId());
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                session.setAttribute("staffId", staff.getId());
                session.setAttribute("staffName", staff.getFullName());
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng nhập thành công! Xin chào, " + user.getUsername());
            return "redirect:/staff/dashboard";

        } else if (roleName.equals("MANAGER")) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng nhập thành công! Xin chào Manager " + user.getUsername());
            return "redirect:/manager/dashboard";

        } else if (roleName.equals("ADMIN")) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng nhập thành công! Xin chào Admin " + user.getUsername());
            return "redirect:/admin/dashboard";

        } else if (roleName.equals("SHIPPER")) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng nhập thành công! Xin chào Shipper " + user.getUsername());
            return "redirect:/shipper/dashboard";
        }

        return "redirect:/auth/login";
    }

    /**
     * Đăng xuất
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Đã đăng xuất thành công!");
        return "redirect:/auth/login";
    }

    /**
     * Redirect theo role
     */
    private String redirectByRole(String roleName) {
        switch (roleName) {
            case "CUSTOMER":
                return "redirect:/customer/dashboard";
            case "STAFF":
                return "redirect:/staff/dashboard";
            case "MANAGER":
                return "redirect:/manager/dashboard";
            case "ADMIN":
                return "redirect:/admin/dashboard";
            case "SHIPPER":
                return "redirect:/shipper/dashboard";
            default:
                return "redirect:/auth/login";
        }
    }
}
