package vn.DucBackend.Controllers.Shipper;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Shipper Location Controller
 * Xử lý cập nhật vị trí của tài xế
 * 
 * Endpoints:
 * - GET /shipper/location - Trang cập nhật vị trí
 */
@Controller
@RequestMapping("/shipper")
public class ShipperLocationController extends ShipperBaseController {

    /**
     * Trang cập nhật vị trí hiện tại
     */
    @GetMapping("/location")
    public String updateLocation(Model model, HttpServletRequest request, Principal principal) {
        addCommonAttributes(model, request, principal);
        model.addAttribute("locations", locationService.findAllLocations());
        return "shipper/location";
    }
}
