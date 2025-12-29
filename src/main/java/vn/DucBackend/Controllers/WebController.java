package vn.DucBackend.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    // ==========================================
    // 1. NHÓM PUBLIC (Trang chủ, Giới thiệu...)
    // ==========================================

    @GetMapping("/")
    public String homePage() {
        return "public/home";
    }

    @GetMapping("/about")
    public String aboutPage() {
        return "public/about";
    }

    // --- KHÁCH VÃNG LAI ---

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
    // 2. NHÓM AUTH (Đăng nhập, Đăng ký)
    // ==========================================

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @GetMapping("/auth/verify-otp")
    public String verifyOtpPage() {
        return "auth/verify-otp";
    }

    // ==========================================
    // 3. NHÓM CUSTOMER (Khách hàng)
    // ==========================================

    @GetMapping("/customer/dashboard")
    public String userDashboard() {
        // File này vẫn nằm ở root customer/
        return "customer/dashboard";
    }

    @GetMapping("/user/profile")
    public String profilePage() {
        return "user/profile";
    }

    @GetMapping("/customer/create-order")
    public String createOrderPage() {
        // SỬA QUAN TRỌNG: Trỏ vào thư mục con customer/order/
        return "customer/order/create-order";
    }

    @PostMapping("/customer/create-order")
    public String handleCreateOrder() {
        System.out.println("Khách hàng đã tạo đơn mới!");
        return "redirect:/customer/dashboard?success=create";
    }

    // =========================================================================
    // 4. NHÓM ADMIN - Đã chuyển sang AdminController.java
    // =========================================================================

    // =========================================================================
    // 5. KHU VỰC MANAGER (Quản lý)
    // =========================================================================

    @GetMapping("/manager/dashboard")
    public String managerDashboard() {
        return "manager/dashboard";
    }

    @GetMapping("/manager/requests")
    public String managerRequestListPage() {
        // Trỏ vào thư mục con: templates/manager/request/requests.html
        return "manager/request/requests";
    }

    @GetMapping("/manager/orders")
    public String managerOrderPage() {
        // Trỏ vào thư mục con: templates/manager/order/orders.html
        return "manager/order/orders";
    }

    @GetMapping("/manager/planning")
    public String shipmentPlanningPage() {
        // Trỏ vào thư mục con: templates/manager/planning/shipment-planning.html
        return "manager/planning/shipment-planning";
    }

    @GetMapping("/manager/locations")
    public String warehousePage() {
        // Trỏ vào thư mục con: templates/manager/location/locations.html
        return "manager/location/locations";
    }
    // // =========================================================================
    // 6. KHU VỰC DRIVER (Tài xế)
    // =========================================================================

    @GetMapping("/driver/dashboard")
    public String driverDashboard() {
        return "driver/dashboard";
    }

    @GetMapping("/driver/shipments")
    public String driverShipmentList() {
        return "driver/task/my-shipments";
    }

    @GetMapping("/driver/shipment-detail")
    public String driverShipmentDetail() {
        return "driver/task/delivery-details";
    }

    // MỚI THÊM
    @GetMapping("/driver/history")
    public String driverHistoryPage() {
        return "driver/history"; // Trỏ đến file templates/driver/history.html
    }
    // ==========================================
    // 7. KHU VỰC COMMON (Dùng chung) - MỚI
    // ==========================================

    @GetMapping("/tracking")
    public String trackingPage() {
        return "common/tracking";
    }

    @GetMapping("/order-detail") // Thực tế sẽ là /order-detail/{id}
    public String orderDetailPage() {
        return "common/order-detail";
    }

    // Trang profile đã khai báo ở phần User rồi,
    // nhưng đảm bảo file vật lý nằm đúng chỗ templates/user/profile.html
}
