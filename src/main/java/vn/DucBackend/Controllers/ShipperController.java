package vn.DucBackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Repositories.*;

/**
 * Shipper Controller - Xử lý các trang dành cho tài xế (driver)
 */
@Controller
@RequestMapping("/driver")
public class ShipperController {

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private LocationRepository locationRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        // TODO: Lọc theo shipper đang đăng nhập
        model.addAttribute("totalTrips", tripRepository.count());
        model.addAttribute("activeTrips", tripRepository.findAll()); // TODO: lọc IN_PROGRESS
        return "driver/dashboard";
    }

    // ==========================================
    // CHUYẾN XE HIỆN TẠI
    // ==========================================
    @GetMapping("/shipments")
    public String myShipments(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        // TODO: Lọc theo shipper đang đăng nhập
        model.addAttribute("trips", tripRepository.findAll());
        return "driver/trip/list";
    }

    @GetMapping("/shipment-detail")
    public String shipmentDetail(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "driver/trip/detail";
    }

    // ==========================================
    // LỊCH SỬ
    // ==========================================
    @GetMapping("/history")
    public String history(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        // TODO: Lọc theo shipper và status = COMPLETED
        model.addAttribute("completedTrips", tripRepository.findAll());
        return "driver/history";
    }

    // ==========================================
    // CẬP NHẬT VỊ TRÍ
    // ==========================================
    @GetMapping("/location")
    public String updateLocation(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("locations", locationRepository.findAll());
        return "driver/location";
    }
}
