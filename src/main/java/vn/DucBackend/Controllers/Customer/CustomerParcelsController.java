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
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.ParcelRepository;

import java.util.List;
import java.util.Optional;

/**
 * Controller xử lý chi tiết kiện hàng cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerParcelsController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private ParcelRepository parcelRepository;

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
            return "redirect:/login";
        }

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);

        if (orderOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

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
            parcels = parcelRepository.searchByRequestIdAndKeyword(id, search.trim());
            model.addAttribute("search", search);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                Parcel.ParcelStatus parcelStatus = Parcel.ParcelStatus.valueOf(status);
                parcels = parcelRepository.findByRequestIdAndStatus(id, parcelStatus);
            } catch (IllegalArgumentException e) {
                parcels = parcelRepository.findByRequestId(id);
            }
            model.addAttribute("status", status);
        } else {
            // Get all parcels
            parcels = parcelRepository.findByRequestId(id);
        }

        model.addAttribute("parcels", parcels);

        // Summary statistics
        Long totalParcels = parcelRepository.countByRequestId(id);
        Long deliveredParcels = parcelRepository.countDeliveredByRequestId(id);
        Long inDeliveryParcels = parcelRepository.countInDeliveryByRequestId(id);
        Long pendingParcels = parcelRepository.countPendingByRequestId(id);

        model.addAttribute("totalParcels", totalParcels != null ? totalParcels : 0L);
        model.addAttribute("deliveredParcels", deliveredParcels != null ? deliveredParcels : 0L);
        model.addAttribute("inDeliveryParcels", inDeliveryParcels != null ? inDeliveryParcels : 0L);
        model.addAttribute("pendingParcels", pendingParcels != null ? pendingParcels : 0L);

        // Parcel statuses for filter dropdown
        model.addAttribute("parcelStatuses", Parcel.ParcelStatus.values());

        return "customer/order/parcels-detail";
    }
}
