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
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ServiceTypeRepository;
import vn.DucBackend.Services.CustomerRequestService;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller xử lý tạo đơn hàng mới cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderCreateController {

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

    @GetMapping("/create-order")
    public String showCreateOrderForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        return "customer/order/create-order";
    }

    @PostMapping("/create-order")
    public String createOrder(
            @RequestParam("senderCode") String senderCode,
            @RequestParam("receiverCode") String receiverCode,
            @RequestParam("senderAddress") String senderAddress,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("productDescription") String productDescription,
            @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "weight", required = false) BigDecimal weight,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra mã người gửi
        if (senderCode == null || senderCode.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập mã khách hàng người gửi!");
            return "redirect:/customer/create-order";
        }

        Optional<Customer> senderOpt = customerRepository.findByCustomerCode(senderCode.trim());
        if (senderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mã khách hàng người gửi không tồn tại: " + senderCode);
            return "redirect:/customer/create-order";
        }

        // Kiểm tra mã người nhận
        if (receiverCode == null || receiverCode.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập mã khách hàng người nhận!");
            return "redirect:/customer/create-order";
        }

        Optional<Customer> receiverOpt = customerRepository.findByCustomerCode(receiverCode.trim());
        if (receiverOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mã khách hàng người nhận không tồn tại: " + receiverCode);
            return "redirect:/customer/create-order";
        }

        Customer sender = senderOpt.get();
        Customer receiver = receiverOpt.get();

        try {
            Location senderLocation = new Location();
            senderLocation.setLocationType(Location.LocationType.SENDER);
            senderLocation.setName(sender.getName());
            senderLocation.setAddressText(senderAddress);
            senderLocation.setIsActive(true);
            senderLocation = locationRepository.save(senderLocation);

            Location receiverLocation = new Location();
            receiverLocation.setLocationType(Location.LocationType.RECEIVER);
            receiverLocation.setName(receiver.getName());
            receiverLocation.setAddressText(receiverAddress);
            receiverLocation.setIsActive(true);
            receiverLocation = locationRepository.save(receiverLocation);

            CustomerRequestDTO requestDTO = new CustomerRequestDTO();
            requestDTO.setSenderId(sender.getId());
            requestDTO.setReceiverId(receiver.getId());
            requestDTO.setSenderLocationId(senderLocation.getId());
            requestDTO.setReceiverLocationId(receiverLocation.getId());

            Long serviceTypeId = serviceTypeRepository.findAll().stream()
                    .findFirst()
                    .map(st -> st.getId())
                    .orElse(1L);
            requestDTO.setServiceTypeId(serviceTypeId);

            requestDTO.setParcelDescription(productDescription);
            requestDTO.setCodAmount(codAmount);
            requestDTO.setNote(note);
            requestDTO.setDistanceKm(new BigDecimal("10.0"));

            CustomerRequestDTO createdRequest = customerRequestService.createRequest(requestDTO);

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
