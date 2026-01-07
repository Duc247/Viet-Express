package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Services.CustomerRequestService;
import vn.DucBackend.Utils.PaginationUtil;

import java.util.List;

/**
 * =============================================================================
 * CUSTOMER ORDER LIST CONTROLLER
 * =============================================================================
 * 
 * Controller xử lý danh sách và tìm kiếm đơn hàng cho Customer
 * 
 * URLS:
 * - GET /customer/orders : Danh sách đơn hàng (có tìm kiếm, lọc, phân trang)
 * - GET /customer/orders/history : Lịch sử đơn hàng
 * 
 * CHỨC NĂNG:
 * - Hiển thị danh sách đơn hàng của customer (là sender hoặc receiver)
 * - Tìm kiếm theo mã đơn, mô tả hàng hóa
 * - Lọc theo trạng thái (PENDING, CONFIRMED, DELIVERED...)
 * - Phân trang kết quả
 * 
 * SERVICES SỬ DỤNG:
 * - CustomerRequestService: Lấy danh sách đơn hàng
 * - PaginationUtil: Hỗ trợ phân trang
 * 
 * =============================================================================
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderListController {

    // =========================================================================
    // DEPENDENCY INJECTION
    // =========================================================================

    /** Service xử lý đơn hàng */
    @Autowired
    private CustomerRequestService customerRequestService;

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

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

    // =========================================================================
    // ENDPOINT: DANH SÁCH ĐƠN HÀNG
    // =========================================================================

    /**
     * DANH SÁCH ĐƠN HÀNG CỦA CUSTOMER
     * 
     * URL: GET /customer/orders
     * 
     * LUỒNG XỬ LÝ:
     * 1. Kiểm tra đăng nhập
     * 2. Lấy tất cả đơn hàng của customer (sender/receiver)
     * 3. Lọc theo keyword (mã đơn, mô tả) nếu có
     * 4. Lọc theo status nếu có
     * 5. Phân trang kết quả
     * 6. Trả về template danh sách
     * 
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param status  Trạng thái cần lọc (optional)
     * @param page    Số trang (mặc định 0)
     * @param model   Model để truyền dữ liệu
     * @return Template "customer/order/list"
     */
    @GetMapping("/orders")
    public String orderList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        // Lấy tất cả đơn hàng của customer (bao gồm cả sender và receiver)
        List<CustomerRequest> orders = customerRequestService.findByCustomerIdEntities(customerId);

        // Lọc theo keyword (tìm trong mã đơn và mô tả hàng hóa)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKey = keyword.trim().toLowerCase();
            orders = orders.stream()
                    .filter(o -> (o.getRequestCode() != null
                            && o.getRequestCode().toLowerCase().contains(searchKey))
                            || (o.getParcelDescription() != null
                                    && o.getParcelDescription().toLowerCase().contains(searchKey)))
                    .toList();
        }

        // Lọc theo trạng thái đơn hàng
        if (status != null && !status.trim().isEmpty()) {
            orders = orders.stream()
                    .filter(o -> o.getStatus().name().equals(status))
                    .toList();
        }

        // Phân trang với PaginationUtil
        var ordersPage = PaginationUtil.paginate(orders, page, PaginationUtil.DEFAULT_PAGE_SIZE);

        // Đưa dữ liệu vào Model cho Thymeleaf render
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("totalCount", ordersPage.getTotalItems());
        model.addAttribute("currentPage", ordersPage.getCurrentPage());
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("pageSize", PaginationUtil.DEFAULT_PAGE_SIZE);

        // Giữ lại giá trị search để hiển thị trong form
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("searchStatus", status);
        model.addAttribute("customerId", customerId);

        return "customer/order/list";
    }

    // =========================================================================
    // ENDPOINT: LỊCH SỬ ĐƠN HÀNG
    // =========================================================================

    /**
     * LỊCH SỬ ĐƠN HÀNG
     * 
     * URL: GET /customer/orders/history
     * 
     * Hiển thị tất cả đơn hàng bao gồm cả đã hoàn thành và đã hủy.
     * 
     * @param model Model để truyền dữ liệu
     * @return Template "customer/order/history"
     */
    @GetMapping("/orders/history")
    public String orderHistory(Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        List<CustomerRequest> orders = customerRequestService.findByCustomerIdEntities(customerId);
        model.addAttribute("orders", orders);

        return "customer/order/history";
    }
}
