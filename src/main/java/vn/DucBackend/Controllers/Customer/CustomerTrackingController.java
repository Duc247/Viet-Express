package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Services.*;

/**
 * =============================================================================
 * CUSTOMER TRACKING CONTROLLER
 * =============================================================================
 * 
 * Controller xử lý tracking (theo dõi đơn hàng) cho Customer
 * 
 * URL: GET /customer/tracking?code=REQ-xxxxxx
 * 
 * CHỨC NĂNG:
 * - Tra cứu đơn hàng theo mã (REQ-xxx)
 * - Hiển thị trạng thái hiện tại
 * - Hiển thị lịch sử vận chuyển (ParcelAction)
 * - Hiển thị danh sách kiện hàng và chuyến xe
 * 
 * PHÂN QUYỀN:
 * - Chỉ sender hoặc receiver của đơn hàng mới được xem
 * - Nếu không có quyền → thông báo lỗi
 * 
 * SERVICES SỬ DỤNG:
 * - CustomerRequestService: Tìm đơn hàng theo mã
 * - TripService: Lấy danh sách chuyến xe
 * - ParcelService: Lấy danh sách kiện hàng
 * 
 * =============================================================================
 */
@Controller
@RequestMapping("/customer")
public class CustomerTrackingController {

    // =========================================================================
    // DEPENDENCY INJECTION
    // =========================================================================

    /** Service xử lý đơn hàng - tìm theo mã, lấy parcel actions */
    @Autowired
    private CustomerRequestService customerRequestService;

    /** Service chuyến xe - lấy danh sách trips */
    @Autowired
    private TripService tripService;

    /** Service kiện hàng - lấy danh sách parcels */
    @Autowired
    private ParcelService parcelService;

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
    // ENDPOINT: TRA CỨU VẬN ĐƠN
    // =========================================================================

    /**
     * TRA CỨU VẬN ĐƠN
     * 
     * URL: GET /customer/tracking?code=REQ-xxxxxx
     * 
     * LUỒNG XỬ LÝ:
     * 1. Kiểm tra đăng nhập
     * 2. Nếu có mã code → tìm đơn hàng
     * 3. Kiểm tra quyền xem (phải là sender hoặc receiver)
     * 4. Pre-fetch các lazy-loaded relationships
     * 5. Lấy thông tin trips, parcels, parcel actions
     * 6. Trả về template tracking
     * 
     * @param requestCode Mã đơn hàng (REQ-xxx) để tra cứu
     * @param model       Model để truyền dữ liệu
     * @param session     Session chứa customerId
     * @return Template "customer/tracking"
     * 
     *         LƯU Ý:
     *         - @Transactional(readOnly = true) để giữ transaction mở,
     *         tránh LazyInitializationException khi truy cập lazy-loaded properties
     */
    @GetMapping("/tracking")
    @Transactional(readOnly = true)
    public String tracking(
            @RequestParam(value = "code", required = false) String requestCode,
            Model model,
            HttpServletRequest request,
            HttpSession session) {

        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        // Nếu có code, tìm kiếm theo request code REQ-xxx hoặc parcel code PCL-xxx
        // TRK-xxx (TrackingCode) đã được loại bỏ
        if (requestCode != null && !requestCode.trim().isEmpty()) {
            String code = requestCode.trim();
            CustomerRequest order = null;

            // Redirect sang trang tra cứu công khai nếu không muốn kiểm tra quyền
            // Hoặc tìm theo request code (REQ-xxx)
            order = customerRequestService.findByRequestCodeEntity(code);

            if (order != null) {
                // Kiểm tra quyền xem - phải là sender hoặc receiver
                boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
                boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);

                if (isSender || isReceiver) {
                    // Fetch các relationships để tránh lazy loading exception
                    // Access các lazy-loaded properties trong transaction
                    if (order.getSender() != null) {
                        order.getSender().getName();
                        order.getSender().getFullName();
                    }
                    if (order.getReceiver() != null) {
                        order.getReceiver().getName();
                        order.getReceiver().getFullName();
                    }
                    if (order.getSenderLocation() != null) {
                        order.getSenderLocation().getName();
                        order.getSenderLocation().getAddressText();
                    }
                    if (order.getReceiverLocation() != null) {
                        order.getReceiverLocation().getName();
                        order.getReceiverLocation().getAddressText();
                    }

                    model.addAttribute("order", order);
                    model.addAttribute("found", true);
                    model.addAttribute("isSender", isSender);
                    model.addAttribute("isReceiver", isReceiver);

                    // Lấy các trips
                    model.addAttribute("trips", tripService.findTripsByRequestIdEntities(order.getId()));

                    // Lấy các parcels
                    model.addAttribute("parcels", parcelService.findByRequestIdEntities(order.getId()));

                    // Lấy lịch sử hành động (parcel actions) - fetch relationships
                    var actions = customerRequestService.findParcelActionsByRequestIdEntities(order.getId());
                    // Pre-fetch các relationships của parcel actions
                    actions.forEach(action -> {
                        if (action.getActionType() != null)
                            action.getActionType().getName();
                        if (action.getToLocation() != null)
                            action.getToLocation().getName();
                    });
                    model.addAttribute("parcelActions", actions);
                } else {
                    model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
                    model.addAttribute("found", false);
                }
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy đơn hàng với mã: " + code);
                model.addAttribute("found", false);
            }

            model.addAttribute("searchCode", code);
        }

        return "customer/tracking";
    }
}