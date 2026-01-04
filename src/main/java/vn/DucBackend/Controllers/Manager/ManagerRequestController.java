package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.LoggingHelper;
import vn.DucBackend.Utils.PaginationUtil;

import java.util.List;

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
    @Autowired
    private ShipperService shipperService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private UserService userService;

    @Autowired
    private LoggingHelper loggingHelper;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // QUẢN LÝ YÊU CẦU - CHỈ HIỂN ĐƠN ĐƯỢC GÁN CHO MANAGER NÀY
    // ==========================================
    @GetMapping("/requests")
    public String requestList(Model model, HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        addCommonAttributes(model, request);

        // Lấy user hiện tại
        User currentUser = userService.getUserEntityByUsername(userDetails.getUsername());

        if (currentUser != null) {
            // Chỉ lấy đơn hàng được gán cho manager này
            List<CustomerRequest> assignedRequests = customerRequestService
                    .findByAssignedManagerEntities(currentUser.getId());
            model.addAttribute("requests", assignedRequests);
            model.addAttribute("totalRequests", assignedRequests.size());
        } else {
            model.addAttribute("requests", java.util.Collections.emptyList());
            model.addAttribute("totalRequests", 0);
        }

        return "manager/request/requests";
    }

    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        CustomerRequest order = customerRequestService.getRequestEntityById(id);
        if (order == null) {
            return "redirect:/manager/requests";
        }

        model.addAttribute("order", order);
        model.addAttribute("locations", locationService.getAllLocationEntities());
        model.addAttribute("shippers", shipperService.getAllShipperEntities());
        model.addAttribute("vehicles", vehicleService.getAllVehicleEntities());
        model.addAttribute("staffs", staffService.getAllStaffEntities());
        model.addAttribute("parcels", parcelService.findByRequestIdEntities(id));
        model.addAttribute("trips", tripService.findTripsByRequestIdEntities(id));
        model.addAttribute("payments", paymentService.findPaymentsByRequestIdEntities(id));
        return "manager/request/detail";
    }

    // CẬP NHẬT LOCATION CHO REQUEST
    @PostMapping("/requests/{id}/update-locations")
    public String updateRequestLocations(
            @PathVariable("id") Long id,
            @RequestParam(value = "senderLocationId", required = false) Long senderLocationId,
            @RequestParam(value = "receiverLocationId", required = false) Long receiverLocationId,
            RedirectAttributes redirectAttributes) {

        CustomerRequest customerRequest = customerRequestService.getRequestEntityById(id);
        if (customerRequest == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        if (senderLocationId != null) {
            Location location = locationService.getLocationEntityById(senderLocationId);
            if (location != null) {
                customerRequest.setSenderLocation(location);
            }
        }
        if (receiverLocationId != null) {
            Location location = locationService.getLocationEntityById(receiverLocationId);
            if (location != null) {
                customerRequest.setReceiverLocation(location);
            }
        }

        customerRequestService.saveRequestEntity(customerRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật địa điểm thành công!");
        return "redirect:/manager/requests/" + id;
    }

    // Chốt đơn → CONFIRMED (chỉ khi receiver đã xác nhận RECEIVER_CONFIRMED)
    @PostMapping("/requests/{id}/confirm")
    public String confirmRequest(@PathVariable("id") Long id, HttpServletRequest httpRequest,
            RedirectAttributes redirectAttributes) {
        CustomerRequest customerRequest = customerRequestService.getRequestEntityById(id);
        if (customerRequest == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

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

        // Ghi log duyệt đơn
        loggingHelper.logOrderConfirmed(null, customerRequest.getRequestCode(), httpRequest);

        redirectAttributes.addFlashAttribute("successMessage", "Đã chốt đơn thành công!");
        return "redirect:/manager/requests/" + id;
    }

    // Force Confirm - Cho phép Manager bypass receiver confirmation
    @PostMapping("/requests/{id}/force-confirm")
    public String forceConfirmRequest(@PathVariable("id") Long id, HttpServletRequest httpRequest,
            RedirectAttributes redirectAttributes) {
        CustomerRequest customerRequest = customerRequestService.getRequestEntityById(id);
        if (customerRequest == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        // Kiểm tra có đủ location không
        if (customerRequest.getSenderLocation() == null || customerRequest.getReceiverLocation() == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cần thiết lập cả điểm lấy hàng và điểm giao trước khi chốt đơn!");
            return "redirect:/manager/requests/" + id;
        }

        // Force update → CONFIRMED (bỏ qua kiểm tra receiver)
        customerRequestService.updateRequestStatus(id, "CONFIRMED");

        // Ghi log duyệt đơn (force)
        loggingHelper.logOrderConfirmed(null, customerRequest.getRequestCode() + " (Force)", httpRequest);

        redirectAttributes.addFlashAttribute("successMessage", "Đã force chốt đơn thành công!");
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

        CustomerRequest order = customerRequestService.getRequestEntityById(id);
        if (order == null) {
            return "redirect:/manager/requests";
        }

        model.addAttribute("order", order);

        java.util.List<Parcel> parcels = parcelService.findByRequestIdEntities(id);

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

        CustomerRequest order = customerRequestService.getRequestEntityById(id);
        if (order == null) {
            return "redirect:/manager/requests";
        }

        model.addAttribute("order", order);
        model.addAttribute("locations", locationService.getAllLocationEntities());

        java.util.List<Trip> trips = tripService.findTripsByRequestIdEntities(id);

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

        CustomerRequest order = customerRequestService.getRequestEntityById(id);
        if (order == null) {
            return "redirect:/manager/requests";
        }

        model.addAttribute("order", order);
        model.addAttribute("trips", tripService.findTripsByRequestIdEntities(id));

        java.util.List<Payment> payments = paymentService.findPaymentsByRequestIdEntities(id);

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

        CustomerRequest customerRequest = customerRequestService.getRequestEntityById(id);
        if (customerRequest == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        Staff staff = staffService.getStaffEntityById(staffId);
        if (staff == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên!");
            return "redirect:/manager/requests/" + id;
        }

        customerRequest.setAssignedStaff(staff);
        customerRequest.setAssignedAt(java.time.LocalDateTime.now());
        customerRequestService.saveRequestEntity(customerRequest);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã giao việc cho " + staff.getFullName() + " thành công!");
        return "redirect:/manager/requests/" + id;
    }
}
