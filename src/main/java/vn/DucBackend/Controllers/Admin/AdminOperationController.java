package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.*;
import vn.DucBackend.Services.*;

/**
 * Admin Operation Controller - Quản lý vận hành (Request, Parcel, Trip,
 * Payment)
 * 
 * Services sử dụng:
 * - CustomerRequestService: Quản lý yêu cầu khách hàng
 * - ParcelService: Quản lý kiện hàng
 * - TripService: Quản lý chuyến xe
 * - PaymentService: Quản lý thanh toán
 * - LocationService: Lấy danh sách địa điểm (cho form)
 * - ShipperService: Lấy danh sách shipper (cho form)
 * - VehicleService: Lấy danh sách phương tiện (cho form)
 */
@Controller
@RequestMapping("/admin")
public class AdminOperationController {

    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private TripService tripService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ShipperService shipperService;
    @Autowired
    private VehicleService vehicleService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // CUSTOMER REQUEST
    // ==========================================

    /**
     * Danh sách yêu cầu khách hàng
     * Service: customerRequestService.findAllRequests()
     */
    @GetMapping("/request")
    public String requestList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("requests", customerRequestService.findAllRequests());
        return "admin/request/list";
    }

    // ==========================================
    // PARCEL
    // ==========================================

    /**
     * Danh sách kiện hàng
     * Service: parcelService.findAllParcels()
     */
    @GetMapping("/parcel")
    public String parcelList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("parcels", parcelService.findAllParcels());
        return "admin/parcel/list";
    }

    /**
     * Chi tiết kiện hàng
     * Service: parcelService.findParcelById()
     */
    @GetMapping("/parcel/{id}")
    public String parcelDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("parcel", parcelService.findParcelById(id).orElse(null));
        return "admin/parcel/detail";
    }

    // ==========================================
    // TRIP
    // ==========================================

    /**
     * Danh sách chuyến xe
     * Service: tripService.findAllTrips()
     */
    @GetMapping("/trip")
    public String tripList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("trips", tripService.findAllTrips());
        return "admin/trip/list";
    }

    /**
     * Form tạo chuyến xe mới
     * Service: locationService.findAllLocations(),
     * shipperService.findAllShippers(),
     * vehicleService.findAll()
     */
    @GetMapping("/trip/create")
    public String tripForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("trip", new TripDTO());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("shippers", shipperService.findAllShippers());
        model.addAttribute("vehicles", vehicleService.findAll());
        return "admin/trip/form";
    }

    // ==========================================
    // PAYMENT
    // ==========================================

    /**
     * Danh sách thanh toán
     * Service: paymentService.findAllPayments()
     */
    @GetMapping("/payment")
    public String paymentList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("payments", paymentService.findAllPayments());
        return "admin/payment/list";
    }

    /**
     * Chi tiết thanh toán
     * Service: paymentService.findPaymentById()
     */
    @GetMapping("/payment/{id}")
    public String paymentDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("payment", paymentService.findPaymentById(id).orElse(null));
        return "admin/payment/detail";
    }
}
