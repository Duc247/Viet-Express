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
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.LoggingHelper;

import java.math.BigDecimal;

/**
 * Controller xử lý tạo đơn hàng mới cho Customer
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderCreateController {

    @Autowired
    private CustomerRequestService customerRequestService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ServiceTypeService serviceTypeService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoggingHelper loggingHelper;

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

    @GetMapping("/create-order")
    public String showCreateOrderForm(Model model, HttpServletRequest request, HttpSession session) {
        // Kiểm tra session - phải đăng nhập mới được tạo đơn
        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        addCommonAttributes(model, request);
        model.addAttribute("serviceTypes", serviceTypeService.findActiveEntities());
        model.addAttribute("customerId", customerId);

        // Lấy thông tin customer để pre-fill form
        Customer customer = customerService.getCustomerEntityById(customerId);
        if (customer != null) {
            model.addAttribute("currentCustomer", customer);
        }

        return "customer/order/create-order";
    }

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
            @RequestParam(value = "parcelCount", defaultValue = "1") Integer parcelCount,
            @RequestParam(value = "lengthCm", required = false) BigDecimal lengthCm,
            @RequestParam(value = "widthCm", required = false) BigDecimal widthCm,
            @RequestParam(value = "heightCm", required = false) BigDecimal heightCm,
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
        Customer sender = customerService.getCustomerEntityByPhone(senderPhone.trim());
        if (sender == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy khách hàng với số điện thoại người gửi: " + senderPhone);
            return "redirect:/customer/create-order";
        }

        // Tìm receiver theo số điện thoại (hoặc dùng sender nếu cùng SĐT)
        Customer receiver;
        if (receiverPhone.trim().equals(senderPhone.trim())) {
            receiver = sender; // Người nhận = người gửi
        } else {
            receiver = customerService.getCustomerEntityByPhone(receiverPhone.trim());
            if (receiver == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Không tìm thấy khách hàng với số điện thoại người nhận: " + receiverPhone);
                return "redirect:/customer/create-order";
            }
        }

        // Validate service type - get from request parameter
        if (serviceTypeId == null) {
            // Try to get from serviceType parameter (old format)
            String serviceTypeStr = request.getParameter("serviceType");
            if (serviceTypeStr != null && !serviceTypeStr.trim().isEmpty()) {
                // Map old service type codes to IDs
                serviceTypeId = serviceTypeService.findEntityByCode(serviceTypeStr.trim())
                        .map(st -> st.getId())
                        .orElse(null);
            }
        }

        if (serviceTypeId == null) {
            serviceTypeId = serviceTypeService.findActiveEntities().stream()
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
            senderLocation = locationService.saveLocationEntity(senderLocation);

            // Tạo location cho receiver
            Location receiverLocation = new Location();
            receiverLocation.setLocationType(Location.LocationType.RECEIVER);
            receiverLocation.setName(
                    receiverName != null && !receiverName.trim().isEmpty() ? receiverName.trim() : receiver.getName());
            receiverLocation.setAddressText(receiverAddress.trim());
            receiverLocation.setIsActive(true);
            receiverLocation = locationService.saveLocationEntity(receiverLocation);

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

            // Ghi log tạo đơn thành công - sử dụng User ID từ Customer
            Long actorUserId = sender.getUser() != null ? sender.getUser().getId() : null;
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
