package vn.DucBackend.Controllers.Shipper;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.ShipperDTO;
import vn.DucBackend.DTO.TripDTO;

/**
 * Shipper Dashboard Controller
 * Xử lý trang tổng quan của tài xế
 * 
 * Endpoints:
 * - GET /shipper/dashboard
 */
@Controller
@RequestMapping("/shipper")
public class ShipperDashboardController extends ShipperBaseController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request, Principal principal) {
        addCommonAttributes(model, request, principal);

        ShipperDTO shipper = getCurrentShipper(principal);
        if (shipper != null) {
            List<TripDTO> activeTrips = tripService.findActiveTripsByShipper(shipper.getId());
            model.addAttribute("activeTrips", activeTrips);
            model.addAttribute("totalTripsCount", tripService.findTripsByShipperId(shipper.getId()).size());
            model.addAttribute("activeTripsCount", activeTrips.size());
            model.addAttribute("completedTripsCount", tripService.countTripsByStatus("COMPLETED"));
        } else {
            model.addAttribute("activeTrips", Collections.emptyList());
            model.addAttribute("totalTripsCount", 0);
            model.addAttribute("activeTripsCount", 0);
            model.addAttribute("completedTripsCount", 0);
        }

        return "shipper/dashboard";
    }

    /**
     * Redirect root /shipper to dashboard
     */
    @GetMapping("")
    public String redirectToDashboard() {
        return "redirect:/shipper/dashboard";
    }
}
