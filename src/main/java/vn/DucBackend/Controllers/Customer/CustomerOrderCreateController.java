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
import vn.DucBackend.DTO.CustomerRequestDTO;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ServiceTypeRepository;
import vn.DucBackend.Services.CustomerRequestService;
import vn.DucBackend.Services.TrackingService;
import vn.DucBackend.Utils.LoggingHelper;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * =============================================================================
 * CUSTOMER ORDER CREATE CONTROLLER
 * =============================================================================
 * 
 * Controller xử lý tạo đơn hàng mới cho Customer (Khách hàng)
 * 
 * URLS:
 * - GET /customer/create-order : Hiển thị form tạo đơn
 * - POST /customer/create-order : Xử lý tạo đơn hàng
 * 
 * LUỒNG TẠO ĐƠN HÀNG:
 * 1. Customer điền form (senderPhone, receiverPhone, địa chỉ, mô tả hàng...)
 * 2. Validate các trường bắt buộc
 * 3. Tìm Customer sender và receiver theo số điện thoại
 * 4. Tạo Location cho sender và receiver
 * 5. Tạo CustomerRequest với status = PENDING
 * 6. Ghi tracking log (CREATED)
 * 7. Redirect về danh sách đơn hàng
 * 
 * SERVICES & REPOSITORIES SỬ DỤNG:
 * - CustomerRequestService: Tạo đơn hàng
 * - LocationRepository: Lưu địa chỉ gửi/nhận
 * - CustomerRepository: Tìm khách hàng theo SĐT
 * - ServiceTypeRepository: Lấy danh sách loại dịch vụ
 * - TrackingService: Ghi log action CREATED
 * - LoggingHelper: Ghi log hệ thống
 * 
 * =============================================================================
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderCreateController {

    // =========================================================================
    // DEPENDENCY INJECTION
    // =========================================================================

    /** Service xử lý đơn hàng - tạo mới, tính phí ship */
    @Autowired
    private CustomerRequestService customerRequestService;

    /** Repository địa điểm - lưu địa chỉ sender/receiver */
    @Autowired
    private LocationRepository locationRepository;

    /** Repository loại dịch vụ - lấy danh sách để chọn */
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    /** Repository khách hàng - tìm theo SĐT */
    @Autowired
    private CustomerRepository customerRepository;

    /** Helper ghi log hệ thống */
    @Autowired
    private LoggingHelper loggingHelper;

    /** Service tracking - ghi action CREATED */
    @Autowired
    private TrackingService trackingService;

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
    // ENDPOINT: HIỂN THỊ FORM TẠO ĐƠN
    // =========================================================================

    /**
     * HIỂN THỊ FORM TẠO ĐƠN HÀNG
     * 
     * URL: GET /customer/create-order
     * 
     * @param model   Model để truyền dữ liệu
     * @param request HttpRequest
     * @param session Session chứa customerId
     * @return Template form tạo đơn
     */
    @GetMapping("/create-order")
    public String showCreateOrderForm(Model model, HttpServletRequest request, HttpSession session) {
        // Kiểm tra session - phải đăng nhập mới được tạo đơn
        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        addCommonAttributes(model, request);
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        model.addAttribute("customerId", customerId);

        // Lấy thông tin customer để pre-fill form (tự động điền SĐT, tên)
        customerRepository.findById(customerId).ifPresent(customer -> {
            model.addAttribute("currentCustomer", customer);
        });

        return "customer/order/create-order";
    }

    // =========================================================================
    // ENDPOINT: XỬ LÝ TẠO ĐƠN HÀNG
    // =========================================================================

    /**
     * XỬ LÝ TẠO ĐƠN HÀNG MỚI
     * 
     * URL: POST /customer/create-order
     * 
     * LUỒNG XỬ LÝ:
     * 1. Validate các trường bắt buộc (SĐT, địa chỉ, mô tả)
     * 2. Tìm Customer sender theo senderPhone
     * 3. Tìm Customer receiver theo receiverPhone
     * 4. Tạo Location entity cho sender và receiver
     * 5. Tạo CustomerRequestDTO và gọi service tạo đơn
     * 6. Ghi tracking log với action = CREATED
     * 7. Redirect với thông báo thành công
     * 
     * @param senderName         Tên người gửi
     * @param senderPhone        SĐT người gửi (BẮT BUỘC - dùng để tìm Customer)
     * @param senderAddress      Địa chỉ lấy hàng (BẮT BUỘC)
     * @param receiverName       Tên người nhận
     * @param receiverPhone      SĐT người nhận (BẮT BUỘC - dùng để tìm Customer)
     * @param receiverAddress    Địa chỉ giao hàng (BẮT BUỘC)
     * @param productDescription Mô tả hàng hóa (BẮT BUỘC)
     * @param serviceTypeId      ID loại dịch vụ (Express, Standard...)
     * @param codAmount          Tiền thu hộ COD (mặc định 0)
     * @param distanceKm         Khoảng cách (dùng tính phí ship)
     * @param note               Ghi chú
     * @param weight             Trọng lượng
     * @param redirectAttributes Để gửi flash message
     * @return Redirect về /customer/orders
     */
    @PostMapping("/create-order")
    public String createOrder(
            @RequestParam(value = "senderName", required = false) String senderName,
            @RequestParam(value = "senderPhone", required = false) String senderPhone,
            @RequestParam(value = "senderAddress", required = false) String senderAddress,
            @RequestParam(value = "receiverName", required = false) String receiverName,
            @RequestParam(value = "receiverPhone", required = false) String receiverPhone,
            @RequestParam(value = "receiverAddress", required = false) String receiverAddress,
            @RequestParam(value = "productDescription", required = false) String productDescription,
            @RequestParam(value = "serviceTypeId", required = false) Long serviceTypeId,
            @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
            @RequestParam(value = "distanceKm", required = false) BigDecimal distanceKm,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "weight", required = false) BigDecimal weight,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra session
        Long sessionCustomerId = getCustomerIdFromSession(session);
        if (sessionCustomerId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!");
            return "redirect:/auth/login";
        }

        // Validate số điện thoại người gửi
        if (senderPhone == null || senderPhone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập số điện thoại người gửi!");
            return "redirect:/customer/create-order";
        }

        // Validate số điện thoại người nhận (bắt buộc)
        if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập số điện thoại người nhận!");
            return "redirect:/customer/create-order";
        }

        // Validate địa chỉ người gửi
        if (senderAddress == null || senderAddress.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập địa chỉ người gửi!");
            return "redirect:/customer/create-order";
        }

        // Validate địa chỉ người nhận (bắt buộc)
        if (receiverAddress == null || receiverAddress.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập địa chỉ người nhận!");
            return "redirect:/customer/create-order";
        }

        if (productDescription == null || productDescription.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập mô tả hàng hóa!");
            return "redirect:/customer/create-order";
        }

        // Tìm sender theo số điện thoại
        Optional<Customer> senderOpt = customerRepository.findByPhone(senderPhone.trim());
        if (senderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy khách hàng với số điện thoại người gửi: " + senderPhone);
            return "redirect:/customer/create-order";
        }
        Customer sender = senderOpt.get();

        // Tìm receiver theo số điện thoại (hoặc dùng sender nếu cùng SĐT)
        Customer receiver;
        if (receiverPhone.trim().equals(senderPhone.trim())) {
            receiver = sender; // Người nhận = người gửi
        } else {
            Optional<Customer> receiverOpt = customerRepository.findByPhone(receiverPhone.trim());
            if (receiverOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Không tìm thấy khách hàng với số điện thoại người nhận: " + receiverPhone);
                return "redirect:/customer/create-order";
            }
            receiver = receiverOpt.get();
        }

        // Validate service type
        if (serviceTypeId == null) {
            serviceTypeId = serviceTypeRepository.findAll().stream()
                    .findFirst()
                    .map(st -> st.getId())
                    .orElse(null);
        }

        if (serviceTypeId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy loại dịch vụ phù hợp!");
            return "redirect:/customer/create-order";
        }

        // Distance - nếu không có thì đặt mặc định
        if (distanceKm == null || distanceKm.compareTo(BigDecimal.ZERO) <= 0) {
            distanceKm = new BigDecimal("10.0"); // Default 10km
        }

        try {
            // Tạo location cho sender
            Location senderLocation = new Location();
            senderLocation.setLocationType(Location.LocationType.SENDER);
            senderLocation
                    .setName(senderName != null && !senderName.trim().isEmpty() ? senderName.trim() : sender.getName());
            senderLocation.setAddressText(senderAddress.trim());
            senderLocation.setIsActive(true);
            senderLocation = locationRepository.save(senderLocation);

            // Tạo location cho receiver
            Location receiverLocation = new Location();
            receiverLocation.setLocationType(Location.LocationType.RECEIVER);
            receiverLocation.setName(
                    receiverName != null && !receiverName.trim().isEmpty() ? receiverName.trim() : receiver.getName());
            receiverLocation.setAddressText(receiverAddress.trim());
            receiverLocation.setIsActive(true);
            receiverLocation = locationRepository.save(receiverLocation);

            // Tạo request DTO
            CustomerRequestDTO requestDTO = new CustomerRequestDTO();
            requestDTO.setSenderId(sender.getId());
            requestDTO.setReceiverId(receiver.getId());
            requestDTO.setSenderLocationId(senderLocation.getId());
            requestDTO.setReceiverLocationId(receiverLocation.getId());
            requestDTO.setServiceTypeId(serviceTypeId);
            requestDTO.setParcelDescription(productDescription.trim());
            requestDTO.setCodAmount(codAmount);
            requestDTO.setNote(note);
            requestDTO.setDistanceKm(distanceKm);

            CustomerRequestDTO createdRequest = customerRequestService.createRequest(requestDTO);

            // Ghi action CREATED vào ParcelAction để tracking
            Long actorUserId = sender.getUser() != null ? sender.getUser().getId() : null;
            try {
                trackingService.logCreated(createdRequest.getId(), actorUserId);
            } catch (Exception e) {
                // Không fail request nếu ghi log thất bại
                System.err.println("Failed to log CREATED action: " + e.getMessage());
            }

            // Ghi log tạo đơn thành công - sử dụng User ID từ Customer
            loggingHelper.logOrderCreated(actorUserId, createdRequest.getRequestCode(), request);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo đơn hàng thành công! Mã vận đơn: " + createdRequest.getRequestCode());

            return "redirect:/customer/orders";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi tạo đơn hàng: " + e.getMessage());
            return "redirect:/customer/create-order";
        }
    }
}
