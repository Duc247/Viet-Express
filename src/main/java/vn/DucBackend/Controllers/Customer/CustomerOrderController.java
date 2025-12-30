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
import vn.DucBackend.DTO.CustomerRequestDTO;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ServiceTypeRepository;
import vn.DucBackend.Services.CustomerRequestService;

import java.math.BigDecimal;

/**
 * Controller xử lý việc tạo đơn hàng mới cho customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderController {

    @Autowired
    private CustomerRequestService customerRequestService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    /**
     * Hiển thị form tạo đơn hàng
     */
    @GetMapping("/create-order")
    public String showCreateOrderForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        // Load danh sách service types để hiển thị trong form
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());

        return "customer/order/create-order";
    }

    /**
     * Xử lý việc tạo đơn hàng mới
     */
    @PostMapping("/create-order")
    public String createOrder(
            @RequestParam("senderAddress") String senderAddress,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("productDescription") String productDescription,
            @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "weight", required = false) BigDecimal weight,
            RedirectAttributes redirectAttributes) {

        try {
            // Bước 1: Tạo Location cho người gửi
            Location senderLocation = new Location();
            senderLocation.setLocationType(Location.LocationType.SENDER);
            senderLocation.setName("Địa chỉ gửi hàng");
            senderLocation.setAddressText(senderAddress);
            senderLocation.setIsActive(true);
            senderLocation = locationRepository.save(senderLocation);

            // Bước 2: Tạo Location cho người nhận
            Location receiverLocation = new Location();
            receiverLocation.setLocationType(Location.LocationType.RECEIVER);
            receiverLocation.setName(receiverName);
            receiverLocation.setAddressText(receiverAddress);
            receiverLocation.setIsActive(true);
            receiverLocation = locationRepository.save(receiverLocation);

            // Bước 3: Tạo CustomerRequestDTO
            CustomerRequestDTO requestDTO = new CustomerRequestDTO();

            // TODO: Thay bằng customer đang đăng nhập
            // Tạm thời dùng customer đầu tiên trong DB
            Long senderId = customerRepository.findAll().stream()
                    .findFirst()
                    .map(c -> c.getId())
                    .orElse(1L);

            requestDTO.setSenderId(senderId);
            requestDTO.setSenderLocationId(senderLocation.getId());
            requestDTO.setReceiverLocationId(receiverLocation.getId());

            // TODO: Cho phép chọn service type từ form
            // Tạm thời dùng service type đầu tiên
            Long serviceTypeId = serviceTypeRepository.findAll().stream()
                    .findFirst()
                    .map(st -> st.getId())
                    .orElse(1L);
            requestDTO.setServiceTypeId(serviceTypeId);

            requestDTO.setParcelDescription(productDescription);
            requestDTO.setCodAmount(codAmount);
            requestDTO.setNote(note);

            // Tạm thời set khoảng cách mặc định (có thể tính từ API sau)
            requestDTO.setDistanceKm(new BigDecimal("10.0"));

            // Bước 4: Gọi service để tạo request
            CustomerRequestDTO createdRequest = customerRequestService.createRequest(requestDTO);

            // Thành công
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo đơn hàng thành công! Mã vận đơn: " + createdRequest.getRequestCode());

            return "redirect:/customer/orders";

        } catch (Exception e) {
            // Xử lý lỗi
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi tạo đơn hàng: " + e.getMessage());

            return "redirect:/customer/create-order";
        }
    }
}
