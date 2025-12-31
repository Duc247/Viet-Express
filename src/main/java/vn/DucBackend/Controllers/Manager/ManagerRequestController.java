package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.PaginationUtil;

import java.util.Optional;

/**
 * Manager Request Controller - Quản lý yêu cầu/đơn hàng
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/manager")
public class ManagerRequestController {

    // Services cho business logic
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

    // Repositories cho template data (Thymeleaf cần Entity)
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
    @Autowired
    private StaffRepository staffRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // QUẢN LÝ YÊU CẦU
    // ==========================================
    @GetMapping("/requests")
    public String requestList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "manager/request/requests";
    }

    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/manager/requests";
        }

        CustomerRequest order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("shippers", shipperRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        model.addAttribute("staffs", staffRepository.findAll());
        model.addAttribute("parcels", parcelRepository.findByRequestId(id));
        model.addAttribute("trips", tripRepository.findTripsByRequestId(id));
        model.addAttribute("payments", paymentRepository.findByRequestId(id));
        return "manager/request/detail";
    }

    // CẬP NHẬT LOCATION CHO REQUEST
    @PostMapping("/requests/{id}/update-locations")
    public String updateRequestLocations(
            @PathVariable("id") Long id,
            @RequestParam(value = "senderLocationId", required = false) Long senderLocationId,
            @RequestParam(value = "receiverLocationId", required = false) Long receiverLocationId,
            RedirectAttributes redirectAttributes) {

        Optional<CustomerRequest> requestOpt = customerRequestRepository.findById(id);
        if (requestOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        CustomerRequest customerRequest = requestOpt.get();

        if (senderLocationId != null) {
            locationRepository.findById(senderLocationId).ifPresent(customerRequest::setSenderLocation);
        }
        if (receiverLocationId != null) {
            locationRepository.findById(receiverLocationId).ifPresent(customerRequest::setReceiverLocation);
        }

        customerRequestRepository.save(customerRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật địa điểm thành công!");
        return "redirect:/manager/requests/" + id;
    }

    // Chốt đơn → CONFIRMED (chỉ khi receiver đã xác nhận RECEIVER_CONFIRMED)
    @PostMapping("/requests/{id}/confirm")
    public String confirmRequest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<CustomerRequest> requestOpt = customerRequestRepository.findById(id);
        if (requestOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        CustomerRequest customerRequest = requestOpt.get();

        // Kiểm tra receiver đã xác nhận chưa
        if (customerRequest.getStatus() != CustomerRequest.RequestStatus.RECEIVER_CONFIRMED) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể chốt đơn! Người nhận chưa xác nhận đơn hàng.");
            return "redirect:/manager/requests/" + id;
        }

        // Kiểm tra có đủ location không
        if (customerRequest.getSenderLocation() == null || customerRequest.getReceiverLocation() == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cần thiết lập cả điểm lấy hàng và điểm giao trước khi chốt đơn!");
            return "redirect:/manager/requests/" + id;
        }

        // Cập nhật trạng thái qua Service → CONFIRMED (cả 2 đã xác nhận)
        customerRequestService.updateRequestStatus(id, "CONFIRMED");

        redirectAttributes.addFlashAttribute("successMessage", "Đã chốt đơn thành công!");
        return "redirect:/manager/requests/" + id;
    }

    // ==========================================
    // XEM CHI TIẾT KIỆN HÀNG CỦA REQUEST
    // ==========================================
    @GetMapping("/requests/{id}/parcels")
    public String requestParcels(
            @PathVariable("id") Long id,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/manager/requests";
        }

        CustomerRequest order = orderOpt.get();
        model.addAttribute("order", order);

        java.util.List<Parcel> parcels = parcelRepository.findByRequestId(id);

        // Filter
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            parcels = parcels.stream()
                    .filter(p -> (p.getParcelCode() != null && p.getParcelCode().toLowerCase().contains(kw)) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(kw)))
                    .toList();
        }
        if (status != null && !status.isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getStatus().name().equals(status))
                    .toList();
        }

        model.addAttribute("parcelsPage", PaginationUtil.paginate(parcels, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "manager/request/parcels";
    }

    // ==========================================
    // XEM CHI TIẾT CHUYẾN CỦA REQUEST
    // ==========================================
    @GetMapping("/requests/{id}/trips")
    public String requestTrips(
            @PathVariable("id") Long id,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/manager/requests";
        }

        CustomerRequest order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("locations", locationRepository.findAll());

        java.util.List<Trip> trips = tripRepository.findTripsByRequestId(id);

        // Filter
        if (status != null && !status.isEmpty()) {
            trips = trips.stream().filter(t -> t.getStatus().name().equals(status)).toList();
        }
        if (type != null && !type.isEmpty()) {
            trips = trips.stream().filter(t -> t.getTripType().name().equals(type)).toList();
        }

        model.addAttribute("tripsPage", PaginationUtil.paginate(trips, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        return "manager/request/trips";
    }

    // ==========================================
    // XEM CHI TIẾT THANH TOÁN CỦA REQUEST
    // ==========================================
    @GetMapping("/requests/{id}/payments")
    public String requestPayments(
            @PathVariable("id") Long id,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/manager/requests";
        }

        CustomerRequest order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("trips", tripRepository.findTripsByRequestId(id));

        java.util.List<Payment> payments = paymentRepository.findByRequestId(id);

        // Filter
        if (status != null && !status.isEmpty()) {
            payments = payments.stream().filter(p -> p.getStatus().name().equals(status)).toList();
        }
        if (type != null && !type.isEmpty()) {
            payments = payments.stream().filter(p -> p.getPaymentType().name().equals(type)).toList();
        }

        model.addAttribute("paymentsPage", PaginationUtil.paginate(payments, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        return "manager/request/payments";
    }

    // ==========================================
    // GIAO VIỆC CHO STAFF
    // ==========================================
    @PostMapping("/requests/{id}/assign-staff")
    public String assignStaffToRequest(
            @PathVariable("id") Long id,
            @RequestParam("staffId") Long staffId,
            RedirectAttributes redirectAttributes) {

        Optional<CustomerRequest> requestOpt = customerRequestRepository.findById(id);
        if (requestOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        Optional<Staff> staffOpt = staffRepository.findById(staffId);
        if (staffOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên!");
            return "redirect:/manager/requests/" + id;
        }

        CustomerRequest customerRequest = requestOpt.get();
        customerRequest.setAssignedStaff(staffOpt.get());
        customerRequest.setAssignedAt(java.time.LocalDateTime.now());
        customerRequestRepository.save(customerRequest);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã giao việc cho " + staffOpt.get().getFullName() + " thành công!");
        return "redirect:/manager/requests/" + id;
    }
}
