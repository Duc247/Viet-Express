package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Entities.Route;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.RouteRepository;
import vn.DucBackend.Services.CustomerRequestService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Manager Route Controller - Quản lý tuyến đường
 * Khi tạo Route mới, tự động cập nhật phí cho các đơn hàng có cùng route
 */
@Controller
@RequestMapping("/manager/routes")
public class ManagerRouteController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CustomerRequestService customerRequestService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // Danh sách tuyến đường
    @GetMapping
    public String routeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        List<Route> routes = routeRepository.findAll();
        model.addAttribute("routes", routes);
        model.addAttribute("totalRoutes", routes.size());

        return "manager/route/routes";
    }

    // Form tạo tuyến đường mới
    @GetMapping("/create")
    public String createForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        // Lấy tất cả locations (chỉ loại WAREHOUSE)
        List<Location> locations = locationRepository.findByIsActiveTrue();
        model.addAttribute("locations", locations);
        model.addAttribute("route", new Route());
        model.addAttribute("isEdit", false);

        return "manager/route/form";
    }

    // Xử lý tạo tuyến đường
    @PostMapping("/create")
    public String createRoute(
            @RequestParam("fromLocationId") Long fromLocationId,
            @RequestParam("toLocationId") Long toLocationId,
            @RequestParam("distanceKm") BigDecimal distanceKm,
            @RequestParam(value = "estimatedTimeHours", required = false) BigDecimal estimatedTimeHours,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "createReverse", defaultValue = "true") boolean createReverse,
            RedirectAttributes redirectAttributes) {

        // Validate không được chọn cùng 1 địa điểm
        if (fromLocationId.equals(toLocationId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Điểm đi và điểm đến không được giống nhau!");
            return "redirect:/manager/routes/create";
        }

        // Kiểm tra tuyến đường đã tồn tại chưa
        if (routeRepository.findByFromLocationIdAndToLocationId(fromLocationId, toLocationId).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tuyến đường này đã tồn tại!");
            return "redirect:/manager/routes/create";
        }

        Location fromLocation = locationRepository.findById(fromLocationId).orElse(null);
        Location toLocation = locationRepository.findById(toLocationId).orElse(null);

        if (fromLocation == null || toLocation == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy địa điểm!");
            return "redirect:/manager/routes/create";
        }

        // Tạo route chiều đi
        Route route = new Route();
        route.setFromLocation(fromLocation);
        route.setToLocation(toLocation);
        route.setDistanceKm(distanceKm);
        route.setEstimatedTimeHours(estimatedTimeHours);
        route.setDescription(description);
        route.setIsActive(true);
        routeRepository.save(route);

        // Cập nhật phí cho đơn hàng có cùng route (chiều đi)
        int updatedCount = customerRequestService.updateShippingFeeForRoute(fromLocationId, toLocationId, distanceKm);

        // Tạo route chiều về (nếu chọn)
        int reverseUpdatedCount = 0;
        if (createReverse
                && !routeRepository.findByFromLocationIdAndToLocationId(toLocationId, fromLocationId).isPresent()) {
            Route reverseRoute = new Route();
            reverseRoute.setFromLocation(toLocation);
            reverseRoute.setToLocation(fromLocation);
            reverseRoute.setDistanceKm(distanceKm);
            reverseRoute.setEstimatedTimeHours(estimatedTimeHours);
            reverseRoute.setDescription(description != null ? description + " (chiều về)" : "Chiều về");
            reverseRoute.setIsActive(true);
            routeRepository.save(reverseRoute);

            // Cập nhật phí cho đơn hàng có route chiều về
            reverseUpdatedCount = customerRequestService.updateShippingFeeForRoute(toLocationId, fromLocationId,
                    distanceKm);
        }

        String message = "Tạo tuyến đường thành công!";
        if (updatedCount + reverseUpdatedCount > 0) {
            message += " Đã cập nhật phí cho " + (updatedCount + reverseUpdatedCount) + " đơn hàng.";
        }
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/manager/routes";
    }

    // Form sửa tuyến đường
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        addCommonAttributes(model, request);

        Route route = routeRepository.findById(id).orElse(null);
        if (route == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tuyến đường!");
            return "redirect:/manager/routes";
        }

        List<Location> locations = locationRepository.findByIsActiveTrue();
        model.addAttribute("locations", locations);
        model.addAttribute("route", route);
        model.addAttribute("isEdit", true);

        return "manager/route/form";
    }

    // Xử lý sửa tuyến đường
    @PostMapping("/{id}/update")
    public String updateRoute(
            @PathVariable("id") Long id,
            @RequestParam("distanceKm") BigDecimal distanceKm,
            @RequestParam(value = "estimatedTimeHours", required = false) BigDecimal estimatedTimeHours,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isActive", defaultValue = "true") boolean isActive,
            RedirectAttributes redirectAttributes) {

        Route route = routeRepository.findById(id).orElse(null);
        if (route == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tuyến đường!");
            return "redirect:/manager/routes";
        }

        route.setDistanceKm(distanceKm);
        route.setEstimatedTimeHours(estimatedTimeHours);
        route.setDescription(description);
        route.setIsActive(isActive);
        routeRepository.save(route);

        // Cập nhật phí cho đơn hàng có cùng route (nếu đã thay đổi khoảng cách)
        int updatedCount = customerRequestService.updateShippingFeeForRoute(
                route.getFromLocation().getId(), route.getToLocation().getId(), distanceKm);

        String message = "Cập nhật tuyến đường thành công!";
        if (updatedCount > 0) {
            message += " Đã cập nhật phí cho " + updatedCount + " đơn hàng.";
        }
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/manager/routes";
    }

    // Xóa tuyến đường
    @PostMapping("/{id}/delete")
    public String deleteRoute(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Route route = routeRepository.findById(id).orElse(null);
        if (route == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tuyến đường!");
            return "redirect:/manager/routes";
        }

        routeRepository.delete(route);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tuyến đường thành công!");
        return "redirect:/manager/routes";
    }
}
