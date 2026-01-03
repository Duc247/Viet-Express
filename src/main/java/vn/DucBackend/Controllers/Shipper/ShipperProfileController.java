package vn.DucBackend.Controllers.Shipper;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.ShipperDTO;

/**
 * Shipper Profile Controller
 * Xử lý xem và chỉnh sửa hồ sơ shipper
 */
@Controller
@RequestMapping("/shipper")
public class ShipperProfileController extends ShipperBaseController {

    /**
     * Xem hồ sơ shipper
     */
    @GetMapping("/profile")
    public String viewProfile(Model model, HttpServletRequest request, Principal principal) {
        addCommonAttributes(model, request, principal);
        ShipperDTO shipper = getCurrentShipper(principal);
        
        if (shipper == null) {
            return "redirect:/shipper/dashboard";
        }
        
        model.addAttribute("shipper", shipper);
        model.addAttribute("locations", locationService.findAllLocations());
        
        return "shipper/profile";
    }

    /**
     * Cập nhật hồ sơ shipper
     */
    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "workingArea", required = false) String workingArea,
            @RequestParam(value = "currentLocationId", required = false) Long currentLocationId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        ShipperDTO shipper = getCurrentShipper(principal);
        
        if (shipper == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin shipper!");
            return "redirect:/shipper/dashboard";
        }
        
        try {
            // Cập nhật thông tin
            if (fullName != null && !fullName.trim().isEmpty()) {
                shipper.setFullName(fullName.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                shipper.setPhone(phone.trim());
            }
            if (workingArea != null) {
                shipper.setWorkingArea(workingArea.trim());
            }
            if (currentLocationId != null) {
                shipper.setCurrentLocationId(currentLocationId);
            }
            
            shipperService.updateShipper(shipper.getId(), shipper);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }
        
        return "redirect:/shipper/profile";
    }
}

