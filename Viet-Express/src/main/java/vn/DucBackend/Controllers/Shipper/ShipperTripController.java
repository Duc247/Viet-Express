package vn.DucBackend.Controllers.Shipper;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.ShipperDTO;
import vn.DucBackend.DTO.TripDTO;

/**
 * Shipper Trip Controller
 * Xử lý quản lý chuyến xe của tài xế
 * 
 * Endpoints:
 * - GET /shipper/trips - Danh sách chuyến xe
 * - GET /shipper/trip/{id} - Chi tiết chuyến xe
 * - POST /shipper/trip/{id}/status - Cập nhật trạng thái
 */
@Controller
@RequestMapping("/shipper")
public class ShipperTripController extends ShipperBaseController {

    /**
     * Danh sách chuyến xe của shipper
     */
    @GetMapping("/trips")
    public String myTrips(Model model, HttpServletRequest request, Principal principal,
            @RequestParam(required = false, defaultValue = "all") String filter) {
        addCommonAttributes(model, request, principal);
        ShipperDTO shipper = getCurrentShipper(principal);

        if (shipper != null) {
            List<TripDTO> trips;
            if ("active".equals(filter)) {
                trips = tripService.findActiveTripsByShipper(shipper.getId());
            } else if ("history".equals(filter) || "completed".equals(filter)) {
                trips = tripService.findTripsByShipperId(shipper.getId());
            } else {
                trips = tripService.findTripsByShipperId(shipper.getId());
            }
            model.addAttribute("trips", trips);
            model.addAttribute("filter", filter);
        }

        return "shipper/trip/list";
    }

    /**
     * Chi tiết một chuyến xe
     */
    @GetMapping("/trip/{id}")
    public String tripDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request,
            Principal principal) {
        addCommonAttributes(model, request, principal);
        Optional<TripDTO> tripOpt = tripService.findTripById(id);
        if (tripOpt.isPresent()) {
            model.addAttribute("trip", tripOpt.get());
            return "shipper/trip/detail";
        } else {
            return "redirect:/shipper/trips";
        }
    }

    /**
     * Cập nhật trạng thái chuyến xe
     */
    @PostMapping("/trip/{id}/status")
    public String updateTripStatus(@PathVariable("id") Long id, @RequestParam("status") String status,
            HttpServletRequest request, Principal principal) {
        ShipperDTO shipper = getCurrentShipper(principal);

        tripService.updateTripStatus(id, status);

        // Ghi log cập nhật trạng thái
        if (shipper != null) {
            if ("IN_PROGRESS".equals(status)) {
                loggingHelper.logTripStarted(shipper.getId(), id, request);
            } else if ("COMPLETED".equals(status)) {
                loggingHelper.logTripEnded(shipper.getId(), id, request);
            }
        }

        return "redirect:/shipper/trip/" + id;
    }
}
