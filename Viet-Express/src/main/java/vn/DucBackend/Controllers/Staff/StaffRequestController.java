package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Staff Request Controller - Tiếp nhận yêu cầu và tạo kiện hàng
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/staff")
public class StaffRequestController {

    // Services cho business logic
    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private ParcelService parcelService;

    // Repositories cho template data
    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ActionTypeRepository actionTypeRepository;
    @Autowired
    private ParcelActionRepository parcelActionRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getStaffIdFromSession(HttpSession session) {
        Object staffId = session.getAttribute("staffId");
        return staffId != null ? (Long) staffId : null;
    }

    private Long getUserIdFromSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId != null ? (Long) userId : null;
    }

    // ==========================================
    // TIẾP NHẬN YÊU CẦU - Hiển thị requests được giao cho staff
    // ==========================================
    @GetMapping("/requests")
    public String requestList(Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        // Lấy requests được giao cho staff này
        List<CustomerRequest> assignedRequests = customerRequestRepository.findByAssignedStaffId(staffId);
        model.addAttribute("requests", assignedRequests);
        return "staff/request/requests";
    }

    // ==========================================
    // CHI TIẾT REQUEST - Form tạo kiện hàng
    // ==========================================
    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        addCommonAttributes(model, request);

        Optional<CustomerRequest> reqOpt = customerRequestRepository.findById(id);
        if (reqOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/staff/requests";
        }

        CustomerRequest customerRequest = reqOpt.get();
        model.addAttribute("customerRequest", customerRequest);

        // Lấy danh sách kiện hàng đã tạo cho request này
        List<Parcel> existingParcels = parcelRepository.findByRequestId(id);
        model.addAttribute("existingParcels", existingParcels);

        return "staff/request/detail";
    }

    // ==========================================
    // TẠO KIỆN HÀNG TỪ REQUEST
    // ==========================================
    @PostMapping("/requests/{id}/create-parcel")
    public String createParcel(@PathVariable("id") Long requestId,
            @RequestParam("description") String description,
            @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
            @RequestParam(value = "weightKg", required = false) BigDecimal weightKg,
            @RequestParam(value = "lengthCm", required = false) BigDecimal lengthCm,
            @RequestParam(value = "widthCm", required = false) BigDecimal widthCm,
            @RequestParam(value = "heightCm", required = false) BigDecimal heightCm,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        Optional<CustomerRequest> reqOpt = customerRequestRepository.findById(requestId);
        if (reqOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/staff/requests";
        }

        CustomerRequest customerRequest = reqOpt.get();
        Staff staff = staffRepository.findById(staffId).orElse(null);

        // Tạo parcel code
        String parcelCode = generateParcelCode(requestId);

        // Tạo parcel mới
        Parcel parcel = new Parcel();
        parcel.setRequest(customerRequest);
        parcel.setParcelCode(parcelCode);
        parcel.setDescription(description);
        parcel.setCodAmount(codAmount);
        parcel.setWeightKg(weightKg);
        parcel.setLengthCm(lengthCm);
        parcel.setWidthCm(widthCm);
        parcel.setHeightCm(heightCm);
        parcel.setStatus(Parcel.ParcelStatus.CREATED);

        // Nếu staff có kho, set location
        if (staff != null && staff.getLocation() != null) {
            parcel.setCurrentLocation(staff.getLocation());
        }

        parcelRepository.save(parcel);

        // Tạo parcel action - CREATED
        createParcelAction(parcel, customerRequest, "CREATED", null,
                staff != null ? staff.getLocation() : null,
                getUserIdFromSession(session),
                "Staff tạo kiện hàng: " + description);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã tạo kiện hàng " + parcelCode + " thành công!");
        return "redirect:/staff/requests/" + requestId;
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================
    private String generateParcelCode(Long requestId) {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = parcelRepository.countByRequestId(requestId) + 1;
        return String.format("PCL-%s-%d-%02d", datePart, requestId, count);
    }

    private void createParcelAction(Parcel parcel, CustomerRequest request, String actionCode,
            Location fromLocation, Location toLocation, Long userId, String note) {
        Optional<ActionType> actionTypeOpt = actionTypeRepository.findByActionCode(actionCode);
        if (actionTypeOpt.isPresent()) {
            ParcelAction action = new ParcelAction();
            action.setParcel(parcel);
            action.setRequest(request);
            action.setActionType(actionTypeOpt.get());
            action.setFromLocation(fromLocation);
            action.setToLocation(toLocation);
            action.setNote(note);

            if (userId != null) {
                User user = new User();
                user.setId(userId);
                action.setActorUser(user);
            }

            parcelActionRepository.save(action);
        }
    }
}
