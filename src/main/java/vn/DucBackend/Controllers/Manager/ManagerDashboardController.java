package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Services.*;
import vn.DucBackend.Repositories.CustomerRequestRepository;

/**
 * Manager Dashboard Controller - Xử lý trang dashboard cho Manager
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/manager")
public class ManagerDashboardController {

    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private TripService tripService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private LocationService locationService;

    // Repository giữ lại cho template data (Thymeleaf cần Entity)
    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        // Sử dụng Services cho counting
        model.addAttribute("requestCount", customerRequestService.findAllRequests().size());
        model.addAttribute("parcelCount", parcelService.findAllParcels().size());
        model.addAttribute("tripCount", tripService.findAllTrips().size());
        model.addAttribute("paymentCount", paymentService.findAllPayments().size());
        model.addAttribute("locationCount", locationService.findAllLocations().size());

        return "manager/dashboard";
    }

    // ==========================================
    // QUẢN LÝ ĐƠN HÀNG (alias)
    // ==========================================
    @GetMapping("/orders")
    public String orderList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        // Giữ Repository cho template vì cần Entity objects
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "manager/request/requests";
    }
}
