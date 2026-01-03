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
            // Get current shipper data to preserve existing values
            ShipperDTO currentShipper = shipperService.findShipperById(shipper.getId())
                    .orElseThrow(() -> new RuntimeException("Shipper not found"));
            
            // Cập nhật thông tin, giữ nguyên các giá trị hiện tại cho các trường không được cập nhật
            ShipperDTO updateDTO = new ShipperDTO();
            updateDTO.setFullName(fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : currentShipper.getFullName());
            updateDTO.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : currentShipper.getPhone());
            updateDTO.setWorkingArea(workingArea != null ? workingArea.trim() : currentShipper.getWorkingArea());
            updateDTO.setCurrentLocationId(currentLocationId != null ? currentLocationId : currentShipper.getCurrentLocationId());
            // Giữ nguyên các giá trị quan trọng
            updateDTO.setIsActive(currentShipper.getIsActive());
            updateDTO.setIsAvailable(currentShipper.getIsAvailable());
            updateDTO.setUserId(currentShipper.getUserId());
            
            shipperService.updateShipper(shipper.getId(), updateDTO);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }
        
        return "redirect:/shipper/profile";
    }
}

