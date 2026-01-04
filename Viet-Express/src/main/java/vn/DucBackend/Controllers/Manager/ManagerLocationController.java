package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.PaginationUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * Manager Location Controller - Quản lý kho & địa điểm
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/manager")
public class ManagerLocationController {

    // Services cho business logic
    @Autowired
    private LocationService locationService;

    // Repositories cho template data
    @Autowired
    private LocationRepository locationRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // QUẢN LÝ KHO & ĐỊA ĐIỂM
    // ==========================================
    @GetMapping("/locations")
    public String locationList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        java.util.List<Location> locations = locationRepository.findAll();

        // Lọc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            locations = locations.stream()
                    .filter(loc -> (loc.getName() != null && loc.getName().toLowerCase().contains(kw)) ||
                            (loc.getAddressText() != null && loc.getAddressText().toLowerCase().contains(kw)) ||
                            (loc.getWarehouseCode() != null && loc.getWarehouseCode().toLowerCase().contains(kw)))
                    .toList();
        }

        // Lọc theo loại
        if (type != null && !type.isEmpty()) {
            locations = locations.stream()
                    .filter(loc -> loc.getLocationType().name().equals(type))
                    .toList();
        }

        model.addAttribute("locationsPage", PaginationUtil.paginate(locations, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        return "manager/location/locations";
    }

    @GetMapping("/locations/{id}")
    public String locationDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Optional<Location> locationOpt = locationRepository.findById(id);
        if (locationOpt.isEmpty()) {
            return "redirect:/manager/locations";
        }

        model.addAttribute("location", locationOpt.get());
        return "manager/location/detail";
    }

    // TẠO LOCATION MỚI
    @PostMapping("/locations/create")
    public String createLocation(
            @RequestParam("name") String name,
            @RequestParam("addressText") String addressText,
            @RequestParam("locationType") String locationType,
            @RequestParam(value = "returnToRequestId", required = false) Long returnToRequestId,
            RedirectAttributes redirectAttributes) {

        Location location = new Location();
        location.setName(name);
        location.setAddressText(addressText);
        location.setLocationType(Location.LocationType.valueOf(locationType));

        // Tạo mã kho nếu là WAREHOUSE
        if (locationType.equals("WAREHOUSE")) {
            location.setWarehouseCode("WH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }

        locationRepository.save(location);
        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo địa điểm mới thành công!");

        if (returnToRequestId != null) {
            return "redirect:/manager/requests/" + returnToRequestId;
        }
        return "redirect:/manager/locations";
    }

    @PostMapping("/locations/{id}/update")
    public String updateLocation(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("addressText") String addressText,
            @RequestParam("locationType") String locationType,
            @RequestParam(value = "warehouseCode", required = false) String warehouseCode,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            RedirectAttributes redirectAttributes) {

        Optional<Location> locationOpt = locationRepository.findById(id);
        if (locationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy địa điểm!");
            return "redirect:/manager/locations";
        }

        Location location = locationOpt.get();
        location.setName(name);
        location.setAddressText(addressText);
        location.setLocationType(Location.LocationType.valueOf(locationType));
        location.setWarehouseCode(warehouseCode);
        location.setDescription(description);
        location.setIsActive(isActive != null ? isActive : true);

        locationRepository.save(location);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật địa điểm thành công!");
        return "redirect:/manager/locations/" + id;
    }

    @PostMapping("/locations/{id}/delete")
    public String deleteLocation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Location> locationOpt = locationRepository.findById(id);
        if (locationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy địa điểm!");
            return "redirect:/manager/locations";
        }

        Location location = locationOpt.get();
        location.setIsActive(false);
        locationRepository.save(location);

        redirectAttributes.addFlashAttribute("successMessage", "Đã vô hiệu hóa địa điểm!");
        return "redirect:/manager/locations";
    }
}
