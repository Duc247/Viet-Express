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
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.ParcelRepository;
import vn.DucBackend.Repositories.TripRepository;

import java.util.List;
import java.util.Optional;

/**
 * Controller xử lý chi tiết chuyến vận chuyển cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerTripsController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private TripRepository tripRepository;

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
        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);

        if (orderOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

        // Kiểm tra quyền xem
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);

        if (customerId != null && !isSender && !isReceiver) {
            model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
            return "redirect:/customer/orders";
        }

        model.addAttribute("order", order);

        // Get trips based on filters
        List<Trip> trips;

        if (search != null && !search.trim().isEmpty()) {
            // Search by keyword (description, note, route)
            trips = tripRepository.searchTripsByRequestIdAndKeyword(id, search.trim());
            model.addAttribute("search", search);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                Trip.TripStatus tripStatus = Trip.TripStatus.valueOf(status);
                trips = tripRepository.findTripsByRequestIdAndStatus(id, tripStatus);
            } catch (IllegalArgumentException e) {
                trips = tripRepository.findTripsByRequestId(id);
            }
            model.addAttribute("status", status);
        } else if (type != null && !type.isEmpty()) {
            // Filter by trip type
            try {
                Trip.TripType tripType = Trip.TripType.valueOf(type);
                trips = tripRepository.findTripsByRequestIdAndType(id, tripType);
            } catch (IllegalArgumentException e) {
                trips = tripRepository.findTripsByRequestId(id);
            }
            model.addAttribute("type", type);
        } else {
            // Get all trips
            trips = tripRepository.findTripsByRequestId(id);
        }

        model.addAttribute("trips", trips);

        // Summary statistics
        Long totalTrips = tripRepository.countTripsByRequestId(id);
        Long completedTrips = tripRepository.countCompletedTripsByRequestId(id);
        Long inProgressTrips = tripRepository.countInProgressTripsByRequestId(id);
        Long pendingTrips = tripRepository.countCreatedTripsByRequestId(id);

        model.addAttribute("totalTrips", totalTrips != null ? totalTrips : 0L);
        model.addAttribute("completedTrips", completedTrips != null ? completedTrips : 0L);
        model.addAttribute("inProgressTrips", inProgressTrips != null ? inProgressTrips : 0L);
        model.addAttribute("pendingTrips", pendingTrips != null ? pendingTrips : 0L);

        // Calculate completion percentage based on delivered parcels
        Long totalParcels = parcelRepository.countByRequestId(id);
        Long deliveredParcels = parcelRepository.countDeliveredByRequestId(id);
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
