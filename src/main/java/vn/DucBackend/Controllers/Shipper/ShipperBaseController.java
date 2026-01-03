package vn.DucBackend.Controllers.Shipper;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.ShipperDTO;
import vn.DucBackend.DTO.UserDTO;
import vn.DucBackend.Services.LocationService;
<<<<<<< Updated upstream
import vn.DucBackend.Services.ParcelService;
=======
>>>>>>> Stashed changes
import vn.DucBackend.Services.ShipperService;
import vn.DucBackend.Services.TripService;
import vn.DucBackend.Services.UserService;
import vn.DucBackend.Utils.LoggingHelper;

/**
 * Base Controller cho tất cả Shipper Controllers
 * Chứa các phương thức chung và dependency injection
 */
public abstract class ShipperBaseController {

    @Autowired
    protected TripService tripService;

    @Autowired
    protected ShipperService shipperService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected LocationService locationService;

    @Autowired
    protected LoggingHelper loggingHelper;

<<<<<<< Updated upstream
    @Autowired
    protected ParcelService parcelService;

=======
>>>>>>> Stashed changes
    /**
     * Thêm các attributes chung vào Model
     */
    protected void addCommonAttributes(Model model, HttpServletRequest request, Principal principal) {
        model.addAttribute("requestURI", request.getRequestURI());
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            ShipperDTO shipper = getCurrentShipper(principal);
            if (shipper != null) {
                model.addAttribute("currentShipper", shipper);
            }
        }
    }

    /**
     * Lấy thông tin Shipper hiện tại từ Principal
     */
    protected ShipperDTO getCurrentShipper(Principal principal) {
        if (principal == null)
            return null;
        Optional<UserDTO> userOpt = userService.findByUsername(principal.getName());
        if (userOpt.isPresent()) {
            Optional<ShipperDTO> shipperOpt = shipperService.findByUserId(userOpt.get().getId());
            return shipperOpt.orElse(null);
        }
        return null;
    }
}
