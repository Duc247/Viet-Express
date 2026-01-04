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
import vn.DucBackend.Entities.Trip;
import vn.DucBackend.Services.CustomerRequestService;
import vn.DucBackend.Services.ParcelService;
import vn.DucBackend.Services.TripService;

import java.util.List;

/**
 * Controller xử lý chi tiết chuyến vận chuyển cho Customer
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/customer")
public class CustomerTripsController {

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

    @GetMapping("/orders/{id}/trips")
    public String tripsDetail(
            @PathVariable("id") Long id,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
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

        // Get trips based on filters
        List<Trip> trips;

        if (search != null && !search.trim().isEmpty()) {
            // Search by keyword (description, note, route)
            trips = tripService.searchTripsByRequestIdAndKeyword(id, search.trim());
            model.addAttribute("search", search);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                trips = tripService.findTripsByRequestIdAndStatusEntities(id, status);
            } catch (IllegalArgumentException e) {
                trips = tripService.findTripsByRequestIdEntities(id);
            }
            model.addAttribute("status", status);
        } else if (type != null && !type.isEmpty()) {
            // Filter by trip type
            try {
                trips = tripService.findTripsByRequestIdAndTypeEntities(id, type);
            } catch (IllegalArgumentException e) {
                trips = tripService.findTripsByRequestIdEntities(id);
            }
            model.addAttribute("type", type);
        } else {
            // Get all trips
            trips = tripService.findTripsByRequestIdEntities(id);
        }

        model.addAttribute("trips", trips);

        // Summary statistics
        Long totalTrips = tripService.countTripsByRequestId(id);
        Long completedTrips = tripService.countCompletedTripsByRequestId(id);
        Long inProgressTrips = tripService.countInProgressTripsByRequestId(id);
        Long pendingTrips = tripService.countCreatedTripsByRequestId(id);

        model.addAttribute("totalTrips", totalTrips != null ? totalTrips : 0L);
        model.addAttribute("completedTrips", completedTrips != null ? completedTrips : 0L);
        model.addAttribute("inProgressTrips", inProgressTrips != null ? inProgressTrips : 0L);
        model.addAttribute("pendingTrips", pendingTrips != null ? pendingTrips : 0L);

        // Calculate completion percentage based on delivered parcels
        Long totalParcels = parcelService.countByRequestId(id);
        Long deliveredParcels = parcelService.countDeliveredByRequestId(id);
        int completionPercentage = 0;
        if (totalParcels != null && totalParcels > 0) {
            completionPercentage = (int) ((deliveredParcels * 100) / totalParcels);
        }
        model.addAttribute("completionPercentage", completionPercentage);
        model.addAttribute("deliveredParcels", deliveredParcels != null ? deliveredParcels : 0L);
        model.addAttribute("totalParcels", totalParcels != null ? totalParcels : 0L);

        // Trip statuses and types for filter dropdowns
        model.addAttribute("tripStatuses", Trip.TripStatus.values());
        model.addAttribute("tripTypes", Trip.TripType.values());

        return "customer/order/trips-detail";
    }
}
