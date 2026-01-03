package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.DTO.CustomerDTO;
import vn.DucBackend.Services.CustomerService;

/**
 * Customer Profile Controller
 * Xử lý xem và chỉnh sửa hồ sơ customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerProfileController {

    @Autowired
    private CustomerService customerService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getCustomerIdFromSession(HttpSession session) {
        Object customerId = session.getAttribute("customerId");
        if (customerId != null) {
            return (Long) customerId;
        }
        return null;
    }

    /**
     * Xem hồ sơ customer
     */
    @GetMapping("/profile")
    public String viewProfile(Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        CustomerDTO customer = customerService.findCustomerById(customerId).orElse(null);
        if (customer == null) {
            return "redirect:/customer/dashboard";
        }

        model.addAttribute("customer", customer);
        return "customer/profile";
    }

    /**
     * Cập nhật hồ sơ customer
     */
    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "gender", required = false) String gender,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        try {
            CustomerDTO customer = customerService.findCustomerById(customerId).orElse(null);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin khách hàng!");
                return "redirect:/customer/dashboard";
            }

            // Cập nhật thông tin
            if (fullName != null && !fullName.trim().isEmpty()) {
                customer.setFullName(fullName.trim());
                customer.setName(fullName.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                customer.setPhone(phone.trim());
            }
            if (email != null && !email.trim().isEmpty()) {
                customer.setEmail(email.trim());
            }
            if (address != null) {
                customer.setAddress(address.trim());
            }
            if (companyName != null) {
                customer.setCompanyName(companyName.trim());
            }
            if (gender != null) {
                customer.setGender(gender);
            }

            customerService.updateCustomer(customerId, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }

        return "redirect:/customer/profile";
    }
}

