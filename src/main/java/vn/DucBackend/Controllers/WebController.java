package vn.DucBackend.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    // ==========================================
    // TRANG CHỦ & GIỚI THIỆU
    // ==========================================
    @GetMapping("/")
    public String homePage() {
        return "public/home";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "public/about";
    }

    // ==========================================
    // GỬI YÊU CẦU VẬN CHUYỂN (KHÁCH VÃNG LAI)
    // ==========================================
    @GetMapping("/request")
    public String showRequestPage() {
        return "public/request";
    }

    @PostMapping("/request")
    public String handleRequestSubmit() {
        System.out.println("Đã nhận yêu cầu vận chuyển mới!");
        return "redirect:/?success=true";
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
