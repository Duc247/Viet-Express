package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String locationForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("location", new Location());
        return "admin/location/form";
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
    public String routeForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("route", new Route());
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/route/form";
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
    public String vehicleForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/vehicle/form";
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
    public String serviceTypeForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("serviceType", new ServiceType());
        return "admin/servicetype/form";
    }
}
