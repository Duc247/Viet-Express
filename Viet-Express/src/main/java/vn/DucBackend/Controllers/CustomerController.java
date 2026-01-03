package vn.DucBackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Repositories.*;

/**
 * Customer Controller - Xử lý các trang dành cho khách hàng
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private TrackingCodeRepository trackingCodeRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        // TODO: Lọc theo customer đang đăng nhập
        model.addAttribute("totalOrders", customerRequestRepository.count());
        model.addAttribute("recentOrders", customerRequestRepository.findAll());
        return "customer/dashboard";
    }

    // ==========================================
    // ĐƠN HÀNG
    // ==========================================
    @GetMapping("/orders")
    public String orderList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        // TODO: Lọc theo customer đang đăng nhập
        model.addAttribute("orders", customerRequestRepository.findAll());
        return "customer/order/list";
    }

    @GetMapping("/orders/history")
    public String orderHistory(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("orders", customerRequestRepository.findAll());
        return "customer/order/history";
    }

    @GetMapping("/create-order")
    public String createOrderForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "customer/order/form";
    }

    @PostMapping("/create-order")
    public String handleCreateOrder() {
        System.out.println("Khách hàng đã tạo đơn mới!");
        return "redirect:/customer/dashboard?success=create";
    }

    // ==========================================
    // TRACKING
    // ==========================================
    @GetMapping("/tracking")
    public String tracking(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "customer/tracking";
    }

    // ==========================================
    // THANH TOÁN
    // ==========================================
    @GetMapping("/payments")
    public String paymentList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        // TODO: Lọc theo customer đang đăng nhập
        model.addAttribute("payments", paymentRepository.findAll());
        return "customer/payments";
    }
}
