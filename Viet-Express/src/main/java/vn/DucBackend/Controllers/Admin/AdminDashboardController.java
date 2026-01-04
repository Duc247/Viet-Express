package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Services.*;

/**
 * Admin Dashboard Controller - Quản lý trang dashboard admin
 * 
 * Services sử dụng:
 * - CustomerRequestService: Đếm tổng số đơn hàng
 * - CustomerService: Đếm tổng số khách hàng
 * - ShipperService: Đếm tổng số shipper
 * - LocationService: Đếm tổng số địa điểm
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShipperService shipperService;
    @Autowired
    private LocationService locationService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    /**
     * Hiển thị trang Dashboard
     * 
     * Service sử dụng:
     * - customerRequestService.findAllRequests().size() -> Đếm tổng đơn hàng
     * - customerService.findAllCustomers().size() -> Đếm tổng khách hàng
     * - shipperService.findAllShippers().size() -> Đếm tổng shipper
     * - locationService.findAllLocations().size() -> Đếm tổng địa điểm
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("totalOrders", customerRequestService.findAllRequests().size());
        model.addAttribute("totalCustomers", customerService.findAllCustomers().size());
        model.addAttribute("totalShippers", shipperService.findAllShippers().size());
        model.addAttribute("totalLocations", locationService.findAllLocations().size());
        return "admin/dashboard";
    }
}
