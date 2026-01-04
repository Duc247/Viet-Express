package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Entities.Parcel;
import vn.DucBackend.Services.CustomerRequestService;
import vn.DucBackend.Services.ParcelService;

import java.util.List;

/**
 * Controller xử lý chi tiết kiện hàng cho Customer
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/customer")
public class CustomerParcelsController {

    @Autowired
    private CustomerRequestService customerRequestService;

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

    @GetMapping("/orders/{id}/parcels")
    public String parcelsDetail(
            @PathVariable("id") Long id,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            Model model,
            HttpServletRequest request,
            HttpSession session) {

        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);

        // Phải đăng nhập để xem chi tiết
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        CustomerRequest order = customerRequestService.getRequestEntityById(id);

        if (order == null) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        // Kiểm tra quyền xem - phải là sender hoặc receiver
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);

        if (!isSender && !isReceiver) {
            model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
            return "redirect:/customer/orders";
        }

        model.addAttribute("order", order);

        // Get parcels based on filters
        List<Parcel> parcels;

        if (search != null && !search.trim().isEmpty()) {
            // Search by keyword
            parcels = parcelService.searchByRequestIdAndKeyword(id, search.trim());
            model.addAttribute("search", search);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                parcels = parcelService.findByRequestIdAndStatusEntities(id, status);
            } catch (IllegalArgumentException e) {
                parcels = parcelService.findByRequestIdEntities(id);
            }
            model.addAttribute("status", status);
        } else {
            // Get all parcels
            parcels = parcelService.findByRequestIdEntities(id);
        }

        model.addAttribute("parcels", parcels);

        // Summary statistics
        Long totalParcels = parcelService.countByRequestId(id);
        Long deliveredParcels = parcelService.countDeliveredByRequestId(id);
        Long inDeliveryParcels = parcelService.countInDeliveryByRequestId(id);
        Long pendingParcels = parcelService.countPendingByRequestId(id);

        model.addAttribute("totalParcels", totalParcels != null ? totalParcels : 0L);
        model.addAttribute("deliveredParcels", deliveredParcels != null ? deliveredParcels : 0L);
        model.addAttribute("inDeliveryParcels", inDeliveryParcels != null ? inDeliveryParcels : 0L);
        model.addAttribute("pendingParcels", pendingParcels != null ? pendingParcels : 0L);

        // Parcel statuses for filter dropdown
        model.addAttribute("parcelStatuses", Parcel.ParcelStatus.values());

        return "customer/order/parcels-detail";
    }
}
