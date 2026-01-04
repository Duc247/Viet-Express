package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.PaginationUtil;

/**
 * Manager Parcel Controller - Quản lý kiện hàng
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/manager")
public class ManagerParcelController {

    // Services cho business logic
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private LocationService locationService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // QUẢN LÝ KIỆN HÀNG
    // ==========================================
    @GetMapping("/parcels")
    public String parcelList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        java.util.List<Parcel> parcels = parcelService.getAllParcelEntities();

        // Lọc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            parcels = parcels.stream()
                    .filter(p -> (p.getParcelCode() != null && p.getParcelCode().toLowerCase().contains(kw)) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(kw)) ||
                            (p.getCurrentLocation() != null
                                    && p.getCurrentLocation().getName().toLowerCase().contains(kw)))
                    .toList();
        }

        // Lọc theo status
        if (status != null && !status.isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getStatus().name().equals(status))
                    .toList();
        }

        model.addAttribute("parcelsPage", PaginationUtil.paginate(parcels, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "manager/parcel/parcels";
    }

    @GetMapping("/parcels/{id}")
    public String parcelDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Parcel parcel = parcelService.getParcelEntityById(id);
        if (parcel == null) {
            return "redirect:/manager/parcels";
        }

        model.addAttribute("parcel", parcel);
        model.addAttribute("locations", locationService.getAllLocationEntities());
        return "manager/parcel/detail";
    }

    @PostMapping("/parcels/{id}/update-location")
    public String updateParcelLocation(
            @PathVariable("id") Long id,
            @RequestParam("locationId") Long locationId,
            @RequestParam("newStatus") String newStatus,
            RedirectAttributes redirectAttributes) {

        // Sử dụng Service cho update location và status
        var result = parcelService.updateParcelLocation(id, locationId, newStatus);

        if (result == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy kiện hàng!");
            return "redirect:/manager/parcels";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật kiện hàng!");
        return "redirect:/manager/parcels/" + id;
    }
}
