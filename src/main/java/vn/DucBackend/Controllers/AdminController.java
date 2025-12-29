package vn.DucBackend.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Admin Controller - Quản lý các trang admin
 * Tên endpoints và templates khớp với tên Entity trong database
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    // Inject requestURI vào model cho sidebar
    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/dashboard";
    }

    // ==========================================
    // VẬN HÀNH (CustomerRequest, Parcel, Trip, Payment)
    // ==========================================
    @GetMapping("/request")
    public String requestList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/request/list";
    }

    @GetMapping("/parcel")
    public String parcelList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/parcel/list";
    }

    @GetMapping("/trip")
    public String tripList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/trip/list";
    }

    @GetMapping("/payment")
    public String paymentList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/payment/list";
    }

    // ==========================================
    // TÀI NGUYÊN (Location, Route, Vehicle, ServiceType)
    // ==========================================
    @GetMapping("/location")
    public String locationList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/location/list";
    }

    @GetMapping("/location/create")
    public String locationForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/location/form";
    }

    @GetMapping("/route")
    public String routeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/route/list";
    }

    @GetMapping("/vehicle")
    public String vehicleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/vehicle/vehicles";
    }

    @GetMapping("/servicetype")
    public String serviceTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/servicetype/list";
    }

    // ==========================================
    // NHÂN SỰ (User, Customer, Shipper, Staff)
    // ==========================================
    @GetMapping("/user")
    public String userList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/user/users";
    }

    @GetMapping("/customer")
    public String customerList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/customer/list";
    }

    @GetMapping("/shipper")
    public String shipperList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/shipper/list";
    }

    @GetMapping("/staff")
    public String staffList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/staff/list";
    }

    // ==========================================
    // MARKETING (CaseStudy)
    // ==========================================
    @GetMapping("/casestudy")
    public String caseStudyList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/casestudy/list";
    }

    // ==========================================
    // HỆ THỐNG (Role, ActionType, SystemLog)
    // ==========================================
    @GetMapping("/role")
    public String roleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/role/list";
    }

    @GetMapping("/actiontype")
    public String actionTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/actiontype/list";
    }

    @GetMapping("/systemlog")
    public String systemLogList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/systemlog/list";
    }

    // ==========================================
    // CONFIG
    // ==========================================
    @GetMapping("/system-config")
    public String systemConfig(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/config/system-config";
    }
}
