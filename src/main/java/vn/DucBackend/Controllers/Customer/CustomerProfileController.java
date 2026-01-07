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
 * =============================================================================
 * CUSTOMER PROFILE CONTROLLER
 * =============================================================================
 * 
 * Controller xử lý hồ sơ cá nhân cho Customer (Khách hàng)
 * 
 * URLS:
 * - GET /customer/profile : Xem hồ sơ cá nhân
 * - POST /customer/profile : Cập nhật hồ sơ cá nhân
 * 
 * CHỨC NĂNG:
 * - Hiển thị thông tin cá nhân (tên, SĐT, email, địa chỉ...)
 * - Cho phép chỉnh sửa các thông tin trên
 * 
 * SERVICES SỬ DỤNG:
 * - CustomerService: CRUD thông tin customer
 * 
 * =============================================================================
 */
@Controller
@RequestMapping("/customer")
public class CustomerProfileController {

    // =========================================================================
    // DEPENDENCY INJECTION
    // =========================================================================

    /** Service xử lý thông tin khách hàng */
    @Autowired
    private CustomerService customerService;

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

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

    // =========================================================================
    // ENDPOINT: XEM HỒ SƠ
    // =========================================================================

    /**
     * XEM HỒ SƠ CÁ NHÂN
     * 
     * URL: GET /customer/profile
     * 
     * @param model   Model để truyền dữ liệu
     * @param session Session chứa customerId
     * @return Template "customer/profile"
     */
    @GetMapping("/profile")
    public String viewProfile(Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        // Lấy thông tin customer từ database
        CustomerDTO customer = customerService.findCustomerById(customerId).orElse(null);
        if (customer == null) {
            return "redirect:/customer/dashboard";
        }

        model.addAttribute("customer", customer);
        return "customer/profile";
    }

    // =========================================================================
    // ENDPOINT: CẬP NHẬT HỒ SƠ
    // =========================================================================

    /**
     * CẬP NHẬT HỒ SƠ CÁ NHÂN
     * 
     * URL: POST /customer/profile
     * 
     * LUỒNG XỬ LÝ:
     * 1. Kiểm tra đăng nhập
     * 2. Lấy thông tin customer hiện tại
     * 3. Cập nhật các trường được gửi lên (nếu có giá trị)
     * 4. Lưu vào database
     * 5. Redirect với thông báo thành công/thất bại
     * 
     * @param fullName           Họ tên đầy đủ
     * @param phone              Số điện thoại
     * @param email              Email
     * @param address            Địa chỉ
     * @param companyName        Tên công ty (nếu là doanh nghiệp)
     * @param gender             Giới tính
     * @param redirectAttributes Flash attributes để hiển thị thông báo
     * @return Redirect về /customer/profile
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
            // Lấy thông tin customer hiện tại
            CustomerDTO customer = customerService.findCustomerById(customerId).orElse(null);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin khách hàng!");
                return "redirect:/customer/dashboard";
            }

            // Cập nhật các trường (chỉ cập nhật nếu có giá trị mới)
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

            // Lưu vào database
            customerService.updateCustomer(customerId, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }

        return "redirect:/customer/profile";
    }
}
