package vn.DucBackend.Controllers;

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
 * Admin Controller - Quản lý các trang admin
 * Tên endpoints và templates khớp với tên Entity trong database
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    // Inject các Repository
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;
    @Autowired
    private CaseStudyRepository caseStudyRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ActionTypeRepository actionTypeRepository;
    @Autowired
    private SystemLogRepository systemLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("totalOrders", customerRequestRepository.count());
        model.addAttribute("totalCustomers", customerRepository.count());
        model.addAttribute("totalShippers", shipperRepository.count());
        model.addAttribute("totalLocations", locationRepository.count());
        return "admin/dashboard";
    }

    // ==========================================
    // VẬN HÀNH (CustomerRequest, Parcel, Trip, Payment)
    // ==========================================
    @GetMapping("/request")
    public String requestList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "admin/request/list";
    }

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

    // ==========================================
    // TÀI NGUYÊN (Location, Route, Vehicle, ServiceType)
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

    // ==========================================
    // NHÂN SỰ (User, Customer, Shipper, Staff)
    // ==========================================
    @GetMapping("/user")
    public String userList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("users", userRepository.findAll());
        return "admin/user/list";
    }

    @GetMapping("/user/create")
    public String userForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user/form";
    }

    @GetMapping("/customer")
    public String customerList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("customers", customerRepository.findAll());
        return "admin/customer/list";
    }

    @GetMapping("/customer/create")
    public String customerForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("customer", new Customer());
        model.addAttribute("users", userRepository.findAll());
        return "admin/customer/form";
    }

    @GetMapping("/shipper")
    public String shipperList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shippers", shipperRepository.findAll());
        return "admin/shipper/list";
    }

    @GetMapping("/shipper/create")
    public String shipperForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shipper", new Shipper());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/shipper/form";
    }

    @GetMapping("/staff")
    public String staffList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("staffList", staffRepository.findAll());
        return "admin/staff/list";
    }

    @GetMapping("/staff/create")
    public String staffForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("staff", new Staff());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "admin/staff/form";
    }

    // ==========================================
    // MARKETING (CaseStudy)
    // ==========================================
    @GetMapping("/casestudy")
    public String caseStudyList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudies", caseStudyRepository.findAll());
        return "admin/casestudy/list";
    }

    @GetMapping("/casestudy/create")
    public String caseStudyForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudy", new CaseStudy());
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "admin/casestudy/form";
    }

    // ==========================================
    // HỆ THỐNG (Role, ActionType, SystemLog)
    // ==========================================
    @GetMapping("/role")
    public String roleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/role/list";
    }

    @GetMapping("/role/create")
    public String roleForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("role", new Role());
        return "admin/role/form";
    }

    @GetMapping("/actiontype")
    public String actionTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionTypes", actionTypeRepository.findAll());
        return "admin/actiontype/list";
    }

    @GetMapping("/actiontype/create")
    public String actionTypeForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionType", new ActionType());
        return "admin/actiontype/form";
    }

    @GetMapping("/systemlog")
    public String systemLogList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("logs", systemLogRepository.findAll());
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
