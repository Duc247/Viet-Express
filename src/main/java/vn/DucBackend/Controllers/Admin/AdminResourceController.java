package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.LocationDTO;
import vn.DucBackend.DTO.RouteDTO;
import vn.DucBackend.DTO.ServiceTypeDTO;
import vn.DucBackend.DTO.VehicleDTO;
import vn.DucBackend.Services.LocationService;
import vn.DucBackend.Services.ServiceTypeService;
import vn.DucBackend.Services.VehicleService;

import java.math.BigDecimal;

/**
 * Admin Resource Controller - Quản lý tài nguyên (Location, Route, Vehicle,
 * ServiceType)
 * 
 * Services sử dụng:
 * - LocationService: CRUD địa điểm và tuyến đường
 * - VehicleService: CRUD phương tiện
 * - ServiceTypeService: CRUD loại dịch vụ
 */
@Controller
@RequestMapping("/admin")
public class AdminResourceController {

    @Autowired
    private LocationService locationService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private ServiceTypeService serviceTypeService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // LOCATION
    // ==========================================

    /**
     * Danh sách địa điểm
     * Service: locationService.findAllLocations()
     */
    @GetMapping("/location")
    public String locationList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("locations", locationService.findAllLocations());
        return "admin/location/list";
    }

    /**
     * Form tạo địa điểm mới
     * Service: (khởi tạo DTO mới)
     */
    @GetMapping("/location/create")
    public String locationCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("location", new LocationDTO());
        model.addAttribute("isEdit", false);
        return "admin/location/form";
    }

    /**
     * Form chỉnh sửa địa điểm
     * Service: locationService.findLocationById()
     */
    @GetMapping("/location/edit/{id}")
    public String locationEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("location", locationService.findLocationById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/location/form";
    }

    /**
     * Lưu địa điểm (tạo mới hoặc cập nhật)
     * Service: locationService.createLocation() hoặc
     * locationService.updateLocation()
     */
    @PostMapping("/location/save")
    public String locationSave(@ModelAttribute LocationDTO locationDTO, RedirectAttributes redirectAttributes) {
        try {
            if (locationDTO.getId() != null) {
                locationService.updateLocation(locationDTO.getId(), locationDTO);
            } else {
                locationService.createLocation(locationDTO);
            }
            redirectAttributes.addFlashAttribute("success", "Lưu địa điểm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/location";
    }

    /**
     * Xóa địa điểm
     * Service: locationService.deleteLocation()
     */
    @GetMapping("/location/delete/{id}")
    public String locationDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            locationService.deleteLocation(id);
            redirectAttributes.addFlashAttribute("success", "Xóa địa điểm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/location";
    }

    /**
     * Bật/tắt trạng thái địa điểm
     * Service: locationService.toggleLocationStatus()
     */
    @GetMapping("/location/toggle/{id}")
    public String locationToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            locationService.toggleLocationStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/location";
    }

    // ==========================================
    // ROUTE
    // ==========================================

    /**
     * Danh sách tuyến đường
     * Service: locationService.findAllRoutes()
     */
    @GetMapping("/route")
    public String routeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("routes", locationService.findAllRoutes());
        return "admin/route/list";
    }

    /**
     * Form tạo tuyến đường mới
     * Service: locationService.findAllLocations()
     */
    @GetMapping("/route/create")
    public String routeCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("route", new RouteDTO());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", false);
        return "admin/route/form";
    }

    /**
     * Form chỉnh sửa tuyến đường
     * Service: locationService.findRouteById(), locationService.findAllLocations()
     */
    @GetMapping("/route/edit/{id}")
    public String routeEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("route", locationService.findRouteById(id).orElse(null));
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", true);
        return "admin/route/form";
    }

    /**
     * Lưu tuyến đường
     * Service: locationService.createRoute() hoặc locationService.updateRoute()
     */
    @PostMapping("/route/save")
    public String routeSave(@RequestParam(required = false) Long id,
            @RequestParam Long fromLocationId,
            @RequestParam Long toLocationId,
            @RequestParam String distanceKm,
            @RequestParam String estimatedTimeHours,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            RedirectAttributes redirectAttributes) {
        try {
            RouteDTO routeDTO = new RouteDTO();
            routeDTO.setFromLocationId(fromLocationId);
            routeDTO.setToLocationId(toLocationId);
            routeDTO.setDistanceKm(new BigDecimal(distanceKm));
            routeDTO.setEstimatedTimeHours(new BigDecimal(estimatedTimeHours));
            routeDTO.setIsActive(isActive);

            if (id != null) {
                locationService.updateRoute(id, routeDTO);
            } else {
                locationService.createRoute(routeDTO);
            }
            redirectAttributes.addFlashAttribute("success", "Lưu tuyến đường thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/route";
    }

    /**
     * Xóa tuyến đường
     * Service: locationService.deleteRoute()
     */
    @GetMapping("/route/delete/{id}")
    public String routeDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            locationService.deleteRoute(id);
            redirectAttributes.addFlashAttribute("success", "Xóa tuyến đường thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/route";
    }

    /**
     * Bật/tắt trạng thái tuyến đường
     * Service: locationService.toggleRouteStatus()
     */
    @GetMapping("/route/toggle/{id}")
    public String routeToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            locationService.toggleRouteStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/route";
    }

    // ==========================================
    // VEHICLE
    // ==========================================

    /**
     * Danh sách phương tiện
     * Service: vehicleService.findAll()
     */
    @GetMapping("/vehicle")
    public String vehicleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("vehicles", vehicleService.findAll());
        return "admin/vehicle/list";
    }

    /**
     * Form tạo phương tiện mới
     * Service: locationService.findAllLocations()
     */
    @GetMapping("/vehicle/create")
    public String vehicleCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("vehicle", new VehicleDTO());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", false);
        return "admin/vehicle/form";
    }

    /**
     * Form chỉnh sửa phương tiện
     * Service: vehicleService.findById(), locationService.findAllLocations()
     */
    @GetMapping("/vehicle/edit/{id}")
    public String vehicleEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("vehicle", vehicleService.findById(id).orElse(null));
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", true);
        return "admin/vehicle/form";
    }

    /**
     * Lưu phương tiện
     * Service: vehicleService.create() hoặc vehicleService.update()
     */
    @PostMapping("/vehicle/save")
    public String vehicleSave(@ModelAttribute VehicleDTO vehicleDTO,
            @RequestParam(required = false) Long currentLocationId,
            RedirectAttributes redirectAttributes) {
        try {
            if (currentLocationId != null) {
                vehicleDTO.setCurrentLocationId(currentLocationId);
            }

            if (vehicleDTO.getId() != null) {
                vehicleService.update(vehicleDTO.getId(), vehicleDTO);
            } else {
                vehicleService.create(vehicleDTO);
            }
            redirectAttributes.addFlashAttribute("success", "Lưu phương tiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/vehicle";
    }

    /**
     * Xóa phương tiện
     * Service: vehicleService.delete()
     */
    @GetMapping("/vehicle/delete/{id}")
    public String vehicleDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phương tiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/vehicle";
    }

    /**
     * Bật/tắt trạng thái phương tiện
     * Service: vehicleService.toggleStatus()
     */
    @GetMapping("/vehicle/toggle/{id}")
    public String vehicleToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/vehicle";
    }

    // ==========================================
    // SERVICE TYPE
    // ==========================================

    /**
     * Danh sách loại dịch vụ
     * Service: serviceTypeService.findAll()
     */
    @GetMapping("/servicetype")
    public String serviceTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceTypes", serviceTypeService.findAll());
        return "admin/servicetype/list";
    }

    /**
     * Form tạo loại dịch vụ mới
     * Service: (khởi tạo DTO mới)
     */
    @GetMapping("/servicetype/create")
    public String serviceTypeCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceType", new ServiceTypeDTO());
        model.addAttribute("isEdit", false);
        return "admin/servicetype/form";
    }

    /**
     * Form chỉnh sửa loại dịch vụ
     * Service: serviceTypeService.findById()
     */
    @GetMapping("/servicetype/edit/{id}")
    public String serviceTypeEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceType", serviceTypeService.findById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/servicetype/form";
    }

    /**
     * Lưu loại dịch vụ
     * Service: serviceTypeService.create() hoặc serviceTypeService.update()
     */
    @PostMapping("/servicetype/save")
    public String serviceTypeSave(@ModelAttribute ServiceTypeDTO serviceTypeDTO,
            RedirectAttributes redirectAttributes) {
        try {
            if (serviceTypeDTO.getId() != null) {
                serviceTypeService.update(serviceTypeDTO.getId(), serviceTypeDTO);
            } else {
                serviceTypeService.create(serviceTypeDTO);
            }
            redirectAttributes.addFlashAttribute("success", "Lưu loại dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/servicetype";
    }

    /**
     * Xóa loại dịch vụ
     * Service: serviceTypeService.delete()
     */
    @GetMapping("/servicetype/delete/{id}")
    public String serviceTypeDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceTypeService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa loại dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/servicetype";
    }

    /**
     * Bật/tắt trạng thái loại dịch vụ
     * Service: serviceTypeService.toggleStatus()
     */
    @GetMapping("/servicetype/toggle/{id}")
    public String serviceTypeToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceTypeService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/servicetype";
    }
}
