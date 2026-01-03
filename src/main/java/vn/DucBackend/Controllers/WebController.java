package vn.DucBackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.DucBackend.DTO.CaseStudyDTO;
import vn.DucBackend.DTO.CustomerDTO;
import vn.DucBackend.DTO.ServiceTypeDTO;
import vn.DucBackend.Services.CaseStudyService;
import vn.DucBackend.Services.CustomerService;
import vn.DucBackend.Services.ServiceTypeService;

import java.util.Optional;

/**
 * Web Controller - Chỉ xử lý các trang PUBLIC (không cần đăng nhập)
 * Các route khác đã được chuyển sang:
 * - AdminController (/admin/*)
 * - CustomerController (/customer/*)
 * - ManagerController (/manager/*)
 * - ShipperController (/driver/*)
 * - AuthController (/auth/*)
 */
@Controller
public class WebController {

    @Autowired
    private CaseStudyService caseStudyService;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private CustomerService customerService;

    // ==========================================
    // TRANG CHỦ & GIỚI THIỆU
    // ==========================================
    @GetMapping("/")
    public String homePage(Model model) {
        // Lấy case studies nổi bật (featured + published) để hiển thị trên trang chủ
        model.addAttribute("caseStudies", caseStudyService.findFeaturedCaseStudies());
        return "public/home";
    }

    @GetMapping({ "/about", "/public/about" })
    public String aboutPage() {
        return "public/about";
    }

    // ==========================================
    // DỊCH VỤ (PUBLIC - KHÔNG CẦN ĐĂNG NHẬP)
    // ==========================================
    @GetMapping("/services")
    public String servicesPage(Model model) {
        model.addAttribute("services", serviceTypeService.findActive());
        return "public/services";
    }

    @GetMapping("/services/{slug}")
    public String serviceDetail(@PathVariable String slug, Model model) {
        ServiceTypeDTO service = serviceTypeService.findBySlug(slug).orElse(null);
        if (service == null || !Boolean.TRUE.equals(service.getIsActive())) {
            return "redirect:/services";
        }
        model.addAttribute("service", service);
        model.addAttribute("allServices", serviceTypeService.findActive());
        return "public/service-detail";
    }

    // ==========================================
    // CASE STUDY DETAIL (PUBLIC - KHÔNG CẦN ĐĂNG NHẬP)
    // ==========================================
    @GetMapping("/casestudy/{slug}")
    public String caseStudyDetail(@PathVariable String slug, Model model) {
        CaseStudyDTO caseStudy = caseStudyService.findBySlug(slug).orElse(null);
        if (caseStudy == null || !Boolean.TRUE.equals(caseStudy.getIsPublished())) {
            return "redirect:/";
        }
        model.addAttribute("caseStudy", caseStudy);
        // Lấy thêm các case study khác để hiển thị sidebar
        model.addAttribute("relatedCaseStudies", caseStudyService.findPublishedCaseStudies());
        return "public/casestudy-detail";
    }

    // ==========================================
    // GỬI YÊU CẦU VẬN CHUYỂN (KHÁCH VÃNG LAI)
    // ==========================================
    @GetMapping("/request")
    public String showRequestPage() {
        return "public/request";
    }

    @PostMapping("/request")
    public String handleRequestSubmit(
            @RequestParam("customerName") String customerName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "weight", required = false) String weight,
            @RequestParam(value = "vehicleType", required = false) String vehicleType,
            @RequestParam("pickupAddress") String pickupAddress,
            @RequestParam("deliveryAddress") String deliveryAddress,
            @RequestParam(value = "note", required = false) String note,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Tìm hoặc tạo customer từ số điện thoại
            Optional<CustomerDTO> customerOpt = customerService.findByPhone(phoneNumber.trim());
            CustomerDTO customer;
            
            if (customerOpt.isPresent()) {
                customer = customerOpt.get();
                // Cập nhật thông tin nếu có thay đổi
                if (email != null && !email.trim().isEmpty() && 
                    (customer.getEmail() == null || customer.getEmail().isEmpty())) {
                    customer.setEmail(email.trim());
                    customerService.updateCustomer(customer.getId(), customer);
                }
            } else {
                // Tạo customer mới cho khách vãng lai (không có user account)
                CustomerDTO newCustomer = new CustomerDTO();
                newCustomer.setName(customerName.trim());
                newCustomer.setFullName(customerName.trim());
                newCustomer.setPhone(phoneNumber.trim());
                newCustomer.setEmail(email != null ? email.trim() : null);
                newCustomer.setAddress(pickupAddress.trim()); // Dùng địa chỉ lấy hàng làm địa chỉ mặc định
                customer = customerService.createCustomer(newCustomer);
            }
            
            // Lưu thông tin request vào note hoặc log (có thể tạo bảng guest_requests sau)
            // Hiện tại chỉ thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", 
                "Cảm ơn bạn đã gửi yêu cầu! Chúng tôi sẽ liên hệ với số điện thoại " + phoneNumber + 
                " trong vòng 30 phút để xác nhận và báo giá chi tiết.");
            
            return "redirect:/?success=true";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại hoặc liên hệ hotline.");
            return "redirect:/request?error=true";
        }
    }

    // ==========================================
    // TRANG DÙNG CHUNG
    // ==========================================
    @GetMapping("/tracking")
    public String trackingPage() {
        return "common/tracking";
    }

    @GetMapping("/order-detail")
    public String orderDetailPage() {
        return "common/order-detail";
    }

    @GetMapping("/user/profile")
    public String profilePage() {
        return "user/profile";
    }
}
