package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Repositories.CustomerRequestRepository;

import java.util.List;

/**
 * Controller xử lý Dashboard cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerDashboardController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private vn.DucBackend.Repositories.PaymentRepository paymentRepository;

    private Long getCustomerIdFromSession(HttpSession session) {
        Object customerId = session.getAttribute("customerId");
        if (customerId != null) {
            return (Long) customerId;
        }
        return null;
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)  // Đảm bảo transaction mở trong suốt method
    public String dashboard(Model model, HttpSession session) {
        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        // 1. Lấy danh sách đơn hàng gần đây
        List<CustomerRequest> recentOrders = customerRequestRepository.findByCustomerId(customerId);

        // 2. Tính toán thống kê
        long totalOrders = recentOrders.size();
        long pendingOrders = recentOrders.stream().filter(o -> "PENDING".equals(o.getStatus().name())).count();
        long inTransitOrders = recentOrders.stream().filter(o -> "IN_TRANSIT".equals(o.getStatus().name())).count();
        long deliveredOrders = recentOrders.stream().filter(o -> "DELIVERED".equals(o.getStatus().name())).count();

        // 3. Tính toán tiền nợ (Tổng Cần trả - Tổng Đã trả)
        // Sử dụng query method với JOIN FETCH để tránh LazyInitializationException
        java.math.BigDecimal totalUnpaid = paymentRepository.findByRequestSenderId(customerId).stream()
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
