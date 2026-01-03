package vn.DucBackend.Controllers;

import java.math.BigDecimal;
import java.util.Optional;
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
import vn.DucBackend.DTO.CustomerRequestDTO;
import vn.DucBackend.DTO.ServiceTypeDTO;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ServiceTypeRepository;
import vn.DucBackend.Services.CaseStudyService;
import vn.DucBackend.Services.CustomerRequestService;
import vn.DucBackend.Services.CustomerService;
import vn.DucBackend.Services.EmailService;
import vn.DucBackend.Services.ServiceTypeService;

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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerRequestService customerRequestService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private EmailService emailService;
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
    @GetMapping({"/request", "/public/request"})
    public String showRequestPage() {
        return "public/request";
    }

    @PostMapping({"/request", "/public/request"})
    public String handleRequestSubmit(
            @RequestParam("customerName") String customerName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "weight", required = false) String weightStr,
            @RequestParam(value = "vehicleType", required = false) String vehicleType,
            @RequestParam("pickupAddress") String pickupAddress,
            @RequestParam("deliveryAddress") String deliveryAddress,
            @RequestParam(value = "note", required = false) String note,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Tìm hoặc tạo customer
            Customer customer;
            Optional<Customer> existingCustomerOpt = customerRepository.findByPhone(phoneNumber.trim());
            
            if (existingCustomerOpt.isPresent()) {
                customer = existingCustomerOpt.get();
            } else {
                // Tạo customer mới (không có user)
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setName(customerName.trim());
                customerDTO.setFullName(customerName.trim());
                customerDTO.setPhone(phoneNumber.trim());
                customerDTO.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
                customerDTO.setAddress(pickupAddress.trim()); // Dùng pickup address làm địa chỉ mặc định
                
                CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
                customer = customerRepository.findById(createdCustomer.getId())
                        .orElseThrow(() -> new RuntimeException("Failed to create customer"));
            }
            
            // Lấy service type mặc định (STANDARD hoặc service đầu tiên)
            Long serviceTypeId = serviceTypeRepository.findByIsActiveTrue().stream()
                    .findFirst()
                    .map(st -> st.getId())
                    .orElseThrow(() -> new RuntimeException("No active service type found"));
            
            // Tạo location cho sender (pickup)
            Location senderLocation = new Location();
            senderLocation.setLocationType(Location.LocationType.SENDER);
            senderLocation.setName(customerName.trim());
            senderLocation.setAddressText(pickupAddress.trim());
            senderLocation.setIsActive(true);
            senderLocation = locationRepository.save(senderLocation);
            
            // Tạo location cho receiver (delivery)
            Location receiverLocation = new Location();
            receiverLocation.setLocationType(Location.LocationType.RECEIVER);
            receiverLocation.setName(customerName.trim());
            receiverLocation.setAddressText(deliveryAddress.trim());
            receiverLocation.setIsActive(true);
            receiverLocation = locationRepository.save(receiverLocation);
            
            // Tạo request DTO
            CustomerRequestDTO requestDTO = new CustomerRequestDTO();
            requestDTO.setSenderId(customer.getId());
            requestDTO.setReceiverId(customer.getId()); // Người nhận = người gửi
            requestDTO.setSenderLocationId(senderLocation.getId());
            requestDTO.setReceiverLocationId(receiverLocation.getId());
            requestDTO.setServiceTypeId(serviceTypeId);
            
            // Mô tả hàng hóa
            StringBuilder description = new StringBuilder();
            if (productName != null && !productName.trim().isEmpty()) {
                description.append(productName.trim());
            }
            if (weightStr != null && !weightStr.trim().isEmpty()) {
                if (description.length() > 0) description.append(" - ");
                description.append("Trọng lượng: ").append(weightStr.trim()).append(" kg");
            }
            if (vehicleType != null && !vehicleType.trim().isEmpty()) {
                if (description.length() > 0) description.append(" - ");
                description.append("Loại xe: ").append(vehicleType.trim());
            }
            requestDTO.setParcelDescription(description.length() > 0 ? description.toString() : "Hàng hóa");
            
            // Ghi chú
            if (note != null && !note.trim().isEmpty()) {
                requestDTO.setNote(note.trim());
            }
            
            // Distance mặc định 10km (có thể tính toán sau)
            requestDTO.setDistanceKm(new BigDecimal("10.0"));
            requestDTO.setCodAmount(BigDecimal.ZERO);
            
            // Tạo request
            CustomerRequestDTO createdRequest = customerRequestService.createRequest(requestDTO);
            
            // Gửi email thông báo (nếu có email)
            if (email != null && !email.trim().isEmpty()) {
                try {
                    String emailContent = String.format("""
                        <h2>Yêu cầu vận chuyển của bạn đã được tiếp nhận!</h2>
                        <p>Xin chào %s,</p>
                        <p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. Yêu cầu vận chuyển của bạn đã được ghi nhận với mã đơn: <strong>%s</strong></p>
                        <p>Chúng tôi sẽ liên hệ với bạn trong vòng 30 phút để xác nhận thông tin và báo giá chi tiết.</p>
                        <hr>
                        <p><strong>Thông tin yêu cầu:</strong></p>
                        <ul>
                            <li>Điểm lấy hàng: %s</li>
                            <li>Điểm giao hàng: %s</li>
                            <li>Mô tả: %s</li>
                        </ul>
                        <p>Trân trọng,<br>Đội ngũ Logistics</p>
                        """, customerName, createdRequest.getRequestCode(), pickupAddress, deliveryAddress, requestDTO.getParcelDescription());
                    
                    emailService.sendHtmlEmail(email.trim(), 
                        "Yêu cầu vận chuyển đã được tiếp nhận - " + createdRequest.getRequestCode(), 
                        emailContent);
                } catch (Exception e) {
                    // Log lỗi nhưng không fail request
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Yêu cầu vận chuyển của bạn đã được tiếp nhận! Mã đơn: " + createdRequest.getRequestCode() + 
                ". Chúng tôi sẽ liên hệ với bạn sớm nhất có thể.");
            return "redirect:/?success=true";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại hoặc liên hệ hotline.");
            return "redirect:/request";
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
