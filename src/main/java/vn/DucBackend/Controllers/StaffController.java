package vn.DucBackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Repositories.*;

/**
 * Staff Controller - Xử lý các trang dành cho nhân viên (staff)
 */
@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private ParcelRepository parcelRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("totalRequests", customerRequestRepository.count()); // Pending processing
        model.addAttribute("totalParcels", parcelRepository.count());
        return "staff/dashboard";
    }

    // ==========================================
    // TIẾP NHẬN YÊU CẦU
    // ==========================================
    @GetMapping("/requests")
    public String requestList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "staff/request/requests";
    }

    // ==========================================
    // QUẢN LÝ KIỆN HÀNG
    // ==========================================
    @GetMapping("/parcels")
    public String parcelList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("parcels", parcelRepository.findAll());
        return "staff/parcel/parcels";
    }
}
