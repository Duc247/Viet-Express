package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Staff Warehouse Controller - Quản lý kho, xem hàng trong kho
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/staff")
public class StaffWarehouseController {

    // Services cho business logic
    @Autowired
    private ParcelService parcelService;

    // Repositories cho template data
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private StaffRepository staffRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getStaffIdFromSession(HttpSession session) {
        Object staffId = session.getAttribute("staffId");
        return staffId != null ? (Long) staffId : null;
    }

    // ==========================================
    // KHO - Xem kiện hàng trong kho với tìm kiếm
    // ==========================================
    @GetMapping("/warehouse")
    public String warehouse(Model model, HttpServletRequest request, HttpSession session,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "requestId", required = false) Long requestId,
            @RequestParam(value = "senderPhone", required = false) String senderPhone,
            @RequestParam(value = "receiverPhone", required = false) String receiverPhone,
            @RequestParam(value = "minLength", required = false) BigDecimal minLength,
            @RequestParam(value = "maxLength", required = false) BigDecimal maxLength,
            @RequestParam(value = "minWidth", required = false) BigDecimal minWidth,
            @RequestParam(value = "maxWidth", required = false) BigDecimal maxWidth,
            @RequestParam(value = "minHeight", required = false) BigDecimal minHeight,
            @RequestParam(value = "maxHeight", required = false) BigDecimal maxHeight) {

        addCommonAttributes(model, request);

        Long staffId = getStaffIdFromSession(session);
        Staff staff = staffId != null ? staffRepository.findById(staffId).orElse(null) : null;

        List<Parcel> parcels;

        if (staff != null && staff.getLocation() != null) {
            // Lấy kiện hàng IN_WAREHOUSE trong kho của staff
            parcels = parcelRepository.findByCurrentLocationIdAndStatus(
                    staff.getLocation().getId(), Parcel.ParcelStatus.IN_WAREHOUSE);
            model.addAttribute("warehouseName", staff.getLocation().getName());
        } else {
            // Fallback: lấy tất cả IN_WAREHOUSE
            parcels = parcelRepository.findByStatus(Parcel.ParcelStatus.IN_WAREHOUSE);
            model.addAttribute("warehouseName", "Tất cả kho");
        }

        // Áp dụng filter
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            parcels = parcels.stream()
                    .filter(p -> (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchLower))
                            || (p.getParcelCode() != null && p.getParcelCode().toLowerCase().contains(searchLower)))
                    .toList();
        }

        if (requestId != null) {
            parcels = parcels.stream()
                    .filter(p -> p.getRequest() != null && requestId.equals(p.getRequest().getId()))
                    .toList();
        }

        if (senderPhone != null && !senderPhone.trim().isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getRequest() != null && p.getRequest().getSender() != null
                            && p.getRequest().getSender().getPhone() != null
                            && p.getRequest().getSender().getPhone().contains(senderPhone))
                    .toList();
        }

        if (receiverPhone != null && !receiverPhone.trim().isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getRequest() != null && p.getRequest().getReceiver() != null
                            && p.getRequest().getReceiver().getPhone() != null
                            && p.getRequest().getReceiver().getPhone().contains(receiverPhone))
                    .toList();
        }

        // Filter by dimensions
        if (minLength != null) {
            parcels = parcels.stream()
                    .filter(p -> p.getLengthCm() != null && p.getLengthCm().compareTo(minLength) >= 0)
                    .toList();
        }
        if (maxLength != null) {
            parcels = parcels.stream()
                    .filter(p -> p.getLengthCm() != null && p.getLengthCm().compareTo(maxLength) <= 0)
                    .toList();
        }
        if (minWidth != null) {
            parcels = parcels.stream()
                    .filter(p -> p.getWidthCm() != null && p.getWidthCm().compareTo(minWidth) >= 0)
                    .toList();
        }
        if (maxWidth != null) {
            parcels = parcels.stream()
                    .filter(p -> p.getWidthCm() != null && p.getWidthCm().compareTo(maxWidth) <= 0)
                    .toList();
        }
        if (minHeight != null) {
            parcels = parcels.stream()
                    .filter(p -> p.getHeightCm() != null && p.getHeightCm().compareTo(minHeight) >= 0)
                    .toList();
        }
        if (maxHeight != null) {
            parcels = parcels.stream()
                    .filter(p -> p.getHeightCm() != null && p.getHeightCm().compareTo(maxHeight) <= 0)
                    .toList();
        }

        model.addAttribute("parcels", parcels);
        model.addAttribute("totalCount", parcels.size());

        // Trả lại các giá trị filter đã nhập
        model.addAttribute("search", search);
        model.addAttribute("requestId", requestId);
        model.addAttribute("senderPhone", senderPhone);
        model.addAttribute("receiverPhone", receiverPhone);
        model.addAttribute("minLength", minLength);
        model.addAttribute("maxLength", maxLength);
        model.addAttribute("minWidth", minWidth);
        model.addAttribute("maxWidth", maxWidth);
        model.addAttribute("minHeight", minHeight);
        model.addAttribute("maxHeight", maxHeight);

        return "staff/warehouse";
    }
}
