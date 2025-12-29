package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;

/**
 * Admin Operation Controller - Quản lý vận hành (Request, Parcel, Trip,
 * Payment)
 */
@Controller
@RequestMapping("/admin")
public class AdminOperationController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private PaymentRepository paymentRepository;
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
    // CUSTOMER REQUEST
    // ==========================================
    @GetMapping("/request")
    public String requestList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "admin/request/list";
    }

    // ==========================================
    // PARCEL
    // ==========================================
    @GetMapping("/parcel")
    public String parcelList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("parcels", parcelRepository.findAll());
        return "admin/parcel/list";
    }

    @GetMapping("/parcel/{id}")
    public String parcelDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("parcel", parcelRepository.findById(id).orElse(null));
        return "admin/parcel/detail";
    }

    // ==========================================
    // TRIP
    // ==========================================
    @GetMapping("/trip")
    public String tripList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("trips", tripRepository.findAll());
        return "admin/trip/list";
    }

    @GetMapping("/trip/create")
    public String tripForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("trip", new Trip());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("shippers", shipperRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "admin/trip/form";
    }

    // ==========================================
    // PAYMENT
    // ==========================================
    @GetMapping("/payment")
    public String paymentList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("payments", paymentRepository.findAll());
        return "admin/payment/list";
    }

    @GetMapping("/payment/{id}")
    public String paymentDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("payment", paymentRepository.findById(id).orElse(null));
        return "admin/payment/detail";
    }
}
