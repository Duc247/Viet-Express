package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Repositories.*;

/**
 * Admin Dashboard Controller - Quản lý trang dashboard admin
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private LocationRepository locationRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("totalOrders", customerRequestRepository.count());
        model.addAttribute("totalCustomers", customerRepository.count());
        model.addAttribute("totalShippers", shipperRepository.count());
        model.addAttribute("totalLocations", locationRepository.count());
        return "admin/dashboard";
    }
}
