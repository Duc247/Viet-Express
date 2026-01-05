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
 * Controller xử lý tracking (theo dõi đơn hàng) cho Customer
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/customer")
public class CustomerTrackingController {

    @Autowired
    private CustomerRequestService customerRequestService;

    @Autowired
    private TripService tripService;

    @Autowired
    private ParcelService parcelService;

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

    /**
     * Hiển thị trang tracking và xử lý tìm kiếm
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