package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Services.CustomerRequestService;
import vn.DucBackend.Services.PaymentService;

import java.util.List;

/**
 * =============================================================================
 * CUSTOMER DASHBOARD CONTROLLER
 * =============================================================================
 * 
 * Controller xử lý trang Dashboard cho Customer (Khách hàng)
 * 
 * URL: /customer/dashboard
 * 
 * CHỨC NĂNG:
 * - Hiển thị thống kê tổng quan đơn hàng
 * - Hiển thị danh sách đơn hàng gần đây
 * - Tính toán tổng tiền còn nợ
 * 
 * ANNOTATIONS:
 * - @Controller: Đánh dấu đây là Spring MVC Controller
 * - @RequestMapping("/customer"): Tất cả URL bắt đầu bằng /customer
 * 
 * SERVICES SỬ DỤNG:
 * - CustomerRequestService: Lấy danh sách đơn hàng
 * - PaymentService: Tính toán tiền thanh toán
 * 
 * =============================================================================
 */
@Controller
@RequestMapping("/customer")
public class CustomerDashboardController {

    // =========================================================================
    // DEPENDENCY INJECTION - Tiêm các Service cần thiết
    // =========================================================================

    /**
     * Service xử lý đơn hàng (CustomerRequest)
     * Dùng để: Lấy danh sách đơn hàng của customer
     */
    @Autowired
    private CustomerRequestService customerRequestService;

    /**
     * Service xử lý thanh toán (Payment)
     * Dùng để: Tính toán tổng tiền còn nợ
     */
    @Autowired
    private PaymentService paymentService;

    // =========================================================================
    // HELPER METHODS - Các hàm hỗ trợ
    // =========================================================================

    /**
     * Lấy Customer ID từ Session
     * 
     * Session là nơi lưu trữ thông tin đăng nhập của user trên server.
     * Khi user đăng nhập, customerId được lưu vào session.
     * 
     * @param session HttpSession chứa thông tin phiên làm việc
     * @return Customer ID nếu đã đăng nhập, null nếu chưa
     */
    private Long getCustomerIdFromSession(HttpSession session) {
        Object customerId = session.getAttribute("customerId");
        if (customerId != null) {
            return (Long) customerId;
        }
        return null;
    }

    // =========================================================================
    // ENDPOINTS - Các điểm cuối API
    // =========================================================================

    /**
     * TRANG DASHBOARD - Hiển thị tổng quan cho Customer
     * 
     * URL: GET /customer/dashboard
     * 
     * LUỒNG XỬ LÝ:
     * 1. Kiểm tra đăng nhập (customerId trong session)
     * 2. Lấy danh sách đơn hàng của customer
     * 3. Tính thống kê: tổng đơn, đơn chờ, đang giao, đã giao
     * 4. Tính tổng tiền còn nợ
     * 5. Đưa dữ liệu vào Model để Thymeleaf render
     * 
     * @param model   Model để truyền dữ liệu sang View (Thymeleaf)
     * @param session Session chứa thông tin đăng nhập
     * @return Tên template: "customer/dashboard" hoặc redirect về login
     * 
     *         ANNOTATIONS:
     *         - @GetMapping: Xử lý HTTP GET request
     *         - @Transactional(readOnly = true): Mở transaction read-only để tránh
     *         LazyInitializationException khi truy cập lazy-loaded properties
     */
    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String dashboard(Model model, HttpSession session) {
        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        // 1. Lấy danh sách đơn hàng gần đây
        List<CustomerRequest> recentOrders = customerRequestService.findByCustomerIdEntities(customerId);

        // 2. Tính toán thống kê
        long totalOrders = recentOrders.size();
        long pendingOrders = recentOrders.stream().filter(o -> "PENDING".equals(o.getStatus().name())).count();
        long inTransitOrders = recentOrders.stream().filter(o -> "IN_TRANSIT".equals(o.getStatus().name())).count();
        long deliveredOrders = recentOrders.stream().filter(o -> "DELIVERED".equals(o.getStatus().name())).count();

        // 3. Tính toán tiền nợ (Tổng Cần trả - Tổng Đã trả)
        java.math.BigDecimal totalUnpaid = paymentService.findByRequestSenderIdEntities(customerId).stream()
                .map(p -> p.getExpectedAmount()
                        .subtract(p.getPaidAmount() != null ? p.getPaidAmount() : java.math.BigDecimal.ZERO))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("inTransitOrders", inTransitOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("totalUnpaid", totalUnpaid);
        model.addAttribute("recentOrders", recentOrders.stream().limit(5).toList());

        return "customer/dashboard";
    }
}
