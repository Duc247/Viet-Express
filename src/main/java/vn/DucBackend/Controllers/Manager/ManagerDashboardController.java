package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Services.*;
import vn.DucBackend.Repositories.UserRepository;

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

    @Autowired
    private UserRepository userRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model, HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        addCommonAttributes(model, request);

        // Lấy user hiện tại
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser != null) {
            Long managerId = currentUser.getId();

            // Đếm số đơn được gán cho manager này
            var assignedRequests = customerRequestService.findByAssignedManager(managerId);
            model.addAttribute("requestCount", assignedRequests.size());

            // Đếm số đơn MỚI được giao (trong 24h)
            Long newAssignments = customerRequestService.countNewAssignmentsForManager(managerId);
            model.addAttribute("newAssignmentCount", newAssignments);

            // Thông báo đơn mới
            if (newAssignments > 0) {
                model.addAttribute("hasNewAssignments", true);
                model.addAttribute("newAssignmentMessage",
                        "Bạn có " + newAssignments + " đơn hàng mới được giao trong 24h qua!");
            }
        } else {
            model.addAttribute("requestCount", 0);
            model.addAttribute("newAssignmentCount", 0);
        }

        // Các thống kê chung
        model.addAttribute("parcelCount", parcelService.findAllParcels().size());
        model.addAttribute("tripCount", tripService.findAllTrips().size());
        model.addAttribute("paymentCount", paymentService.findAllPayments().size());
        model.addAttribute("locationCount", locationService.findAllLocations().size());

        return "manager/dashboard";
    }

    // ==========================================
    // QUẢN LÝ ĐƠN HÀNG (alias) - REDIRECT TO REQUESTS
    // ==========================================
    @GetMapping("/orders")
    public String orderList() {
        return "redirect:/manager/requests";
    }
}
