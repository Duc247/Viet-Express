package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.DTO.ParcelDTO;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Services.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Staff Warehouse Controller - Quản lý kho, xem hàng trong kho
 * Chỉ sử dụng Service layer - không gọi Repository trực tiếp
 */
@Controller
@RequestMapping("/staff")
public class StaffWarehouseController {

    @Autowired
    private ParcelService parcelService;
    @Autowired
    private StaffService staffService;

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
        Staff staff = staffId != null ? staffService.getStaffEntityById(staffId) : null;

        List<ParcelDTO> parcels;

        if (staff != null && staff.getLocation() != null) {
            // Lấy kiện hàng IN_WAREHOUSE trong kho của staff - Sử dụng Service
            parcels = parcelService.findByLocationIdAndStatus(staff.getLocation().getId(), "IN_WAREHOUSE");
            model.addAttribute("warehouseName", staff.getLocation().getName());
        } else {
            // Fallback: lấy tất cả IN_WAREHOUSE
            parcels = parcelService.findParcelsByStatus("IN_WAREHOUSE");
            model.addAttribute("warehouseName", "Tất cả kho");
        }

        // Áp dụng filter theo mô tả/mã kiện
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            parcels = parcels.stream()
                    .filter(p -> (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchLower))
                            || (p.getParcelCode() != null && p.getParcelCode().toLowerCase().contains(searchLower)))
                    .toList();
        }

        if (requestId != null) {
            parcels = parcels.stream()
                    .filter(p -> requestId.equals(p.getRequestId()))
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
