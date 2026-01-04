package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.*;
import vn.DucBackend.Repositories.UserRepository;
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
    @Autowired
    private UserRepository userRepository;

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

    /**
     * Chi tiết yêu cầu khách hàng
     * Service: customerRequestService.findRequestById()
     */
    @GetMapping("/request/{id}")
    public String requestDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        CustomerRequestDTO requestDTO = customerRequestService.findRequestById(id).orElse(null);
        if (requestDTO == null) {
            return "redirect:/admin/request";
        }
        model.addAttribute("customerRequest", requestDTO);
        // Danh sách managers để gán
        model.addAttribute("managers", userRepository.findActiveUsersByRole("MANAGER"));
        return "admin/request/detail";
    }
    /**
     * Gán Manager cho đơn hàng
     */
    @PostMapping("/request/{id}/assign-manager")
    public String assignManager(@PathVariable Long id,
            @RequestParam Long managerId,
            RedirectAttributes redirectAttributes) {
        try {
            customerRequestService.assignManager(id, managerId);
            redirectAttributes.addFlashAttribute("success", "Đã giao đơn hàng cho Manager thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/request/" + id;
    }

    /**
     * Form sửa yêu cầu khách hàng
     * Service: customerRequestService.findRequestById(),
     * locationService.findAllLocations()
     */
    @GetMapping("/request/{id}/edit")
    public String requestEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        CustomerRequestDTO requestDTO = customerRequestService.findRequestById(id).orElse(null);
        if (requestDTO == null) {
            return "redirect:/admin/request";
        }
        model.addAttribute("customerRequest", requestDTO);
        model.addAttribute("locations", locationService.findAllLocations());
        return "admin/request/form";
    }

    /**
     * Tạo mới đơn hàng (Admin)
     * Template: admin/request/form
     */
    @PostMapping("/request/create")
    public String createRequest(@ModelAttribute CustomerRequestDTO dto, RedirectAttributes redirectAttributes) {
        try {
            CustomerRequestDTO created = customerRequestService.createRequest(dto);
            redirectAttributes.addFlashAttribute("success", "Tạo đơn hàng thành công!");
            return "redirect:/admin/request/" + created.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/request";
        }
    }

    /**
     * Cập nhật đơn hàng (Admin)
     * Template action: /admin/request/{id}/update
     */
    @PostMapping("/request/{id}/update")
    public String updateRequest(@PathVariable Long id, @ModelAttribute CustomerRequestDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            customerRequestService.updateRequest(id, dto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/request/" + id;
    }

    /**
     * Xoá đơn hàng (Admin)
     */
    @PostMapping("/request/{id}/delete")
    public String deleteRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerRequestService.deleteRequest(id);
            redirectAttributes.addFlashAttribute("success", "Đã xoá đơn hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/request";
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

    /**
     * Chi tiết chuyến xe
     * Service: tripService.findTripById()
     */
    @GetMapping("/trip/{id}")
    public String tripDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        TripDTO tripDTO = tripService.findTripById(id).orElse(null);
        if (tripDTO == null) {
            return "redirect:/admin/trip";
        }
        model.addAttribute("trip", tripDTO);
        return "admin/trip/detail";
    }

    /**
     * Form sửa chuyến xe
     * Service: tripService.findTripById(), locationService, shipperService,
     * vehicleService
     */
    @GetMapping("/trip/{id}/edit")
    public String tripEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        TripDTO tripDTO = tripService.findTripById(id).orElse(null);
        if (tripDTO == null) {
            return "redirect:/admin/trip";
        }
        model.addAttribute("trip", tripDTO);
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("shippers", shipperService.findAllShippers());
        model.addAttribute("vehicles", vehicleService.findAll());
        return "admin/trip/form";
    }

    /**
     * Lưu Trip (create/update) (Admin)
     * Template action: /admin/trip/save
     */
    @PostMapping("/trip/save")
    public String saveTrip(@ModelAttribute TripDTO dto, RedirectAttributes redirectAttributes) {
        try {
            TripDTO saved;
            if (dto.getId() != null) {
                saved = tripService.updateTrip(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("success", "Cập nhật chuyến xe thành công!");
            } else {
                saved = tripService.createTrip(dto);
                redirectAttributes.addFlashAttribute("success", "Tạo chuyến xe thành công!");
            }
            return "redirect:/admin/trip/" + saved.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/trip";
        }
    }

    /**
     * Xoá Trip (Admin)
     * Template action: /admin/trip/{id}/delete
     */
    @PostMapping("/trip/{id}/delete")
    public String deleteTrip(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tripService.deleteTrip(id);
            redirectAttributes.addFlashAttribute("success", "Đã xoá chuyến xe!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/trip";
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
