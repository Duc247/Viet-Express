package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;

/**
 * Admin Resource Controller - Quản lý tài nguyên (Location, Route, Vehicle,
 * ServiceType)
 */
@Controller
@RequestMapping("/admin")
public class AdminResourceController {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // LOCATION
    // ==========================================
    @GetMapping("/location")
    public String locationList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/location/list";
    }

    @GetMapping("/location/create")
    public String locationCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("location", new Location());
        model.addAttribute("isEdit", false);
        return "admin/location/form";
    }

    @GetMapping("/location/edit/{id}")
    public String locationEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("location", locationRepository.findById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/location/form";
    }

    @PostMapping("/location/save")
    public String locationSave(@ModelAttribute Location location, RedirectAttributes redirectAttributes) {
        try {
            locationRepository.save(location);
            redirectAttributes.addFlashAttribute("success", "Lưu địa điểm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/location";
    }

    @GetMapping("/location/delete/{id}")
    public String locationDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            locationRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa địa điểm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/location";
    }

    @GetMapping("/location/toggle/{id}")
    public String locationToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Location location = locationRepository.findById(id).orElseThrow();
            location.setIsActive(!location.getIsActive());
            locationRepository.save(location);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/location";
    }

    // ==========================================
    // ROUTE
    // ==========================================
    @GetMapping("/route")
    public String routeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("routes", routeRepository.findAll());
        return "admin/route/list";
    }

    @GetMapping("/route/create")
    public String routeCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("route", new Route());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("isEdit", false);
        return "admin/route/form";
    }

    @GetMapping("/route/edit/{id}")
    public String routeEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("route", routeRepository.findById(id).orElse(null));
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("isEdit", true);
        return "admin/route/form";
    }

    @PostMapping("/route/save")
    public String routeSave(@RequestParam(required = false) Long id,
            @RequestParam Long fromLocationId,
            @RequestParam Long toLocationId,
            @RequestParam String distanceKm,
            @RequestParam String estimatedTimeHours,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            RedirectAttributes redirectAttributes) {
        try {
            Route route = (id != null) ? routeRepository.findById(id).orElse(new Route()) : new Route();
            route.setFromLocation(locationRepository.findById(fromLocationId).orElseThrow());
            route.setToLocation(locationRepository.findById(toLocationId).orElseThrow());
            route.setDistanceKm(new java.math.BigDecimal(distanceKm));
            route.setEstimatedTimeHours(new java.math.BigDecimal(estimatedTimeHours));
            route.setIsActive(isActive);
            routeRepository.save(route);
            redirectAttributes.addFlashAttribute("success", "Lưu tuyến đường thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/route";
    }

    @GetMapping("/route/delete/{id}")
    public String routeDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            routeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa tuyến đường thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/route";
    }

    @GetMapping("/route/toggle/{id}")
    public String routeToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Route route = routeRepository.findById(id).orElseThrow();
            route.setIsActive(!route.getIsActive());
            routeRepository.save(route);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/route";
    }

    // ==========================================
    // VEHICLE
    // ==========================================
    @GetMapping("/vehicle")
    public String vehicleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "admin/vehicle/list";
    }

    @GetMapping("/vehicle/create")
    public String vehicleCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("isEdit", false);
        return "admin/vehicle/form";
    }

    @GetMapping("/vehicle/edit/{id}")
    public String vehicleEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("vehicle", vehicleRepository.findById(id).orElse(null));
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("isEdit", true);
        return "admin/vehicle/form";
    }

    @PostMapping("/vehicle/save")
    public String vehicleSave(@ModelAttribute Vehicle vehicle,
            @RequestParam(required = false) Long currentLocationId,
            RedirectAttributes redirectAttributes) {
        try {
            if (currentLocationId != null) {
                vehicle.setCurrentLocation(locationRepository.findById(currentLocationId).orElse(null));
            }
            vehicleRepository.save(vehicle);
            redirectAttributes.addFlashAttribute("success", "Lưu phương tiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/vehicle";
    }

    @GetMapping("/vehicle/delete/{id}")
    public String vehicleDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phương tiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/vehicle";
    }

    @GetMapping("/vehicle/toggle/{id}")
    public String vehicleToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Vehicle vehicle = vehicleRepository.findById(id).orElseThrow();
            // Toggle between AVAILABLE and INACTIVE
            if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                vehicle.setStatus(Vehicle.VehicleStatus.INACTIVE);
            } else {
                vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            }
            vehicleRepository.save(vehicle);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/vehicle";
    }

    // ==========================================
    // SERVICE TYPE
    // ==========================================
    @GetMapping("/servicetype")
    public String serviceTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        return "admin/servicetype/list";
    }

    @GetMapping("/servicetype/create")
    public String serviceTypeCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceType", new ServiceType());
        model.addAttribute("isEdit", false);
        return "admin/servicetype/form";
    }

    @GetMapping("/servicetype/edit/{id}")
    public String serviceTypeEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceType", serviceTypeRepository.findById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/servicetype/form";
    }

    @PostMapping("/servicetype/save")
    public String serviceTypeSave(@ModelAttribute ServiceType serviceType, RedirectAttributes redirectAttributes) {
        try {
            serviceTypeRepository.save(serviceType);
            redirectAttributes.addFlashAttribute("success", "Lưu loại dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/servicetype";
    }

    @GetMapping("/servicetype/delete/{id}")
    public String serviceTypeDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceTypeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa loại dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/servicetype";
    }

    @GetMapping("/servicetype/toggle/{id}")
    public String serviceTypeToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ServiceType serviceType = serviceTypeRepository.findById(id).orElseThrow();
            serviceType.setIsActive(!serviceType.getIsActive());
            serviceTypeRepository.save(serviceType);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/servicetype";
    }
}
