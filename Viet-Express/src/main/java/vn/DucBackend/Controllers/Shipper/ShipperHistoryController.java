package vn.DucBackend.Controllers.Shipper;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.ShipperDTO;

/**
 * Shipper History Controller
 * Xử lý lịch sử giao hàng của tài xế
 * 
 * Endpoints:
 * - GET /shipper/history - Lịch sử chuyến xe
 */
@Controller
@RequestMapping("/shipper")
public class ShipperHistoryController extends ShipperBaseController {

    /**
     * Xem lịch sử chuyến xe đã hoàn thành
     */
    @GetMapping("/history")
    public String history(Model model, HttpServletRequest request, Principal principal) {
        addCommonAttributes(model, request, principal);

        ShipperDTO shipper = getCurrentShipper(principal);
        if (shipper != null) {
            // Get only completed/cancelled trips for history
            model.addAttribute("trips", tripService.findCompletedTripsByShipper(shipper.getId()));
            model.addAttribute("filter", "history");
        }

        return "shipper/trip/list";
    }
}
