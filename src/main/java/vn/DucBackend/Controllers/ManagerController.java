package vn.DucBackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Repositories.*;

/**
 * Manager Controller - Xử lý các trang dành cho quản lý kho
 */
@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("totalRequests", customerRequestRepository.count());
        model.addAttribute("totalParcels", parcelRepository.count());
        model.addAttribute("totalTrips", tripRepository.count());
        model.addAttribute("totalShippers", shipperRepository.count());
        return "manager/dashboard";
    }

    // ==========================================
    // QUẢN LÝ ĐƠN HÀNG
    // ==========================================
    @GetMapping("/requests")
    public String requestList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "manager/request/requests";
    }

    @GetMapping("/orders")
    public String orderList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("orders", customerRequestRepository.findAll());
        return "manager/order/orders";
    }

    // ==========================================
    // LẬP KẾ HOẠCH
    // ==========================================
    @GetMapping("/planning")
    public String shipmentPlanning(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("trips", tripRepository.findAll());
        model.addAttribute("shippers", shipperRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "manager/planning/planning";
    }

    // ==========================================
    // QUẢN LÝ KHO
    // ==========================================
    @GetMapping("/locations")
    public String warehouseList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("locations", locationRepository.findAll());
        return "manager/location/locations";
    }

    // ==========================================
    // QUẢN LÝ KIỆN HÀNG
    // ==========================================
    @GetMapping("/parcels")
    public String parcelList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("parcels", parcelRepository.findAll());
        return "manager/parcel/parcels";
    }

    // ==========================================
    // QUẢN LÝ TÀI XẾ
    // ==========================================
    @GetMapping("/shippers")
    public String shipperList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shippers", shipperRepository.findAll());
        return "manager/shipper/shippers";
    }
}
